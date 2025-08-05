package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.*;

public class MenuInit 
{
	public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, StargateJourney.MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu>> INTERFACE =
            registerMenuType(InterfaceMenu::new, "interface");
	
	public static final DeferredHolder<MenuType<?>, MenuType<RingPanelMenu>> RING_PANEL =
            registerMenuType(RingPanelMenu::new, "ring_panel");
	
	public static final DeferredHolder<MenuType<?>, MenuType<DHDCrystalMenu>> DHD_CRYSTAL =
            registerMenuType(DHDCrystalMenu::new, "dhd_crystal");
	
	public static final DeferredHolder<MenuType<?>, MenuType<MilkyWayDHDMenu>> MILKY_WAY_DHD =
            registerMenuType(MilkyWayDHDMenu::new, "milky_way_dhd");
	
	public static final DeferredHolder<MenuType<?>, MenuType<PegasusDHDMenu>> PEGASUS_DHD =
            registerMenuType(PegasusDHDMenu::new, "pegasus_dhd");
	
	public static final DeferredHolder<MenuType<?>, MenuType<ClassicDHDMenu>> CLASSIC_DHD =
            registerMenuType(ClassicDHDMenu::new, "classic_dhd");
	
	public static final DeferredHolder<MenuType<?>, MenuType<NaquadahGeneratorMenu>> NAQUADAH_GENERATOR =
            registerMenuType(NaquadahGeneratorMenu::new, "naquadah_generator");
	
	public static final DeferredHolder<MenuType<?>, MenuType<ZPMHubMenu>> ZPM_HUB =
            registerMenuType(ZPMHubMenu::new, "zpm_hub");
	
	public static final DeferredHolder<MenuType<?>, MenuType<CrystallizerMenu>> CRYSTALLIZER =
            registerMenuType(CrystallizerMenu::new, "crystallizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<LiquidizerMenu>> NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.LiquidNaquadah::new, "naquadah_liquidizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<LiquidizerMenu>> HEAVY_NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.HeavyLiquidNaquadah::new, "heavy_naquadah_liquidizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<TransceiverMenu>> TRANSCEIVER =
            registerMenuType(TransceiverMenu::new, "transceiver");
	
	public static final DeferredHolder<MenuType<?>, MenuType<BatteryMenu>> NAQUADAH_BATTERY =
			registerMenuType(BatteryMenu::new, "naquadah_battery");



    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINERS.register(name, () -> IMenuTypeExtension.create(factory));
    }

	
	public static void register(IEventBus eventBus)
	{
        CONTAINERS.register(eventBus);
    }

}
