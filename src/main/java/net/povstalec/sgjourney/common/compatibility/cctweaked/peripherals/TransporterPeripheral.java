package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericTransporterFunctions;

public class TransporterPeripheral extends InterfacePeripheral
{
	protected AbstractTransporterEntity transporter;
	
	public TransporterPeripheral(AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity transporter)
	{
		super(interfaceEntity);
		this.transporter = transporter;
		
		transporter.registerInterfaceMethods(new SGJourneyPeripheralWrapper<>(this, interfaceEntity.getInterfaceType()));
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(computer, context, this.interfaceEntity, this.transporter, arguments);
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final String getTransporterType()
	{
		return GenericTransporterFunctions.getTransporterType(transporter);
	}
	
	@LuaFunction
	public final boolean isTransporterConnected()
	{
		return GenericTransporterFunctions.isTransporterConnected(transporter);
	}
	
	@LuaFunction
	public final long getTransporterEnergy()
	{
		return GenericTransporterFunctions.getTransporterEnergy(transporter);
	}
}
