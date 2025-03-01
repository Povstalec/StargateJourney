package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.RotatingStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.RotatingStargateFunctions;

public class RotationMethods
{
	public static class GetCurrentSymbol implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getCurrentSymbol";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(RotatingStargateFunctions.getCurrentSymbol(stargate));
		}
	}
	
	public static class IsCurrentSymbol implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "isCurrentSymbol";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(RotatingStargateFunctions.isCurrentSymbol(stargate, arguments.getInt(0)));
		}
	}
	
	public static class EncodeChevron implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "encodeChevron";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.encodeChevron(stargate)));
		}
	}
	
	public static class GetRotation implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getRotation";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(RotatingStargateFunctions.getRotation(stargate));
		}
	}
	
	public static class GetRotationDegrees implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getRotationDegrees";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(RotatingStargateFunctions.getRotationDegrees(stargate));
		}
	}
	
	public static class RotateClockwise implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateClockwise";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				return StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.rotateClockwise(stargate, desiredSymbol));
			});
			
			return result;
		}
	}
	
	public static class RotateAntiClockwise implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateAntiClockwise";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				return StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.rotateAntiClockwise(stargate, desiredSymbol));
			});
			
			return result;
		}
	}
	
	public static class EndRotation implements InterfaceMethod<RotatingStargateEntity>
	{
		@Override
		public String getName()
		{
			return "endRotation";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, RotatingStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.endRotation(stargate)));
		}
	}
}
