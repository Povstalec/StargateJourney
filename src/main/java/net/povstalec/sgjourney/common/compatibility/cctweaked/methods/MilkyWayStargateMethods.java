package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class MilkyWayStargateMethods
{
	public static class RotateClockwise implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateClockwise";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(stargate.isChevronOpen())
					throw new LuaException("Can't rotate while chevron is open");
				else if(desiredSymbol < -1 || desiredSymbol > stargate.totalSymbols())
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				Stargate.Feedback feedback = stargate.startRotation(desiredSymbol, true);
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
		}
	}
	
	public static class RotateCounterClockwise implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "rotateCounterClockwise";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(stargate.isChevronOpen())
					throw new LuaException("Can't rotate while chevron is open");
				else if(desiredSymbol < -1 || desiredSymbol > stargate.totalSymbols())
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				Stargate.Feedback feedback = stargate.startRotation(desiredSymbol, false);
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
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
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Stargate.Feedback feedback = stargate.endRotation(true);
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
		}
	}
	
	public static class OpenChevron implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "openChevron";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Stargate.Feedback feedback = stargate.openChevron();
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
		}
	}
	
	public static class CloseChevron implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "closeChevron";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Stargate.Feedback feedback = stargate.closeChevron();
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
		}
	}
	
	public static class IsChevronOpen implements InterfaceMethod<MilkyWayStargateEntity>
	{
		@Override
		public String getName()
		{
			return "isChevronOpen";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.isChevronOpen()};
			});
			
			return result;
		}
	}
}
