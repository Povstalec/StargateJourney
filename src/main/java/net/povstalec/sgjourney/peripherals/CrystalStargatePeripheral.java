package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;

public class CrystalStargatePeripheral extends BasicStargatePeripheral
{
	protected CrystalInterfaceEntity crystalInterface;
	
	public CrystalStargatePeripheral(CrystalInterfaceEntity crystalInterface, AbstractStargateEntity stargate)
	{
		super(crystalInterface, stargate);
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
		
		if(other instanceof CrystalStargatePeripheral peripheral)
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
	public void inputSymbol(ILuaContext context, int symbol) throws LuaException
	{
		//This needs to be executed on the main thread, otherwise you won't be able to dial
		context.executeMainThreadTask(() ->
		{
			stargate.engageSymbol(symbol);
			return null;
		});
	}
}
