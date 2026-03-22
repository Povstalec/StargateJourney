package net.povstalec.sgjourney.common.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.menu.*;

public class MenuInit 
{
	public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StargateJourney.MODID);

	public static final RegistryObject<MenuType<InterfaceMenu<BasicInterfaceEntity>>> BASIC_INTERFACE =
            registerMenuType(InterfaceMenu.Basic::new, "basic_interface");
	public static final RegistryObject<MenuType<InterfaceMenu<CrystalInterfaceEntity>>> CRYSTAL_INTERFACE =
		registerMenuType(InterfaceMenu.Crystal::new, "crystal_interface");
	public static final RegistryObject<MenuType<InterfaceMenu<AdvancedCrystalInterfaceEntity>>> ADVANCED_CRYSTAL_INTERFACE =
			registerMenuType(InterfaceMenu.AdvancedCrystal::new, "advnaced_crystal_interface");
	
	public static final RegistryObject<MenuType<RingPanelMenu>> RING_PANEL =
            registerMenuType(RingPanelMenu::new, "ring_panel");
	
	public static final RegistryObject<MenuType<DHDCrystalMenu>> DHD_CRYSTAL =
            registerMenuType(DHDCrystalMenu::new, "dhd_crystal");
	
	public static final RegistryObject<MenuType<MilkyWayDHDMenu>> MILKY_WAY_DHD =
            registerMenuType(MilkyWayDHDMenu::new, "milky_way_dhd");
	
	public static final RegistryObject<MenuType<PegasusDHDMenu>> PEGASUS_DHD =
            registerMenuType(PegasusDHDMenu::new, "pegasus_dhd");
	
	public static final RegistryObject<MenuType<ClassicDHDMenu>> CLASSIC_DHD =
            registerMenuType(ClassicDHDMenu::new, "classic_dhd");
	
	public static final RegistryObject<MenuType<NaquadahGeneratorMenu>> NAQUADAH_GENERATOR =
            registerMenuType(NaquadahGeneratorMenu::new, "naquadah_generator");
	
	public static final RegistryObject<MenuType<ZPMHubMenu>> ZPM_HUB =
            registerMenuType(ZPMHubMenu::new, "zpm_hub");
	
	public static final RegistryObject<MenuType<CrystallizerMenu.Crystallizer>> CRYSTALLIZER =
            registerMenuType(CrystallizerMenu.Crystallizer::new, "crystallizer");
	
	public static final RegistryObject<MenuType<CrystallizerMenu.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZER =
			registerMenuType(CrystallizerMenu.AdvancedCrystallizer::new, "advanced_crystallizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu.LiquidNaquadah>> NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.LiquidNaquadah::new, "naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu.HeavyLiquidNaquadah>> HEAVY_NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.HeavyLiquidNaquadah::new, "heavy_naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<TransceiverMenu>> TRANSCEIVER =
            registerMenuType(TransceiverMenu::new, "transceiver");
	
	public static final RegistryObject<MenuType<BatteryMenu>> NAQUADAH_BATTERY =
			registerMenuType(BatteryMenu::new, "naquadah_battery");



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

	
	public static void register(IEventBus eventBus) 
	{
        CONTAINERS.register(eventBus);
    }

}
