package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.TransceiverFunctions;

public class TransceiverMethods
{
	public static class SetFrequency implements TransceiverMethod
	{
		@Override
		public String getName()
		{
			return "setFrequency";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, TransceiverEntity transceiver, IArguments arguments) throws LuaException
		{
			TransceiverFunctions.setFrequency(transceiver, arguments.getInt(0));
			
			return MethodResult.of();
		}
	}
	
	public static class SetCurrentCode implements TransceiverMethod
	{
		@Override
		public String getName()
		{
			return "setCurrentCode";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, TransceiverEntity transceiver, IArguments arguments) throws LuaException
		{
			TransceiverFunctions.setCurrentCode(transceiver, arguments.getString(0));
			
			return MethodResult.of();
		}
	}
	
	public static class SendTransmission implements TransceiverMethod
	{
		@Override
		public String getName()
		{
			return "sendTransmission";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, TransceiverEntity transceiver, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() ->
			{
				TransceiverFunctions.sendTransmission(transceiver);
				return new Object[] {true};
			});
		}
	}
	
	public static class CheckConnectedShielding implements TransceiverMethod
	{
		@Override
		public String getName()
		{
			return "checkConnectedShielding";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, TransceiverEntity transceiver, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {TransceiverFunctions.checkConnectedShielding(transceiver)});
		}
	}
}
