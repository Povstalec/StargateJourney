package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.blocks.dhd.ClassicDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.MilkyWayDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.PegasusDHDBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.items.*;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public class TabInit
{
	public static final ResourceLocation STARGATE_ITEMS_LOCATION = ResourceLocation.tryBuild(StargateJourney.MODID, "stargate_items");
	public static final ResourceLocation STARGATE_STUFF_LOCATION = ResourceLocation.tryBuild(StargateJourney.MODID, "stargate_stuff");
	public static final ResourceLocation STARGATE_BLOCKS_LOCATION = ResourceLocation.tryBuild(StargateJourney.MODID, "stargate_blocks");
	
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =  DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StargateJourney.MODID);
	
	public static final RegistryObject<CreativeModeTab> STARGATE_BLOCKS = CREATIVE_MODE_TABS.register("stargate_blocks", () ->
			CreativeModeTab.builder().icon(() -> new ItemStack(BlockInit.NAQUADAH_IRON_BLOCK.get()))
					.title(Component.translatable("itemGroup.stargate_blocks"))
					.displayItems((parameters, output) ->
					{
						if(parameters.hasPermissions())
						{
						
						}
						
						output.accept(ItemInit.RAW_NAQUADAH.get());
						output.accept(ItemInit.REFINED_NAQUADAH.get());
						output.accept(ItemInit.NAQUADAH_INGOT.get());
						output.accept(ItemInit.NAQUADAH_NUGGET.get());
						
						output.accept(ItemInit.NAQUADAH_COPPER_MIXTURE.get());
						output.accept(ItemInit.NAQUADAH_COPPER_ALLOY.get());
						output.accept(ItemInit.NAQUADAH_COPPER_NUGGET.get());
						
						output.accept(ItemInit.NAQUADAH_IRON_MIXTURE.get());
						output.accept(ItemInit.NAQUADAH_IRON_ALLOY.get());
						output.accept(ItemInit.NAQUADAH_IRON_NUGGET.get());
						
						output.accept(ItemInit.PURE_NAQUADAH.get());
						output.accept(ItemInit.NAQUADAH.get());
						
						output.accept(ItemInit.RAW_NAQUADRIA.get());
						
						output.accept(ItemInit.RAW_TRINIUM.get());
						output.accept(ItemInit.TRINIUM_INGOT.get());
						output.accept(ItemInit.TRINIUM_NUGGET.get());
						
						output.accept(ItemInit.LIQUID_NAQUADAH_BUCKET.get());
						output.accept(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET.get());
						output.accept(ItemInit.VIAL.get());
						output.accept(VialItem.liquidNaquadahSetup());
						output.accept(VialItem.heavyLiquidNaquadahSetup());
						
						output.accept(ItemInit.NAQUADAH_ROD.get());
						output.accept(ItemInit.NAQUADAH_IRON_ROD.get());
						output.accept(ItemInit.NAQUADAH_COPPER_ROD.get());
						output.accept(ItemInit.TRINIUM_ROD.get());
						output.accept(ItemInit.REACTION_CHAMBER.get());
						output.accept(ItemInit.NAQUADAH_GENERATOR_CORE.get());
						output.accept(NaquadahFuelRodItem.fuelRodSetup());
						//output.accept(ItemInit.LIQUID_NAQUADAH_REACTOR_CORE.get());
						output.accept(ItemInit.PLASMA_CONVERTER.get());
						output.accept(ItemInit.MATTER_PROJECTOR.get());
						
						output.accept(ItemInit.NAQUADAH_POWER_CELL.get());
						output.accept(PowerCellItem.liquidNaquadahSetup());
						output.accept(PowerCellItem.heavyLiquidNaquadahSetup());
						
						output.accept(ItemInit.ANCIENT_TRANSPORT_RING.get());
						output.accept(ItemInit.GOAULD_TRANSPORT_RING.get());
						
						output.accept(ItemInit.POCKET_CRYSTAL_COMPUTER.get());
						output.accept(ItemInit.PDA.get());
						//output.accept(ItemInit.ANCIENT_REMOTE.get());
						
						output.accept(ItemInit.GDO.get());
						
						//output.accept(ItemInit.ARCHEOLOGIST_NOTEBOOK.get());
						
						output.accept(ItemInit.NAQUADAH_SWORD.get());
						output.accept(ItemInit.NAQUADAH_PICKAXE.get());
						output.accept(ItemInit.NAQUADAH_AXE.get());
						output.accept(ItemInit.NAQUADAH_SHOVEL.get());
						output.accept(ItemInit.NAQUADAH_HOE.get());
						
						output.accept(ItemInit.NAQUADAH_HELMET.get());
						output.accept(ItemInit.NAQUADAH_CHESTPLATE.get());
						output.accept(ItemInit.NAQUADAH_LEGGINGS.get());
						output.accept(ItemInit.NAQUADAH_BOOTS.get());
						
						output.accept(ItemInit.TRINIUM_SWORD.get());
						output.accept(ItemInit.TRINIUM_PICKAXE.get());
						output.accept(ItemInit.TRINIUM_AXE.get());
						output.accept(ItemInit.TRINIUM_SHOVEL.get());
						output.accept(ItemInit.TRINIUM_HOE.get());
						
						output.accept(ItemInit.TRINIUM_HELMET.get());
						output.accept(ItemInit.TRINIUM_CHESTPLATE.get());
						output.accept(ItemInit.TRINIUM_LEGGINGS.get());
						output.accept(ItemInit.TRINIUM_BOOTS.get());
						
						output.accept(ItemInit.TRINIUM_ARROW.get());
						
						output.accept(ItemInit.KARA_KESH.get());
						output.accept(ItemInit.GOAULD_RING_REMOTE.get());
						
						output.accept(ItemInit.JAFFA_STAFF_HEAD.get());
						output.accept(ItemInit.MATOK.get());
						
						output.accept(ItemInit.JACKAL_HELMET.get());
						output.accept(ItemInit.FALCON_HELMET.get());
						output.accept(ItemInit.JAFFA_HELMET.get());
						output.accept(ItemInit.JAFFA_CHESTPLATE.get());
						output.accept(ItemInit.JAFFA_LEGGINGS.get());
						output.accept(ItemInit.JAFFA_BOOTS.get());
						
						output.accept(ItemInit.SYSTEM_LORD_HELMET.get());
						output.accept(ItemInit.SYSTEM_LORD_CHESTPLATE.get());
						output.accept(ItemInit.SYSTEM_LORD_LEGGINGS.get());
						output.accept(ItemInit.SYSTEM_LORD_BOOTS.get());
						
						output.accept(ItemInit.ZPM.get());
						output.accept(PersonalShieldItem.personalShieldSetup());
						
						output.accept(ItemInit.SYRINGE.get());
						output.accept(SyringeItem.addContents(SyringeItem.Contents.PROTOTYPE_ATA));
						output.accept(SyringeItem.addContents(SyringeItem.Contents.ATA));
						
						output.accept(ItemInit.UNITY_SHARD.get());
						
						// output.accept(ItemInit.CRYSTAL_CONFIGURATOR.get());
						
						output.accept(ItemInit.CRYSTAL_BASE.get());
						output.accept(ItemInit.ADVANCED_CRYSTAL_BASE.get());
						
						output.accept(ItemInit.CRYSTAL_ADAPTER.get());
						output.accept(ItemInit.ADVANCED_CRYSTAL_ADAPTER.get());
						output.accept(ItemInit.LARGE_CONTROL_CRYSTAL.get());
						output.accept(ItemInit.CONTROL_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_CONTROL_CRYSTAL.get());
						output.accept(ItemInit.MEMORY_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_MEMORY_CRYSTAL.get());
						output.accept(ItemInit.MATERIALIZATION_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_MATERIALIZATION_CRYSTAL.get());
						output.accept(ItemInit.ENERGY_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_ENERGY_CRYSTAL.get());
						output.accept(ItemInit.TRANSFER_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get());
						output.accept(ItemInit.COMMUNICATION_CRYSTAL.get());
						output.accept(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get());
						
						//output.accept(ItemInit.SMALL_NAQUADAH_BATTERY.get());
						
						output.accept(ItemInit.GOAULD.get());
						output.accept(ItemInit.GOAULD_CARCASS.get());
						output.accept(ItemInit.COOKED_GOAULD.get());
						output.accept(ItemInit.GOAULD_FOSSIL.get());
					})
					.build());
	
	public static final RegistryObject<CreativeModeTab> STARGATE_STUFF = CREATIVE_MODE_TABS.register("stargate_stuff", () ->
			CreativeModeTab.builder().icon(() -> new ItemStack(BlockInit.MILKY_WAY_STARGATE.get()))
					.title(Component.translatable("itemGroup.stargate_stuff"))
					.withTabsBefore(STARGATE_BLOCKS_LOCATION)
					.displayItems((parameters, output) ->
					{
						output.accept(BlockInit.UNIVERSE_STARGATE.get());
						output.accept(BlockInit.MILKY_WAY_STARGATE.get());
						output.accept(AbstractStargateBaseBlock.localPointOfOrigin(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
						output.accept(MilkyWayDHDBlock.milkyWayCrystalSetup());
						output.accept(BlockInit.PEGASUS_STARGATE.get());
						output.accept(PegasusStargateBlock.localSymbols(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
						output.accept(PegasusDHDBlock.pegasusCrystalSetup());
						output.accept(BlockInit.CLASSIC_STARGATE.get());
						output.accept(AbstractStargateBaseBlock.localPointOfOrigin(new ItemStack(BlockInit.CLASSIC_STARGATE.get())));
						output.accept(BlockInit.CLASSIC_STARGATE_BASE_BLOCK.get());
						output.accept(BlockInit.CLASSIC_STARGATE_CHEVRON_BLOCK.get());
						output.accept(BlockInit.CLASSIC_STARGATE_RING_BLOCK.get());
						output.accept(ClassicDHDBlock.classicCrystalSetup());
						output.accept(BlockInit.TOLLAN_STARGATE.get());
						
						output.accept(ItemInit.FUSION_CORE.get());
						output.accept(ItemInit.NAQUADAH_GENERATOR_CORE.get());
						output.accept(NaquadahFuelRodItem.fuelRodSetup());
						//output.accept(ItemInit.LIQUID_NAQUADAH_REACTOR_CORE.get());
						
						output.accept(BlockInit.UNIVERSE_STARGATE_CHEVRON.get());
						
						output.accept(BlockInit.BASIC_INTERFACE.get());
						output.accept(BlockInit.CRYSTAL_INTERFACE.get());
						output.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());
						
						output.accept(ItemInit.STARGATE_SHIELDING_RING.get());
						
						output.accept(ItemInit.COPPER_IRIS.get());
						output.accept(ItemInit.IRON_IRIS.get());
						output.accept(ItemInit.GOLDEN_IRIS.get());
						output.accept(ItemInit.DIAMOND_IRIS.get());
						output.accept(ItemInit.NETHERITE_IRIS.get());
						
						output.accept(ItemInit.NAQUADAH_IRIS.get());
						output.accept(ItemInit.NAQUADAH_COPPER_IRIS.get());
						output.accept(ItemInit.NAQUADAH_IRON_IRIS.get());
						output.accept(ItemInit.TRINIUM_IRIS.get());
						
						output.accept(ItemInit.BRONZE_IRIS.get());
						output.accept(ItemInit.STEEL_IRIS.get());
						
						output.accept(ItemInit.CALL_FORWARDING_DEVICE.get());
						
						if(CommonStargateConfig.enable_classic_stargate_upgrades.get())
						{
							output.accept(StargateUpgradeItem.stargateType(BlockInit.UNIVERSE_STARGATE.get()));
							output.accept(StargateUpgradeItem.stargateType(BlockInit.MILKY_WAY_STARGATE.get()));
							output.accept(StargateUpgradeItem.stargateType(BlockInit.PEGASUS_STARGATE.get()));
							output.accept(StargateUpgradeItem.stargateType(BlockInit.TOLLAN_STARGATE.get()));
						}
						if(CommonStargateConfig.enable_stargate_variants.get())
						{
							parameters.holders()
								.lookup(StargateVariant.REGISTRY_KEY)
								.ifPresent(regLookup ->
										regLookup.listElementIds()
												.forEach(variantId ->
														output.accept(
																StargateVariantItem.stargateVariant(
																		variantId.location().toString()
																)
														)
												)
								);
						}
						})
						.build());
	
	public static final RegistryObject<CreativeModeTab> STARGATE_ITEMS = CREATIVE_MODE_TABS.register("stargate_items", () ->
			CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
					.title(Component.translatable("itemGroup.stargate_items"))
					.displayItems((parameters, output) ->
					{
						output.accept(BlockInit.UNIVERSE_STARGATE_CHEVRON.get());
						
						output.accept(BlockInit.ANCIENT_TRANSPORT_RINGS.get());
						output.accept(BlockInit.GOAULD_TRANSPORT_RINGS.get());
						output.accept(BlockInit.GOAULD_RING_PANEL.get());
						
						output.accept(BlockInit.ARCHEOLOGY_TABLE.get());
						output.accept(BlockInit.GOLDEN_IDOL.get());
						
						output.accept(BlockInit.SANDSTONE_HIEROGLYPHS.get());
						output.accept(BlockInit.SANDSTONE_SWITCH.get());
						output.accept(BlockInit.SANDSTONE_WITH_LAPIS.get());
						output.accept(BlockInit.SANDSTONE_WITH_GOLD.get());
						output.accept(BlockInit.SANDSTONE_SYMBOL.get());
						output.accept(BlockInit.SANDSTONE_CARTOUCHE.get());
						
						output.accept(BlockInit.RED_SANDSTONE_GLYPHS.get());
						output.accept(BlockInit.RED_SANDSTONE_WITH_LAPIS.get());
						output.accept(BlockInit.RED_SANDSTONE_WITH_GOLD.get());
						output.accept(BlockInit.RED_SANDSTONE_SYMBOL.get());
						output.accept(BlockInit.RED_SANDSTONE_CARTOUCHE.get());
						
						output.accept(BlockInit.STONE_SYMBOL.get());
						output.accept(BlockInit.STONE_CARTOUCHE.get());
						
						output.accept(BlockInit.FIRE_PIT.get());
						
						output.accept(BlockInit.NAQUADAH_LIQUIDIZER.get());
						output.accept(BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
						
						output.accept(BlockInit.CRYSTALLIZER.get());
						output.accept(BlockInit.ADVANCED_CRYSTALLIZER.get());
						
						output.accept(BlockInit.NAQUADAH_REACTOR.get());
						output.accept(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
						output.accept(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
						
						output.accept(BlockInit.BASIC_INTERFACE.get());
						output.accept(BlockInit.CRYSTAL_INTERFACE.get());
						output.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());
						
						output.accept(BlockInit.TRANSCEIVER.get());
						
						output.accept(BlockInit.ANCIENT_GENE_DETECTOR.get());
						
						output.accept(BlockInit.ZPM_HUB.get());
						
						output.accept(BlockInit.NAQUADAH_WIRE.get());
						output.accept(BlockInit.SMALL_NAQUADAH_CABLE.get());
						output.accept(BlockInit.MEDIUM_NAQUADAH_CABLE.get());
						output.accept(BlockInit.LARGE_NAQUADAH_CABLE.get());
						
						//output.accept(BlockInit.LARGE_NAQUADAH_BATTERY.get());
						
						output.accept(BlockInit.SULFUR_SAND.get());
						output.accept(BlockInit.BUDDING_UNITY.get());
						output.accept(BlockInit.SMALL_UNITY_BUD.get());
						output.accept(BlockInit.MEDIUM_UNITY_BUD.get());
						output.accept(BlockInit.LARGE_UNITY_BUD.get());
						output.accept(BlockInit.UNITY_CLUSTER.get());
						
						output.accept(BlockInit.NAQUADAH_ORE.get());
						output.accept(BlockInit.DEEPSLATE_NAQUADAH_ORE.get());
						output.accept(BlockInit.NETHER_NAQUADAH_ORE.get());
						output.accept(BlockInit.NAQUADRIA_ORE.get());
						output.accept(BlockInit.DEEPSLATE_NAQUADRIA_ORE.get());
						output.accept(BlockInit.NETHER_NAQUADRIA_ORE.get());
						output.accept(BlockInit.TRINIUM_ORE.get());
						output.accept(BlockInit.DEEPSLATE_TRINIUM_ORE.get());
						output.accept(BlockInit.NETHER_TRINIUM_ORE.get());
						
						output.accept(BlockInit.RAW_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.PURE_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.RAW_NAQUADRIA_BLOCK.get());
						output.accept(BlockInit.RAW_TRINIUM_BLOCK.get());
						
						// Naquadah-Iron Blocks
						output.accept(BlockInit.NAQUADAH_BLOCK.get());
						output.accept(BlockInit.NAQUADAH_STAIRS.get());
						output.accept(BlockInit.NAQUADAH_SLAB.get());
						output.accept(BlockInit.CUT_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.CUT_NAQUADAH_STAIRS.get());
						output.accept(BlockInit.CUT_NAQUADAH_SLAB.get());
						output.accept(BlockInit.NAQUADAH_PILLAR.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_STAIRS.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_SLAB.get());
						output.accept(BlockInit.CHISELED_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_BLOCK.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_STAIRS.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_SLAB.get());
						
						// Naquadah-Copper Blocks
						output.accept(BlockInit.NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.EXPOSED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.EXPOSED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.EXPOSED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WEATHERED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WEATHERED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WEATHERED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.OXIDIZED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.OXIDIZED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.OXIDIZED_NAQUADAH_COPPER_SLAB.get());
						
						output.accept(BlockInit.CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_SLAB.get());
						
						output.accept(BlockInit.NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.EXPOSED_NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.WEATHERED_NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.OXIDIZED_NAQUADAH_COPPER_PILLAR.get());
						
						output.accept(BlockInit.POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.OXIDIZED_NAQUADAH_COPPER_LAMP.get());
						
						output.accept(BlockInit.CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.EXPOSED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WEATHERED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.OXIDIZED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						
						output.accept(BlockInit.SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						
						output.accept(BlockInit.WAXED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_SLAB.get());
						
						output.accept(BlockInit.WAXED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_SLAB.get());
						
						output.accept(BlockInit.WAXED_NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_PILLAR.get());
						output.accept(BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_PILLAR.get());
						
						output.accept(BlockInit.WAXED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_LAMP.get());
						output.accept(BlockInit.WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_LAMP.get());
						
						output.accept(BlockInit.WAXED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_EXPOSED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_WEATHERED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_OXIDIZED_CHISELED_NAQUADAH_COPPER_BLOCK.get());
						
						output.accept(BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						output.accept(BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get());
						output.accept(BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS.get());
						output.accept(BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB.get());
						
						// Naquadah-Iron Blocks
						output.accept(BlockInit.NAQUADAH_IRON_BLOCK.get());
						output.accept(BlockInit.NAQUADAH_IRON_STAIRS.get());
						output.accept(BlockInit.NAQUADAH_IRON_SLAB.get());
						output.accept(BlockInit.CUT_NAQUADAH_IRON_BLOCK.get());
						output.accept(BlockInit.CUT_NAQUADAH_IRON_STAIRS.get());
						output.accept(BlockInit.CUT_NAQUADAH_IRON_SLAB.get());
						output.accept(BlockInit.NAQUADAH_IRON_PILLAR.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_IRON_BLOCK.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_IRON_STAIRS.get());
						output.accept(BlockInit.POLISHED_NAQUADAH_IRON_SLAB.get());
						output.accept(BlockInit.CHISELED_NAQUADAH_IRON_BLOCK.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_IRON_BLOCK.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_IRON_STAIRS.get());
						output.accept(BlockInit.SMOOTH_NAQUADAH_IRON_SLAB.get());
						
						output.accept(BlockInit.TRINIUM_BLOCK.get());
						output.accept(BlockInit.TRINIUM_STAIRS.get());
						output.accept(BlockInit.TRINIUM_SLAB.get());
						output.accept(BlockInit.CUT_TRINIUM_BLOCK.get());
						output.accept(BlockInit.CUT_TRINIUM_STAIRS.get());
						output.accept(BlockInit.CUT_TRINIUM_SLAB.get());
						output.accept(BlockInit.TRINIUM_PILLAR.get());
						output.accept(BlockInit.POLISHED_TRINIUM_BLOCK.get());
						output.accept(BlockInit.POLISHED_TRINIUM_STAIRS.get());
						output.accept(BlockInit.POLISHED_TRINIUM_SLAB.get());
						output.accept(BlockInit.CHISELED_TRINIUM_BLOCK.get());
						output.accept(BlockInit.SMOOTH_TRINIUM_BLOCK.get());
						output.accept(BlockInit.SMOOTH_TRINIUM_STAIRS.get());
						output.accept(BlockInit.SMOOTH_TRINIUM_SLAB.get());
					})
					.withTabsBefore(STARGATE_STUFF_LOCATION).build());
	
	@SubscribeEvent
	public static void addCreative(final BuildCreativeModeTabContentsEvent event)
	{
		if(event.getTabKey() == CreativeModeTabs.OP_BLOCKS && event.hasPermissions())
		{
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.UNIVERSE_STARGATE.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(MilkyWayDHDBlock.generatedDHD(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.PEGASUS_STARGATE.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(PegasusDHDBlock.generatedDHD(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.CLASSIC_STARGATE.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(ClassicDHDBlock.generatedDHD(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.TOLLAN_STARGATE.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.GOAULD_TRANSPORT_RINGS.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
			event.accept(InventoryUtil.generationStep(new ItemStack(BlockInit.GOAULD_RING_PANEL.get()), StructureGenEntity.Step.SETUP), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
		}
	}
	
	public static void register(IEventBus eventBus)
	{
		CREATIVE_MODE_TABS.register(eventBus);
	}
}
