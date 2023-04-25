package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.povstalec.sgjourney.common.stargate.Stargate.WormholeTravel;

public class CommonStargateConfig
{
	public static ForgeConfigSpec.IntValue max_wormhole_open_time;
	public static ForgeConfigSpec.BooleanValue end_connection_from_both_ends;
	public static ForgeConfigSpec.EnumValue<WormholeTravel> two_way_wormholes;
	public static ForgeConfigSpec.BooleanValue reverse_wormhole_kills;
	public static ForgeConfigSpec.BooleanValue enable_redstone_dialing;
	
	// Energy Related
	public static ForgeConfigSpec.BooleanValue enable_energy_bypass;
	public static ForgeConfigSpec.BooleanValue can_draw_power_from_both_ends;

	public static ForgeConfigSpec.LongValue system_wide_connection_energy_draw;
	public static ForgeConfigSpec.LongValue interstellar_connection_energy_draw;
	public static ForgeConfigSpec.LongValue intergalactic_connection_energy_draw;
	public static ForgeConfigSpec.LongValue system_wide_connection_energy_cost;
	public static ForgeConfigSpec.LongValue interstellar_connection_energy_cost;
	public static ForgeConfigSpec.LongValue intergalactic_connection_energy_cost;
	public static ForgeConfigSpec.IntValue energy_bypass_multiplier;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Address Config");
		
		max_wormhole_open_time = server
				.comment("The maximum amount of time the Stargate will be open for in seconds")
				.defineInRange("server.max_wormhole_open_time", 228, 10, 2280);
		
		end_connection_from_both_ends = server
				.comment("If false, the Wormhole connection can only be ended from the dialing side")
				.define("server.end_connection_from_both_ends", true);
		
		two_way_wormholes = server
				.comment("If true, wormholes can be traveled through both ways")
				.defineEnum("server.two_way_wormholes", WormholeTravel.CREATIVE_ONLY);
		
		reverse_wormhole_kills = server
				.comment("If true, going through the wrong side of the wormhole will result in death")
				.define("server.reverse_wormhole_kills", true);
		
		enable_redstone_dialing = server
				.comment("Enables the use of redstone for manual Stargate dialing")
				.define("server.enable_redstone_dialing", true);
		
		// Energy Related
		enable_energy_bypass = server
				.comment("The maximum connection time can be extended by increasing the energy input")
				.define("server.enable_energy_bypass", false);
		
		can_draw_power_from_both_ends = server
				.comment("If true, the wormhole will draw power from both connected Stargates")
				.define("server.can_draw_power_from_both_ends", false);
		
		system_wide_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for system-wide connections")
				.defineInRange("server.system_wide_connection_energy_draw", 500L, 0, 9223372036854775807L);
		
		interstellar_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for interstellar connections")
				.defineInRange("server.interstellar_connection_energy_draw", 1000L, 0, 9223372036854775807L);
		
		intergalactic_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for intergalactic connections")
				.defineInRange("server.intergalactic_connection_energy_draw", 1000000L, 0, 9223372036854775807L);
		
		system_wide_connection_energy_cost = server
				.comment("The amount of energy required to estabilish a connection inside a solar system")
				.defineInRange("server.system_wide_connection_energy_cost", 50000L, 0L, 9223372036854775807L);
		
		interstellar_connection_energy_cost = server
				.comment("The amount of energy required to estabilish a connection inside the galaxy")
				.defineInRange("server.interstellar_connection_energy_cost", 100000L, 0L, 9223372036854775807L);
		
		intergalactic_connection_energy_cost = server
				.comment("The amount of energy required to estabilish a connection outside the galaxy")
				.defineInRange("server.intergalactic_connection_energy_cost", 100000000000L, 0L, 9223372036854775807L);
		
		energy_bypass_multiplier = server
				.comment("The energy required to keep the Stargate open after exceeding the maximum open time is multiplied by this number")
				.defineInRange("server.energy_bypass_multiplier", 100000, 1, 2147483647);
	}
}
