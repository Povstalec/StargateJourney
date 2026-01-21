package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonTransporterConfig
{
	public static ForgeConfigSpec.LongValue ring_panel_energy_capacity;
	public static ForgeConfigSpec.LongValue ring_panel_max_energy_receive;
	public static ForgeConfigSpec.LongValue ring_panel_max_energy_extract;
	public static ForgeConfigSpec.LongValue ring_panel_button_press_energy_cost;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		ring_panel_energy_capacity = server
				.comment("Energy capacity of the Ring Panel")
				.defineInRange("server.ring_panel_energy_capacity", 200000, 0L, Long.MAX_VALUE);
		
		ring_panel_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the Ring Panel in one tick")
				.defineInRange("server.ring_panel_max_energy_receive", 100000, 0L, Long.MAX_VALUE);
		
		ring_panel_max_energy_extract = server
				.comment("Maximum amount of energy that can be extracted from the Ring Panel in one tick")
				.defineInRange("server.ring_panel_max_energy_extract", 50000L, 0L, Long.MAX_VALUE);
		
		ring_panel_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the Ring Panel")
				.defineInRange("server.ring_panel_button_press_energy_cost", 5000L, 0L, Long.MAX_VALUE);
	}
}
