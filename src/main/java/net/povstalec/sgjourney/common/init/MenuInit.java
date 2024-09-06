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
import net.povstalec.sgjourney.common.menu.ClassicDHDMenu;
import net.povstalec.sgjourney.common.menu.CrystallizerMenu;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;
import net.povstalec.sgjourney.common.menu.LiquidizerMenu;
import net.povstalec.sgjourney.common.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.menu.NaquadahGeneratorMenu;
import net.povstalec.sgjourney.common.menu.PegasusDHDMenu;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;
import net.povstalec.sgjourney.common.menu.TransceiverMenu;
import net.povstalec.sgjourney.common.menu.ZPMHubMenu;

public class MenuInit 
{
	public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StargateJourney.MODID);

	public static final RegistryObject<MenuType<InterfaceMenu>> INTERFACE =
            registerMenuType(InterfaceMenu::new, "interface");
	
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
	
	public static final RegistryObject<MenuType<CrystallizerMenu>> CRYSTALLIZER =
            registerMenuType(CrystallizerMenu::new, "crystallizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu>> NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.LiquidNaquadah::new, "naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu>> HEAVY_NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.HeavyLiquidNaquadah::new, "heavy_naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<TransceiverMenu>> TRANSCEIVER =
            registerMenuType(TransceiverMenu::new, "transceiver");



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

	
	public static void register(IEventBus eventBus) 
	{
        CONTAINERS.register(eventBus);
    }

}
