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

	public static final RegistryObject<SoundEvent> UNIVERSE_CHEVRON_ENGAGE = registerSoundEvent("universe_chevron_engage");
	public static final RegistryObject<SoundEvent> UNIVERSE_DIAL_FAIL = registerSoundEvent("universe_dial_fail");
	
	public static final RegistryObject<SoundEvent> MILKY_WAY_CHEVRON_ENCODE = registerSoundEvent("milky_way_chevron_encode");
	public static final RegistryObject<SoundEvent> MILKY_WAY_CHEVRON_ENGAGE = registerSoundEvent("milky_way_chevron_engage");
	public static final RegistryObject<SoundEvent> MILKY_WAY_DIAL_FAIL = registerSoundEvent("milky_way_dial_fail");

	public static final RegistryObject<SoundEvent> PEGASUS_RING_SPIN = registerSoundEvent("pegasus_ring_spin");
	public static final RegistryObject<SoundEvent> PEGASUS_CHEVRON_ENGAGE = registerSoundEvent("pegasus_chevron_engage");
	public static final RegistryObject<SoundEvent> PEGASUS_DIAL_FAIL = registerSoundEvent("pegasus_dial_fail");

	public static final RegistryObject<SoundEvent> WORMHOLE_CLOSE = registerSoundEvent("wormhole_close");
	public static final RegistryObject<SoundEvent> WORMHOLE_ENTER = registerSoundEvent("wormhole_enter");

	public static final RegistryObject<SoundEvent> MILKY_WAY_DHD_ENTER = registerSoundEvent("milky_way_dhd_enter");
	
	public static final RegistryObject<SoundEvent> MATOK_FIRE = registerSoundEvent("matok_fire");

	public static final RegistryObject<SoundEvent> EQUIP_NAQUADAH_ARMOR = registerSoundEvent("equip_naquadah_armor");
	
	private static RegistryObject<SoundEvent> registerSoundEvent(String sound)
	{
		return SOUNDS.register(sound, () -> new SoundEvent(new ResourceLocation(StargateJourney.MODID, sound)));
	}
	
	public static void register(IEventBus eventBus)
	{
		SOUNDS.register(eventBus);
	}
}
