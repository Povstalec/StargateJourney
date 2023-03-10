package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;

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
	public final boolean isConnectedToStargate()
	{
		return basicInterface.isConnectedToStargate();
	}
	
	@LuaFunction
	public final long getStargateEnergy() throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		return basicInterface.getStargate().getEnergyStored();
	}
	
	@LuaFunction
	public final void raiseChevron() throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		if(!basicInterface.raiseChevron())
			throw new LuaException("Stargate cannot raise chevron");
	}
	
	@LuaFunction
	public final void lowerChevron() throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		if(!basicInterface.lowerChevron())
			throw new LuaException("Stargate cannot lower chevron");
			
	}
	
	@LuaFunction
	public final void rotateClockwise(int symbol) throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		AbstractStargateEntity stargate = basicInterface.getStargate();
		
		if(stargate instanceof MilkyWayStargateEntity)
			basicInterface.rotateStargate(true, symbol);
		else
			throw new LuaException("Stargate cannot rotate");
	}
	
	@LuaFunction
	public final void rotateAntiClockwise(int symbol) throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		AbstractStargateEntity stargate = basicInterface.getStargate();
		
		if(stargate instanceof MilkyWayStargateEntity)
			basicInterface.rotateStargate(false, symbol);
		else
			throw new LuaException("Stargate cannot rotate");
	}
	
	@LuaFunction
	public final int getChevronsEngaged() throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		return basicInterface.getChevronsEngaged();
	}
	
	@LuaFunction
	public final int getOpenTime() throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		return basicInterface.getOpenTime();
	}
	
	@LuaFunction
	public final boolean isCurrentSymbol(int symbol) throws LuaException
	{
		if(!isConnectedToStargate())
			throw new LuaException("Interface is not connected to a Stargate");
		
		AbstractStargateEntity stargate = basicInterface.getStargate();
		
		if(stargate instanceof MilkyWayStargateEntity)
			basicInterface.isCurrentSymbol(symbol);
		else
			throw new LuaException("Stargate cannot rotate");
		
		return false;
	}
}
