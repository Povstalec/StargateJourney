package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.PegasusStargateFunctions;

public class PegasusStargateMethods
{
	public static class DynamicSymbols implements InterfaceMethod<PegasusStargateEntity>
	{
		@Override
		public String getName()
		{
			return "dynamicSymbols";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, PegasusStargateEntity stargate, IArguments arguments) throws LuaException
		{
			boolean dynamicSymbols = arguments.getBoolean(0);
			return context.executeMainThreadTask(() -> new Object[] {PegasusStargateFunctions.dynamicSymbols(stargate, dynamicSymbols)});
		}
	}
	
	public static class OverrideSymbols implements InterfaceMethod<PegasusStargateEntity>
	{
		@Override
		public String getName()
		{
			return "overrideSymbols";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, PegasusStargateEntity stargate, IArguments arguments) throws LuaException
		{
			String symbols = arguments.getString(0);
			return context.executeMainThreadTask(() -> new Object[] {PegasusStargateFunctions.overrideSymbols(stargate, symbols)});
		}
	}
	
	public static class OverridePointOfOrigin implements InterfaceMethod<PegasusStargateEntity>
	{
		@Override
		public String getName()
		{
			return "overridePointOfOrigin";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, PegasusStargateEntity stargate, IArguments arguments) throws LuaException
		{
			String pointOfOrigin = arguments.getString(0);
			return context.executeMainThreadTask(() -> new Object[] {PegasusStargateFunctions.overridePointOfOrigin(stargate, pointOfOrigin)});
		}
	}
}
