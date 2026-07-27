package net.povstalec.sgjourney.common.advancements;

import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.AddressRegion;
import net.povstalec.sgjourney.common.sgjourney.Galaxy;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

import javax.annotation.Nullable;

public class WormholeTravelCriterion extends SimpleCriterionTrigger<WormholeTravelCriterion.WormholeTravelTrigger>
{
	public static final WormholeTravelCriterion INSTANCE = new WormholeTravelCriterion();
	private static final ResourceLocation CRITERION_ID = new ResourceLocation(StargateJourney.MODID, "stargate_wormhole_travel");
	
	@Override
	protected WormholeTravelTrigger createInstance(JsonObject obj, EntityPredicate.Composite playerPredicate, DeserializationContext predicateDeserializer)
	{
		Optional<StargateConnection.Type> connectionType = Optional.empty();
		
		Optional<ResourceLocation> initialDimension = Optional.empty();
		Optional<ResourceLocation> destinationDimension = Optional.empty();
		
		Optional<ResourceLocation> initialAddressRegion = Optional.empty();
		Optional<ResourceLocation> destinationAddressRegion = Optional.empty();
		
		Optional<ResourceLocation> initialGalaxy = Optional.empty();
		Optional<ResourceLocation> destinationGalaxy = Optional.empty();
		
		Optional<Long> distanceTraveled = Optional.empty();
		
		if(GsonHelper.isStringValue(obj, "connection_type"))
		{
			String name = GsonHelper.getAsString(obj, "connection_type");
			StargateConnection.Type type = StargateConnection.Type.fromString(name);
			if(type == null)
				throw new JsonSyntaxException("Name '" + name + "' is not a valid Connection Type (SYSTEM_WIDE, INTERSTELLAR_INTERGALACTIC)");
			
			connectionType = Optional.of(type);
		}
		
		if(GsonHelper.isStringValue(obj, "from_dimension"))
			initialDimension = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "from_dimension")));
		if(GsonHelper.isStringValue(obj, "to_dimension"))
			destinationDimension = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "to_dimension")));
		
		if(GsonHelper.isStringValue(obj, "from_address_region"))
			initialAddressRegion = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "from_address_region")));
		if(GsonHelper.isStringValue(obj, "to_address_region"))
			destinationAddressRegion = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "to_address_region")));
		
		if(GsonHelper.isStringValue(obj, "from_galaxy"))
			initialGalaxy = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "from_galaxy")));
		if(GsonHelper.isStringValue(obj, "to_galaxy"))
			destinationGalaxy = Optional.ofNullable(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "to_galaxy")));
		
		if(GsonHelper.isNumberValue(obj, "distance"))
			distanceTraveled = Optional.of(GsonHelper.getAsLong(obj, "distance"));
		
		return new WormholeTravelTrigger(playerPredicate, connectionType,
				initialDimension, destinationDimension, initialAddressRegion,
				destinationAddressRegion, initialGalaxy, destinationGalaxy, distanceTraveled);
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

	@Override
	public ResourceLocation getId()
	{
		return CRITERION_ID;
	}
	
	public static class WormholeTravelTrigger extends AbstractCriterionTriggerInstance
	{
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

		public WormholeTravelTrigger(EntityPredicate.Composite entity, Optional<StargateConnection.Type> connectionType,
									 Optional<ResourceLocation> initialDimension, Optional<ResourceLocation> destinationDimension,
									 Optional<ResourceLocation> initialAddressRegion, Optional<ResourceLocation> destinationAddressRegion,
									 Optional<ResourceLocation> initialGalaxy, Optional<ResourceLocation> destinationGalaxy,
									 Optional<Long> distanceTraveled)
		{
			super(WormholeTravelCriterion.CRITERION_ID, entity);
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

		public JsonObject serializeToJson(SerializationContext predicateSerializer)
		{
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			
			if(connectionType != null)
				jsonObject.add("connection_type", new JsonPrimitive(connectionType.getSerializedName()));
			
			if(initialDimension != null)
				jsonObject.add("from_dimension", new JsonPrimitive(initialDimension.toString()));
			if(destinationDimension != null)
				jsonObject.add("to_dimension", new JsonPrimitive(destinationDimension.toString()));
			
			if(initialAddressRegion != null)
				jsonObject.add("from_address_region", new JsonPrimitive(initialAddressRegion.toString()));
			if(destinationAddressRegion != null)
				jsonObject.add("to_address_region", new JsonPrimitive(destinationAddressRegion.toString()));
			
			if(initialGalaxy != null)
				jsonObject.add("from_galaxy", new JsonPrimitive(initialGalaxy.toString()));
			if(destinationGalaxy != null)
				jsonObject.add("to_galaxy", new JsonPrimitive(destinationGalaxy.toString()));
			
			if(distanceTraveled != null)
				jsonObject.add("distance", new JsonPrimitive(distanceTraveled));
			
			return jsonObject;
		}
	}
	
	
}
