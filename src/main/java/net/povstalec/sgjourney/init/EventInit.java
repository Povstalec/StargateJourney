package net.povstalec.sgjourney.init;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.blocks.SGJourneyBaseEntityBlock;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.misc.TreasureMapForEmeraldsTrade;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID)
public class EventInit
{
	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event)
	{
		Level level = event.getServer().overworld();
		
		StargateNetwork.get(level).loadDimensions(level);
		StargateNetwork.get(level).registerPlanets(level);
	}
	
	@SubscribeEvent
	public static void onRegisterModTabs(final CreativeModeTabEvent.Register event)
	{
		event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_items"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_items"))
			.icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(ItemInit.RAW_NAQUADAH.get());
				items.accept(ItemInit.NAQUADAH_ALLOY.get());
				items.accept(ItemInit.NAQUADAH.get());
				
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
			});
		});
		
		event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_blocks"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_blocks"))
			.icon(() -> new ItemStack(BlockInit.NAQUADAH_BLOCK.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(BlockInit.MILKY_WAY_STARGATE.get());
				items.accept(BlockInit.MILKY_WAY_DHD.get());
				items.accept(BlockInit.PEGASUS_STARGATE.get());
				items.accept(BlockInit.PEGASUS_DHD.get());

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
			});
		});
	}
	
	@SubscribeEvent
	public static void addCreative(final CreativeModeTabEvent.BuildContents event)
	{
		if(event.getTab() == CreativeModeTabs.OP_BLOCKS)
		{
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

		    trades.get(villagerLevel).add(new TreasureMapForEmeraldsTrade(8, StructureTagInit.ON_ARCHEOLOGIST_MAPS, "filled_map.sgjourney.archeologist", MapDecoration.Type.RED_X, 1, 80));
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

		    trades.get(villagerLevel).add(new TreasureMapForEmeraldsTrade(8, StructureTagInit.HAS_STARGATE, "filled_map.sgjourney.astria_porta", MapDecoration.Type.RED_X, 1, 80));
		}
	}
}
