package net.povstalec.sgjourney.common.advancements;

import java.util.Objects;

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
		boolean disintegrated = GsonHelper.getAsBoolean(obj, "disintegrated");
		
		return new WormholeTravelTrigger(playerPredicate, disintegrated);
	}

	public void trigger(ServerPlayer player, boolean disintegrated)
	{
		this.trigger(player, (trigger -> true));
	}

	@Override
	public ResourceLocation getId()
	{
		return CRITERION_ID;
	}
	
	public static class WormholeTravelTrigger extends AbstractCriterionTriggerInstance
	{
		private final boolean disintegrated;

		public WormholeTravelTrigger(EntityPredicate.Composite entity, boolean disintegrated)
		{
			super(WormholeTravelCriterion.CRITERION_ID, entity);
			this.disintegrated = disintegrated;
		}
		
		public boolean matches(WormholeTravelTrigger trigger)
		{
			return Objects.equals(trigger.disintegrated, this.disintegrated);
		}

		public JsonObject serializeToJson(SerializationContext predicateSerializer)
		{
			JsonObject jsonObject = super.serializeToJson(predicateSerializer);
			jsonObject.add("disintegrated", new JsonPrimitive(disintegrated));
			
			return jsonObject;
		}
	}
	
	
}
