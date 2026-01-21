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
		SKIPPABLE_ERROR, // An error that can be skipped during dialing if there are other candidates for target Stargate
		MAJOR_ERROR;
		
		public boolean isError()
		{
			return this != INFO;
		}
		
		public boolean shouldPlaySound()
		{
			return this == MAJOR_ERROR || this == SKIPPABLE_ERROR;
		}
	}
	
	public enum Feedback
	{
		NONE(0, TransporterInfo.FeedbackType.INFO, "none"),
		UNKNOWN_ERROR(-1, TransporterInfo.FeedbackType.ERROR, "unknown");
		
		private int code;
		private final TransporterInfo.FeedbackType type;
		private final String message;
		private final Component feedbackMessage;
		
		Feedback(int code, TransporterInfo.FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
			
			if(type.isError())
				this.feedbackMessage = createError(message, type == TransporterInfo.FeedbackType.MAJOR_ERROR || type == TransporterInfo.FeedbackType.SKIPPABLE_ERROR);
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
		
		public boolean isSkippable()
		{
			return this.type == TransporterInfo.FeedbackType.SKIPPABLE_ERROR;
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
