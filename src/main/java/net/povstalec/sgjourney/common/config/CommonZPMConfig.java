package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonZPMConfig
{
	public static ForgeConfigSpec.LongValue zpm_energy_per_level_of_entropy;
	public static ForgeConfigSpec.LongValue zpm_hub_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey ZPM Config");
		
		// ZPM Energy
		zpm_energy_per_level_of_entropy = server
				.comment("The energy that can be extracted from a single level of entropy")
				.defineInRange("server.zpm_energy_per_level_of_entropy", 100000000000L, 1L, 9223372036854775807L);

		zpm_hub_max_transfer = server
				.comment("Maximum amount of energy that can be transferred from the ZPM Hub in one tick")
				.defineInRange("server.zpm_hub_max_transfer", 100000000000L, 1L, 9223372036854775807L);
	}
}
