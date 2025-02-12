package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;

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
				stargate.dynamicSymbols(dynamicSymbols);
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
			ResourceLocation symbols = ResourceLocation.tryParse(arguments.getString(0));
			
			if(symbols != null)
			{
				context.executeMainThreadTask(() ->
				{
					stargate.symbolInfo().setSymbols(symbols);
					return null;
				});
			}
			
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
			ResourceLocation pointOfOrigin = ResourceLocation.tryParse(arguments.getString(0));
			
			if(pointOfOrigin != null)
			{
				context.executeMainThreadTask(() ->
				{
					stargate.symbolInfo().setPointOfOrigin(pointOfOrigin);
					return null;
				});
			}
			
			return MethodResult.of();
		}
	}
}
