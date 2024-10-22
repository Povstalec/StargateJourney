package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;

public class SoundInit
{
	// CREDIT GOES TO: Carter's Addon Pack | https://github.com/RafaelDeJongh/cap_resources
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, StargateJourney.MODID);
	
	public static final DeferredHolder<SoundEvent, SoundEvent> WORMHOLE_ENTER = registerSoundEvent("wormhole_enter");
	public static final DeferredHolder<SoundEvent, SoundEvent> WORMHOLE_IDLE = registerSoundEvent("wormhole_idle");

	public static final DeferredHolder<SoundEvent, SoundEvent> IRIS_THUD = registerSoundEvent("iris_thud");

	public static final DeferredHolder<SoundEvent, SoundEvent> MILKY_WAY_DHD_ENTER = registerSoundEvent("milky_way_dhd_enter");
	public static final DeferredHolder<SoundEvent, SoundEvent> MILKY_WAY_DHD_PRESS = registerSoundEvent("milky_way_dhd_press");

	public static final DeferredHolder<SoundEvent, SoundEvent> PEGASUS_DHD_ENTER = registerSoundEvent("pegasus_dhd_enter");
	public static final DeferredHolder<SoundEvent, SoundEvent> PEGASUS_DHD_PRESS = registerSoundEvent("pegasus_dhd_press");

	public static final DeferredHolder<SoundEvent, SoundEvent> CLASSIC_DHD_ENTER = registerSoundEvent("classic_dhd_enter");
	public static final DeferredHolder<SoundEvent, SoundEvent> CLASSIC_DHD_PRESS = registerSoundEvent("classic_dhd_press");
	
	public static final DeferredHolder<SoundEvent, SoundEvent> MATOK_FIRE = registerSoundEvent("matok_fire");
	public static final DeferredHolder<SoundEvent, SoundEvent> MATOK_ATTACK = registerSoundEvent("matok_attack");
	public static final DeferredHolder<SoundEvent, SoundEvent> MATOK_OPEN = registerSoundEvent("matok_open");
	public static final DeferredHolder<SoundEvent, SoundEvent> MATOK_CLOSE = registerSoundEvent("matok_close");

	public static final DeferredHolder<SoundEvent, SoundEvent> EQUIP_NAQUADAH_ARMOR = registerSoundEvent("equip_naquadah_armor");
	
	public static final DeferredHolder<SoundEvent, SoundEvent> EMPTY = registerSoundEvent("empty");
	
	
	
	private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String sound)
	{
		return SOUNDS.register(sound, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(StargateJourney.MODID, sound)));
	}
	
	
	
	public static void register(IEventBus eventBus)
	{
		SOUNDS.register(eventBus);
	}
}
