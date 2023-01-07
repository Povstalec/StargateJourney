package net.povstalec.sgjourney.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerStargateConfig
{
	public static ForgeConfigSpec.IntValue max_wormhole_open_time;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Address Config");
		
		max_wormhole_open_time = server
				.comment("The amount of time the Stargate will be open for in seconds <Min: 10, Max: 2280>")
				.defineInRange("server.max_wormhole_open_time", 60, 10, 2280);
	}
}
