package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;

public class CrystalInterfacePeripheral extends BasicInterfacePeripheral
{
	protected CrystalInterfaceEntity crystalInterface;
	
	public CrystalInterfacePeripheral(CrystalInterfaceEntity crystalInterface)
	{
		super(crystalInterface);
		this.crystalInterface = crystalInterface;
	}
	
	@Override
	public String getType()
	{
		return "advanced_interface";
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		if(other instanceof CrystalInterfacePeripheral peripheral)
		{
			if(peripheral.crystalInterface == this.crystalInterface)
				return true;
		}
		
		return false;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public void inputSymbol(int symbol) throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		crystalInterface.inputSymbol(symbol);
	}
}
