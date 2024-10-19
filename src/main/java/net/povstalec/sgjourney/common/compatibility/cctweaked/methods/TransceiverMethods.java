package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;

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
			int frequency = arguments.getInt(0);
			transceiver.setFrequency(frequency);
			
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
			String message = arguments.getString(0);
			transceiver.setCurrentCode(message);
			
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
			context.executeMainThreadTask(() ->
			{
				transceiver.sendTransmission();
				
				return new Object[] {};
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				int state = transceiver.checkShieldingState();
				
				if(state < 0)
					return new Object[] {null};
				
				return new Object[] {state};
			});
			
			return result;
		}
	}
}
