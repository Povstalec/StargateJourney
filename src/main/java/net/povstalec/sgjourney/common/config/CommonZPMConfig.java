package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonZPMConfig
{
	public static ForgeConfigSpec.BooleanValue zpm_has_energy_capability;
	
	public static ForgeConfigSpec.IntValue zpm_max_entropy;
	public static ForgeConfigSpec.LongValue zpm_energy_per_entropy_level;
	
	public static ForgeConfigSpec.LongValue zpm_plug_max_transfer;
	public static ForgeConfigSpec.LongValue zpm_port_max_transfer;
	public static ForgeConfigSpec.LongValue zpm_hub_max_transfer;
	
	public static ForgeConfigSpec.BooleanValue dhd_holds_zpm;
	public static ForgeConfigSpec.BooleanValue stargates_use_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue tech_uses_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue other_mods_use_zero_point_energy;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		zpm_has_energy_capability = server
			.comment("If true, the ZPM Item will expose an Energy Capability that other mods can use to directly drain energy from it")
			.define("server.zpm_has_energy_capability", false);
		
		
		
		zpm_max_entropy = server
			.comment("The maximum amount of entropy the ZPM can reach before being depleted")
			.defineInRange("server.zpm_max_entropy", 1_000, 1, Integer.MAX_VALUE);
		
		zpm_energy_per_entropy_level = server
				.comment("The energy that can be extracted from a single level of entropy")
				.defineInRange("server.zpm_energy_per_entropy_level", 100_000_000_000L, 1L, Long.MAX_VALUE);
		
		
		
		zpm_plug_max_transfer = server
			.comment("Maximum amount of energy that can be transferred from the ZPM Plug in one tick")
			.defineInRange("server.zpm_plug_max_transfer", 1_000_000_000L, 1L, Long.MAX_VALUE);
		
		zpm_port_max_transfer = server
			.comment("Maximum amount of energy that can be transferred from the ZPM Port in one tick")
			.defineInRange("server.zpm_port_max_transfer", 10_000_000_000L, 1L, Long.MAX_VALUE);

		zpm_hub_max_transfer = server
				.comment("Maximum amount of energy that can be transferred from the ZPM Hub in one tick")
				.defineInRange("server.zpm_hub_max_transfer", 100_000_000_000L, 1L, Long.MAX_VALUE);
		
		
		
		dhd_holds_zpm = server
			.comment("If true, DHDs will be able to hold ZPMs and extract energy from them in order to power Stargates")
			.define("server.dhd_holds_zpm", true);
		
		
		
		stargates_use_zero_point_energy = server
				.comment("If true, it will be possible to power Stargates (and by extension Interfaces and DHDs) with energy from ZPMs")
				.define("server.stargates_use_zero_point_energy", true);
		
		tech_uses_zero_point_energy = server
				.comment("If true, it will be possible to power Stargate Journey technology with energy from ZPMs")
				.define("server.tech_uses_zero_point_energy", true);
		
		other_mods_use_zero_point_energy = server
				.comment("If true, it will be possible to power technology and cables from other mods with energy from ZPMs")
				.define("server.other_mods_use_zero_point_energy", true);
	}
}
