package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonStargateNetworkConfig
{
	public static SGJourneyConfigValue.BooleanValue use_datapack_addresses;
	public static SGJourneyConfigValue.BooleanValue generate_random_solar_systems;
	public static SGJourneyConfigValue.BooleanValue random_addresses_from_seed;
	
	public static SGJourneyConfigValue.BooleanValue disable_dhd_preference;
	public static SGJourneyConfigValue.BooleanValue primary_stargate;
	
	public static void init(ModConfigSpec.Builder server)
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
		
		
		
		disable_dhd_preference = new SGJourneyConfigValue.BooleanValue(server, "server.disable_dhd_preference", 
				false,
				"Stargates Network will not consider DHDs when choosing preferred Stargate");
		
		primary_stargate = new SGJourneyConfigValue.BooleanValue(server, "server.primary_stargate",
				false,
				"Stargates Network will prioritize Primary Stargates");
	}
}