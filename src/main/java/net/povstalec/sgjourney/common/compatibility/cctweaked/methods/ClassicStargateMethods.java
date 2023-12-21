package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;

public class ClassicStargateMethods
{
	public static class EngageSymbol implements InterfaceMethod<ClassicStargateEntity>
	{
		@Override
		public String getName()
		{
			return "engageSymbol";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, ClassicStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				int feedback = stargate.engageSymbol(desiredSymbol).getCode();
				return new Object[] {feedback};
			});
			
			return result;
		}
	}
}
