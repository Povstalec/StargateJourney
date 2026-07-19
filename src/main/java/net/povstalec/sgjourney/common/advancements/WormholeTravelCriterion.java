package net.povstalec.sgjourney.common.advancements;

import java.util.Objects;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.sgjourney.AddressRegion;
import net.povstalec.sgjourney.common.sgjourney.Galaxy;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WormholeTravelCriterion extends SimpleCriterionTrigger<WormholeTravelCriterion.WormholeTravelTriggerInstance>
{
	public Codec<WormholeTravelTriggerInstance> codec()
	{
		return WormholeTravelTriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, StargateConnection.Type connectionType, @Nullable ResourceKey<Level> initialDimension, @Nullable ResourceKey<Level> destinationDimension,
						@Nullable ResourceKey<AddressRegion> initialAddressRegion, @Nullable ResourceKey<AddressRegion> destinationAddressRegion,
						@Nullable ResourceKey<Galaxy> initialGalaxy, @Nullable ResourceKey<Galaxy> destinationGalaxy, long distanceTraveled)
	{
		this.trigger(player, (trigger -> trigger.matches(connectionType,
				initialDimension != null ? initialDimension.location() : null, destinationDimension != null ? destinationDimension.location() : null,
				initialAddressRegion != null ? initialAddressRegion.location() : null, destinationAddressRegion != null ? destinationAddressRegion.location() : null,
				initialGalaxy != null ? initialGalaxy.location() : null, destinationGalaxy != null ? destinationGalaxy.location() : null, distanceTraveled)));
	}

	public static class WormholeTravelTriggerInstance implements SimpleCriterionTrigger.SimpleInstance
	{
		@Nullable
		private final ContextAwarePredicate entity;
		@Nullable
		private final StargateConnection.Type connectionType;
		@Nullable
		private final ResourceLocation initialDimension;
		@Nullable
		private final ResourceLocation destinationDimension;
		@Nullable
		private final ResourceLocation initialAddressRegion;
		@Nullable
		private final ResourceLocation destinationAddressRegion;
		@Nullable
		private final ResourceLocation initialGalaxy;
		@Nullable
		private final ResourceLocation destinationGalaxy;
		@Nullable
		private final Long distanceTraveled;
		
		public static final Codec<WormholeTravelTriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(
					EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(trigger -> Optional.ofNullable(trigger.entity)),
					StargateConnection.Type.CODEC.optionalFieldOf("connection_type").forGetter(trigger -> Optional.ofNullable(trigger.connectionType)),
					ResourceLocation.CODEC.optionalFieldOf("from_dimension").forGetter(trigger -> Optional.ofNullable(trigger.initialDimension)),
					ResourceLocation.CODEC.optionalFieldOf("to_dimension").forGetter(trigger -> Optional.ofNullable(trigger.destinationDimension)),
					ResourceLocation.CODEC.optionalFieldOf("from_address_region").forGetter(trigger -> Optional.ofNullable(trigger.initialAddressRegion)),
					ResourceLocation.CODEC.optionalFieldOf("to_address_region").forGetter(trigger -> Optional.ofNullable(trigger.destinationAddressRegion)),
					ResourceLocation.CODEC.optionalFieldOf("from_galaxy").forGetter(trigger -> Optional.ofNullable(trigger.initialGalaxy)),
					ResourceLocation.CODEC.optionalFieldOf("to_galaxy").forGetter(trigger -> Optional.ofNullable(trigger.destinationAddressRegion)),
					Codec.LONG.optionalFieldOf("distance").forGetter(trigger -> Optional.ofNullable(trigger.distanceTraveled))
			).apply(instance, WormholeTravelTriggerInstance::new);
		});
		
		public WormholeTravelTriggerInstance(Optional<ContextAwarePredicate> entity, Optional<StargateConnection.Type> connectionType,
									 Optional<ResourceLocation> initialDimension, Optional<ResourceLocation> destinationDimension,
									 Optional<ResourceLocation> initialAddressRegion, Optional<ResourceLocation> destinationAddressRegion,
									 Optional<ResourceLocation> initialGalaxy, Optional<ResourceLocation> destinationGalaxy,
									 Optional<Long> distanceTraveled)
		{
			this.entity = entity.orElse(null);
			this.connectionType = connectionType.orElse(null);
			this.initialDimension = initialDimension.orElse(null);
			this.destinationDimension = destinationDimension.orElse(null);
			this.initialAddressRegion = initialAddressRegion.orElse(null);
			this.destinationAddressRegion = destinationAddressRegion.orElse(null);
			this.initialGalaxy = initialGalaxy.orElse(null);
			this.destinationGalaxy = destinationGalaxy.orElse(null);
			this.distanceTraveled = distanceTraveled.orElse(null);
		}
		
		public boolean matches(StargateConnection.Type connectionType, ResourceLocation initialDimension, ResourceLocation destinationDimension,
							   ResourceLocation initialAddressRegion, ResourceLocation destinationAddressRegion,
							   ResourceLocation initialGalaxy, ResourceLocation destinationGalaxy, long distanceTraveled)
		{
			if(this.connectionType != null && !Objects.equals(this.connectionType, connectionType))
				return false;
			
			if(this.initialDimension != null && !Objects.equals(this.initialDimension, initialDimension))
				return false;
			if(this.destinationDimension != null && !Objects.equals(this.destinationDimension, destinationDimension))
				return false;
			
			if(this.initialAddressRegion != null && !Objects.equals(this.initialAddressRegion, initialAddressRegion))
				return false;
			if(this.destinationAddressRegion != null && !Objects.equals(this.destinationAddressRegion, destinationAddressRegion))
				return false;
			
			if(this.initialGalaxy != null && !Objects.equals(this.initialGalaxy, initialGalaxy))
				return false;
			if(this.destinationGalaxy != null && !Objects.equals(this.destinationGalaxy, destinationGalaxy))
				return false;
			
			if(this.distanceTraveled != null && this.distanceTraveled > distanceTraveled)
				return false;
			
			return true;
		}
		
		@Override
		public @NotNull Optional<ContextAwarePredicate> player()
		{
			return Optional.ofNullable(entity);
		}
	}
}
