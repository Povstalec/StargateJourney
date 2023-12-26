package net.povstalec.sgjourney.common.stargate;

import net.minecraft.util.StringRepresentable;

public enum ConnectionState implements StringRepresentable
{
	IDLE("idle", false, false),
	
	OUTGOING_CONNECTION("outgoing_connection", true, true),
	INCOMING_CONNECTION("incoming_connection", true, false);
	
	private final String name;
	private final boolean isConnected;
	private final boolean isDialingOut;
	
	private ConnectionState(String name, boolean isConnected, boolean isDialingOut)
	{
		this.name = name;
		this.isConnected = isConnected;
		this.isDialingOut = isDialingOut;
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
	
	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	public boolean isDialingOut()
	{
		return this.isDialingOut;
	}

}
