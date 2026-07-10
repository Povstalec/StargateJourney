package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;

public class CommonGenerationConfig
{
	public static SGJourneyConfigValue.BooleanValue common_stargate_generation;
	public static SGJourneyConfigValue.BooleanValue common_stargate_search;
	
	public static SGJourneyConfigValue.BooleanValue generate_obstructed_stargates;

	public static ForgeConfigSpec.IntValue stargate_generation_center_x_chunk_offset;
	public static ForgeConfigSpec.IntValue stargate_generation_center_z_chunk_offset;
	public static ForgeConfigSpec.IntValue stargate_generation_x_bound;
	public static ForgeConfigSpec.IntValue stargate_generation_z_bound;
	public static ForgeConfigSpec.IntValue buried_stargate_generation_x_bound;
	public static ForgeConfigSpec.IntValue buried_stargate_generation_z_bound;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		common_stargate_generation = new SGJourneyConfigValue.BooleanValue(server, "server.common_stargate_generation",
				false,
				"Common Stargates will generate");
		
		common_stargate_search = new SGJourneyConfigValue.BooleanValue(server, "server.common_stargate_search",
				false,
				"When false, Stargate Network won't include Common Stargates in the search for ungenerated Stargate Structures");
		
		
		
		generate_obstructed_stargates = new SGJourneyConfigValue.BooleanValue(server, "server.generate_obstructed_stargates",
				true,
				"If true, Stargate structures with an obstructed variant will generate as obstructed (in order to prevent cheesing progression for Vanilla/other mods)");
		
		
		
		stargate_generation_center_x_chunk_offset = server
				.comment("X chunk center offset of structures that contain a Stargate")
				.defineInRange("server.stargate_generation_center_x_chunk_offset", 0, -UniqueStructurePlacement.MAX_CHUNKS, UniqueStructurePlacement.MAX_CHUNKS);
		
		stargate_generation_center_z_chunk_offset = server
				.comment("Z chunk center offset of structures that contain a Stargate")
				.defineInRange("server.stargate_generation_center_z_chunk_offset", 0, -UniqueStructurePlacement.MAX_CHUNKS, UniqueStructurePlacement.MAX_CHUNKS);
		
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
