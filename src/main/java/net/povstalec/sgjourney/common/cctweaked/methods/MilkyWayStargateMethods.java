package net.povstalec.sgjourney.common.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class MilkyWayStargateMethods
{
	public static class GetCurrentSymbol implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getCurrentSymbol";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(stargate.getCurrentSymbol());
		}
	}
	
	public static class IsCurrentSymbol implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "isCurrentSymbol";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int symbol = arguments.getInt(0);
			
			return MethodResult.of(stargate.isCurrentSymbol(symbol));
		}
	}
	
	public static class GetRotation implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getRotation";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(stargate.getRotation());
		}
	}
	
	public static class RotateClockwise implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateClockwise";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			if(desiredSymbol < -1 || desiredSymbol > 38)
				throw new LuaException("Symbol out of bounds <-1, 38>");
			
			context.executeMainThreadTask(() ->
			{
				stargate.startRotation(desiredSymbol, true);
				return null;
			});
			
			return MethodResult.of();
		}
	}
	
	public static class RotateAntiClockwise implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateAntiClockwise";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			if(desiredSymbol < -1 || desiredSymbol > 38)
				throw new LuaException("Symbol out of bounds <-1, 38>");
			
			context.executeMainThreadTask(() ->
			{
				stargate.startRotation(desiredSymbol, false);
				return null;
			});
			
			return MethodResult.of();
		}
	}
	
	public static class EndRotation implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "endRotation";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			context.executeMainThreadTask(() ->
			{
				stargate.endRotation();
				return null;
			});
			
			return MethodResult.of();
		}
	}
	
	public static class RaiseChevron implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "raiseChevron";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.raiseChevron().getCode()};
			});
			
			return result;
		}
	}
	
	public static class LowerChevron implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "lowerChevron";
		}

		@Override
		public MethodResult use(ILuaContext context, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.lowerChevron().getCode()};
			});
			
			return result;
		}
	}
}
