package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;
import net.povstalec.sgjourney.common.stargate.Stargate.WormholeTravel;

public class CommonStargateConfig
{
	public static ModConfigSpec.BooleanValue stargate_loads_chunk_when_connected;
	
	public static ModConfigSpec.IntValue max_wormhole_open_time;
	public static ModConfigSpec.BooleanValue end_connection_from_both_ends;
	public static ModConfigSpec.EnumValue<WormholeTravel> two_way_wormholes;
	public static ModConfigSpec.BooleanValue reverse_wormhole_kills;
	public static ModConfigSpec.BooleanValue can_break_connected_stargate;
	
	public static ModConfigSpec.BooleanValue kawoosh_destroys_blocks;
	public static ModConfigSpec.BooleanValue kawoosh_disintegrates_items;
	public static ModConfigSpec.BooleanValue kawoosh_disintegrates_entities;
	
	public static ModConfigSpec.BooleanValue enable_redstone_dialing;
	public static ModConfigSpec.BooleanValue always_display_stargate_id;
	public static ModConfigSpec.IntValue max_obstructive_blocks;
	
	public static ModConfigSpec.BooleanValue allow_interstellar_8_chevron_addresses;
	public static ModConfigSpec.BooleanValue allow_system_wide_connections;

	public static ModConfigSpec.BooleanValue enable_address_choice;
	public static ModConfigSpec.BooleanValue enable_classic_stargate_upgrades;
	public static ModConfigSpec.BooleanValue enable_stargate_variants;

	public static ModConfigSpec.BooleanValue universe_best_direction;
	public static ModConfigSpec.BooleanValue universe_fast_rotation;
	
	public static ModConfigSpec.EnumValue<ChevronLockSpeed> universe_chevron_lock_speed;
	public static ModConfigSpec.EnumValue<ChevronLockSpeed> milky_way_chevron_lock_speed;
	public static ModConfigSpec.EnumValue<ChevronLockSpeed> pegasus_chevron_lock_speed;
	public static ModConfigSpec.EnumValue<ChevronLockSpeed> classic_chevron_lock_speed;
	public static ModConfigSpec.EnumValue<ChevronLockSpeed> tollan_chevron_lock_speed;
	
	// Energy Related
	public static ModConfigSpec.BooleanValue enable_energy_bypass;
	public static ModConfigSpec.BooleanValue can_draw_power_from_both_ends;

	public static ModConfigSpec.LongValue system_wide_connection_energy_draw;
	public static ModConfigSpec.LongValue interstellar_connection_energy_draw;
	public static ModConfigSpec.LongValue intergalactic_connection_energy_draw;
	public static ModConfigSpec.LongValue system_wide_connection_energy_cost;
	public static ModConfigSpec.LongValue interstellar_connection_energy_cost;
	public static ModConfigSpec.LongValue intergalactic_connection_energy_cost;
	public static ModConfigSpec.LongValue stargate_energy_capacity;
	public static ModConfigSpec.LongValue stargate_energy_max_receive;
	public static ModConfigSpec.IntValue energy_bypass_multiplier;
	
