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
			int freq = arguments.getInt(0);
			TransceiverFunctions.setFrequency(transceiver, freq);
			
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
			String idc = arguments.getString(0);
			if (idc.length() > 1024)
				return MethodResult.of(false);
			TransceiverFunctions.setCurrentCode(transceiver, idc);
			
			return MethodResult.of(true);
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
			context.executeMainThreadTask(() ->
			{
				TransceiverFunctions.sendTransmission(transceiver);
				return null;
			});
			
			return MethodResult.of();
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
			MethodResult result = context.executeMainThreadTask(() -> new Object[] {TransceiverFunctions.checkConnectedShielding(transceiver)});
			
			return result;
		}
	}
}
