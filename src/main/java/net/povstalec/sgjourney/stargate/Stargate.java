package net.povstalec.sgjourney.stargate;

import net.minecraft.network.chat.Component;

public class Stargate
{
	public enum Gen
	{
		GEN_0(0),
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
	
	public enum FilterType
	{
		NONE,
		WHITELIST,
		BLACKLIST;
	}
	
	public enum Feedback
	{
		NONE(0, ""),
		NO_POWER(1, "Stargate does not have enough power to estabilish a connection"),
		RAN_OUT_OF_POWER(2, "Stargate ran out of power"),
		MAX_CONNECTION_TIME(3, "Stargate exceeded maximum connection time"),
		WRONG_DISCONNECT_SIDE(4, "Cannot disconnect Stargate from this side"),
		ALREADY_CONNECTED(5, "Stargate is already connected"),
		OBSTRUCTED(6, "Stargate is obstructed");
		
		private final int code;
		private final String feedbackMessage;
		
		private Feedback(int code, String feedbackMessage)
		{
			this.code = code;
			this.feedbackMessage = feedbackMessage;
		}
		
		public String getFeedbackMessage()
		{
			return this.feedbackMessage;
		}
	}
}
