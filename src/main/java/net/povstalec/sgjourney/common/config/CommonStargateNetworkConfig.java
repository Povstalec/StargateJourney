package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonStargateNetworkConfig
{
	public static SGJourneyConfigValue.BooleanValue use_datapack_addresses;
	public static SGJourneyConfigValue.BooleanValue generate_random_solar_systems;
	public static SGJourneyConfigValue.BooleanValue random_addresses_from_seed;

	public static ForgeConfigSpec.IntValue stargate_generation_center_x_chunk_offset;
	public static ForgeConfigSpec.IntValue stargate_generation_center_z_chunk_offset;
	public static ForgeConfigSpec.IntValue stargate_generation_x_bound;
	public static ForgeConfigSpec.IntValue stargate_generation_z_bound;
	public static ForgeConfigSpec.IntValue buried_stargate_generation_x_bound;
	public static ForgeConfigSpec.IntValue buried_stargate_generation_z_bound;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		use_datapack_addresses = new SGJourneyConfigValue.BooleanValue(server, "server.use_datapack_addresses", 
				true, //TODO Change to false
				"Stargate Network will use addresses from datapacks");
		
		generate_random_solar_systems = new SGJourneyConfigValue.BooleanValue(server, "server.generate_random_solar_systems", 
				true, 
				"Stargate Network will generate random Solar System for each dimension not registered through a datapack");
		
		random_addresses_from_seed = new SGJourneyConfigValue.BooleanValue(server, "server.random_addresses_from_seed", 
				true, 
				"Stargate Network will randomize addresses based on the world seed");

		
		
		stargate_generation_center_x_chunk_offset = server
				.comment("X chunk center offset of structures that contain a Stargate")
				.defineInRange("server.stargate_generation_center_x_chunk_offset", 0, -512, 512);
		
		stargate_generation_center_z_chunk_offset = server
				.comment("Z chunk center offset of structures that contain a Stargate")
				.defineInRange("server.stargate_generation_center_z_chunk_offset", 0, -512, 512);
		
		stargate_generation_x_bound = server
				.comment("X chunk bounds within which a Structure containing a Stargate may generate")
				.defineInRange("server.stargate_generation_x_bound", 64, 0, 64);
		
		stargate_generation_z_bound = server
				.comment("Z chunk bounds within which a Structure containing a Stargate may generate")
				.defineInRange("server.stargate_generation_z_bound", 64, 0, 64);
		
		buried_stargate_generation_x_bound = server
				.comment("X chunk bounds within which a Buried Stargate may generate")
				.defineInRange("server.buried_stargate_generation_x_bound", 64, 0, 64);
		
		buried_stargate_generation_z_bound = server
				.comment("Z chunk bounds within which a Buried Stargate may generate")
				.defineInRange("server.buried_stargate_generation_z_bound", 64, 0, 64);
	}
}
