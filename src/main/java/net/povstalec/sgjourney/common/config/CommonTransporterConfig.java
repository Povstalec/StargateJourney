package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonTransporterConfig
{
	public static ForgeConfigSpec.LongValue ring_panel_energy_capacity;
	public static ForgeConfigSpec.LongValue ring_panel_max_energy_receive;
	public static ForgeConfigSpec.LongValue ring_panel_max_energy_extract;
	public static ForgeConfigSpec.LongValue ring_panel_button_press_energy_cost;
	
	public static ForgeConfigSpec.LongValue ancient_transport_rings_energy_capacity;
	public static ForgeConfigSpec.LongValue ancient_transport_rings_max_energy_receive;
	
	public static ForgeConfigSpec.LongValue goauld_transport_rings_energy_capacity;
	public static ForgeConfigSpec.LongValue goauld_transport_rings_max_energy_receive;
	
	public static ForgeConfigSpec.LongValue transporter_transport_energy_cost;
	public static ForgeConfigSpec.LongValue transporter_transport_distance_energy_cost;
	public static ForgeConfigSpec.LongValue transporter_dimension_transport_energy_cost;
	
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
		
		
		
		ancient_transport_rings_energy_capacity = server
				.comment("Energy capacity of the Ancient Transport Rings")
				.defineInRange("server.ancient_transport_rings_energy_capacity", 1000000, 0L, Long.MAX_VALUE);
		
		ancient_transport_rings_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the Ancient Transport Rings in one tick")
				.defineInRange("server.ancient_transport_rings_max_energy_receive", 1000000, 0L, Long.MAX_VALUE);
		
		
		
		goauld_transport_rings_energy_capacity = server
				.comment("Energy capacity of the Goa'uld Transport Rings")
				.defineInRange("server.goauld_transport_rings_energy_capacity", 1000000, 0L, Long.MAX_VALUE);
		
		goauld_transport_rings_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the Goa'uld Transport Rings in one tick")
				.defineInRange("server.goauld_transport_rings_max_energy_receive", 1000000, 0L, Long.MAX_VALUE);
		
		
		
		transporter_transport_energy_cost = server
				.comment("The amount of energy that is used for establishing a transport between two Transporters")
				.defineInRange("server.transporter_transport_energy_cost", 5000L, 0L, Long.MAX_VALUE);
		
		transporter_transport_distance_energy_cost = server
				.comment("The amount of energy added to total energy cost of transporting per block travelled")
				.defineInRange("server.transporter_transport_distance_energy_cost", 64L, 0L, Long.MAX_VALUE);
		
		transporter_dimension_transport_energy_cost = server
				.comment("The amount of energy added to total energy cost of transporting to another Dimension in the same Address Region")
				.defineInRange("server.transporter_dimension_transport_energy_cost", 50000L, 0L, Long.MAX_VALUE);
	}
}
