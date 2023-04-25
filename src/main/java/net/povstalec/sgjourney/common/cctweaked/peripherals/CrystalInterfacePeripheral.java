package net.povstalec.sgjourney.common.cctweaked.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.CrystalInterfaceEntity;

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
		return "crystal_interface";
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		return this.getClass() == other.getClass() && this.crystalInterface == ((CrystalInterfacePeripheral) other).crystalInterface;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public String getLocalAddress()
	{
		return crystalInterface.getLocalAddress();
	}
}
