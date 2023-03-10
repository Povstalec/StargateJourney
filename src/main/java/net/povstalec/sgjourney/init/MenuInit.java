package net.povstalec.sgjourney.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.menu.ClassicDHDMenu;
import net.povstalec.sgjourney.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.menu.NaquadahGeneratorMenu;
import net.povstalec.sgjourney.menu.PegasusDHDMenu;
import net.povstalec.sgjourney.menu.RingPanelMenu;
import net.povstalec.sgjourney.menu.ZPMHubMenu;

public class MenuInit 
{
	public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StargateJourney.MODID);
	
	public static final RegistryObject<MenuType<RingPanelMenu>> RING_PANEL =
            registerMenuType(RingPanelMenu::new, "ring_panel");
	
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



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

	
	public static void register(IEventBus eventBus) 
	{
        CONTAINERS.register(eventBus);
    }

}
