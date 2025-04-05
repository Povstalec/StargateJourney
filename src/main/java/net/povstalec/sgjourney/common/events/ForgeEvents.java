package net.povstalec.sgjourney.common.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadah;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadahProvider;
import net.povstalec.sgjourney.common.config.CommonGeneticConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.init.VillagerInit;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.misc.TreasureMapForEmeraldsTrade;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{
	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		
		StargateNetwork.get(server).updateNetwork(server);
		StargateNetwork.get(server).addStargates(server);

		TransporterNetwork.get(server).updateNetwork(server);
	}
	
	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event)
	{
		MinecraftServer server = event.getServer();
		if(event.phase.equals(TickEvent.Phase.START) && server != null)
			StargateNetwork.get(server).handleConnections();
	}
	
	private static AbstractStargateEntity getStargateAtPos(Level level, BlockPos pos, BlockState blockstate)
	{
		if(blockstate.getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			AbstractStargateEntity stargate = stargateBlock.getStargate(level, pos, blockstate);
			
			return stargate;
		}
		
		return null;
	}
	
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Level level = event.getLevel();
		Entity entity = event.getEntity();
		
		if(level.isClientSide())
			return;
		
		if(event.getEntity() instanceof AbstractVillager villager)
			AncientGene.inheritGene(villager, CommonGeneticConfig.villager_ata_gene_inheritance_chance.get());
		else if(event.getEntity() instanceof Human human)
			AncientGene.inheritGene(human, CommonGeneticConfig.human_ata_gene_inheritance_chance.get());
		
		// Lightning recharging the Stargate
		if(entity instanceof LightningBolt lightning)
		{
			Vec3 vec3 = lightning.position();
			BlockPos strikePosition = new BlockPos((int) Math.round(vec3.x), (int) Math.round(vec3.y - 1.0E-6D), (int) Math.round(vec3.z));

			List<AbstractStargateEntity> list = new ArrayList<AbstractStargateEntity>();
			BlockState blockstate = level.getBlockState(strikePosition);
			
			AbstractStargateEntity stargateCandidate = getStargateAtPos(level, strikePosition, blockstate);
			if(stargateCandidate != null)
				list.add(stargateCandidate);
			
			for(Direction direction : Direction.values())
			{
				BlockPos pos = strikePosition.relative(direction);
				BlockState state = level.getBlockState(pos);
				
				AbstractStargateEntity stargate = getStargateAtPos(level, pos, state);
				if(stargate != null)
					list.add(stargate);
			}

			Set<AbstractStargateEntity> set = new HashSet<AbstractStargateEntity>(list);
			set.stream().forEach(stargate -> stargate.receiveEnergy(100000, false));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
	{
		Player player = event.getEntity();
		
		long seed = ((ServerLevel) player.getLevel()).getSeed();
		seed += player.getUUID().hashCode();
		
		AncientGene.inheritGene(seed, player, CommonGeneticConfig.player_ata_gene_inheritance_chance.get());
	}
	
	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event)
	{
		Entity entity = event.getEntity();
		Level level = entity.getLevel();
		
		//TODO Make this into something you can edit with Datapacks
		if(!level.dimension().location().equals(new ResourceLocation(StargateJourney.MODID, "cavum_tenebrae")))
			return;
		
		if(entity instanceof Player player)
		{
			if(player.isCreative() && player.getAbilities().flying)
				return;
			else if(player.isSpectator() && player.getAbilities().flying)
				return;
		}
		
		long daytime = (level.getDayTime() + 6000) % 24000;
		double percentage = (double) daytime / 12000;
		
		double sin = Math.sin(percentage * Math.PI - Math.PI / 2);
		double cos = Math.cos(percentage * Math.PI - Math.PI / 2);
		Vec3 gravityVector = new Vec3(Math.abs(cos) > 0.2 ? 0.07 * cos : 0, sin < 0 ? 0 : 0.07 * sin, 0);
		
		Vec3 movementVector = entity.getDeltaMovement();
		movementVector = movementVector.add(gravityVector);
		entity.setDeltaMovement(movementVector);
		
		entity.fallDistance = entity.fallDistance * (float) (-sin + 1);
	}
	
	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		Entity entity = event.getEntity();
		Entity attacker = event.getSource().getDirectEntity();
		float damage = event.getAmount();
		
		event.setCanceled(onAttackOrHurt(entity, attacker, damage));
	}
	
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event)
	{
		Entity entity = event.getEntity();
		Entity attacker = event.getSource().getDirectEntity();
		float damage = event.getAmount();
		
		event.setCanceled(onAttackOrHurt(entity, attacker, damage));
	}
	
	private static boolean onAttackOrHurt(Entity entity, Entity attacker, float damage)
	{
		if(entity instanceof Player player)
		{
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if(stack.is(ItemInit.PERSONAL_SHIELD_EMITTER.get()) && PersonalShieldItem.getFluidAmount(stack) > 0)
			{
				int naquadahDepleted = (int) damage;

				PersonalShieldItem.drainNaquadah(stack, naquadahDepleted);
				
				if(attacker instanceof LivingEntity livingAttacker)
					livingAttacker.knockback(0.5D, player.getX() - attacker.getX(), player.getZ() - attacker.getZ());
				
				return true;
			}
		}
		return false;
	}
	
	@SubscribeEvent
	public static void onProjectileHit(ProjectileImpactEvent event)
	{
		if(event.getRayTraceResult() instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof Player player)
		{
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if(stack.is(ItemInit.PERSONAL_SHIELD_EMITTER.get()) && PersonalShieldItem.getFluidAmount(stack) > 0)
			{
				Projectile projectile = event.getProjectile();
				
				int naquadahDepleted = (int) projectile.getDeltaMovement().length();
				
				PersonalShieldItem.drainNaquadah(stack, naquadahDepleted);
				
				projectile.setDeltaMovement(projectile.getDeltaMovement().reverse().scale(0.2));
				
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) // Add cover block to Stargate when player is clicking on a face of another block
	{
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		
		if(!state.getMaterial().isReplaceable())
		{
			pos = event.getPos().relative(event.getFace());
			state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof AbstractStargateBlock stargate && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof BlockItem)
			{
				if(stargate.setCover(state, level, pos, event.getEntity(), InteractionHand.MAIN_HAND, event.getHitVec()))
				{
					event.getEntity().swing(InteractionHand.MAIN_HAND);

					event.setCanceled(true);
				}
			}
		}
		
	}
	
	@SubscribeEvent
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) // Prevent player from breaking the Stargate when it has cover blocks
	{
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof ProtectedBlock protectedBlock)
		{
			// Player doesn't have permissions to break the Block
			if(!protectedBlock.hasPermissions(level, pos, state, event.getEntity(), true))
			{
				event.setCanceled(true);
				return;
			}
		}
		
		if(state.getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			AbstractStargateEntity stargate = stargateBlock.getStargate(level, pos, state);
			
			if(stargate != null && !stargate.blockCover.blockStates.isEmpty())
			{
				StargatePart part = state.getValue(AbstractStargateBlock.PART);
				
				if(stargate.blockCover.getBlockAt(part).isEmpty())
				{
					stargate.spawnCoverParticles();
					
					event.getEntity().displayClientMessage(Component.translatable("block.sgjourney.stargate.break_cover_blocks"), true);
					event.setCanceled(true);
				}
			}
		}
		
	}
	
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) // Break individual Blocks covering the Stargate
	{
		BlockState state = event.getState();
		
		if(state.getBlock() instanceof ProtectedBlock protectedBlock)
		{
			Player player = event.getPlayer();
			Level level = player.getLevel();
			BlockPos pos = event.getPos();
			
			// Player doesn't have permissions to break the Block
			if(!protectedBlock.hasPermissions(level, pos, state, player, false))
			{
				event.setCanceled(true);
				return;
			}
		}
		
		if(state.getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			Player player = event.getPlayer();
			Level level = player.getLevel();
			BlockPos pos = event.getPos();
			AbstractStargateEntity stargate = stargateBlock.getStargate(level, pos, state);
			
			if(stargate != null)
			{
				if(!stargate.hasPermissions(player, false))
					event.setCanceled(true);
				
				else if(!stargate.blockCover.blockStates.isEmpty())
				{
					StargatePart part = event.getState().getValue(AbstractStargateBlock.PART);
					
					if (stargate.blockCover.mineBlockAt(level, player, part, pos))
						event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event)
	{
		if(event.getObject() instanceof Player)
		{
			if(!event.getObject().getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).isPresent())
				event.addCapability(new ResourceLocation(StargateJourney.MODID, "bloodstream_naquadah"), new BloodstreamNaquadahProvider());
			
			if(!event.getObject().getCapability(AncientGeneProvider.ANCIENT_GENE).isPresent())
				event.addCapability(new ResourceLocation(StargateJourney.MODID, "ancient_gene"), new AncientGeneProvider());
		}
		
		else if(event.getObject() instanceof AbstractVillager)
		{
			if(!event.getObject().getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).isPresent())
				event.addCapability(new ResourceLocation(StargateJourney.MODID, "bloodstream_naquadah"), new BloodstreamNaquadahProvider());
			
			if(!event.getObject().getCapability(AncientGeneProvider.ANCIENT_GENE).isPresent())
				event.addCapability(new ResourceLocation(StargateJourney.MODID, "ancient_gene"), new AncientGeneProvider());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event)
	{
		Player original = event.getOriginal();
		Player clone = event.getEntity();
		original.reviveCaps();
		
		original.getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).ifPresent(oldCap ->
			clone.getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).ifPresent(newCap -> newCap.copyFrom(oldCap)));
		
		original.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(oldCap -> 
			clone.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(newCap -> newCap.copyFrom(oldCap)));
		
		original.invalidateCaps();
	}
	
	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
	{
		event.register(BloodstreamNaquadah.class);
		event.register(AncientGene.class);
	}
	
	@SubscribeEvent
	public static void addCustomTrades(VillagerTradesEvent event)
	{
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 1;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.PAPER, 20), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 1;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(BlockInit.GOLDEN_IDOL.get(), 1), new ItemStack(Items.EMERALD, 5), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 1;

		    trades.get(villagerLevel).add(new TreasureMapForEmeraldsTrade(8, TagInit.Structures.ON_ARCHEOLOGIST_MAPS, "filled_map.sgjourney.archeologist", MapDecoration.Type.RED_X, 1, 80));
		}
		
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 2;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 4), new ItemStack(Items.COMPASS, 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 2;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 4), new ItemStack(Items.WRITABLE_BOOK, 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 2;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.GOLD_INGOT, 3), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		}
		
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 3;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 3), new ItemStack(BlockInit.FIRE_PIT.get(), 4), 1, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 3;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(BlockInit.SANDSTONE_HIEROGLYPHS.get(), 3), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 3;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 4), new ItemStack(BlockInit.SANDSTONE_WITH_LAPIS.get(), 3), 4, 12, 0.09F));
		}
		
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 4;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 4), new ItemStack(BlockInit.STONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		            
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 4;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.EMERALD, 4), new ItemStack(BlockInit.SANDSTONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
			int villagerLevel = 4;
			
			trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
					new ItemStack(Items.EMERALD, 4), new ItemStack(BlockInit.RED_SANDSTONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		}
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 4;

		    trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
		            new ItemStack(Items.BONE, 4), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		}
		
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
		{
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		    int villagerLevel = 5;

		    trades.get(villagerLevel).add(new TreasureMapForEmeraldsTrade.StargateMapTrade(8, "filled_map.sgjourney.chappa_ai", 80));
		}
	}
}
