package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

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
		NO_TRANSPORTER_AT_COORDS(-3, TransporterInfo.FeedbackType.MAJOR_ERROR, "no_transporter_at_coords"),
		NOT_ENOUGH_POWER(-4, TransporterInfo.FeedbackType.MAJOR_ERROR, "not_enough_power"),
		NOT_ENOUGH_POWER_IN_TARGET(-5, TransporterInfo.FeedbackType.MAJOR_ERROR, "not_enough_power_in_target"),
		SELF_OBSTRUCTED(-6, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_obstructed"),
		TARGET_OBSTRUCTED(-7, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_obstructed"),
		SELF_CONNECT(-8, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_connect"),
		ALREADY_CONNECTED(-9, TransporterInfo.FeedbackType.MAJOR_ERROR, "already_connected"),
		SELF_RESTRICTED(-10, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_restricted"),
		TARGET_RESTRICTED(-11, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_restricted"),
		TARGET_NOT_WHITELISTED(-12, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_not_whitelisted"),
		NOT_WHITELISTED_BY_TARGET(-13, TransporterInfo.FeedbackType.MAJOR_ERROR, "not_whitelisted_by_target"),
		TARGET_BLACKLISTED(-14, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_blacklisted"),
		BLACKLISTED_BY_TARGET(-15, TransporterInfo.FeedbackType.MAJOR_ERROR, "blacklisted_by_target"),
		
		TARGET_OUT_OF_RANGE(-16, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_out_of_range"),
		OUT_OF_RANGE_OF_TARGET(-17, TransporterInfo.FeedbackType.MAJOR_ERROR, "out_of_range_of_target"),
		SELF_NO_INTERDIMENSIONAL_TRANSPORT(-18, TransporterInfo.FeedbackType.MAJOR_ERROR, "self_no_interdimensional_transport"),
		TARGET_NO_INTERDIMENSIONAL_TRANSPORT(-19, TransporterInfo.FeedbackType.MAJOR_ERROR, "target_no_interdimensional_transport"),
		NO_INTERSTELLAR_TRANSPORT(-20, TransporterInfo.FeedbackType.MAJOR_ERROR, "no_interstellar_transport"),
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, TransporterInfo.FeedbackType.INFO, "connection_ended.disconnect"),
		CONNECTION_ENDED_BY_NETWORK(8, TransporterInfo.FeedbackType.INFO, "connection_ended.transporter_network"),
		CONNECTION_NOT_FINISHED(-21, TransporterInfo.FeedbackType.ERROR, "connection_not_finished"), //TODO Is this even necessary?
		
		TRANSPORTER_DESTROYED(-22, TransporterInfo.FeedbackType.ERROR, "transporter_destroyed"),
		COULD_NOT_REACH_TARGET_TRANSPORTER(-23, TransporterInfo.FeedbackType.MAJOR_ERROR, "could_not_reach_target_transporter"),
		INTERRUPTED_BY_INCOMING_CONNECTION(-24, TransporterInfo.FeedbackType.ERROR, "interrupted_by_incoming_connection"), //TODO
		
		TARGET_NOT_LOADED(-25, TransporterInfo.FeedbackType.ERROR, "target_not_loaded");
		
		private final int code;
		private final TransporterInfo.FeedbackType type;
		private final String message;
		
		Feedback(int code, TransporterInfo.FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
		}
		
		public int getCode()
		{
			return this.code;
		}
		
		public String getMessage()
		{
			return this.message;
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
		
		public FeedbackMessage withInfo(Object... additionalInfo)
		{
			return new FeedbackMessage(this, additionalInfo);
		}
	}
	
	public record FeedbackMessage(Feedback feedback, Object... additionalInfo)
	{
		public Component getMessageComponent()
		{
			if(feedback().isError())
				return createError(feedback().message, feedback().type == FeedbackType.MAJOR_ERROR, additionalInfo());
			else
				return createInfo(feedback().message, additionalInfo());
		}
		
		@Override
		public @NotNull String toString()
		{
			StringBuilder message = new StringBuilder(feedback().getMessage());
			
			for(Object info : additionalInfo())
			{
				message.append(", ").append(info);
			}
			
			return message.toString();
		}
	}
	
	private static Component createInfo(String feedback, Object... additionalInfo)
	{
		return Component.translatable("message.sgjourney.transporter.info." + feedback, additionalInfo);
	}
	
	private static Component createError(String feedback, boolean majorError, Object... additionalInfo)
	{
		return Component.translatable("message.sgjourney.transporter.error." + feedback, additionalInfo).withStyle(majorError ? ChatFormatting.DARK_RED : ChatFormatting.RED);
	}
}
