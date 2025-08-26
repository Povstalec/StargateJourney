package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;
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
			double frequency = arguments.getDouble(0);
			if(frequency > Integer.MAX_VALUE || frequency < Integer.MIN_VALUE)
				throw new LuaException("Frequency " + frequency + " out of range for <" + Integer.MIN_VALUE + ", " + Integer.MAX_VALUE + ">");
			TransceiverFunctions.setFrequency(transceiver, (int) frequency);
			
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
			String code = arguments.getString(0);
			if(code.length() > 1024)
				return MethodResult.of(false);
			
			TransceiverFunctions.setCurrentCode(transceiver, code);
			
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
