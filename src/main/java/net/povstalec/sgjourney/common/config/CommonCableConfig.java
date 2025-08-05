package net.povstalec.sgjourney.common.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonCableConfig
{
	public static ModConfigSpec.IntValue max_cables_in_network;
	
	public static ModConfigSpec.LongValue lightning_strike_energy;
	
	public static ModConfigSpec.LongValue naquadah_wire_max_transfer;
	public static ModConfigSpec.LongValue small_naquadah_cable_max_transfer;
	public static ModConfigSpec.LongValue medium_naquadah_cable_max_transfer;
	public static ModConfigSpec.LongValue large_naquadah_cable_max_transfer;
	
	public static ModConfigSpec.BooleanValue naquadah_wire_transfers_zero_point_energy;
	public static ModConfigSpec.BooleanValue small_naquadah_cable_transfers_zero_point_energy;
	public static ModConfigSpec.BooleanValue medium_naquadah_cable_transfers_zero_point_energy;
	public static ModConfigSpec.BooleanValue large_naquadah_cable_transfers_zero_point_energy;
	
	public static void init(ModConfigSpec.Builder server)
	{
		max_cables_in_network = server
				.comment("The maximum number of Cable Blocks in a single Cable Network")
				.defineInRange("server.max_cables_in_network", 4096, 1, Integer.MAX_VALUE);
		
		
		
		lightning_strike_energy = server
				.comment("The amount of energy that will be generated if Lightning strikes a Stargate or a Lightning Rod connected to a cable")
				.defineInRange("server.lightning_strike_energy", 100000L, 0L, Long.MAX_VALUE);
		
		
		
		naquadah_wire_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Naquadah Wire per tick")
				.defineInRange("server.naquadah_wire_max_transfer", 5000L, 1L, Long.MAX_VALUE);
		
		small_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Small Naquadah Cable per tick")
				.defineInRange("server.small_naquadah_cable_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		medium_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Medium Naquadah Cable per tick")
				.defineInRange("server.medium_naquadah_cable_max_transfer", 10000000L, 1L, Long.MAX_VALUE);
		
		large_naquadah_cable_max_transfer = server
				.comment("Maximum amount of energy that can be transferred through the Large Naquadah Cable per tick")
				.defineInRange("server.large_naquadah_cable_max_transfer", 100000000000L, 1L, Long.MAX_VALUE);
		
		
		
		naquadah_wire_transfers_zero_point_energy = server
				.comment("If true, Naquadah Wires will be able to transfer energy from ZPMs")
				.define("server.naquadah_wire_transfers_zero_point_energy", false);
		
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
