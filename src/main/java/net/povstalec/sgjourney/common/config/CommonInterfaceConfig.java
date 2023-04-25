package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonInterfaceConfig
{
	public static ForgeConfigSpec.LongValue basic_interface_capacity;
	public static ForgeConfigSpec.LongValue basic_interface_max_transfer;
	
	public static ForgeConfigSpec.LongValue crystal_interface_capacity;
	public static ForgeConfigSpec.LongValue crystal_interface_max_transfer;
	
	public static ForgeConfigSpec.LongValue advanced_crystal_interface_capacity;
	public static ForgeConfigSpec.LongValue advanced_crystal_interface_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Interface Config");
		
		basic_interface_capacity = server
				.comment("The amount of energy a Basic Interface can hold")
				.defineInRange("server.basic_interface_capacity", 10000000L, 1L, 2147483647L);
		
		basic_interface_max_transfer = server
				.comment("The maximum amount of energy a Basic Interface can transfer at once")
				.defineInRange("server.basic_interface_max_transfer", 100000L, 1L, 2147483647L);
		
		
		
		crystal_interface_capacity = server
				.comment("The amount of energy a Crystal Interface can hold")
				.defineInRange("server.crystal_interface_capacity", 100000000L, 1L, 2147483647L);
		
		crystal_interface_max_transfer = server
				.comment("The maximum amount of energy a Crystal Interface can transfer at once")
				.defineInRange("server.crystal_interface_max_transfer", 1000000L, 1L, 2147483647L);
		
		
		
		advanced_crystal_interface_capacity = server
				.comment("The amount of energy an Advanced Crystal Interface can hold")
				.defineInRange("server.advanced_crystal_interface_capacity", 100000000L, 1L, 2147483647L);
		
		advanced_crystal_interface_max_transfer = server
				.comment("The maximum amount of energy an Advanced Crystal Interface can transfer at once")
				.defineInRange("server.advanced_crystal_interface_max_transfer", 1000000L, 1L, 2147483647L);
	}
}
