package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonDHDConfig
{
	public static ForgeConfigSpec.LongValue universe_dhd_energy_buffer_capacity;
	public static ForgeConfigSpec.LongValue universe_dhd_max_energy_receive;
	public static ForgeConfigSpec.LongValue universe_dhd_max_energy_extract;
	public static ForgeConfigSpec.LongValue universe_dhd_button_press_energy_cost;
	
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
		universe_dhd_energy_buffer_capacity = server
			.comment("Capacity of the energy buffer inside the Universe DHD")
			.defineInRange("server.universe_dhd_energy_buffer_capacity", 300_000L, 0L, Long.MAX_VALUE);
		
		universe_dhd_max_energy_receive = server
			.comment("Maximum amount of energy that can be transferred to the Universe DHD in one tick")
			.defineInRange("server.universe_dhd_max_energy_receive", 100_000L, 0L, Long.MAX_VALUE);
		
		universe_dhd_max_energy_extract = server
			.comment("Maximum amount of energy that can be transferred from the Universe DHD in one tick")
			.defineInRange("server.universe_dhd_max_energy_extract", 10_000_000L, 0L, Long.MAX_VALUE);
		
		universe_dhd_button_press_energy_cost = server
			.comment("Energy depleted by pressing a button on the Universe DHD")
			.defineInRange("server.universe_dhd_button_press_energy_cost", 5_000L, 0L, Long.MAX_VALUE);
		
		
		
		milky_way_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the Milky Way DHD")
				.defineInRange("server.milky_way_dhd_energy_buffer_capacity", 300_000L, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the Milky Way DHD in one tick")
				.defineInRange("server.milky_way_dhd_max_energy_receive", 100_000L, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the Milky Way DHD in one tick")
				.defineInRange("server.milky_way_dhd_max_energy_extract", 10_000_000L, 0L, Long.MAX_VALUE);
		
		milky_way_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the Milky Way DHD")
				.defineInRange("server.milky_way_dhd_button_press_energy_cost", 5_000L, 0L, Long.MAX_VALUE);
		
		
		
		pegasus_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the Pegasus DHD")
				.defineInRange("server.pegasus_dhd_energy_buffer_capacity", 500_000L, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the Pegasus DHD in one tick")
				.defineInRange("server.pegasus_dhd_max_energy_receive", 150_000L, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the Pegasus DHD in one tick")
				.defineInRange("server.pegasus_dhd_max_energy_extract", 100_000_000L, 0L, Long.MAX_VALUE);
		
		pegasus_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the Pegasus DHD")
				.defineInRange("server.pegasus_dhd_button_press_energy_cost", 5_000L, 0L, Long.MAX_VALUE);
		
		
		
		classic_dhd_energy_buffer_capacity = server
				.comment("Capacity of the energy buffer inside the Classic DHD")
				.defineInRange("server.classic_dhd_energy_buffer_capacity", 300_000L, 0L, Long.MAX_VALUE);
		
		classic_dhd_max_energy_receive = server
				.comment("Maximum amount of energy that can be transferred to the DClassic HD in one tick")
				.defineInRange("server.classic_dhd_max_energy_receive", 100_000L, 0L, Long.MAX_VALUE);
		
		classic_dhd_max_energy_extract = server
				.comment("Maximum amount of energy that can be transferred from the Classic DHD in one tick")
				.defineInRange("server.classic_dhd_max_energy_extract", 5_000_000L, 0L, Long.MAX_VALUE);
		
		classic_dhd_button_press_energy_cost = server
				.comment("Energy depleted by pressing a button on the Classic DHD")
				.defineInRange("server.classic_dhd_button_press_energy_cost", 5_000L, 0L, Long.MAX_VALUE);
	}
}
