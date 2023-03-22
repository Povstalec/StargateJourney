package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;

public class BasicStargatePeripheral extends BasicInterfacePeripheral
{
	protected AbstractStargateEntity stargate;
	
	public BasicStargatePeripheral(BasicInterfaceEntity basicInterface, AbstractStargateEntity stargate)
	{
		super(basicInterface);
		this.stargate = stargate;
	}
	
	@Override
	public String getType() //TODO
	{
		return "basic_interface";
	}

	@Override
	public boolean equals(IPeripheral other) //TODO
	{
		if(this == other)
			return true;
		
		if(other instanceof BasicStargatePeripheral peripheral)
		{
			if(peripheral.stargate == this.stargate && peripheral.basicInterface == this.basicInterface)
				return true;
		}
		
		return false;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	/*@LuaFunction //TODO
	public final boolean isConnectedToStargate()
	{
		return basicInterface.isConnectedToStargate();
	}*/
	
	@LuaFunction
	public final long getStargateEnergy()
	{
		return stargate.getEnergyStored();
	}
	
	@LuaFunction
	public final int getChevronsEngaged()
	{
		return stargate.getChevronsEngaged();
	}
	
	@LuaFunction
	public final int getOpenTime()
	{
		return stargate.getOpenTime();
	}
	
	@LuaFunction
	public final void disconnectStargate()
	{
		stargate.disconnectStargate();
	}
}
