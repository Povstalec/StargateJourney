package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonCrystalConfig
{
	public static ForgeConfigSpec.LongValue energy_crystal_capacity;
	public static ForgeConfigSpec.LongValue advanced_energy_crystal_capacity;
	public static ForgeConfigSpec.LongValue energy_crystal_max_transfer;
	public static ForgeConfigSpec.LongValue advanced_energy_crystal_max_transfer;
	
	public static ForgeConfigSpec.LongValue transfer_crystal_max_transfer;
	public static ForgeConfigSpec.LongValue advanced_transfer_crystal_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		energy_crystal_capacity = server
				.comment("The amount of energy an Energy Crystal can hold")
				.defineInRange("server.energy_crystal_capacity", 500000L, 1L, Long.MAX_VALUE);
		
		advanced_energy_crystal_capacity = server
				.comment("The amount of energy an Advanced Energy Crystal can hold")
				.defineInRange("server.advanced_energy_crystal_capacity", 2000000L, 1L, Long.MAX_VALUE);
		
		energy_crystal_max_transfer = server
				.comment("The amount of energy that can be transfered into and out of an Energy Crystal per tick")
				.defineInRange("server.energy_crystal_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		advanced_energy_crystal_max_transfer = server
				.comment("The amount of energy that can be transfered into and out of an Advanced Energy Crystal per tick")
				.defineInRange("server.advanced_energy_crystal_max_transfer", 1000000L, 1L, Long.MAX_VALUE);
		
		
		
		transfer_crystal_max_transfer = server
				.comment("The amount of energy a Transfer Crystal can transfer per tick")
				.defineInRange("server.transfer_crystal_max_transfer", 2500L, 1L, Long.MAX_VALUE);
		
		advanced_transfer_crystal_max_transfer = server
				.comment("The amount of energy an Advanced Transfer Crystal can transfer per tick")
				.defineInRange("server.advanced_transfer_crystal_max_transfer", 5000L, 1L, Long.MAX_VALUE);
	}
}
