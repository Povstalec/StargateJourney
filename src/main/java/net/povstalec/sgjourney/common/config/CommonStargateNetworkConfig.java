package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonStargateNetworkConfig
{
	public static SGJourneyConfigValue.BooleanValue use_datapack_addresses;
	public static SGJourneyConfigValue.BooleanValue generate_random_solar_systems;
	public static SGJourneyConfigValue.BooleanValue random_addresses_from_seed;
	
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
	}
}
