package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;

public class InterfacePeripheral implements IPeripheral
{
	protected AbstractInterfaceEntity interfaceEntity;
	
	public InterfacePeripheral(AbstractInterfaceEntity interfaceEntity)
	{
		this.interfaceEntity = interfaceEntity;
	}
	
	@Override
	public String getType()
	{
		return interfaceEntity.getInterfaceType().getName();
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		return this.getClass() == other.getClass() && this.interfaceEntity == ((InterfacePeripheral) other).interfaceEntity;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final long getEnergy() throws LuaException
	{
		return interfaceEntity.getEnergyStored();
	}
	
	@LuaFunction
	public final long getEnergyTarget() throws LuaException
	{
		return interfaceEntity.getEnergyTarget();
	}
	
	//TODO
	/*@LuaFunction
	public final long setEnergyTarget() throws LuaException
	{
		return interfaceEntity.setEnergyTarget();
	}*/
}
