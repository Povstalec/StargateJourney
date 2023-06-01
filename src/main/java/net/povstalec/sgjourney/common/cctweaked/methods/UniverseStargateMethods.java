package net.povstalec.sgjourney.common.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;

public class UniverseStargateMethods
{
	public static class EngageSymbol implements InterfaceMethod<UniverseStargateEntity>
	{
		@Override
		public String getName()
		{
			return "engageSymbol";
		}

		@Override
		public MethodResult use(ILuaContext context, UniverseStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			if(desiredSymbol < 0 || desiredSymbol > 35)
				throw new LuaException("Symbol out of bounds <0, 35>");
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.engageSymbol(desiredSymbol)};
			});
			
			return result;
		}
	}
}
