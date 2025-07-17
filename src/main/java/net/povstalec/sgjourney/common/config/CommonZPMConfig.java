package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonZPMConfig
{
	public static ForgeConfigSpec.LongValue zpm_energy_per_level_of_entropy;
	public static ForgeConfigSpec.LongValue zpm_hub_max_transfer;
	
	public static ForgeConfigSpec.BooleanValue stargates_use_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue tech_uses_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue other_mods_use_zero_point_energy;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		zpm_energy_per_level_of_entropy = server
				.comment("The energy that can be extracted from a single level of entropy")
				.defineInRange("server.zpm_energy_per_level_of_entropy", 100000000000L, 1L, 9223372036854775807L);

		zpm_hub_max_transfer = server
				.comment("Maximum amount of energy that can be transferred from the ZPM Hub in one tick")
				.defineInRange("server.zpm_hub_max_transfer", 100000000000L, 1L, 9223372036854775807L);
		
		
		
		stargates_use_zero_point_energy = server
				.comment("If true, it will be possible to power Stargates (and by extension Interfaces) with energy from ZPMs")
				.define("server.stargates_use_zero_point_energy", true);
		
		tech_uses_zero_point_energy = server
				.comment("If true, it will be possible to power Stargate Journey technology with energy from ZPMs")
				.define("server.tech_uses_zero_point_energy", true);
		
		other_mods_use_zero_point_energy = server
				.comment("If true, it will be possible to power technology and cables from other mods with energy from ZPMs")
				.define("server.other_mods_use_zero_point_energy", false);
	}
}
