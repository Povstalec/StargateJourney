package net.povstalec.sgjourney.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerAddressConfig
{
	public static ForgeConfigSpec.BooleanValue generate_random_addresses;
	public static ForgeConfigSpec.BooleanValue use_datapack_addresses;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Address Config");
		
		use_datapack_addresses = server
				.comment("Stargate Journey will use addresses from datapacks")
				.define("server.use_datapack_addresses", true);
		
		generate_random_addresses = server
				.comment("Stargate Journey will generate random addresses for each world.")
				.define("server.generate_random_addresses", false);
	}
}
