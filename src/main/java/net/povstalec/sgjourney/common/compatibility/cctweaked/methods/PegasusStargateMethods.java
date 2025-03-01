package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
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
			
			context.executeMainThreadTask(() ->
			{
				PegasusStargateFunctions.dynamicSymbols(stargate, dynamicSymbols);
				return null;
			});
			
			return MethodResult.of();
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
			context.executeMainThreadTask(() ->
			{
				PegasusStargateFunctions.overrideSymbols(stargate, arguments.getString(0));
				return null;
			});
			
			return MethodResult.of();
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
			context.executeMainThreadTask(() ->
			{
				PegasusStargateFunctions.overridePointOfOrigin(stargate, arguments.getString(0));
				return null;
			});
			
			return MethodResult.of();
		}
	}
}
