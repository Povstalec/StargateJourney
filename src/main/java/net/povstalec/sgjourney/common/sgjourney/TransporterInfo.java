package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TransporterInfo
{
	public enum FeedbackType
	{
		INFO,
		ERROR,
		MAJOR_ERROR;
		
		public boolean isError()
		{
			return this != INFO;
		}
		
		public boolean shouldPlaySound()
		{
			return this == MAJOR_ERROR;
		}
	}
	
	public enum Feedback
	{
		NONE(0, TransporterInfo.FeedbackType.INFO, "none"),
		UNKNOWN_ERROR(-1, TransporterInfo.FeedbackType.ERROR, "unknown"),
		
		// Establishing Connection
		CONNECTION_ESTABLISHED_DIMENSIONAL(1, TransporterInfo.FeedbackType.INFO, "connection_established.dimensional"),
		CONNECTION_ESTABLISHED_SYSTEM_WIDE(2, TransporterInfo.FeedbackType.INFO, "connection_established.system_wide"),
		CONNECTION_ESTABLISHED_RELAYED_DIMENSIONAL(3, TransporterInfo.FeedbackType.INFO, "connection_established.relayed_dimensional"),
		CONNECTION_ESTABLISHED_RELAYED_SYSTEM_WIDE(4, TransporterInfo.FeedbackType.INFO, "connection_established.relayed_system_wide"),
		CONNECTION_ESTABLISHED_RELAYED_INTERSTELLAR(5, TransporterInfo.FeedbackType.INFO, "connection_established.relayed_interstellar"),
		CONNECTION_ESTABLISHED_RELAYED_INTERGALACTIC(6, TransporterInfo.FeedbackType.INFO, "connection_established.relayed_intergalactic"),
		
		INVALID_TRANSPORTER_ID(-2, TransporterInfo.FeedbackType.MAJOR_ERROR, "invalid_transporter_id"),
		NOT_ENOUGH_POWER(-3, TransporterInfo.FeedbackType.MAJOR_ERROR, "not_enough_power"), //TODO
		SELF_OBSTRUCTED(-4, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_obstructed"),
		TARGET_OBSTRUCTED(-5, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_obstructed"),
		SELF_CONNECT(-6, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_connect"),
		ALREADY_CONNECTED(-7, TransporterInfo.FeedbackType.MAJOR_ERROR, "already_connected"),
		TARGET_RESTRICTED(-8, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_restricted"),
		TARGET_NOT_WHITELISTED(-9, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_not_whitelisted"),
		NOT_WHITELISTED_BY_TARGET(-10, TransporterInfo.FeedbackType.MAJOR_ERROR, "not_whitelisted_by_target"),
		TARGET_BLACKLISTED(-11, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_blacklisted"),
		BLACKLISTED_BY_TARGET(-12, TransporterInfo.FeedbackType.MAJOR_ERROR, "blacklisted_by_target"),
		//TODO no transporter at coords
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, TransporterInfo.FeedbackType.INFO, "connection_ended.disconnect"),
		CONNECTION_ENDED_BY_NETWORK(8, TransporterInfo.FeedbackType.INFO, "connection_ended.transporter_network"),
		CONNECTION_NOT_FINISHED(-13, TransporterInfo.FeedbackType.ERROR, "connection_not_finished"), //TODO
		
		TRANSPORTER_DESTROYED(-13, TransporterInfo.FeedbackType.ERROR, "transporter_destroyed"),
		COULD_NOT_REACH_TARGET_TRANSPORTER(-14, TransporterInfo.FeedbackType.MAJOR_ERROR, "could_not_reach_target_transporter"),
		INTERRUPTED_BY_INCOMING_CONNECTION(-15, TransporterInfo.FeedbackType.ERROR, "interrupted_by_incoming_connection"), //TODO
		
		TARGET_NOT_LOADED(-16, TransporterInfo.FeedbackType.ERROR, "target_not_loaded");
		
		private final int code;
		private final TransporterInfo.FeedbackType type;
		private final String message;
		private final Component feedbackMessage;
		
		Feedback(int code, TransporterInfo.FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
			
			if(type.isError())
				this.feedbackMessage = createError(message, type == TransporterInfo.FeedbackType.MAJOR_ERROR);
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
		
		public static TransporterInfo.Feedback fromOrdinal(int ordinal)
		{
			if(ordinal < 0 || ordinal >= TransporterInfo.Feedback.values().length)
				return NONE;
			
			return TransporterInfo.Feedback.values()[ordinal];
		}
	}
	
	private static Component createInfo(String feedback)
	{
		return Component.translatable("message.sgjourney.transporter.info." + feedback);
	}
	
	private static Component createError(String feedback, boolean majorError)
	{
		MutableComponent component = Component.translatable("message.sgjourney.transporter.error." + feedback);
		
		return majorError ? component.withStyle(ChatFormatting.DARK_RED) : component.withStyle(ChatFormatting.RED);
	}
}
