package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonTransmissionConfig
{
	public static ModConfigSpec.IntValue max_transmission_jumps;

	public static ModConfigSpec.IntValue max_gdo_transmission_distance;
	public static ModConfigSpec.IntValue max_transceiver_transmission_distance;
	public static ModConfigSpec.IntValue max_stargate_transmission_distance;
	
	public static void init(ModConfigSpec.Builder server)
	{
		max_transmission_jumps = server
				.comment("Max number of times a transmission can be forwarded before it disappears")
				.defineInRange("server.max_transmission_jumps", 1, 1, 16);
		
		
		
		max_gdo_transmission_distance = server
				.comment("Max distance from which the GDO transmission can reach a transmission receiver")
				.defineInRange("server.max_gdo_transmission_distance", 20, 1, 128);
		
		max_transceiver_transmission_distance = server
				.comment("Max distance from which the Transceiver transmission can reach a transmission receiver")
				.defineInRange("server.max_transceiver_transmission_distance", 20, 1, 128);
		
		max_stargate_transmission_distance = server
				.comment("Max distance from which the Stargate transmission can reach a transmission receiver")
				.defineInRange("server.max_stargate_transmission_distance", 20, 1, 128);
	}
}
