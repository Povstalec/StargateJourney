package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.*;

import java.util.function.Supplier;

public class EntityInit 
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, StargateJourney.MODID);
	
	// Projectiles
	public static final DeferredHolder<EntityType<?>, EntityType<PlasmaProjectile>> JAFFA_PLASMA = ENTITIES.register("jaffa_plasma",
			() -> EntityType.Builder.<PlasmaProjectile>of(PlasmaProjectile::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4)
					.setUpdateInterval(20)
					.build(StargateJourney.sgjourneyLocation("jaffa_plasma").toString()));
	
	public static final DeferredHolder<EntityType<?>, EntityType<TriniumArrow>> TRINIUM_ARROW = ENTITIES.register("trinium_arrow",
			() -> EntityType.Builder.<TriniumArrow>of(TriniumArrow::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.clientTrackingRange(4)
					.setUpdateInterval(20)
					.build(StargateJourney.sgjourneyLocation("trinium_arrow").toString()));
	
	
	// Creatures
	public static final DeferredHolder<EntityType<?>, EntityType<Mastadge>> MASTADGE = ENTITIES.register("mastadge",
			() -> EntityType.Builder.<Mastadge>of(Mastadge::new, MobCategory.CREATURE)
					.sized(1.3964844F, 1.6F)
					.clientTrackingRange(10)
					.build(StargateJourney.sgjourneyLocation("mastadge").toString()));
	
	public static final DeferredHolder<EntityType<?>, EntityType<AbydosLizard>> ABYDOS_LIZARD = ENTITIES.register("abydos_lizard",
			() -> EntityType.Builder.<AbydosLizard>of(AbydosLizard::new, MobCategory.CREATURE)
					.sized(0.6F, 0.7F)
					.clientTrackingRange(8)
					.build(StargateJourney.sgjourneyLocation("abydos_lizard").toString()));
	
	public static final DeferredHolder<EntityType<?>, EntityType<Goauld>> GOAULD = ENTITIES.register("goauld",
			() -> EntityType.Builder.<Goauld>of(Goauld::new, MobCategory.CREATURE)
					.sized(0.4F, 0.4F)
					.build(StargateJourney.sgjourneyLocation("goauld").toString()));
	
	public static final DeferredHolder<EntityType<?>, EntityType<Human>> HUMAN = ENTITIES.register("human",
			() -> EntityType.Builder.<Human>of(Human::new, MobCategory.CREATURE)
					.sized(0.625F, 1.95F)
					.clientTrackingRange(10)
					.build(StargateJourney.sgjourneyLocation("human").toString()));
	
	public static final DeferredHolder<EntityType<?>, EntityType<Jaffa>> JAFFA = ENTITIES.register("jaffa",
			() -> EntityType.Builder.<Jaffa>of(Jaffa::new, MobCategory.CREATURE)
					.sized(0.625F, 1.95F)
					.clientTrackingRange(10)
					.build(StargateJourney.sgjourneyLocation("jaffa").toString()));
	
	public static void register(IEventBus eventBus)
	{
		ENTITIES.register(eventBus);
	}
}
