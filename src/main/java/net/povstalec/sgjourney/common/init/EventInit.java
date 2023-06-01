package net.povstalec.sgjourney.common.init;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadah;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadahProvider;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.misc.TreasureMapForEmeraldsTrade;

public class EventInit
{
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public class ForgeEvents
	{
		@SubscribeEvent
		public static void onServerStarting(ServerStartingEvent event)
		{
			MinecraftServer server = event.getServer();
			
			StargateNetwork.get(server).updateNetwork(server);
		}
		
		@SubscribeEvent
		public static void onTick(TickEvent.ServerTickEvent event)
		{
			MinecraftServer server = event.getServer();
			if(event.phase.equals(TickEvent.Phase.START) && server != null)
				StargateNetwork.get(server).handleConnections();
		}
		
		@SubscribeEvent
		public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
		{
			Player player = event.getEntity();
			
			if(player.getName().getString().equals("Dev") || player.getName().getString().equals("Woldericz_junior"))
				AncientGene.addAncient(player);
			else
				AncientGene.inheritGene(player);
		}
		
		@SubscribeEvent
		public static void onLivingHurt(LivingAttackEvent event)
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
				if(stack.is(ItemInit.PERSONAL_SHIELD_EMITTER.get()) && PersonalShieldItem.getEnergy(stack) > 0)
				{
					int energyDepleted = (int) damage * 500;

					PersonalShieldItem.depleteEnergy(stack, energyDepleted);
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
				if(stack.is(ItemInit.PERSONAL_SHIELD_EMITTER.get()) && PersonalShieldItem.getEnergy(stack) > 0)
				{
					Projectile projectile = event.getProjectile();
					
					int energyDepleted = (int) projectile.getDeltaMovement().length() * 500;
					
					PersonalShieldItem.depleteEnergy(stack, energyDepleted);
					projectile.setDeltaMovement(projectile.getDeltaMovement().reverse().scale(0.2));
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void onEntityJoined(EntityJoinLevelEvent event)
		{
			if(event.getLevel().isClientSide())
				return;
			
			if(event.getEntity() instanceof AbstractVillager villager)
				AncientGene.inheritGene(villager);
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
			            new ItemStack(Items.BONE, 4), new ItemStack(Items.EMERALD, 1), 4, 12, 0.09F));
			}
			
			if(event.getType() == VillagerInit.ARCHEOLOGIST.get())
			{
				Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
			    int villagerLevel = 5;

			    trades.get(villagerLevel).add(new TreasureMapForEmeraldsTrade(8, TagInit.Structures.HAS_STARGATE, "filled_map.sgjourney.astria_porta", MapDecoration.Type.RED_X, 1, 80));
			}
		}
	}
	
	
	
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public class ModEvents
	{
		@SubscribeEvent
		public static void entityAttributeEvent(EntityAttributeCreationEvent event)
		{
			event.put(EntityInit.GOAULD.get(), Goauld.createAttributes().build());
		}
	}
	
	
}
