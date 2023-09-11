package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;

public class Stargate
{
	// Thickness (Includes Chevrons)
	public static final float STANDARD_THICKNESS = 9.0F;
	public static final float TOLLAN_THICKNESS = 5.0F;
	// Vertical Height
	public static final float VERTICAL_CENTER_STANDARD_HEIGHT = 0.5F;
	public static final float VERTICAL_CENTER_TOLLAN_HEIGHT = 0.0F;
	// Horizontal Height
	public static final float HORIZONTAL_CENTER_STANDARD_HEIGHT = (STANDARD_THICKNESS / 2) / 16;
	public static final float HORIZONTAL_CENTER_TOLLAN_HEIGHT = (TOLLAN_THICKNESS / 2) / 16;
	
	public static final int[] DIALING_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 6, 7, 8, 4, 5};
	
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 5, 6, 7, 8};
	
	private static long systemWideConnectionCost = CommonStargateConfig.system_wide_connection_energy_cost.get();
	private static long interstellarConnectionCost = CommonStargateConfig.interstellar_connection_energy_cost.get();
	private static long intergalacticConnectionCost = CommonStargateConfig.intergalactic_connection_energy_cost.get();

	private static long systemWideConnectionDraw = CommonStargateConfig.system_wide_connection_energy_draw.get();
	private static long interstellarConnectionDraw = CommonStargateConfig.interstellar_connection_energy_draw.get();
	private static long intergalacticConnectionDraw = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	
	public enum Type
	{
		UNIVERSE(StargatePart.DEFAULT_PARTS, ChevronLockSpeed.SLOW, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT),
		MILKY_WAY(StargatePart.DEFAULT_PARTS, ChevronLockSpeed.SLOW, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT),
		PEGASUS(StargatePart.DEFAULT_PARTS, ChevronLockSpeed.MEDIUM, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT),
		TOLLAN(StargatePart.TOLLAN_PARTS, ChevronLockSpeed.MEDIUM, VERTICAL_CENTER_TOLLAN_HEIGHT, HORIZONTAL_CENTER_TOLLAN_HEIGHT),
		CLASSIC(StargatePart.DEFAULT_PARTS, ChevronLockSpeed.SLOW, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
		
		private ArrayList<StargatePart> parts;
		private ChevronLockSpeed chevronLockSpeed;
		private float verticalCenterHeight;
		private float horizontalCenterHeight;
		
		private Type(ArrayList<StargatePart> parts, ChevronLockSpeed chevronLockSpeed, float verticalCenterHeight, float horizontalCenterHeight)
		{
			this.parts = parts;
			this.chevronLockSpeed = chevronLockSpeed;
			this.verticalCenterHeight = verticalCenterHeight;
			this.horizontalCenterHeight = horizontalCenterHeight;
		}
		
		public ArrayList<StargatePart> getParts()
		{
			return this.parts;
		}
		
		public float getVerticalCenterHeight()
		{
			return this.verticalCenterHeight;
		}
		
		public float getHorizontalCenterHeight()
		{
			return this.horizontalCenterHeight;
		}
		
		public ChevronLockSpeed getChevronLockSpeed()
		{
			return this.chevronLockSpeed;
		}
	}
	
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
		ALREADY_CONNECTED(-11, FeedbackType.MAJOR_ERROR, createError("already_connected", true)),
		NO_GALAXY(-12, FeedbackType.MAJOR_ERROR, createError("no_galaxy", true)),
		NO_DIMENSIONS(-13, FeedbackType.MAJOR_ERROR, createError("no_dimensions", true)),
		NO_STARGATES(-14, FeedbackType.MAJOR_ERROR, createError("no_stargates", true)),

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
		INTERRUPTED_BY_INCOMING_CONNECTION(-21, FeedbackType.ERROR, createError("interrupted_by_incoming_connection", false)),
		
		// Universe
		
		// Milky Way
		CHEVRON_RAISED(11, FeedbackType.INFO, createInfo("chevron_raised")),
		CHEVRON_ALREADY_RAISED(-22, FeedbackType.ERROR, createError("chevron_already_raised", false)),
		CHEVRON_ALREADY_LOWERED(-23, FeedbackType.ERROR, createError("chevron_already_lowered", false));
		
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
