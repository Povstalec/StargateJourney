package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.MilkyWayStargateFunctions;
import net.povstalec.sgjourney.common.compatibility.computer_functions.RotatingStargateFunctions;

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
				else if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				return StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.rotateClockwise(stargate, desiredSymbol));
			});
			
			return result;
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
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, MilkyWayStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(stargate.isChevronOpen())
					throw new LuaException("Can't rotate while chevron is open");
				else if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
					throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");
				
				return StargateMethods.returnedFeedback(interfaceEntity, RotatingStargateFunctions.rotateAntiClockwise(stargate, desiredSymbol));
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
			return context.executeMainThreadTask(() -> StargateMethods.returnedFeedback(interfaceEntity, MilkyWayStargateFunctions.openChevron(stargate)));
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
			return context.executeMainThreadTask(() -> StargateMethods.returnedFeedback(interfaceEntity, MilkyWayStargateFunctions.closeChevron(stargate)));
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
			return context.executeMainThreadTask(() -> new Object[] {MilkyWayStargateFunctions.isChevronOpen(stargate)});
		}
	}
}
