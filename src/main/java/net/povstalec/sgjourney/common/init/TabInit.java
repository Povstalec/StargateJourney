package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.SGJourneyBaseEntityBlock;
import net.povstalec.sgjourney.common.blocks.dhd.MilkyWayDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.PegasusDHDBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;
import net.povstalec.sgjourney.common.items.SyringeItem;
import net.povstalec.sgjourney.common.items.VialItem;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;

public class TabInit
{
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =  DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StargateJourney.MODID);
	
	public static RegistryObject<CreativeModeTab> STARGATE_ITEMS = CREATIVE_MODE_TABS.register("stargate_items", () ->
		CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
		.title(Component.translatable("creativemodetab.stargate_items")).build());

	public static RegistryObject<CreativeModeTab> STARGATE_BLOCKS = CREATIVE_MODE_TABS.register("stargate_blocks", () ->
		CreativeModeTab.builder().icon(() -> new ItemStack(BlockInit.MILKY_WAY_STARGATE.get()))
		.title(Component.translatable("creativemodetab.stargate_blocks")).build());
	
	@SubscribeEvent
	public static void addCreative(final BuildCreativeModeTabContentsEvent event)
	{
		if(event.getTab() == STARGATE_ITEMS.get())
		{
			event.accept(ItemInit.RAW_NAQUADAH.get());
			event.accept(ItemInit.NAQUADAH_ALLOY.get());
			event.accept(ItemInit.PURE_NAQUADAH.get());
			event.accept(ItemInit.NAQUADAH.get());
			event.accept(ItemInit.LIQUID_NAQUADAH_BUCKET.get());
			event.accept(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET.get());
			event.accept(ItemInit.VIAL.get());
			event.accept(VialItem.liquidNaquadahSetup());
			event.accept(VialItem.heavyLiquidNaquadahSetup());

			event.accept(ItemInit.NAQUADAH_ROD.get());
			event.accept(ItemInit.REACTION_CHAMBER.get());
			event.accept(ItemInit.PLASMA_CONVERTER.get());
			
			event.accept(ItemInit.PDA.get());
			
			event.accept(ItemInit.NAQUADAH_SWORD.get());
			event.accept(ItemInit.NAQUADAH_PICKAXE.get());
			event.accept(ItemInit.NAQUADAH_AXE.get());
			event.accept(ItemInit.NAQUADAH_SHOVEL.get());
			event.accept(ItemInit.NAQUADAH_HOE.get());
			
			event.accept(ItemInit.NAQUADAH_HELMET.get());
			event.accept(ItemInit.NAQUADAH_CHESTPLATE.get());
			event.accept(ItemInit.NAQUADAH_LEGGINGS.get());
			event.accept(ItemInit.NAQUADAH_BOOTS.get());
			
			event.accept(ItemInit.KARA_KESH.get());
			event.accept(ItemInit.RING_REMOTE.get());
			
			event.accept(ItemInit.MATOK.get());
			
			event.accept(ItemInit.JACKAL_HELMET.get());
			//event.accept(ItemInit.HORUS_HELMET.get());
			event.accept(ItemInit.JAFFA_HELMET.get());
			event.accept(ItemInit.JAFFA_CHESTPLATE.get());
			event.accept(ItemInit.JAFFA_LEGGINGS.get());
			event.accept(ItemInit.JAFFA_BOOTS.get());

			event.accept(ItemInit.ZPM.get());
			//event.accept(ItemInit.PERSONAL_SHIELD_EMITTER.get());
			event.accept(PersonalShieldItem.personalShieldSetup());

			event.accept(ItemInit.SYRINGE.get());
			event.accept(SyringeItem.addContents(SyringeItem.Contents.PROTOTYPE_ATA));
			event.accept(SyringeItem.addContents(SyringeItem.Contents.ATA));

			event.accept(ItemInit.CRYSTAL_BASE.get());
			event.accept(ItemInit.ADVANCED_CRYSTAL_BASE.get());
			
			event.accept(ItemInit.LARGE_CONTROL_CRYSTAL.get());
			event.accept(ItemInit.CONTROL_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_CONTROL_CRYSTAL.get());
			event.accept(ItemInit.MEMORY_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_MEMORY_CRYSTAL.get());
			//event.accept(MemoryCrystalItem.atlantisAddress());
			//event.accept(MemoryCrystalItem.abydosAddress());
			event.accept(ItemInit.MATERIALIZATION_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_MATERIALIZATION_CRYSTAL.get());
			event.accept(ItemInit.ENERGY_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_ENERGY_CRYSTAL.get());
			event.accept(ItemInit.TRANSFER_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get());
			event.accept(ItemInit.COMMUNICATION_CRYSTAL.get());
			event.accept(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get());
			
			if(CommonStargateConfig.enable_classic_stargate_upgrades.get())
			{
				event.accept(StargateUpgradeItem.stargateType(BlockInit.UNIVERSE_STARGATE.get()));
				event.accept(StargateUpgradeItem.stargateType(BlockInit.MILKY_WAY_STARGATE.get()));
				event.accept(StargateUpgradeItem.stargateType(BlockInit.PEGASUS_STARGATE.get()));
				event.accept(StargateUpgradeItem.stargateType(BlockInit.TOLLAN_STARGATE.get()));
			}
			if(CommonStargateConfig.enable_stargate_variants.get())
			{
				event.accept(ItemInit.STARGATE_VARIANT_CRYSTAL.get());
				event.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_movie"));
				event.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_promo"));
				event.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_sg-1"));
				event.accept(StargateVariantItem.stargateVariant("sgjourney:classic_milky_way"));
			}
		}
		else if(event.getTab() == STARGATE_BLOCKS.get())
		{
			event.accept(BlockInit.UNIVERSE_STARGATE.get());
			event.accept(BlockInit.MILKY_WAY_STARGATE.get());
			event.accept(MilkyWayDHDBlock.milkyWayCrystalSetup());
			event.accept(BlockInit.PEGASUS_STARGATE.get());
			event.accept(PegasusDHDBlock.pegasusCrystalSetup());
			event.accept(BlockInit.CLASSIC_STARGATE.get());
			event.accept(BlockInit.CLASSIC_STARGATE_BASE_BLOCK.get());
			event.accept(BlockInit.CLASSIC_STARGATE_CHEVRON_BLOCK.get());
			event.accept(BlockInit.CLASSIC_STARGATE_RING_BLOCK.get());
			event.accept(BlockInit.CLASSIC_DHD.get());
			event.accept(BlockInit.TOLLAN_STARGATE.get());

			event.accept(BlockInit.TRANSPORT_RINGS.get());
			event.accept(BlockInit.RING_PANEL.get());
			
			event.accept(BlockInit.ARCHEOLOGY_TABLE.get());
			event.accept(BlockInit.GOLDEN_IDOL.get());

			event.accept(BlockInit.NAQUADAH_ORE.get());
			event.accept(BlockInit.DEEPSLATE_NAQUADAH_ORE.get());
			event.accept(BlockInit.NETHER_NAQUADAH_ORE.get());
			event.accept(BlockInit.RAW_NAQUADAH_BLOCK.get());
			
			event.accept(BlockInit.NAQUADAH_BLOCK.get());
			event.accept(BlockInit.NAQUADAH_STAIRS.get());
			event.accept(BlockInit.NAQUADAH_SLAB.get());
			event.accept(BlockInit.CUT_NAQUADAH_BLOCK.get());
			event.accept(BlockInit.CUT_NAQUADAH_STAIRS.get());
			event.accept(BlockInit.CUT_NAQUADAH_SLAB.get());
			
			event.accept(BlockInit.SANDSTONE_HIEROGLYPHS.get());
			event.accept(BlockInit.SANDSTONE_SWITCH.get());
			event.accept(BlockInit.SANDSTONE_WITH_LAPIS.get());
			event.accept(BlockInit.SANDSTONE_SYMBOL.get());
			event.accept(BlockInit.STONE_SYMBOL.get());
			
			event.accept(BlockInit.SANDSTONE_CARTOUCHE.get());
			event.accept(BlockInit.STONE_CARTOUCHE.get());
			
			event.accept(BlockInit.FIRE_PIT.get());

			event.accept(BlockInit.NAQUADAH_LIQUIDIZER.get());
			event.accept(BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
			
			event.accept(BlockInit.CRYSTALLIZER.get());
			event.accept(BlockInit.ADVANCED_CRYSTALLIZER.get());

			event.accept(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
			event.accept(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
			
			event.accept(BlockInit.BASIC_INTERFACE.get());
			event.accept(BlockInit.CRYSTAL_INTERFACE.get());
			event.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());

			event.accept(BlockInit.ANCIENT_GENE_DETECTOR.get());
			
			event.accept(BlockInit.ZPM_HUB.get());
		}
		else if(event.getTabKey() == CreativeModeTabs.OP_BLOCKS && event.hasPermissions())
		{
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.UNIVERSE_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.CLASSIC_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.TOLLAN_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.TRANSPORT_RINGS.get())));
		}
	}
	
	public static void register(IEventBus eventBus)
	{
		CREATIVE_MODE_TABS.register(eventBus);
	}
}
