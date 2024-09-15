package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;

public interface TransceiverMethod
{
	public String getName();
	
	public MethodResult use(IComputerAccess computer, ILuaContext context, TransceiverEntity transceiver, IArguments arguments) throws LuaException;
}
