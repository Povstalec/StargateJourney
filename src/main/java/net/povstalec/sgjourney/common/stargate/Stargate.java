package net.povstalec.sgjourney.common.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Stargate
{
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
	
	public static enum ChevronLockSpeed
	{
		SLOW(3),
		MEDIUM(2),
		FAST(1);
		
		private int multiplier;
		
		ChevronLockSpeed(int multiplier)
		{
			this.multiplier = multiplier;
		}
		
		public int getMultiplier()
		{
			return this.multiplier;
		}
		
		public int getChevronWaitTicks()
		{
			return this.multiplier * 4;
		}
		
		public int getKawooshStartTicks()
		{
			return getChevronWaitTicks() * 9;
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
		NONE(0, FeedbackType.INFO, "none"),
		UNKNOWN_ERROR(-1, FeedbackType.ERROR, "unknown"),
		
		// Chevron/Symbol
		SYMBOL_ENCODED(1, FeedbackType.INFO, "symbol_encoded"),
		SYMBOL_IN_ADDRESS(-2, FeedbackType.ERROR, "symbol_in_address"),
		SYMBOL_OUT_OF_BOUNDS(-3, FeedbackType.ERROR, "symbol_out_of_bounds"),
		ENCODE_WHEN_CONNECTED(-4, FeedbackType.ERROR, "encode_when_connected"),
		
		// Establishing Connection
		CONNECTION_ESTABLISHED_SYSTEM_WIDE(2, FeedbackType.INFO, "connection_established.system_wide"),
		CONNECTION_ESTABLISHED_INTERSTELLAR(3, FeedbackType.INFO, "connection_established.interstellar"),
		CONNECTION_ESTABLISHED_INTERGALACTIC(4, FeedbackType.INFO, "connection_established.intergalactic"),
		
		INCOMPLETE_ADDRESS(-5, FeedbackType.MAJOR_ERROR, "incomplete_address"),
		INVALID_ADDRESS(-6, FeedbackType.MAJOR_ERROR, "invalid_address"),
		NOT_ENOUGH_POWER(-7, FeedbackType.MAJOR_ERROR, "not_enough_power"),
		SELF_OBSTRUCTED(-8, FeedbackType.MAJOR_ERROR, "self_obstructed"),
		TARGET_OBSTRUCTED(-9, FeedbackType.MAJOR_ERROR, "target_obstructed"),
		SELF_DIAL(-10, FeedbackType.MAJOR_ERROR, "self_dial"),
		SAME_SYSTEM_DIAL(-11, FeedbackType.MAJOR_ERROR, "same_system_dial"),
		ALREADY_CONNECTED(-12, FeedbackType.MAJOR_ERROR, "already_connected"),
		NO_GALAXY(-13, FeedbackType.MAJOR_ERROR, "no_galaxy"),
		NO_DIMENSIONS(-14, FeedbackType.MAJOR_ERROR, "no_dimensions"),
		NO_STARGATES(-15, FeedbackType.MAJOR_ERROR, "no_stargates"),
		TARGET_RESTRICTED(-16, FeedbackType.MAJOR_ERROR, "target_restricted"),
		INVALID_8_CHEVRON_ADDRESS(-17, FeedbackType.MAJOR_ERROR, "invalid_8_chevron_address"),
		INVALID_SYSTEM_WIDE_CONNECTION(-18, FeedbackType.MAJOR_ERROR, "invalid_system_wide_connection"),

		// Wormhole TODO
		//TRANSPORT_SUCCESSFUL(5, FeedbackType.INFO, createInfo("wormhole.transport_successful")),
		//ENTITY_DESTROYED(6, FeedbackType.INFO, createInfo("wormhole.entity_destroyed")),
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, FeedbackType.INFO, "connection_ended.disconnect"),
		CONNECTION_ENDED_BY_POINT_OF_ORIGIN(8, FeedbackType.INFO, "connection_ended.point_of_origin"),
		CONNECTION_ENDED_BY_NETWORK(9, FeedbackType.INFO, "connection_ended.stargate_network"),
		CONNECTION_ENDED_BY_AUTOCLOSE(10, FeedbackType.INFO, "connection_ended.autoclose"),
		EXCEEDED_CONNECTION_TIME(-19, FeedbackType.ERROR, "exceeded_connection_time"),
		RAN_OUT_OF_POWER(-20, FeedbackType.ERROR, "ran_out_of_power"),
		CONNECTION_REROUTED(-21, FeedbackType.ERROR, "connection_rerouted"),
		WRONG_DISCONNECT_SIDE(-22, FeedbackType.ERROR, "wrong_disconnect_side"),
		CONNECTION_FORMING(-23, FeedbackType.ERROR, "connection_forming"),

		STARGATE_DESTROYED(-24, FeedbackType.ERROR, "stargate_destroyed"),
		COULD_NOT_REACH_TARGET_STARGATE(-25, FeedbackType.MAJOR_ERROR, "could_not_reach_target_stargate"),
		INTERRUPTED_BY_INCOMING_CONNECTION(-26, FeedbackType.ERROR, "interrupted_by_incoming_connection"),
		
		// Universe
		
		// Milky Way
		CHEVRON_RAISED(11, FeedbackType.INFO, "chevron_opened"),
		ROTATING(12, FeedbackType.INFO, "rotating"),
		ROTATION_BLOCKED(-27, FeedbackType.INFO, "rotation_blocked"),
		NOT_ROTATING(-28, FeedbackType.INFO, "not_rotating"),
		ROTATION_STOPPED(13, FeedbackType.INFO, "rotation_stopped"),
		CHEVRON_ALREADY_RAISED(-29, FeedbackType.ERROR, "chevron_already_opened"),
		CHEVRON_ALREADY_LOWERED(-30, FeedbackType.ERROR, "chevron_already_closed"),
		CHEVRON_NOT_RAISED(-31, FeedbackType.ERROR, "chevron_not_raised"),
		CANNOT_ENCODE_POINT_OF_ORIGIN(-32, FeedbackType.ERROR, "cannot_encode_point_of_origin");
		
		// Pegasus
		
		private int code;
		private final FeedbackType type;
		private final String message;
		private final Component feedbackMessage;
		
		private Feedback(int code, FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
			
			if(type.isError())
				this.feedbackMessage = createError(message, type == FeedbackType.MAJOR_ERROR);
			else
				this.feedbackMessage = createInfo(message);
		}
		
		public int getCode()
		{
			return this.code;
		}
		
		public String getMessage()
		{
			return this.message;
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
	
	public static class RGBA
	{
		private float red;
		private float green;
		private float blue;
		private float alpha;
		
		public static final RGBA DEFAULT_RGBA = new RGBA(255, 255, 255, 255);
		
		public RGBA(int red, int green, int blue, int alpha)
		{
			this.red = (float) red / 255;
			this.green = (float) green / 255;
			this.blue = (float) blue / 255;
			this.alpha = (float) alpha / 255;
		}
		
		public float getRed()
		{
			return red;
		}
		
		public float getGreen()
		{
			return green;
		}
		
		public float getBlue()
		{
			return blue;
		}
		
		public float getAlpha()
		{
			return alpha;
		}
	}
}
