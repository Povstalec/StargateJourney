package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonInterfaceConfig
{
	public static ForgeConfigSpec.LongValue default_energy_target;
	
	public static ForgeConfigSpec.LongValue basic_interface_capacity;
	public static ForgeConfigSpec.LongValue basic_interface_max_transfer;
	
	public static ForgeConfigSpec.LongValue crystal_interface_capacity;
	public static ForgeConfigSpec.LongValue crystal_interface_max_transfer;
	
	public static ForgeConfigSpec.LongValue advanced_crystal_interface_capacity;
	public static ForgeConfigSpec.LongValue advanced_crystal_interface_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		default_energy_target = server
				.comment("The default amount of energy the Interface will try to provide until it stops")
				.defineInRange("server.default_energy_target", 200000L, 1L, Long.MAX_VALUE);
		
		
		
		basic_interface_capacity = server
				.comment("The amount of energy a Basic Interface can hold")
				.defineInRange("server.basic_interface_capacity", 10000000L, 1L, Long.MAX_VALUE);
		
		basic_interface_max_transfer = server
				.comment("The maximum amount of energy a Basic Interface can transfer (in and out) at once")
				.defineInRange("server.basic_interface_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		
		
		crystal_interface_capacity = server
				.comment("The amount of energy a Crystal Interface can hold")
				.defineInRange("server.crystal_interface_capacity", 100000000L, 1L, Long.MAX_VALUE);
		
		crystal_interface_max_transfer = server
				.comment("The maximum amount of energy a Crystal Interface can transfer (in and out) at once")
				.defineInRange("server.crystal_interface_max_transfer", 10000000L, 1L, Long.MAX_VALUE);
		
		
		
		advanced_crystal_interface_capacity = server
				.comment("The amount of energy an Advanced Crystal Interface can hold")
				.defineInRange("server.advanced_crystal_interface_capacity", 100000000000L, 1L, Long.MAX_VALUE);
		
		advanced_crystal_interface_max_transfer = server
				.comment("The maximum amount of energy an Advanced Crystal Interface can transfer (in and out) at once")
				.defineInRange("server.advanced_crystal_interface_max_transfer", 100000000000L, 1L, Long.MAX_VALUE);
	}
}
