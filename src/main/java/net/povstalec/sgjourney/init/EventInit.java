package net.povstalec.sgjourney.init;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.blocks.SGJourneyBaseEntityBlock;
import net.povstalec.sgjourney.blocks.dhd.MilkyWayDHDBlock;
import net.povstalec.sgjourney.capabilities.AncientGene;
import net.povstalec.sgjourney.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.capabilities.BloodstreamNaquadah;
import net.povstalec.sgjourney.capabilities.BloodstreamNaquadahProvider;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.items.SyringeItem;
import net.povstalec.sgjourney.misc.TreasureMapForEmeraldsTrade;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID)
public class EventInit
{
	public static CreativeModeTab ITEM_TAB;
	public static CreativeModeTab BLOCK_TAB;
	
	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		
		StargateNetwork.get(server).updateNetwork(server);
	}
	
	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event)
	{
		if(event.phase.equals(TickEvent.Phase.START))
		{
			MinecraftServer server = event.getServer();
			StargateNetwork.get(server).handleConnections(server);
		}
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
	public static void onRegisterModTabs(final CreativeModeTabEvent.Register event)
	{
		ITEM_TAB = event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_items"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_items"))
			.icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(ItemInit.RAW_NAQUADAH.get());
				items.accept(ItemInit.NAQUADAH_ALLOY.get());
				items.accept(ItemInit.PURE_NAQUADAH.get());
				items.accept(ItemInit.NAQUADAH.get());
				items.accept(ItemInit.LIQUID_NAQUADAH_BUCKET.get());
				items.accept(ItemInit.LIQUID_NAQUADAH_BOTTLE.get());
				
				items.accept(ItemInit.PDA.get());
				
				items.accept(ItemInit.NAQUADAH_SWORD.get());
				items.accept(ItemInit.NAQUADAH_PICKAXE.get());
				items.accept(ItemInit.NAQUADAH_AXE.get());
				items.accept(ItemInit.NAQUADAH_SHOVEL.get());
				items.accept(ItemInit.NAQUADAH_HOE.get());
				
				items.accept(ItemInit.NAQUADAH_HELMET.get());
				items.accept(ItemInit.NAQUADAH_CHESTPLATE.get());
				items.accept(ItemInit.NAQUADAH_LEGGINGS.get());
				items.accept(ItemInit.NAQUADAH_BOOTS.get());
				
				items.accept(ItemInit.KARA_KESH.get());
				
				items.accept(ItemInit.MATOK.get());
				
				items.accept(ItemInit.JACKAL_HELMET.get());
				//items.accept(ItemInit.HORUS_HELMET.get());
				items.accept(ItemInit.JAFFA_HELMET.get());
				items.accept(ItemInit.JAFFA_CHESTPLATE.get());
				items.accept(ItemInit.JAFFA_LEGGINGS.get());
				items.accept(ItemInit.JAFFA_BOOTS.get());

				items.accept(ItemInit.ZPM.get());

				items.accept(ItemInit.SYRINGE.get());
				items.accept(SyringeItem.addContents(SyringeItem.Contents.PROTOTYPE_ATA));
				items.accept(SyringeItem.addContents(SyringeItem.Contents.ATA));

				items.accept(ItemInit.LARGE_CONTROL_CRYSTAL.get());
				items.accept(ItemInit.MEMORY_CRYSTAL.get());
				items.accept(ItemInit.TRANSPORTATION_CRYSTAL.get());
				items.accept(ItemInit.ENERGY_CRYSTAL.get());
				items.accept(ItemInit.COMMUNICATION_CRYSTAL.get());
			});
		});
		
		BLOCK_TAB = event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_blocks"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_blocks"))
			.icon(() -> new ItemStack(BlockInit.NAQUADAH_BLOCK.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(BlockInit.UNIVERSE_STARGATE.get());
				items.accept(BlockInit.MILKY_WAY_STARGATE.get());
				//items.accept(BlockInit.MILKY_WAY_DHD.get());
				items.accept(MilkyWayDHDBlock.milkyWayCrystalSetup(new ItemStack(BlockInit.MILKY_WAY_DHD.get())));
				items.accept(BlockInit.PEGASUS_STARGATE.get());
				items.accept(BlockInit.PEGASUS_DHD.get());
				//items.accept(BlockInit.CLASSIC_STARGATE.get());
				items.accept(BlockInit.CLASSIC_DHD.get());

				items.accept(BlockInit.TRANSPORT_RINGS.get());
				items.accept(BlockInit.RING_PANEL.get());
				
				items.accept(BlockInit.ARCHEOLOGY_TABLE.get());
				items.accept(BlockInit.GOLDEN_IDOL.get());

				items.accept(BlockInit.NAQUADAH_ORE.get());
				items.accept(BlockInit.DEEPSLATE_NAQUADAH_ORE.get());
				items.accept(BlockInit.NETHER_NAQUADAH_ORE.get());
				items.accept(BlockInit.RAW_NAQUADAH_BLOCK.get());
				
				items.accept(BlockInit.NAQUADAH_BLOCK.get());
				items.accept(BlockInit.NAQUADAH_STAIRS.get());
				items.accept(BlockInit.NAQUADAH_SLAB.get());
				
				items.accept(BlockInit.SANDSTONE_HIEROGLYPHS.get());
				items.accept(BlockInit.SANDSTONE_SWITCH.get());
				items.accept(BlockInit.SANDSTONE_WITH_LAPIS.get());
				items.accept(BlockInit.SANDSTONE_SYMBOL.get());
				items.accept(BlockInit.STONE_SYMBOL.get());
				
				items.accept(BlockInit.SANDSTONE_CARTOUCHE.get());
				items.accept(BlockInit.STONE_CARTOUCHE.get());
				
				items.accept(BlockInit.FIRE_PIT.get());

				items.accept(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
				items.accept(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				items.accept(BlockInit.BASIC_INTERFACE.get());
				items.accept(BlockInit.CRYSTAL_INTERFACE.get());

				items.accept(BlockInit.ANCIENT_GENE_DETECTOR.get());
				
				items.accept(BlockInit.ZPM_HUB.get());
			});
		});
	}
	
	@SubscribeEvent
	public static void addCreative(final CreativeModeTabEvent.BuildContents event)
	{
		if(event.getTab() == CreativeModeTabs.OP_BLOCKS)
		{
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.UNIVERSE_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.TRANSPORT_RINGS.get())));
		}
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
