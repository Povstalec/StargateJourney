package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;

public class MilkyWayStargatePeripheral extends BasicStargatePeripheral
{
	private MilkyWayStargateEntity stargate;
	
	public MilkyWayStargatePeripheral(BasicInterfaceEntity basicInterface, MilkyWayStargateEntity stargate)
	{
		super(basicInterface, (AbstractStargateEntity) stargate);
		this.stargate = stargate;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final void raiseChevron() throws LuaException
	{
		if(!stargate.raiseChevron())
			throw new LuaException("Stargate cannot raise chevron");
	}
	
	@LuaFunction
	public final void lowerChevron(ILuaContext context) throws LuaException
	{
		//This needs to be executed on the main thread, otherwise you won't be able to dial
		context.executeMainThreadTask(() ->
		{
			if(!stargate.lowerChevron())
				throw new LuaException("Stargate cannot lower chevron");
			return null;
		});
	}
	
	@LuaFunction
	public final void rotateClockwise(ILuaContext context, int symbol) throws LuaException
	{
		context.executeMainThreadTask(() ->
		{
			setStargateRotation(true, symbol);
			return null;
		});
	}
	
	@LuaFunction
	public final void rotateAntiClockwise(ILuaContext context, int symbol) throws LuaException
	{
		context.executeMainThreadTask(() ->
		{
			setStargateRotation(false, symbol);
			return null;
		});
	}
	
	@LuaFunction
	public final boolean isCurrentSymbol(int symbol)
	{
		return stargate.isCurrentSymbol(symbol);
	}
	
	@LuaFunction
	public final int getCurrentSymbol()
	{
		return stargate.getCurrentSymbol();
	}
	
	@LuaFunction
	public final int getRotationDegrees()
	{
		return stargate.getRotation();
	}
	
	private void setStargateRotation(boolean clockwise, int symbol)
	{
		this.basicInterface.rotateStargate(clockwise, symbol);
	}
}
