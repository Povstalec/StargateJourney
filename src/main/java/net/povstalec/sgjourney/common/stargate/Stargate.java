package net.povstalec.sgjourney.common.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;

public class Stargate
{

	private static long systemWideConnectionCost = CommonStargateConfig.system_wide_connection_energy_cost.get();
	private static long interstellarConnectionCost = CommonStargateConfig.interstellar_connection_energy_cost.get();
	private static long intergalacticConnectionCost = CommonStargateConfig.intergalactic_connection_energy_cost.get();

	private static long systemWideConnectionDraw = CommonStargateConfig.system_wide_connection_energy_draw.get();
	private static long interstellarConnectionDraw = CommonStargateConfig.interstellar_connection_energy_draw.get();
	private static long intergalacticConnectionDraw = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	
	public enum Gen
	{
		NONE(0),
		GEN_1(1),
		GEN_2(2),
		GEN_3(3);
		
		private final int gen;
		
		private Gen(int gen)
		{
			this.gen = gen;
		}
		
		public int getGen()
		{
			return this.gen;
		}
	}
	
	public enum ConnectionType
	{
		SYSTEM_WIDE(systemWideConnectionCost, systemWideConnectionDraw),
		INTERSTELLAR(interstellarConnectionCost, interstellarConnectionDraw),
		INTERGALACTIC(intergalacticConnectionCost, intergalacticConnectionDraw);
		
		private long estabilishingPowerCost;
		private long powerDraw;
		
		ConnectionType(long estabilishingPowerCost, long powerDraw)
		{
			this.estabilishingPowerCost = estabilishingPowerCost;
			this.powerDraw = powerDraw;
		}
		
		public long getEstabilishingPowerCost()
		{
			return this.estabilishingPowerCost;
		}
		
		public long getPowerDraw()
		{
			return this.powerDraw;
		}
	}
	
	public static enum WormholeTravel
	{
		ENABLED,
		CREATIVE_ONLY,
		DISABLED;
	}
	
	public enum FilterType
	{
		NONE,
		WHITELIST,
		BLACKLIST;
	}
	
	public enum FeedbackType
	{
		INFO,
		ERROR,
		MAJOR_ERROR;
		
		public boolean isError()
		{
			return this == ERROR || this == MAJOR_ERROR;
		}
		
		public boolean shouldPlaySound()
		{
			return this == MAJOR_ERROR;
		}
	}
	
	public enum Feedback
	{
		NONE(0, FeedbackType.INFO, Component.empty()),
		UNKNOWN_ERROR(-1, FeedbackType.ERROR, createError("unknown", true)),
		
		// Chevron/Symbol
		SYMBOL_ENCODED(1, FeedbackType.INFO, createInfo("symbol_encoded")),
		SYMBOL_IN_ADDRESS(-2, FeedbackType.ERROR, createError("symbol_in_addres", false)),
		SYMBOL_OUT_OF_BOUNDS(-3, FeedbackType.ERROR, createError("symbol_out_of_bounds", false)),
		
		// Estabilishing Connection
		CONNECTION_ESTABILISHED_SYSTEM_WIDE(2, FeedbackType.INFO, createInfo("connection_estabilished.system_wide")),
		CONNECTION_ESTABILISHED_INTERSTELLAR(3, FeedbackType.INFO, createInfo("connection_estabilished.interstellar")),
		CONNECTION_ESTABILISHED_INTERGALACTIC(4, FeedbackType.INFO, createInfo("connection_estabilished.intergalactic")),
		INCOPLETE_ADDRESS(-4, FeedbackType.ERROR, createError("incomplete_address", false)),
		INVALID_ADDRESS(-5, FeedbackType.ERROR, createError("invalid_address", false)),
		NOT_ENOUGH_POWER(-6, FeedbackType.MAJOR_ERROR, createError("not_enough_power", true)),
		SELF_OBSTRUCTED(-7, FeedbackType.MAJOR_ERROR, createError("self_obstructed", true)),
		TARGET_OBSTRUCTED(-8, FeedbackType.ERROR, createError("target_obstructed", false)),
		SELF_DIAL(-9, FeedbackType.MAJOR_ERROR, createError("self_dial", true)),
		SAME_SYSTEM_DIAL(-10, FeedbackType.MAJOR_ERROR, createError("same_system_dial", true)),
		ALREADY_CONNECTED(-11, FeedbackType.ERROR, createError("already_connected", false)),
		NO_GALAXY(-12, FeedbackType.ERROR, createError("no_galaxy", false)),
		NO_DIMENSIONS(-13, FeedbackType.ERROR, createError("no_dimensions", false)),
		NO_STARGATES(-14, FeedbackType.ERROR, createError("no_stargates", false)),

