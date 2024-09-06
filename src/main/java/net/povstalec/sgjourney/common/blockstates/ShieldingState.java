package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.util.StringRepresentable;

public enum ShieldingState implements StringRepresentable
{
	OPEN("open", (short) 0),
	
	MOVING_1("moving_1", (short) 20),
	MOVING_2("moving_2", (short) 29),
	MOVING_3("moving_3", (short) 40),
	MOVING_4("moving_4", (short) 48),
	
	CLOSED("closed", (short) 56);
	
	public static final short MAX_PROGRESS = 58;
	
	private final String name;
	private final short progress;
	
	private ShieldingState(String name, short progress)
	{
		this.name = name;
		this.progress = progress;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.name;
	}
	
	public short getProgress()
	{
		return this.progress;
	}
	
	public boolean progressEquals(short progress)
	{
		return this.progress == progress;
	}
	
	public boolean isBefore(ShieldingState other)
	{
		return this.progress < other.progress;
	}
	
	public boolean isBefore(short otherProgress)
	{
		return this.progress < otherProgress;
	}
	
	public boolean isAfter(ShieldingState other)
	{
		return this.progress > other.progress;
	}
	
	public boolean isAfter(short otherProgress)
	{
		return this.progress > otherProgress;
	}
	
	public static ShieldingState fromProgress(short progress)
	{
		if(progress < MOVING_1.progress)
			return OPEN;
		else if(progress < MOVING_2.progress)
			return MOVING_1;
		else if(progress < MOVING_3.progress)
			return MOVING_2;
		else if(progress < MOVING_4.progress)
			return MOVING_3;
		else if(progress < CLOSED.progress)
			return MOVING_4;
		
		return CLOSED;
	}
}
