package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;

public class SoundInit
{
	// CREDIT GOES TO: Carter's Addon Pack | https://github.com/RafaelDeJongh/cap_resources
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, StargateJourney.MODID);
	
	public static final RegistryObject<SoundEvent> WORMHOLE_ENTER = registerSoundEvent("wormhole_enter");
	public static final RegistryObject<SoundEvent> WORMHOLE_IDLE = registerSoundEvent("wormhole_idle");

	public static final RegistryObject<SoundEvent> IRIS_THUD = registerSoundEvent("iris_thud");

	public static final RegistryObject<SoundEvent> MILKY_WAY_DHD_ENTER = registerSoundEvent("milky_way_dhd_enter");
	public static final RegistryObject<SoundEvent> MILKY_WAY_DHD_PRESS = registerSoundEvent("milky_way_dhd_press");

	public static final RegistryObject<SoundEvent> PEGASUS_DHD_ENTER = registerSoundEvent("pegasus_dhd_enter");
	public static final RegistryObject<SoundEvent> PEGASUS_DHD_PRESS = registerSoundEvent("pegasus_dhd_press");

	public static final RegistryObject<SoundEvent> CLASSIC_DHD_ENTER = registerSoundEvent("classic_dhd_enter");
	public static final RegistryObject<SoundEvent> CLASSIC_DHD_PRESS = registerSoundEvent("classic_dhd_press");
	
	public static final RegistryObject<SoundEvent> RING_PANEL_PRESS = registerSoundEvent("ring_panel_press");
	
	public static final RegistryObject<SoundEvent> MATOK_FIRE = registerSoundEvent("matok_fire");
	public static final RegistryObject<SoundEvent> MATOK_ATTACK = registerSoundEvent("matok_attack");
	public static final RegistryObject<SoundEvent> MATOK_OPEN = registerSoundEvent("matok_open");
	public static final RegistryObject<SoundEvent> MATOK_CLOSE = registerSoundEvent("matok_close");

	public static final RegistryObject<SoundEvent> EQUIP_NAQUADAH_ARMOR = registerSoundEvent("equip_naquadah_armor");
	
	public static final RegistryObject<SoundEvent> EMPTY = registerSoundEvent("empty");
	
	
	
	private static RegistryObject<SoundEvent> registerSoundEvent(String sound)
	{
		return SOUNDS.register(sound, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(StargateJourney.MODID, sound)));
	}
	
	
	
	public static void register(IEventBus eventBus)
	{
		SOUNDS.register(eventBus);
	}
}