	public static void init(ModConfigSpec.Builder server)
	{
		stargate_loads_chunk_when_connected = server
				.comment("If true, Stargate will load the chunk it's in while it's connected to another Stargate")
				.define("server.stargate_loads_chunk_when_connected", true);
		
		
		
		max_wormhole_open_time = server
				.comment("The maximum amount of time the Stargate will be open for in seconds")
				.defineInRange("server.max_wormhole_open_time", 228, 10, Integer.MAX_VALUE); // 38 minutes == 2280 seconds
		
		end_connection_from_both_ends = server
				.comment("If false, the Wormhole connection can only be ended from the dialing side")
				.define("server.end_connection_from_both_ends", false);
		
		two_way_wormholes = server
				.comment("ENABLED - Two way travel possible; CREATIVE_ONLY - Two way travel limited to Players in Creative Mode; DISABLED - Two way travel impossible")
				.defineEnum("server.two_way_wormholes", WormholeTravel.CREATIVE_ONLY);
		
		reverse_wormhole_kills = server
				.comment("If true, going through the wrong side of the wormhole will result in death")
				.define("server.reverse_wormhole_kills", true);
		
		can_break_connected_stargate = server
				.comment("If false, it will be impossible to break the Stargate through mining while it's connected")
				.define("server.can_break_connected_stargate", false);
		
		
		
		kawoosh_destroys_blocks = server
				.comment("If true, allow the destruction of Blocks by Kawooshes")
				.define("server.kawoosh_destroys_blocks", true);
		
		kawoosh_disintegrates_entities = server
				.comment("If true, allow the disintegration of Entities by Kawooshes")
				.define("server.kawoosh_disintegrates_entities", true);
		
		kawoosh_disintegrates_items = server
				.comment("If true, allow the disintegration of Items by Kawooshes")
				.define("server.kawoosh_disintegrates_items", true);
		
		
		
		allow_interstellar_8_chevron_addresses = server
				.comment("Decides if 8-chevron addresses can be used for dialing within the same galaxy")
				.define("server.allow_interstellar_8_chevron_addresses", false);
				
		allow_system_wide_connections = server
				.comment("Decides if two Stargates from the same Solar System should be able to connect")
				.define("server.allow_system_wide_connections", true);
		
		
		
		enable_redstone_dialing = server
				.comment("Enables the use of redstone for manual Stargate dialing")
				.define("server.enable_redstone_dialing", true);
		
		max_obstructive_blocks = server
				.comment("The maximum amount of blocks allowed within the ring area before Stargate becomes obstructed")
				.defineInRange("server.max_obstructive_blocks", 12, 1, 21);
		
		
		
		enable_address_choice = server
				.comment("Enables choosing the Address when first creating a Stargate by right-clicking it with a renamed Control Crystal")
				.define("server.enable_address_choice", false);
		
		enable_classic_stargate_upgrades = server
				.comment("Enables upgrading the Classic Stargate")
				.define("server.enable_classic_stargate_upgrades", true);
		
		enable_stargate_variants = server
				.comment("Enables creating Stargate Variants")
				.define("server.enable_stargate_variants", true);
		
		always_display_stargate_id = server
				.comment("If true, Stargate item will always display the 9-Chevron Address of the Stargate in the inventory")
				.define("server.always_display_stargate_id", false);
		
		
		
		universe_best_direction = server
				.comment("If true, the Universe Stargate will always rotate in the best direction; If false, the Universe Stargate will alternate between rotation directions")
				.define("server.universe_stargate_best_direction", true);
		
		universe_fast_rotation = server
				.comment("If true, the Universe Stargate will rotate faster (Fast full rotation takes 108 ticks, slow full toration takes 162 ticks)")
				.define("server.universe_fast_rotation", true);
		
		
		
		universe_chevron_lock_speed = server
				.comment("FAST - Incoming Chevrons take 4 Ticks to lock; MEDIUM - Incoming Chevrons take 8 Ticks to lock; SLOW - Incoming Chevrons take 12 Ticks to lock")
				.defineEnum("server.universe_chevron_lock_speed", ChevronLockSpeed.SLOW);
		
		milky_way_chevron_lock_speed = server
				.comment("FAST - Incoming Chevrons take 4 Ticks to lock; MEDIUM - Incoming Chevrons take 8 Ticks to lock; SLOW - Incoming Chevrons take 12 Ticks to lock")
				.defineEnum("server.milky_way_chevron_lock_speed", ChevronLockSpeed.SLOW);
		
		pegasus_chevron_lock_speed = server
				.comment("FAST - Incoming Chevrons take 4 Ticks to lock; MEDIUM - Incoming Chevrons take 8 Ticks to lock; SLOW - Incoming Chevrons take 12 Ticks to lock")
				.defineEnum("server.pegasus_chevron_lock_speed", ChevronLockSpeed.MEDIUM);
		
		classic_chevron_lock_speed = server
				.comment("FAST - Incoming Chevrons take 4 Ticks to lock; MEDIUM - Incoming Chevrons take 8 Ticks to lock; SLOW - Incoming Chevrons take 12 Ticks to lock")
				.defineEnum("server.classic_chevron_lock_speed", ChevronLockSpeed.SLOW);
		
		tollan_chevron_lock_speed = server
				.comment("FAST - Incoming Chevrons take 4 Ticks to lock; MEDIUM - Incoming Chevrons take 8 Ticks to lock; SLOW - Incoming Chevrons take 12 Ticks to lock")
				.defineEnum("server.tollan_chevron_lock_speed", ChevronLockSpeed.MEDIUM);
		
		// Energy Related
		enable_energy_bypass = server
				.comment("The maximum connection time can be extended by increasing the energy input")
				.define("server.enable_energy_bypass", true);
		
		can_draw_power_from_both_ends = server
				.comment("If true, the wormhole will draw power from both connected Stargates")
				.define("server.can_draw_power_from_both_ends", true);
		
		
		
		system_wide_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for system-wide connections")
				.defineInRange("server.system_wide_connection_energy_draw", 5L, 0, 9223372036854775807L);
		
		interstellar_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for interstellar connections")
				.defineInRange("server.interstellar_connection_energy_draw", 50L, 0, 9223372036854775807L);
		
		intergalactic_connection_energy_draw = server
				.comment("The amount of energy cost of keeping the wormhole open each tick for intergalactic connections")
				.defineInRange("server.intergalactic_connection_energy_draw", 50000L, 0, 9223372036854775807L);
		
		system_wide_connection_energy_cost = server
				.comment("The amount of energy required to establish a connection inside a solar system")
				.defineInRange("server.system_wide_connection_energy_cost", 50000L, 0L, 9223372036854775807L);
		
		interstellar_connection_energy_cost = server
				.comment("The amount of energy required to establish a connection inside the galaxy")
				.defineInRange("server.interstellar_connection_energy_cost", 100000L, 0L, 9223372036854775807L);
		
		intergalactic_connection_energy_cost = server
				.comment("The amount of energy required to establish a connection outside the galaxy")
				.defineInRange("server.intergalactic_connection_energy_cost", 100000000000L, 0L, 9223372036854775807L);
		
		stargate_energy_capacity = server
				.comment("The maximum amount of energy the Stargate can hold")
				.defineInRange("server.stargate_energy_capacity", 1000000000000L, 0L, 9223372036854775807L);
		
		stargate_energy_max_receive = server
				.comment("The maximum amount of energy the Stargate can receive at once")
				.defineInRange("server.stargate_energy_max_receive", 1000000000000L, 0L, 9223372036854775807L);
		
		energy_bypass_multiplier = server
				.comment("The energy required to keep the Stargate open after exceeding the maximum open time is multiplied by this number")
				.defineInRange("server.energy_bypass_multiplier", 100000, 1, 2147483647);
		
		
	}
}
