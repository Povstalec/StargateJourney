package net.povstalec.sgjourney.common.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.HeavyNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.JaffaPouch;
import net.povstalec.sgjourney.common.config.CommonCableConfig;
import net.povstalec.sgjourney.common.config.CommonGeneticConfig;
import net.povstalec.sgjourney.common.data.Factions;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.init.VillagerInit;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.misc.TreasureMapForEmeraldsTrade;
import net.povstalec.sgjourney.common.sgjourney.SpaceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = StargateJourney.MODID)
public class ForgeEvents
{
	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event)
	{
		// Reset valid fluid caches whenever Datapacks get reloaded
		if(event.getPlayer() == null)
		{
			NaquadahLiquidizerEntity.VALID_FLUIDS_CACHE.clear();
			HeavyNaquadahLiquidizerEntity.VALID_FLUIDS_CACHE.clear();
			
			CrystallizerEntity.VALID_FLUIDS_CACHE.clear();
			AdvancedCrystallizerEntity.VALID_FLUIDS_CACHE.clear();
		}
	}
	
	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		
		SpaceLocation.registerSpaceLocations(server);
		
		StargateNetwork.get(server).updateNetwork();
		Universe.get(server).assignSpaceLocationsToAddressRegions();
		StargateNetwork.get(server).addStargates();

		TransporterNetwork.get(server).updateNetwork();
		TransporterNetwork.get(server).addTransporters();
	}
	
	@SubscribeEvent
	public static void onTick(ServerTickEvent.Pre event) //TODO Is Pre really what we need here?
	{
		MinecraftServer server = event.getServer();
		
		Factions.get(server).tickFactions(server.getTickCount());
		
		StargateNetwork.get(server).handleConnections();
		TransporterNetwork.get(server).handleConnections();
	}
	
	@SubscribeEvent
	public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		if(!event.getEntity().level().isClientSide())
			SpaceLocation.updatePlayerClientGravity((ServerPlayer) event.getEntity());
	}
	
	private static AbstractStargateEntity<?> getStargateAtPos(Level level, BlockPos pos, BlockState blockstate)
	{
		if(blockstate.getBlock() instanceof AbstractStargateBlock stargateBlock)
			return stargateBlock.getStargate(level, pos, blockstate);
		
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
			AncientGene.spawnInheritedGene(villager, CommonGeneticConfig.villager_ata_gene_inheritance_chance.get());
		else if(event.getEntity() instanceof Human human)
			AncientGene.spawnInheritedGene(human, CommonGeneticConfig.human_ata_gene_inheritance_chance.get());
		
		// Lightning recharging the Stargate
		if(entity instanceof LightningBolt lightning)
		{
			Vec3 vec3 = lightning.position();
			BlockPos strikePosition = new BlockPos((int) Math.round(vec3.x), (int) Math.round(vec3.y - 1.0E-6D), (int) Math.round(vec3.z));

			List<AbstractStargateEntity<?>> list = new ArrayList<>();
			BlockState blockstate = level.getBlockState(strikePosition);
			
			AbstractStargateEntity<?> stargateCandidate = getStargateAtPos(level, strikePosition, blockstate);
			if(stargateCandidate != null)
				list.add(stargateCandidate);
			
			for(Direction direction : Direction.values())
			{
				BlockPos pos = strikePosition.relative(direction);
				BlockState state = level.getBlockState(pos);
				
				AbstractStargateEntity<?> stargate = getStargateAtPos(level, pos, state);
				if(stargate != null)
					list.add(stargate);
			}

			Set<AbstractStargateEntity<?>> set = new HashSet<>(list);
			set.forEach(stargate -> stargate.energyStorage.receiveLongEnergy(CommonCableConfig.lightning_strike_energy.get(), false));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
	{
		Player player = event.getEntity();
		
		if(CommonGeneticConfig.ancient_players.get().contains(player.getName().getString()) || CommonGeneticConfig.ancient_players.get().contains(player.getStringUUID()))
			AncientGene.spawnAncientGene(player);
		else if(CommonGeneticConfig.inherited_ancient_gene_players.get().contains(player.getName().getString()) || CommonGeneticConfig.inherited_ancient_gene_players.get().contains(player.getStringUUID()))
			AncientGene.spawnInheritedGene(player);
		else if(CommonGeneticConfig.artificial_ancient_gene_players.get().contains(player.getName().getString()) || CommonGeneticConfig.artificial_ancient_gene_players.get().contains(player.getStringUUID()))
			AncientGene.spawnArtificialGene(player);
		else if(CommonGeneticConfig.no_ancient_gene_players.get().contains(player.getName().getString()) || CommonGeneticConfig.no_ancient_gene_players.get().contains(player.getStringUUID()))
			AncientGene.spawnNoGene(player);
		else
		{
			long seed = ((ServerLevel) player.level()).getSeed();
			seed += player.getUUID().hashCode();
			
			AncientGene.spawnInheritedGene(seed, player, CommonGeneticConfig.player_ata_gene_inheritance_chance.get());
		}
		
		if(!player.level().isClientSide())
			SpaceLocation.updatePlayerClientGravity((ServerPlayer) player);
	}
	
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event)
	{
		Entity entity = event.getEntity();
		Level level = entity.level();
		
		if(entity instanceof LivingEntity livingEntity)
		{
			JaffaPouch jaffaPouch = entity.getCapability(JaffaPouch.JAFFA_POUCH_CAPABILITY);
			if(jaffaPouch != null)
				jaffaPouch.tick(livingEntity);
			
			GoauldHost goauldHost = entity.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
			if(goauldHost != null)
				goauldHost.tick(livingEntity);
		}
		
		double parentGravity = level.isClientSide() ? SpaceLocation.currentGravity : SpaceLocation.fromDimension(level.getServer(), level.dimension()).getParentGravity();
		if(parentGravity == 0.0)
			return; // This planet's parent doesn't affect it with its gravity in any noticable way
		
		if(entity instanceof Player player)
		{
			if(player.isCreative() && player.getAbilities().flying || player.isSpectator() || player.isFallFlying())
				return;
		}
		
		long daytime = (level.getDayTime() + 6000) % 24000;
		double percentage = (double) daytime / 12000;
		
		double sin = Math.sin(percentage * Math.PI - Math.PI / 2);
		double cos = Math.cos(percentage * Math.PI - Math.PI / 2);
		Vec3 gravityVector = new Vec3(Math.abs(cos) > 0.2 ? parentGravity * cos : 0, sin < 0 ? 0 : parentGravity * sin, 0);
		
		Vec3 movementVector = entity.getDeltaMovement();
		movementVector = movementVector.add(gravityVector);
		entity.setDeltaMovement(movementVector);
		
		entity.fallDistance = entity.fallDistance * (float) (-sin + 1);
	}
	
	//TODO I'm guessing this is is now handled in the damage event
	/*@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		LivingEntity entity = event.getEntity();
		Entity attacker = event.getSource().getDirectEntity();
		float damage = event.getAmount();
		
		event.setCanceled(onAttackOrHurt(entity, attacker, damage));
	}*/
	
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event)
	{
		LivingEntity entity = event.getEntity();
		Entity attacker = event.getSource().getDirectEntity();
		float damage = event.getOriginalDamage();
		
		if(onAttackOrHurt(entity, attacker, damage))
			event.setNewDamage(0);
	}
	
	private static boolean onAttackOrHurt(LivingEntity entity, Entity attacker, float damage)
	{
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if(stack.is(ItemInit.PERSONAL_SHIELD_EMITTER.get()) && PersonalShieldItem.getFluidAmount(stack) > 0)
		{
			PersonalShieldItem.drainNaquadah(stack, Math.round(damage));
			
			if(attacker instanceof LivingEntity livingAttacker)
				livingAttacker.knockback(0.5D, entity.getX() - attacker.getX(), entity.getZ() - attacker.getZ());
			
			return true;
		}
		
		return false;
	}
	
	@SubscribeEvent
	public static void onProjectileHit(ProjectileImpactEvent event)
	{
		if(event.getRayTraceResult() instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof LivingEntity entity)
		{
			ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
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
		
		if(!state.canBeReplaced())
		{
			pos = event.getPos().relative(event.getFace());
			state = level.getBlockState(pos);
			
			ItemStack stack = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
			if(state.getBlock() instanceof AbstractStargateBlock stargate && stack.getItem() instanceof BlockItem)
			{
				if(stargate.setCover(stack, state, level, pos, event.getEntity(), InteractionHand.MAIN_HAND, event.getHitVec()))
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
			AbstractStargateEntity<?> stargate = stargateBlock.getStargate(level, pos, state);
			
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
			Level level = player.level();
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
			Level level = player.level();
			BlockPos pos = event.getPos();
			AbstractStargateEntity<?> stargate = stargateBlock.getStargate(level, pos, state);
			
			if(stargate != null)
			{
				if(!stargate.hasPermissions(player, false))
					event.setCanceled(true);
				
				else if(!stargate.blockCover.blockStates.isEmpty())
				{
					StargatePart part = event.getState().getValue(AbstractStargateBlock.PART);
					
					if(stargate.blockCover.mineBlockAt(level, player, part, pos))
						event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDetonate(ExplosionEvent.Detonate event)
	{
		Level level = event.getLevel();
		// Prevent Protected Block Entities from being destroyed by explosions
		event.getAffectedBlocks().removeIf(pos ->
		{
			BlockState state = level.getBlockState(pos);
			return state.getBlock() instanceof ProtectedBlock block && block.canExplode(level, pos, state, event.getExplosion());
		});
	}
	
	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event)
	{
		Player original = event.getOriginal();
		Player clone = event.getEntity();
		
		GoauldHost goauldHost = original.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
		if(goauldHost != null)
		{
			GoauldHost newGoauldHost = clone.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
			if(newGoauldHost != null)
				newGoauldHost.copyFrom(goauldHost);
		}
		
		JaffaPouch jaffaPouch = original.getCapability(JaffaPouch.JAFFA_POUCH_CAPABILITY);
		if(jaffaPouch != null)
		{
			JaffaPouch newJaffaPouch = clone.getCapability(JaffaPouch.JAFFA_POUCH_CAPABILITY);
			if(newJaffaPouch != null)
				newJaffaPouch.copyFrom(jaffaPouch);
		}
		
		AncientGene ataGene = original.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		if(ataGene != null)
		{
			AncientGene newAtaGene = clone.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
			if(newAtaGene != null)
				newAtaGene.copyFrom(ataGene);
		}
	}
	
	private static void addCartographerTrades(VillagerTradesEvent event)
	{
		Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		
		List<VillagerTrades.ItemListing> level2Trades = trades.get(2);
		level2Trades.add(new TreasureMapForEmeraldsTrade(13, TagInit.Structures.ON_ARCHEOLOGIST_MAPS, "filled_map.sgjourney.archeologist", MapDecorationTypes.RED_X, 12, 80));
	}
	
	private static void addArcheologistTrades(VillagerTradesEvent event)
	{
		Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
		
		List<VillagerTrades.ItemListing> level1Trades = trades.get(1);
		level1Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.PAPER, 20), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		level1Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(BlockInit.GOLDEN_IDOL.get(), 1), new ItemStack(Items.EMERALD, 5), 4, 12, 0.09F));
		level1Trades.add(new TreasureMapForEmeraldsTrade(8, TagInit.Structures.ON_ARCHEOLOGIST_MAPS, "filled_map.sgjourney.archeologist", MapDecorationTypes.RED_X, 12, 80));
		
		List<VillagerTrades.ItemListing> level2Trades = trades.get(2);
		level2Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.COMPASS, 1), 4, 12, 0.09F));
		level2Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.WRITABLE_BOOK, 1), 4, 12, 0.09F));
		level2Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.GOLD_INGOT, 3), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		
		List<VillagerTrades.ItemListing> level3Trades = trades.get(3);
		level3Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(BlockInit.FIRE_PIT.get(), 4), 1, 12, 0.09F));
		level3Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(BlockInit.SANDSTONE_HIEROGLYPHS.get(), 3), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		level3Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(BlockInit.SANDSTONE_WITH_LAPIS.get(), 3), 4, 12, 0.09F));
		
		List<VillagerTrades.ItemListing> level4Trades = trades.get(4);
		level4Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(BlockInit.STONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		level4Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(BlockInit.SANDSTONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		level4Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(BlockInit.RED_SANDSTONE_SYMBOL.get(), 1), 4, 12, 0.09F));
		level4Trades.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.BONE, 4), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
		
		List<VillagerTrades.ItemListing> level5Trades = trades.get(5);
		level5Trades.add(new TreasureMapForEmeraldsTrade.StargateMapTrade(8, "filled_map.sgjourney.chappa_ai", 80));
	}
	
	@SubscribeEvent
	public static void addCustomTrades(VillagerTradesEvent event)
	{
		if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
			addArcheologistTrades(event);
		else if(event.getType() == VillagerProfession.CARTOGRAPHER)
			addCartographerTrades(event);
	}
}
