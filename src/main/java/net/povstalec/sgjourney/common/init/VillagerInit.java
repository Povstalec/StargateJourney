package net.povstalec.sgjourney.common.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;

import java.lang.reflect.InvocationTargetException;

//A class that registers Villager professions and their points of interest
public class VillagerInit
{
	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, StargateJourney.MODID);
	
	public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, StargateJourney.MODID);
	
	
	
	public static final RegistryObject<PoiType> ARCHEOLOGIST_POI = POI_TYPES.register("archeologist",
		() -> new PoiType("archeologist", PoiType.getBlockStates(BlockInit.ARCHEOLOGY_TABLE.get()), 1, 1));
	
	public static final RegistryObject<VillagerProfession> ARCHEOLOGIST = VILLAGER_PROFESSIONS.register("archeologist",
		() -> new VillagerProfession("archeologist", ARCHEOLOGIST_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER));
	
	public static void registerPOIs()
	{
		try
		{
			ObfuscationReflectionHelper.findMethod(PoiType.class, "registerBlockStates", PoiType.class).invoke(null, ARCHEOLOGIST.get());
		}
		catch (InvocationTargetException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void register(IEventBus eventBus)
	{
		POI_TYPES.register(eventBus);
		VILLAGER_PROFESSIONS.register(eventBus);
	}
}
