package net.povstalec.sgjourney.common.advancements;

import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.povstalec.sgjourney.StargateJourney;

public class WormholeTravelCriterion extends SimpleCriterionTrigger<WormholeTravelCriterion.WormholeTravelTrigger>
{
	public static final WormholeTravelCriterion INSTANCE = new WormholeTravelCriterion();
	private static final ResourceLocation CRITERION_ID = new ResourceLocation(StargateJourney.MODID, "stargate_wormhole_travel");
	
	@Override
	protected WormholeTravelTrigger createInstance(JsonObject obj, EntityPredicate.Composite playerPredicate, DeserializationContext predicateDeserializer)
	{
		Optional<ResourceLocation> initialDimension = Optional.empty();
		Optional<ResourceLocation> destinationDimension = Optional.empty();
		Optional<Long> distanceTraveled = Optional.empty();
		
		if(GsonHelper.isStringValue(obj, "from"))
			initialDimension = Optional.of(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "from")));
		
		if(GsonHelper.isStringValue(obj, "to"))
			destinationDimension = Optional.of(ResourceLocation.tryParse(GsonHelper.getAsString(obj, "to")));
		
		if(GsonHelper.isNumberValue(obj, "distance"))
			distanceTraveled = Optional.of(GsonHelper.getAsLong(obj, "distance"));
		
		return new WormholeTravelTrigger(playerPredicate, initialDimension, destinationDimension, distanceTraveled);
	}

	public void trigger(ServerPlayer player, ResourceLocation initialDimension, ResourceLocation destinationDimension, long distanceTraveled)
	{
		this.trigger(player, (trigger -> trigger.matches(initialDimension, destinationDimension, distanceTraveled)));
	}

	@Override
	public ResourceLocation getId()
	{
		return CRITERION_ID;
	}
	
	public static class WormholeTravelTrigger extends AbstractCriterionTriggerInstance
	{
		private final Optional<ResourceLocation> initialDimension;
		private final Optional<ResourceLocation> destinationDimension;
		private final Optional<Long> distanceTraveled;

		public WormholeTravelTrigger(EntityPredicate.Composite entity,
				Optional<ResourceLocation> initialDimension, Optional<ResourceLocation> destinationDimension,
				Optional<Long> distanceTraveled)
		{
			super(WormholeTravelCriterion.CRITERION_ID, entity);
			this.initialDimension = initialDimension;
			this.destinationDimension = destinationDimension;
			this.distanceTraveled = distanceTraveled;
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

		public JsonObject serializeToJson(SerializationContext predicateSerializer)
		{
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			
			if(initialDimension.isPresent())
				jsonObject.add("from", new JsonPrimitive(initialDimension.get().toString()));
			if(destinationDimension.isPresent())
				jsonObject.add("to", new JsonPrimitive(destinationDimension.get().toString()));
			if(distanceTraveled.isPresent())
				jsonObject.add("distance", new JsonPrimitive(distanceTraveled.get()));
			
			return jsonObject;
		}
	}
	
	
}
