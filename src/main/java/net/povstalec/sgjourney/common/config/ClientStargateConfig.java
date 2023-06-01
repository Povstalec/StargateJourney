package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientStargateConfig
{
	public static SGJourneyConfigValue.BooleanValue unique_symbols;
	public static SGJourneyConfigValue.BooleanValue use_movie_stargate_model;
	public static SGJourneyConfigValue.BooleanValue milky_way_stargate_back_lights_up;
	public static SGJourneyConfigValue.BooleanValue pegasus_stargate_back_lights_up;

	public static SGJourneyConfigValue.RGBAValue universe_rgba;
	public static SGJourneyConfigValue.RGBAValue milky_way_rgba;
	public static SGJourneyConfigValue.RGBAValue pegasus_rgba;
	public static SGJourneyConfigValue.RGBAValue classic_rgba;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		client.comment("Stargate Journey Client Stargate Config");
		
		unique_symbols = new SGJourneyConfigValue.BooleanValue(client, "client.unique_symbols", 
				false, 
				"If true Solar Systems will use unique Symbols");
		
		use_movie_stargate_model = new SGJourneyConfigValue.BooleanValue(client, "client.use_movie_stargate_model", 
				false, 
				"Decide if Milky Way Stargate should use the Movie Stargate model");
		
		milky_way_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.milky_way_stargate_back_lights_up", 
				false, 
				"Decide if Chevrons on the back of Milky Way Stargate should light up");
		
		pegasus_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.pegasus_stargate_back_lights_up", 
				true, 
				"Decide if Chevrons on the back of Pegasus Stargate should light up");
		
		universe_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.universe_stargate", 
				255, 255, 255, 255, 
				"The RGBA values in Universe Stargate's Event Horizon");

		milky_way_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.milky_way_stargate", 
				55, 55, 255, 255, 
				"The RGBA values in Milky Way Stargate's Event Horizon");

		pegasus_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.pegasus_stargate", 
				55, 55, 255, 255, 
				"The RGBA values in Pegasus Stargate's Event Horizon");

		classic_rgba = new SGJourneyConfigValue.RGBAValue(client, "client.classic_stargate", 
				55, 55, 255, 255, 
				"The RGBA values in Classic Stargate's Event Horizon");
	}
}
