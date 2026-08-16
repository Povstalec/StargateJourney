package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonNaquadahGeneratorConfig
{
	public static ForgeConfigSpec.IntValue naquadah_rod_max_fuel;
	
	public static ForgeConfigSpec.LongValue naquadah_reactor_reaction_time;
	public static ForgeConfigSpec.LongValue naquadah_reactor_energy_per_tick;
	public static ForgeConfigSpec.LongValue naquadah_reactor_capacity;
	public static ForgeConfigSpec.LongValue naquadah_reactor_max_transfer;
	
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_i_reaction_time;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_i_energy_per_tick;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_i_capacity;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_i_max_transfer;
	
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_ii_reaction_time;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_ii_energy_per_tick;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_ii_capacity;
	public static ForgeConfigSpec.LongValue naquadah_generator_mark_ii_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		naquadah_rod_max_fuel = server
				.comment("The maximum amount of fuel stored in a single Naquadah Fuel Rod")
				.defineInRange("server.naquadah_rod_max_fuel", 256, 1, Integer.MAX_VALUE);
		
		
		
		naquadah_reactor_reaction_time = server
				.comment("The amount of time in ticks for which the Naquadah Reactor will generate energy from one unit of fuel")
				.defineInRange("server.naquadah_reactor_reaction_time", 100L, 1L, Long.MAX_VALUE);
		
		naquadah_reactor_energy_per_tick = server
				.comment("The amount of FE generated per one tick of reaction by the Naquadah Reactor")
				.defineInRange("server.naquadah_reactor_energy_per_tick", 1000L, 1L, Long.MAX_VALUE);
		
		naquadah_reactor_capacity = server
				.comment("The amount of energy a Naquadah Reactor can hold")
				.defineInRange("server.naquadah_reactor_capacity", 100000L, 1L, Long.MAX_VALUE);
		
		naquadah_reactor_max_transfer = server
				.comment("The maximum amount of energy a Naquadah Reactor can transfer at once")
				.defineInRange("server.naquadah_reactor_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		
		
		naquadah_generator_mark_i_reaction_time = server
				.comment("The amount of time in ticks for which the Mark I Naquadah Generator will generate energy from one unit of fuel")
				.defineInRange("server.naquadah_generator_mark_i_reaction_time", 100L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_i_energy_per_tick = server
				.comment("The amount of FE generated per one tick of reaction by the Mark I Naquadah Generator")
				.defineInRange("server.naquadah_generator_mark_i_energy_per_tick", 1000L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_i_capacity = server
				.comment("The amount of energy a Mark I Naquadah Generator can hold")
				.defineInRange("server.naquadah_generator_mark_i_capacity", 100000L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_i_max_transfer = server
				.comment("The maximum amount of energy a Mark I Naquadah Generator can transfer at once")
				.defineInRange("server.naquadah_generator_mark_i_max_transfer", 100000L, 1L, Long.MAX_VALUE);
		
		
		
		naquadah_generator_mark_ii_reaction_time = server
				.comment("The amount of time in ticks for which the Mark II Naquadah Generator will generate energy from one unit of fuel")
				.defineInRange("server.naquadah_generator_mark_ii_reaction_time", 150L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_ii_energy_per_tick = server
				.comment("The amount of FE generated per one tick of reaction by the Mark II Naquadah Generator")
				.defineInRange("server.naquadah_generator_mark_ii_energy_per_tick", 1200L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_ii_capacity = server
				.comment("The amount of energy a Mark II Naquadah Generator can hold")
				.defineInRange("server.naquadah_generator_mark_ii_capacity", 1200000L, 1L, Long.MAX_VALUE);
		
		naquadah_generator_mark_ii_max_transfer = server
				.comment("The maximum amount of energy a Mark II Naquadah Generator can transfer at once")
				.defineInRange("server.naquadah_generator_mark_ii_max_transfer", 1000000L, 1L, Long.MAX_VALUE);
	}
}
