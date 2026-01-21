package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.*;

public class EntityInit 
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, StargateJourney.MODID);
	
	// Projectiles
	public static final RegistryObject<EntityType<PlasmaProjectile>> JAFFA_PLASMA = ENTITIES.register("jaffa_plasma",
					() -> EntityType.Builder.<PlasmaProjectile>of(PlasmaProjectile::new, MobCategory.MISC)
						.sized(0.25F, 0.25F)
						.setUpdateInterval(20)
						.build(new ResourceLocation(StargateJourney.MODID, "jaffa_plasma").toString()));
	
	public static final RegistryObject<EntityType<TriniumArrow>> TRINIUM_ARROW = ENTITIES.register("trinium_arrow",
			() -> EntityType.Builder.<TriniumArrow>of(TriniumArrow::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.setUpdateInterval(20)
					.build(new ResourceLocation(StargateJourney.MODID, "trinium_arrow").toString()));

	
	// Creatures
	public static final RegistryObject<EntityType<Goauld>> GOAULD = ENTITIES.register("goauld",
					() -> EntityType.Builder.<Goauld>of(Goauld::new, MobCategory.CREATURE)
						.sized(0.4F, 0.4F)
						.build(new ResourceLocation(StargateJourney.MODID, "goauld").toString()));
	
	public static final RegistryObject<EntityType<Human>> HUMAN = ENTITIES.register("human",
			() -> EntityType.Builder.<Human>of(Human::new, MobCategory.CREATURE)
					.sized(0.625F, 1.95F)
					.clientTrackingRange(8)
					.build(new ResourceLocation(StargateJourney.MODID, "human").toString()));
	
	public static final RegistryObject<EntityType<Jaffa>> JAFFA = ENTITIES.register("jaffa",
			() -> EntityType.Builder.<Jaffa>of(Jaffa::new, MobCategory.CREATURE)
					.sized(0.625F, 1.95F)
					.clientTrackingRange(8)
					.build(new ResourceLocation(StargateJourney.MODID, "jaffa").toString()));
	
	public static void register(IEventBus eventBus)
	{
		ENTITIES.register(eventBus);
	}
}
