package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientStargateConfig
{
	public static SGJourneyConfigValue.BooleanValue stargate_variants;
	public static SGJourneyConfigValue.BooleanValue unique_symbols;
	public static SGJourneyConfigValue.BooleanValue enable_vortex;
	public static SGJourneyConfigValue.IntValue event_horizon_distortion;
	
	public static SGJourneyConfigValue.BooleanValue use_movie_stargate_model;
	public static SGJourneyConfigValue.BooleanValue cap_movie_chevron_locking;
	public static SGJourneyConfigValue.BooleanValue movie_front_chevrons_locking;
	public static SGJourneyConfigValue.BooleanValue alternate_movie_chevron_locking;
	public static SGJourneyConfigValue.BooleanValue universe_front_rotates;
	public static SGJourneyConfigValue.BooleanValue milky_way_stargate_back_lights_up;
	public static SGJourneyConfigValue.BooleanValue pegasus_stargate_back_lights_up;

	public static SGJourneyConfigValue.RGBAValue universe_rgba;
	public static SGJourneyConfigValue.RGBAValue milky_way_rgba;
	public static SGJourneyConfigValue.RGBAValue pegasus_rgba;
	public static SGJourneyConfigValue.RGBAValue classic_rgba;
	public static SGJourneyConfigValue.RGBAValue tollan_rgba;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		stargate_variants = new SGJourneyConfigValue.BooleanValue(client, "client.stargate_variants", 
				true, 
				"If true you will be able to see Stargate Variants");
		
		unique_symbols = new SGJourneyConfigValue.BooleanValue(client, "client.unique_symbols", 
				false, 
				"If true Solar Systems will use unique Symbols");
		
		enable_vortex = new SGJourneyConfigValue.BooleanValue(client, "client.enable_vortex", 
				false, 
				"If true Wormholes will produce Vortex after a Kawoosh");
		
		event_horizon_distortion = new SGJourneyConfigValue.IntValue(client, "client.event_horizon_distortion", 
				25, 0, 25, 
				"The amount of distortion the Stargate Event Horizon will experience");
		
		use_movie_stargate_model = new SGJourneyConfigValue.BooleanValue(client, "client.use_movie_stargate_model", 
				false, 
				"Decide if Milky Way Stargate should use the Movie Stargate model");
		
		universe_front_rotates = new SGJourneyConfigValue.BooleanValue(client, "client.universe_front_rotates", 
				false, 
				"If true only the front of the Universe Stargate will rotate");
		
		cap_movie_chevron_locking = new SGJourneyConfigValue.BooleanValue(client, "client.cap_movie_chevron_locking", 
				false, 
				"Decide if Movie Stargate model should use chevron locking based on Carter's Addon Pack");
		
		movie_front_chevrons_locking = new SGJourneyConfigValue.BooleanValue(client, "client.movie_front_chevrons_locking", 
				false, 
				"Decide if Movie Stargate model should only move front chevrons");
		
		alternate_movie_chevron_locking = new SGJourneyConfigValue.BooleanValue(client, "client.alternate_movie_chevron_locking", 
				false, 
				"Decide if Movie Stargate model should use alternate chevron locking");
		
		milky_way_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.milky_way_stargate_back_lights_up", 
				false, 
				"Decide if Chevrons on the back of Milky Way Stargate should light up");
		
		pegasus_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.pegasus_stargate_back_lights_up", 
				true, 
				"Decide if Chevrons on the back of Pegasus Stargate should light up");
		
		universe_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.universe_stargate", 
				200, 220, 255, 255, 
				"The RGBA values in Universe Stargate's Event Horizon");

		milky_way_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.milky_way_stargate", 
				55, 55, 255, 255, 
				"The RGBA values in Milky Way Stargate's Event Horizon");

		pegasus_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.pegasus_stargate", 
				25, 25, 255, 255, 
				"The RGBA values in Pegasus Stargate's Event Horizon");

		classic_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.classic_stargate", 
				39, 113, 255, 255, 
				"The RGBA values in Classic Stargate's Event Horizon");

		tollan_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.tollan_stargate", 
				50, 100, 240, 255, 
				"The RGBA values in Tollan Stargate's Event Horizon");
	}
}
