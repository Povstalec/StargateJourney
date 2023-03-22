package net.povstalec.sgjourney.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;

public class BasicInterfacePeripheral implements IPeripheral
{
	protected BasicInterfaceEntity basicInterface;
	List<String> methodNames = new LinkedList<>();
	
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
		
		if(other instanceof BasicInterfacePeripheral peripheral)
		{
			if(peripheral.basicInterface == this.basicInterface)
				return true;
		}
		
		return false;
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