		// Wormhole TODO
		//TRANSPORT_SUCCESSFUL(5, FeedbackType.INFO, createInfo("wormhole.transport_successful")),
		//ENTITY_DESTROYED(6, FeedbackType.INFO, createInfo("wormhole.entity_destroyed")),
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, FeedbackType.INFO, createInfo("connection_ended.disconnect")),
		CONNECTION_ENDED_BY_POINT_OF_ORIGIN(8, FeedbackType.INFO, createInfo("connection_ended.point_of_origin")),
		CONNECTION_ENDED_BY_NETWORK(9, FeedbackType.INFO, createInfo("connection_ended.stargate_network")),
		CONNECTION_ENDED_BY_AUTOCLOSE(10, FeedbackType.INFO, createInfo("connection_ended.autoclose")),
		EXCEEDED_CONNECTION_TIME(-15, FeedbackType.ERROR, createError("exceeded_connection_time", false)),
		RAN_OUT_OF_POWER(-16, FeedbackType.ERROR, createError("ran_out_of_power", false)),
		CONNECTION_REROUTED(-17, FeedbackType.ERROR, createError("connection_rerouted", false)),
		WRONG_DISCONNECT_SIDE(-18, FeedbackType.ERROR, createError("wrong_disconnect_side", false)),

		STARGATE_DESTROYED(-19, FeedbackType.ERROR, createError("stargate_destroyed", false)),
		TARGET_STARGATE_DOES_NOT_EXIST(-20, FeedbackType.ERROR, createError("target_stargate_does_not_exist", false)),
		
		// Universe
		
		// Milky Way
		CHEVRON_RAISED(11, FeedbackType.INFO, createInfo("chevron_raised")),
		CHEVRON_ALREADY_RAISED(-21, FeedbackType.ERROR, createError("chevron_already_raised", false)),
		CHEVRON_ALREADY_LOWERED(-22, FeedbackType.ERROR, createError("chevron_already_lowered", false));
		
		// Pegasus
		
		private int code;
		private final FeedbackType type;
		private final Component feedbackMessage;
		
		private Feedback(int code, FeedbackType type, Component feedbackMessage)
		{
			this.code = code;
			this.type = type;
			this.feedbackMessage = feedbackMessage;
		}
		
		public int getCode()
		{
			return this.code;
		}
		
		public Component getFeedbackMessage()
		{
			return this.feedbackMessage;
		}
		
		public boolean playFailSound()
		{
			return this.type.shouldPlaySound();
		}
		
		public boolean isError()
		{
			return this.type.isError();
		}
		
		/*public String getEnglishText()
		{
			if(feedbackMessage.getContents() instanceof TranslatableContents translatable)
				return Language.loadDefault().;
			return "";
		}*/
	}
	
	private static Component createInfo(String feedback)
	{
		return Component.translatable("message.sgjourney.stargate.info." + feedback);
	}
	
	private static Component createError(String feedback, boolean majorError)
	{
		MutableComponent component = Component.translatable("message.sgjourney.stargate.error." + feedback);
		
		return majorError ? component.withStyle(ChatFormatting.DARK_RED) : component.withStyle(ChatFormatting.RED);
	}
}
