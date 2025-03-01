package net.povstalec.sgjourney.common.init;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.dhd.ClassicDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.MilkyWayDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.PegasusDHDBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateBlock;
import net.povstalec.sgjourney.common.blocks.tech.AbstractTransporterBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;
import net.povstalec.sgjourney.common.items.SyringeItem;
import net.povstalec.sgjourney.common.items.VialItem;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID)
public class TabInit
{
	public static final ResourceLocation STARGATE_ITEMS = new ResourceLocation(StargateJourney.MODID, "stargate_items");
	public static final ResourceLocation STARGATE_STUFF = new ResourceLocation(StargateJourney.MODID, "stargate_stuff");
	public static final ResourceLocation STARGATE_BLOCKS = new ResourceLocation(StargateJourney.MODID, "stargate_blocks");
	
	@SubscribeEvent
	public static void onRegisterModTabs(final CreativeModeTabEvent.Register event)
	{
		event.registerCreativeModeTab(STARGATE_ITEMS, (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_items"))
			.icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(ItemInit.RAW_NAQUADAH.get());
				items.accept(ItemInit.NAQUADAH_ALLOY.get());
				items.accept(ItemInit.NAQUADAH_ALLOY_NUGGET.get());
				items.accept(ItemInit.REFINED_NAQUADAH.get());
				items.accept(ItemInit.PURE_NAQUADAH.get());
				items.accept(ItemInit.NAQUADAH.get());
				items.accept(ItemInit.LIQUID_NAQUADAH_BUCKET.get());
				items.accept(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET.get());
				items.accept(ItemInit.VIAL.get());
				items.accept(VialItem.liquidNaquadahSetup());
				items.accept(VialItem.heavyLiquidNaquadahSetup());

				items.accept(ItemInit.NAQUADAH_ROD.get());
				items.accept(ItemInit.REACTION_CHAMBER.get());
				items.accept(ItemInit.NAQUADAH_GENERATOR_CORE.get());
				items.accept(ItemInit.PLASMA_CONVERTER.get());
				
				items.accept(ItemInit.PDA.get());
				//items.accept(ItemInit.UNIVERSE_DIALER.get());

				items.accept(ItemInit.GDO.get());
				
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
				items.accept(ItemInit.RING_REMOTE.get());
				
				items.accept(ItemInit.MATOK.get());
				
				items.accept(ItemInit.JACKAL_HELMET.get());
				//items.accept(ItemInit.HORUS_HELMET.get());
				items.accept(ItemInit.JAFFA_HELMET.get());
				items.accept(ItemInit.JAFFA_CHESTPLATE.get());
				items.accept(ItemInit.JAFFA_LEGGINGS.get());
				items.accept(ItemInit.JAFFA_BOOTS.get());

				items.accept(NaquadahFuelRodItem.fuelRodSetup());
				
				items.accept(ItemInit.ZPM.get());
				items.accept(PersonalShieldItem.personalShieldSetup());

				items.accept(ItemInit.SYRINGE.get());
				items.accept(SyringeItem.addContents(SyringeItem.Contents.PROTOTYPE_ATA));
				items.accept(SyringeItem.addContents(SyringeItem.Contents.ATA));
				
				items.accept(ItemInit.UNITY_SHARD.get());
				
				items.accept(ItemInit.CRYSTAL_BASE.get());
				items.accept(ItemInit.ADVANCED_CRYSTAL_BASE.get());
				
				items.accept(ItemInit.LARGE_CONTROL_CRYSTAL.get());
				items.accept(ItemInit.CONTROL_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_CONTROL_CRYSTAL.get());
				items.accept(ItemInit.MEMORY_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_MEMORY_CRYSTAL.get());
				//items.accept(MemoryCrystalItem.atlantisAddress());
				//items.accept(MemoryCrystalItem.abydosAddress());
				items.accept(ItemInit.MATERIALIZATION_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_MATERIALIZATION_CRYSTAL.get());
				items.accept(ItemInit.ENERGY_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_ENERGY_CRYSTAL.get());
				items.accept(ItemInit.TRANSFER_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get());
				items.accept(ItemInit.COMMUNICATION_CRYSTAL.get());
				items.accept(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get());
			});
		});
		
		event.registerCreativeModeTab(STARGATE_STUFF,
				List.of(STARGATE_ITEMS), List.of(STARGATE_BLOCKS), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_stuff"))
			.icon(() -> new ItemStack(BlockInit.MILKY_WAY_STARGATE.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(BlockInit.UNIVERSE_STARGATE.get());
				items.accept(BlockInit.MILKY_WAY_STARGATE.get());
				items.accept(AbstractStargateBaseBlock.localPointOfOrigin(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
				items.accept(MilkyWayDHDBlock.milkyWayCrystalSetup(false));
				items.accept(BlockInit.PEGASUS_STARGATE.get());
				items.accept(PegasusStargateBlock.localSymbols(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
				items.accept(PegasusDHDBlock.pegasusCrystalSetup(false));
				items.accept(BlockInit.CLASSIC_STARGATE.get());
				items.accept(AbstractStargateBaseBlock.localPointOfOrigin(new ItemStack(BlockInit.CLASSIC_STARGATE.get())));
				items.accept(BlockInit.CLASSIC_STARGATE_BASE_BLOCK.get());
				items.accept(BlockInit.CLASSIC_STARGATE_CHEVRON_BLOCK.get());
				items.accept(BlockInit.CLASSIC_STARGATE_RING_BLOCK.get());
				items.accept(ClassicDHDBlock.classicCrystalSetup(false));
				items.accept(BlockInit.TOLLAN_STARGATE.get());
				
				items.accept(ItemInit.FUSION_CORE.get());
				items.accept(ItemInit.NAQUADAH_GENERATOR_CORE.get());
				items.accept(NaquadahFuelRodItem.fuelRodSetup());
				
				items.accept(BlockInit.UNIVERSE_STARGATE_CHEVRON.get());
				
				items.accept(BlockInit.BASIC_INTERFACE.get());
				items.accept(BlockInit.CRYSTAL_INTERFACE.get());
				items.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());

				items.accept(ItemInit.STARGATE_SHIELDING_RING.get());
				
				items.accept(ItemInit.COPPER_IRIS.get());
				items.accept(ItemInit.IRON_IRIS.get());
				items.accept(ItemInit.GOLDEN_IRIS.get());
				items.accept(ItemInit.DIAMOND_IRIS.get());
				items.accept(ItemInit.NETHERITE_IRIS.get());

				items.accept(ItemInit.NAQUADAH_ALLOY_IRIS.get());
				//items.accept(ItemInit.TRINIUM_IRIS.get());

				items.accept(ItemInit.BRONZE_IRIS.get());
				items.accept(ItemInit.STEEL_IRIS.get());

				items.accept(ItemInit.CALL_FORWARDING_DEVICE.get());

				if(CommonStargateConfig.enable_classic_stargate_upgrades.get())
				{
					items.accept(StargateUpgradeItem.stargateType(BlockInit.UNIVERSE_STARGATE.get()));
					items.accept(StargateUpgradeItem.stargateType(BlockInit.MILKY_WAY_STARGATE.get()));
					items.accept(StargateUpgradeItem.stargateType(BlockInit.PEGASUS_STARGATE.get()));
					items.accept(StargateUpgradeItem.stargateType(BlockInit.TOLLAN_STARGATE.get()));
				}
				if(CommonStargateConfig.enable_stargate_variants.get())
				{
					items.accept(ItemInit.STARGATE_VARIANT_CRYSTAL.get());
					items.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_movie"));
					items.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_promo"));
					items.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_sg-1"));
					items.accept(StargateVariantItem.stargateVariant("sgjourney:pegasus_atlantis"));
					items.accept(StargateVariantItem.stargateVariant("sgjourney:classic_milky_way"));
				}
			});
		});
		
		event.registerCreativeModeTab(STARGATE_BLOCKS, (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_blocks"))
			.icon(() -> new ItemStack(BlockInit.NAQUADAH_BLOCK.get()))
			.displayItems((flag, items, hasPermisions) ->
			{
				items.accept(BlockInit.UNIVERSE_STARGATE_CHEVRON.get());
				
				items.accept(BlockInit.TRANSPORT_RINGS.get());
				items.accept(BlockInit.RING_PANEL.get());
				
				items.accept(BlockInit.ARCHEOLOGY_TABLE.get());
				items.accept(BlockInit.GOLDEN_IDOL.get());
				
				items.accept(BlockInit.SULFUR_SAND.get());
				items.accept(BlockInit.BUDDING_UNITY.get());
				items.accept(BlockInit.SMALL_UNITY_BUD.get());
				items.accept(BlockInit.MEDIUM_UNITY_BUD.get());
				items.accept(BlockInit.LARGE_UNITY_BUD.get());
				items.accept(BlockInit.UNITY_CLUSTER.get());

				items.accept(BlockInit.NAQUADAH_ORE.get());
				items.accept(BlockInit.DEEPSLATE_NAQUADAH_ORE.get());
				items.accept(BlockInit.NETHER_NAQUADAH_ORE.get());
				items.accept(BlockInit.RAW_NAQUADAH_BLOCK.get());
				items.accept(BlockInit.PURE_NAQUADAH_BLOCK.get());
				
				items.accept(BlockInit.NAQUADAH_BLOCK.get());
				items.accept(BlockInit.NAQUADAH_STAIRS.get());
				items.accept(BlockInit.NAQUADAH_SLAB.get());
				items.accept(BlockInit.CUT_NAQUADAH_BLOCK.get());
				items.accept(BlockInit.CUT_NAQUADAH_STAIRS.get());
				items.accept(BlockInit.CUT_NAQUADAH_SLAB.get());
				
				items.accept(BlockInit.SANDSTONE_HIEROGLYPHS.get());
				items.accept(BlockInit.SANDSTONE_SWITCH.get());
				items.accept(BlockInit.SANDSTONE_WITH_LAPIS.get());
				items.accept(BlockInit.SANDSTONE_WITH_GOLD.get());
				items.accept(BlockInit.SANDSTONE_SYMBOL.get());
				items.accept(BlockInit.SANDSTONE_CARTOUCHE.get());
				
				items.accept(BlockInit.RED_SANDSTONE_GLYPHS.get());
				items.accept(BlockInit.RED_SANDSTONE_WITH_LAPIS.get());
				items.accept(BlockInit.RED_SANDSTONE_WITH_GOLD.get());
				items.accept(BlockInit.RED_SANDSTONE_SYMBOL.get());
				items.accept(BlockInit.RED_SANDSTONE_CARTOUCHE.get());
				
				items.accept(BlockInit.STONE_SYMBOL.get());
				items.accept(BlockInit.STONE_CARTOUCHE.get());
				
				items.accept(BlockInit.FIRE_PIT.get());

				items.accept(BlockInit.NAQUADAH_LIQUIDIZER.get());
				items.accept(BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
				
				items.accept(BlockInit.CRYSTALLIZER.get());
				items.accept(BlockInit.ADVANCED_CRYSTALLIZER.get());

				items.accept(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
				items.accept(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				items.accept(BlockInit.BASIC_INTERFACE.get());
				items.accept(BlockInit.CRYSTAL_INTERFACE.get());
				items.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());

				items.accept(BlockInit.TRANSCEIVER.get());

				items.accept(BlockInit.ANCIENT_GENE_DETECTOR.get());
				
				items.accept(BlockInit.ZPM_HUB.get());
			});
		});
	}
	
	@SubscribeEvent
	public static void addCreative(final CreativeModeTabEvent.BuildContents event)
	{
		if(event.getTab() == CreativeModeTabs.OP_BLOCKS && event.hasPermissions())
		{
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.UNIVERSE_STARGATE.get())));
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
			event.accept(MilkyWayDHDBlock.milkyWayCrystalSetup(true));
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
			event.accept(PegasusDHDBlock.pegasusCrystalSetup(true));
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.CLASSIC_STARGATE.get())));
			event.accept(ClassicDHDBlock.classicCrystalSetup(true));
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.TOLLAN_STARGATE.get())));
			event.accept(AbstractTransporterBlock.excludeFromNetwork(new ItemStack(BlockInit.TRANSPORT_RINGS.get())));
		}
	}
}
