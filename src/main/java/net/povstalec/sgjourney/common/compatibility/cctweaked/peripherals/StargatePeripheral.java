package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericStargateFunctions;

import javax.annotation.Nullable;

public class StargatePeripheral extends InterfacePeripheral
{
	@Nullable
	protected AbstractStargateEntity<?> stargate;
	
	public StargatePeripheral(AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity<?> stargate)
	{
		super(interfaceEntity);
		this.stargate = stargate;
		
		stargate.registerInterfaceMethods(new SGJourneyPeripheralWrapper<>(this, interfaceEntity.getInterfaceType()));
	}
	
	@Override
	public void markInvalid()
	{
		this.stargate = null;
	}
	
	protected AbstractStargateEntity<?> getStargateOrThrow() throws LuaException
	{
		if(stargate == null)
			throw new LuaException("Stargate Peripheral is no longer valid!");
		
		return stargate;
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(computer, context, this.interfaceEntity, getStargateOrThrow(), arguments);
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
	public final int getStargateGeneration() throws LuaException
	{
		return GenericStargateFunctions.getStargateGeneration(getStargateOrThrow());
	}
	
	@LuaFunction
	public final String getStargateType() throws LuaException
	{
		return GenericStargateFunctions.getStargateType(getStargateOrThrow());
	}
	
	@LuaFunction
	public final boolean isStargateConnected() throws LuaException
	{
		return GenericStargateFunctions.isStargateConnected(getStargateOrThrow());
	}
	
	@LuaFunction
	public final boolean isStargateDialingOut() throws LuaException
	{
		return GenericStargateFunctions.isStargateDialingOut(getStargateOrThrow());
	}
	
	@LuaFunction
	public final boolean isWormholeOpen() throws LuaException
	{
		return GenericStargateFunctions.isWormholeOpen(getStargateOrThrow());
	}
	
	@LuaFunction
	public final long getStargateEnergy() throws LuaException
	{
		return GenericStargateFunctions.getStargateEnergy(getStargateOrThrow());
	}
	
	@LuaFunction
	public final int getChevronsEngaged() throws LuaException
	{
		return GenericStargateFunctions.getChevronsEngaged(getStargateOrThrow());
	}
	
	@LuaFunction
	public final int getOpenTime() throws LuaException
	{
		return GenericStargateFunctions.getOpenTime(getStargateOrThrow());
	}
	
	@LuaFunction
	public final MethodResult disconnectStargate(ILuaContext context) throws LuaException
	{
		return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.disconnectStargate(getStargateOrThrow())});
	}
}
