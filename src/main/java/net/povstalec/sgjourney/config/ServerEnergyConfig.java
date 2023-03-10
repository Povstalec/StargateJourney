package net.povstalec.sgjourney.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerEnergyConfig
{
	public static ForgeConfigSpec.BooleanValue disable_energy_use;

	public static ForgeConfigSpec.LongValue zpm_energy_per_level_of_entropy;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Energy Config");
		
		// General Energy
		disable_energy_use = server
				.comment("Disable energy requirements for blocks added by Stargate Journey")
				.define("server.disable_energy_use", true);
		
		// ZPM Energy
		
		zpm_energy_per_level_of_entropy = server
				.comment("The energy that can be extracted from a single level of entropy")
				.defineInRange("server.zpm_energy_per_level_of_entropy", 1000000000000000000L, 1L, 9223372036854775807L);
	}
}
