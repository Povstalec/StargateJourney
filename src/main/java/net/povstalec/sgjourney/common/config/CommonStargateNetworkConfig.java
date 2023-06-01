package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonStargateNetworkConfig
{
	public static SGJourneyConfigValue.BooleanValue generate_random_addresses;
	public static SGJourneyConfigValue.BooleanValue use_datapack_addresses;
	public static SGJourneyConfigValue.BooleanValue random_addresses_from_seed;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Stargate Network Config");
		
		use_datapack_addresses = new SGJourneyConfigValue.BooleanValue(server, "server.use_datapack_addresses", 
				true, 
				"Stargate Network will use addresses from datapacks");
		
		generate_random_addresses = new SGJourneyConfigValue.BooleanValue(server, "server.generate_random_addresses", 
				true, 
				"Stargate Network will generate random addresses for each world.");
		
		random_addresses_from_seed = new SGJourneyConfigValue.BooleanValue(server, "server.random_addresses_from_seed", 
				false, 
				"Stargate Network will randomize addresses based on the world seed");
	}
}
