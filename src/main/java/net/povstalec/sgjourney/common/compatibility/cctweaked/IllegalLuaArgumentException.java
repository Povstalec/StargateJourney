package net.povstalec.sgjourney.common.compatibility.cctweaked;

import dan200.computercraft.api.lua.LuaException;

import javax.annotation.Nullable;

public class IllegalLuaArgumentException extends LuaException
{
	public IllegalLuaArgumentException(@Nullable String message)
	{
		super(message);
	}
	
	public IllegalLuaArgumentException(@Nullable String message, int level)
	{
		super(message, level);
	}
	
	@Override
	public Throwable fillInStackTrace()
	{
		return this;
	}
}
