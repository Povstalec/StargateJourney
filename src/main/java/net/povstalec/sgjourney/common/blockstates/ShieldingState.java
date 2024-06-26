package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.util.StringRepresentable;

public enum ShieldingState implements StringRepresentable
{
	OPEN("open", (short) 0, (short) 0),
	
	MOVING_1("moving_1", (short) 1, (short) 24),
	MOVING_2("moving_2", (short) 2, (short)36),
	MOVING_3("moving_3", (short) 3, (short) 44),
	MOVING_4("moving_4", (short) 4, (short) 53),
	
	CLOSED("closed", (short) 5, (short) 58);
	
	public static final short MAX_PROGRESS = CLOSED.getProgress();
	
	private final String name;
	private final short number;
	private final short progress;
	
	private ShieldingState(String name, short number, short progress)
	{
		this.name = name;
		this.number = number;
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
		return this.number < other.number;
	}
	
	public boolean isAfter(ShieldingState other)
	{
		return other.number < this.number;
	}
}
