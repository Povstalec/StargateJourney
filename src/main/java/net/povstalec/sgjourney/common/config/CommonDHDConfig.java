package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonDHDConfig
{
	public static ForgeConfigSpec.LongValue milky_way_dhd_energy_buffer_capacity;
	public static ForgeConfigSpec.LongValue milky_way_dhd_max_energy_receive;
	public static ForgeConfigSpec.LongValue milky_way_dhd_max_energy_extract;
	public static ForgeConfigSpec.LongValue milky_way_dhd_button_press_energy_cost;
	
	public static ForgeConfigSpec.LongValue pegasus_dhd_energy_buffer_capacity;
	public static ForgeConfigSpec.LongValue pegasus_dhd_max_energy_receive;
	public static ForgeConfigSpec.LongValue pegasus_dhd_max_energy_extract;
	public static ForgeConfigSpec.LongValue pegasus_dhd_button_press_energy_cost;
	
	public static ForgeConfigSpec.LongValue classic_dhd_energy_buffer_capacity;
	public static ForgeConfigSpec.LongValue classic_dhd_max_energy_receive;
	public static ForgeConfigSpec.LongValue classic_dhd_max_energy_extract;
	public static ForgeConfigSpec.LongValue classic_dhd_button_press_energy_cost;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		milky_way_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the DHD")
				.defineInRange("server.milky_way_dhd_energy_buffer_capacity", 300000, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the DHD in one tick")
				.defineInRange("server.milky_way_dhd_max_energy_receive", 100000L, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the DHD in one tick")
				.defineInRange("server.milky_way_dhd_max_energy_extract", 10000000L, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the DHD")
				.defineInRange("server.milky_way_dhd_button_press_energy_cost", 5000L, 0L, Long.MAX_VALUE);
		
		
		
		pegasus_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the DHD")
				.defineInRange("server.pegasus_dhd_energy_buffer_capacity", 500000, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the DHD in one tick")
				.defineInRange("server.pegasus_dhd_max_energy_receive", 150000L, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the DHD in one tick")
				.defineInRange("server.pegasus_dhd_max_energy_extract", 100000000L, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the DHD")
				.defineInRange("server.pegasus_dhd_button_press_energy_cost", 5000L, 0L, Long.MAX_VALUE);
		
		
		
		classic_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the DHD")
				.defineInRange("server.classic_dhd_energy_buffer_capacity", 300000, 0L, Long.MAX_VALUE);
		
		classic_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the DHD in one tick")
				.defineInRange("server.classic_dhd_max_energy_receive", 100000L, 0L, Long.MAX_VALUE);
		
		classic_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the DHD in one tick")
				.defineInRange("server.classic_dhd_max_energy_extract", 5000000L, 0L, Long.MAX_VALUE);
		
		classic_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the DHD")
				.defineInRange("server.classic_dhd_button_press_energy_cost", 5000L, 0L, Long.MAX_VALUE);
	}
}
