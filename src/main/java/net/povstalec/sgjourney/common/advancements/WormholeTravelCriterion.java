package net.povstalec.sgjourney.common.advancements;

import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class WormholeTravelCriterion extends SimpleCriterionTrigger<WormholeTravelCriterion.WormholeTravelTriggerInstance>
{
	public static final WormholeTravelCriterion INSTANCE = new WormholeTravelCriterion();
	
	public Codec<WormholeTravelTriggerInstance> codec() {
		return WormholeTravelTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ResourceLocation initialDimension, ResourceLocation destinationDimension, long distanceTraveled)
	{
		this.trigger(player, (trigger -> trigger.matches(initialDimension, destinationDimension, distanceTraveled)));
	}

	public static record WormholeTravelTriggerInstance(Optional<ContextAwarePredicate> player,
											   Optional<ResourceLocation> initialDimension,
											   Optional<ResourceLocation> destinationDimension,
											   Optional<Long> distanceTraveled) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<WormholeTravelTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(WormholeTravelTriggerInstance::player),
					ResourceLocation.CODEC.optionalFieldOf("from").forGetter(WormholeTravelTriggerInstance::initialDimension),
					ResourceLocation.CODEC.optionalFieldOf("to").forGetter(WormholeTravelTriggerInstance::destinationDimension),
					Codec.LONG.optionalFieldOf("distance").forGetter(WormholeTravelTriggerInstance::distanceTraveled)
			).apply(instance, WormholeTravelTriggerInstance::new);
		});

		public Optional<ResourceLocation> initialDimension() {
			return this.initialDimension;
		}

		public Optional<ResourceLocation> destinationDimension() {
			return this.destinationDimension;
		}

		public Optional<Long> distanceTraveled() {
			return this.distanceTraveled;
		}

		public boolean matches(ResourceLocation initialDimension, ResourceLocation destinationDimension, long distanceTraveled)
		{
			if(this.initialDimension.isPresent())
			{
				if(!Objects.equals(this.initialDimension.get(), initialDimension))
					return false;
			}

			if(this.destinationDimension.isPresent())
			{
				if(!Objects.equals(this.destinationDimension.get(), destinationDimension))
					return false;
			}

			if(this.distanceTraveled.isPresent())
			{
				if(this.distanceTraveled.get() > distanceTraveled)
					return false;
			}

			return true;
		}
	}
}
