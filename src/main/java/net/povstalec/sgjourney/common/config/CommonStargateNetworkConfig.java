package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonStargateNetworkConfig
{
	public static SGJourneyConfigValue.BooleanValue randomize_addresses;
	public static SGJourneyConfigValue.BooleanValue generate_random_address_regions;
	public static SGJourneyConfigValue.BooleanValue random_addresses_from_seed;
	public static SGJourneyConfigValue.BooleanValue primary_stargate;
	
	public static SGJourneyConfigValue.BooleanValue disable_dhd_preference;
	public static SGJourneyConfigValue.BooleanValue disable_stargate_generation_preference;
	
	public static void init(ModConfigSpec.Builder server)
	{
		randomize_addresses = new SGJourneyConfigValue.BooleanValue(server, "server.randomize_addresses",
				false, // False by default to make it easier for people to look up addresses
				"Stargate Network will randomize addresses, rather than using the addresses from datapacks");
		
		generate_random_address_regions = new SGJourneyConfigValue.BooleanValue(server, "server.generate_random_solar_systems",
				true,
				"Stargate Network will generate random Solar System for each dimension not registered through a datapack");
		
		random_addresses_from_seed = new SGJourneyConfigValue.BooleanValue(server, "server.random_addresses_from_seed", 
				false, // False by default to make it easier for people to look up addresses
				"If true, Stargate Network will randomize addresses based on the world seed, otherwise they'll be based on the hash of the Dimension ID");
		
		primary_stargate = new SGJourneyConfigValue.BooleanValue(server, "server.primary_stargate",
				false,
				"Stargates Network will prioritize Primary Stargates");
		
		
		
		disable_dhd_preference = new SGJourneyConfigValue.BooleanValue(server, "server.disable_dhd_preference", 
				false,
				"Stargates Network will not consider DHDs when choosing a preferred Stargate");
		
		
		
		disable_stargate_generation_preference = new SGJourneyConfigValue.BooleanValue(server, "server.disable_stargate_generation_preference",
			false,
			"Stargates Network will not consider the generation of a Stargate when choosing a preferred Stargate");
	}
}
