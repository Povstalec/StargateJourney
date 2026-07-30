package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericTransporterFunctions;

import javax.annotation.Nullable;

public class TransporterPeripheral extends InterfacePeripheral
{
	@Nullable
	protected AbstractTransporterEntity<?> transporter;
	
	public TransporterPeripheral(AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter)
	{
		super(interfaceEntity, transporter);
		this.transporter = transporter;
		
		transporter.registerInterfaceMethods(new SGJourneyPeripheralWrapper<>(this, interfaceEntity.getInterfaceType()));
	}
	
	public void markInvalid()
	{
		this.transporter = null;
	}
	
	protected AbstractTransporterEntity<?> getTransporterOrThrow() throws LuaException
	{
		if(transporter == null)
			throw new LuaException("Transporter Peripheral is no longer valid!");
		
		return transporter;
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(computer, context, this.interfaceEntity, getTransporterOrThrow(), arguments);
	}
	
	@Override
	public boolean equals(IPeripheral other)
	{
		if(!super.equals(other))
			return false;
		
		return this.transporter == ((TransporterPeripheral) other).transporter;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final String getTransporterType() throws LuaException
	{
		return GenericTransporterFunctions.getTransporterType(getTransporterOrThrow());
	}
	
	@LuaFunction
	public final boolean isTransporterConnected() throws LuaException
	{
		return GenericTransporterFunctions.isTransporterConnected(getTransporterOrThrow());
	}
	
	@LuaFunction
	public final long getTransporterEnergy() throws LuaException
	{
		return GenericTransporterFunctions.getTransporterEnergy(getTransporterOrThrow());
	}
}
