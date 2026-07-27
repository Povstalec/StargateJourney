package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericStargateFunctions;

public class StargatePeripheral extends InterfacePeripheral
{
	protected AbstractStargateEntity<?> stargate;
	
	public StargatePeripheral(AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity<?> stargate)
	{
		super(interfaceEntity);
		this.stargate = stargate;
		
		stargate.registerInterfaceMethods(new SGJourneyPeripheralWrapper<>(this, interfaceEntity.getInterfaceType()));
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(computer, context, this.interfaceEntity, this.stargate, arguments);
	}
	
	@Override
	public boolean equals(IPeripheral other)
	{
		if(!super.equals(other))
			return false;
		
		return this.stargate == ((StargatePeripheral) other).stargate;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final int getStargateGeneration()
	{
		return GenericStargateFunctions.getStargateGeneration(stargate);
	}
	
	@LuaFunction
	public final String getStargateType()
	{
		return GenericStargateFunctions.getStargateType(stargate);
	}
	
	@LuaFunction
	public final boolean isStargateConnected()
	{
		return GenericStargateFunctions.isStargateConnected(stargate);
	}
	
	@LuaFunction
	public final boolean isStargateDialingOut()
	{
		return GenericStargateFunctions.isStargateDialingOut(stargate);
	}
	
	@LuaFunction
	public final boolean isWormholeOpen()
	{
		return GenericStargateFunctions.isWormholeOpen(stargate);
	}
	
	@LuaFunction
	public final long getStargateEnergy()
	{
		return GenericStargateFunctions.getStargateEnergy(stargate);
	}
	
	@LuaFunction
	public final int getChevronsEngaged()
	{
		return GenericStargateFunctions.getChevronsEngaged(stargate);
	}
	
	@LuaFunction
	public final int getOpenTime()
	{
		return GenericStargateFunctions.getOpenTime(stargate);
	}
	
	@LuaFunction
	public final MethodResult disconnectStargate(ILuaContext context) throws LuaException
	{
		return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.disconnectStargate(stargate)});
	}
}
