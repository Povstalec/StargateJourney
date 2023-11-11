package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface InterfaceMethod<ConnectedBlockEntity extends BlockEntity>
{
	public String getName();
	
	public MethodResult use(IComputerAccess computer, ILuaContext context, ConnectedBlockEntity blockEntity, IArguments arguments) throws LuaException;
}
