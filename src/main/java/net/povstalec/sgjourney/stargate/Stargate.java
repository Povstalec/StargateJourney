package net.povstalec.sgjourney.stargate;

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
		NONE(""),
		NO_POWER("Stargate does not have enough power to estabilish a connection"),
		RAN_OUT_OF_POWER("Stargate ran out of power"),
		MAX_CONNECTION_TIME("Stargate exceeded maximum connection time"),
		WRONG_DISCONNECT_SIDE("Cannot disconnect Stargate from this side"),
		ALREADY_CONNECTED("Stargate is already connected"),
		OBSTRUCTED("Stargate is obstructed");
		
		private final String feedbackMessage;
		
		private Feedback(String feedbackMessage)
		{
			this.feedbackMessage = feedbackMessage;
		}
		
		public String getFeedbackMessage()
		{
			return this.feedbackMessage;
		}
	}
}
