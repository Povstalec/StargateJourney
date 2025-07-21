package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonCableConfig
{
	public static ForgeConfigSpec.LongValue small_naquadah_cable_max_transfer;
	public static ForgeConfigSpec.LongValue medium_naquadah_cable_max_transfer;
	public static ForgeConfigSpec.LongValue large_naquadah_cable_max_transfer;
	
	public static ForgeConfigSpec.BooleanValue small_naquadah_cable_transfers_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue medium_naquadah_cable_transfers_zero_point_energy;
	public static ForgeConfigSpec.BooleanValue large_naquadah_cable_transfers_zero_point_energy;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		small_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Small Naquadah Cable per tick")
				.defineInRange("server.small_naquadah_cable_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		medium_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Medium Naquadah Cable per tick")
				.defineInRange("server.medium_naquadah_cable_max_transfer", 10000000L, 1L, Long.MAX_VALUE);
		
		large_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Large Naquadah Cable per tick")
				.defineInRange("server.large_naquadah_cable_max_transfer", 100000000000L, 1L, Long.MAX_VALUE);
		
		
		
		small_naquadah_cable_transfers_zero_point_energy = server
				.comment("If true, Small Naquadah Cables will be able to transfer energy from ZPMs")
				.define("server.small_naquadah_cable_transfers_zero_point_energy", false);
		
		medium_naquadah_cable_transfers_zero_point_energy = server
				.comment("If true, Medium Naquadah Cables will be able to transfer energy from ZPMs")
				.define("server.medium_naquadah_cable_transfers_zero_point_energy", true);
		
		large_naquadah_cable_transfers_zero_point_energy = server
				.comment("If true, Large Naquadah Cables will be able to transfer energy from ZPMs")
				.define("server.large_naquadah_cable_transfers_zero_point_energy", true);
	}
}
