package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.util.StringRepresentable;

public enum Receiving implements StringRepresentable
{
	FALSE("false", 0),
	RECEIVING_INCORRECT("receiving_incorrect", 1),
	RECEIVING_CORRECT("receiving_correct", 15);
	
	private String name;
	private int redstonePower;
	
	Receiving(String name, int redstonePower)
	{
		this.name = name;
		this.redstonePower = redstonePower;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.name;
	}

	public int getRedstonePower()
	{
		return this.redstonePower;
	}
}
