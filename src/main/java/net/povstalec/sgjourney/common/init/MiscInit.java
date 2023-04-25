package net.povstalec.sgjourney.common.init;

import net.minecraftforge.event.RegisterCommandsEvent;

public class MiscInit
{
	public static void registerCommands(RegisterCommandsEvent event)
	{
		CommandInit.register(event.getDispatcher());
	}
}
