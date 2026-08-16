package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.povstalec.sgjourney.common.misc.RenderAMD;

public class ClientStargateConfig
{
	public static SGJourneyConfigValue.EnumValue<RenderAMD> render_amd;
	
	public static SGJourneyConfigValue.BooleanValue stargate_variants;
	public static SGJourneyConfigValue.BooleanValue unique_symbols;
	public static SGJourneyConfigValue.BooleanValue shiny_event_horizons;
	public static SGJourneyConfigValue.BooleanValue enable_vortex;
	public static SGJourneyConfigValue.IntValue event_horizon_distortion;
	
	public static SGJourneyConfigValue.BooleanValue use_movie_stargate_model;
	public static SGJourneyConfigValue.BooleanValue movie_primary_chevron_opens;
	public static SGJourneyConfigValue.BooleanValue alternate_movie_chevron_locking;
	public static SGJourneyConfigValue.BooleanValue universe_front_rotates;
	public static SGJourneyConfigValue.BooleanValue milky_way_stargate_back_lights_up;
	public static SGJourneyConfigValue.BooleanValue pegasus_stargate_back_lights_up;

	public static SGJourneyConfigValue.IntValue stargate_full_sound_distance;
	public static SGJourneyConfigValue.IntValue stargate_max_sound_distance;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		render_amd = new SGJourneyConfigValue.EnumValue(client, "client.render_amd",
				RenderAMD.AUTO,
				"If ENABLED, event horizons will render in a way that is compatible with AMD, if DISABLED they won't, AUTO detection by default");
		
		
		
		stargate_variants = new SGJourneyConfigValue.BooleanValue(client, "client.stargate_variants",
				true, 
				"If true you will be able to see Stargate Variants");
		
		unique_symbols = new SGJourneyConfigValue.BooleanValue(client, "client.unique_symbols", 
				false, 
				"If true Solar Systems will use unique Symbols");
		
		shiny_event_horizons = new SGJourneyConfigValue.BooleanValue(client, "client.shiny_event_horizons", 
				true, 
				"Decide if Event Horizons should be shinier");
		
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
		
		movie_primary_chevron_opens = new SGJourneyConfigValue.BooleanValue(client, "client.movie_primary_chevron_opens", 
				false, 
				"Decide if the Primary Chevron on the Movie Stargate model should open");
		
		alternate_movie_chevron_locking = new SGJourneyConfigValue.BooleanValue(client, "client.alternate_movie_chevron_locking", 
				false, 
				"Decide if Movie Stargate model should use alternate chevron locking");
		
		milky_way_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.milky_way_stargate_back_lights_up", 
				false, 
				"Decide if Chevrons on the back of Milky Way Stargate should light up");
		
		pegasus_stargate_back_lights_up = new SGJourneyConfigValue.BooleanValue(client, "client.pegasus_stargate_back_lights_up", 
				true, 
				"Decide if Chevrons on the back of Pegasus Stargate should light up");
		
		
		
		stargate_full_sound_distance = new SGJourneyConfigValue.IntValue(client, "client.stargate_full_sound_distance", 
				32, 0, 128, 
				"The distance at which the sounds a Stargate makes can still be heard at full volume");

		stargate_max_sound_distance = new SGJourneyConfigValue.IntValue(client, "client.stargate_max_sound_distance", 
				64, 16, 128, 
				"The distance at which the sounds a Stargate makes can still be heard");
	}
}
