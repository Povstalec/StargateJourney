package net.povstalec.sgjourney.common.init;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

@Mod.EventBusSubscriber(modid = StargateJourney.MODID)
public class TabInit
{
	@SubscribeEvent
	public static void onRegisterModTabs(final CreativeModeTabEvent.Register event)
	{
		event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_items"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_items"))
			.icon(() -> new ItemStack(ItemInit.NAQUADAH.get()))
			.displayItems((parameters, output) ->
			{
				output.accept(ItemInit.RAW_NAQUADAH.get());
				output.accept(ItemInit.NAQUADAH_ALLOY.get());
				output.accept(ItemInit.PURE_NAQUADAH.get());
				output.accept(ItemInit.NAQUADAH.get());
				output.accept(ItemInit.LIQUID_NAQUADAH_BUCKET.get());
				output.accept(ItemInit.HEAVY_LIQUID_NAQUADAH_BUCKET.get());
				output.accept(ItemInit.VIAL.get());
				output.accept(VialItem.liquidNaquadahSetup());
				output.accept(VialItem.heavyLiquidNaquadahSetup());

				output.accept(ItemInit.NAQUADAH_ROD.get());
				output.accept(ItemInit.REACTION_CHAMBER.get());
				output.accept(ItemInit.PLASMA_CONVERTER.get());
				
				output.accept(ItemInit.PDA.get());
				
				output.accept(ItemInit.NAQUADAH_SWORD.get());
				output.accept(ItemInit.NAQUADAH_PICKAXE.get());
				output.accept(ItemInit.NAQUADAH_AXE.get());
				output.accept(ItemInit.NAQUADAH_SHOVEL.get());
				output.accept(ItemInit.NAQUADAH_HOE.get());
				
				output.accept(ItemInit.NAQUADAH_HELMET.get());
				output.accept(ItemInit.NAQUADAH_CHESTPLATE.get());
				output.accept(ItemInit.NAQUADAH_LEGGINGS.get());
				output.accept(ItemInit.NAQUADAH_BOOTS.get());
				
				output.accept(ItemInit.KARA_KESH.get());
				output.accept(ItemInit.RING_REMOTE.get());
				
				output.accept(ItemInit.MATOK.get());
				
				output.accept(ItemInit.JACKAL_HELMET.get());
				//items.accept(ItemInit.HORUS_HELMET.get());
				output.accept(ItemInit.JAFFA_HELMET.get());
				output.accept(ItemInit.JAFFA_CHESTPLATE.get());
				output.accept(ItemInit.JAFFA_LEGGINGS.get());
				output.accept(ItemInit.JAFFA_BOOTS.get());

				output.accept(ItemInit.ZPM.get());
				//items.accept(ItemInit.PERSONAL_SHIELD_EMITTER.get());
				output.accept(PersonalShieldItem.personalShieldSetup());

				output.accept(ItemInit.SYRINGE.get());
				output.accept(SyringeItem.addContents(SyringeItem.Contents.PROTOTYPE_ATA));
				output.accept(SyringeItem.addContents(SyringeItem.Contents.ATA));
				
				output.accept(ItemInit.CRYSTAL_BASE.get());
				output.accept(ItemInit.ADVANCED_CRYSTAL_BASE.get());
				
				output.accept(ItemInit.LARGE_CONTROL_CRYSTAL.get());
				output.accept(ItemInit.CONTROL_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_CONTROL_CRYSTAL.get());
				output.accept(ItemInit.MEMORY_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_MEMORY_CRYSTAL.get());
				//output.accept(MemoryCrystalItem.atlantisAddress());
				//output.accept(MemoryCrystalItem.abydosAddress());
				output.accept(ItemInit.MATERIALIZATION_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_MATERIALIZATION_CRYSTAL.get());
				output.accept(ItemInit.ENERGY_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_ENERGY_CRYSTAL.get());
				output.accept(ItemInit.TRANSFER_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get());
				output.accept(ItemInit.COMMUNICATION_CRYSTAL.get());
				output.accept(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get());
				
				if(CommonStargateConfig.enable_classic_stargate_upgrades.get())
				{
					output.accept(StargateUpgradeItem.stargateType(BlockInit.UNIVERSE_STARGATE.get()));
					output.accept(StargateUpgradeItem.stargateType(BlockInit.MILKY_WAY_STARGATE.get()));
					output.accept(StargateUpgradeItem.stargateType(BlockInit.PEGASUS_STARGATE.get()));
					output.accept(StargateUpgradeItem.stargateType(BlockInit.TOLLAN_STARGATE.get()));
				}
				if(CommonStargateConfig.enable_stargate_variants.get())
				{
					output.accept(ItemInit.STARGATE_VARIANT_CRYSTAL.get());
					output.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_movie"));
					output.accept(StargateVariantItem.stargateVariant("sgjourney:milky_way_sg-1"));
					output.accept(StargateVariantItem.stargateVariant("sgjourney:classic_milky_way"));
				}
			});
		});
		
		event.registerCreativeModeTab(new ResourceLocation(StargateJourney.MODID, "stargate_blocks"), (builder) ->
		{
			builder.title(Component.translatable("itemGroup.stargate_blocks"))
			.icon(() -> new ItemStack(BlockInit.NAQUADAH_BLOCK.get()))
			.displayItems((parameters, output) ->
			{
				output.accept(BlockInit.UNIVERSE_STARGATE.get());
				output.accept(BlockInit.MILKY_WAY_STARGATE.get());
				output.accept(MilkyWayDHDBlock.milkyWayCrystalSetup());
				output.accept(BlockInit.PEGASUS_STARGATE.get());
				output.accept(PegasusDHDBlock.pegasusCrystalSetup());
				output.accept(BlockInit.CLASSIC_STARGATE.get());
				output.accept(BlockInit.CLASSIC_STARGATE_BASE_BLOCK.get());
				output.accept(BlockInit.CLASSIC_STARGATE_CHEVRON_BLOCK.get());
				output.accept(BlockInit.CLASSIC_STARGATE_RING_BLOCK.get());
				output.accept(BlockInit.CLASSIC_DHD.get());
				output.accept(BlockInit.TOLLAN_STARGATE.get());

				output.accept(BlockInit.TRANSPORT_RINGS.get());
				output.accept(BlockInit.RING_PANEL.get());
				
				output.accept(BlockInit.ARCHEOLOGY_TABLE.get());
				output.accept(BlockInit.GOLDEN_IDOL.get());

				output.accept(BlockInit.NAQUADAH_ORE.get());
				output.accept(BlockInit.DEEPSLATE_NAQUADAH_ORE.get());
				output.accept(BlockInit.NETHER_NAQUADAH_ORE.get());
				output.accept(BlockInit.RAW_NAQUADAH_BLOCK.get());
				
				output.accept(BlockInit.NAQUADAH_BLOCK.get());
				output.accept(BlockInit.NAQUADAH_STAIRS.get());
				output.accept(BlockInit.NAQUADAH_SLAB.get());
				output.accept(BlockInit.CUT_NAQUADAH_BLOCK.get());
				output.accept(BlockInit.CUT_NAQUADAH_STAIRS.get());
				output.accept(BlockInit.CUT_NAQUADAH_SLAB.get());
				
				output.accept(BlockInit.SANDSTONE_HIEROGLYPHS.get());
				output.accept(BlockInit.SANDSTONE_SWITCH.get());
				output.accept(BlockInit.SANDSTONE_WITH_LAPIS.get());
				output.accept(BlockInit.SANDSTONE_SYMBOL.get());
				output.accept(BlockInit.STONE_SYMBOL.get());
				
				output.accept(BlockInit.SANDSTONE_CARTOUCHE.get());
				output.accept(BlockInit.STONE_CARTOUCHE.get());
				
				output.accept(BlockInit.FIRE_PIT.get());
				
				output.accept(BlockInit.NAQUADAH_LIQUIDIZER.get());
				output.accept(BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
				
				output.accept(BlockInit.CRYSTALLIZER.get());
				output.accept(BlockInit.ADVANCED_CRYSTALLIZER.get());

				output.accept(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
				output.accept(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				output.accept(BlockInit.BASIC_INTERFACE.get());
				output.accept(BlockInit.CRYSTAL_INTERFACE.get());
				output.accept(BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());

				output.accept(BlockInit.ANCIENT_GENE_DETECTOR.get());
				
				output.accept(BlockInit.ZPM_HUB.get());
			});
		});
	}
	
	@SubscribeEvent
	public static void addCreative(final CreativeModeTabEvent.BuildContents event)
	{
		if(event.getTab() == CreativeModeTabs.OP_BLOCKS && event.hasPermissions())
		{
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.UNIVERSE_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.MILKY_WAY_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.PEGASUS_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.CLASSIC_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.TOLLAN_STARGATE.get())));
			event.accept(SGJourneyBaseEntityBlock.excludeFromNetwork(new ItemStack(BlockInit.TRANSPORT_RINGS.get())));
		}
	}
}
