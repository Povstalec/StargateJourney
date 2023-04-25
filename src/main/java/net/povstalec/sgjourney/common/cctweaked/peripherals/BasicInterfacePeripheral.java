package net.povstalec.sgjourney.common.cctweaked.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.BasicInterfaceEntity;

public class BasicInterfacePeripheral implements IPeripheral
{
	protected BasicInterfaceEntity basicInterface;
	
	public BasicInterfacePeripheral(BasicInterfaceEntity basicInterface)
	{
		this.basicInterface = basicInterface;
	}
	
	@Override
	public String getType()
	{
		return "basic_interface";
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		return this.getClass() == other.getClass() && this.basicInterface == ((BasicInterfacePeripheral) other).basicInterface;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final long getEnergy() throws LuaException
	{
		return basicInterface.getEnergyStored();
	}
}
