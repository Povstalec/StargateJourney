package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonNaquadahGeneratorConfig
{
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_i_reaction_time;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_i_energy_per_tick;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_i_capacity;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_i_max_transfer;
	
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_ii_reaction_time;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_ii_energy_per_tick;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_ii_capacity;
	public static ForgeConfigSpec.IntValue naquadah_generator_mark_ii_max_transfer;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Energy Config");
		
		naquadah_generator_mark_i_reaction_time = server
				.comment("The amount of time in ticks for which the Mark I Naquadah Generator will generate energy from one piece of Weapons Grade Naquadah")
				.defineInRange("server.naquadah_generator_mark_i_reaction_time", 100, 1, 2147483647);
		
		naquadah_generator_mark_i_energy_per_tick = server
				.comment("The amount of FE generated per one tick of reaction by the Mark I Naquadah Generator")
				.defineInRange("server.naquadah_generator_mark_i_energy_per_tick", 1000, 1, 2147483647);
		
		naquadah_generator_mark_i_capacity = server
				.comment("The amount of energy a Mark I Naquadah Generator can hold")
				.defineInRange("server.naquadah_generator_mark_i_capacity", 10000000, 1, 2147483647);
		
		naquadah_generator_mark_i_max_transfer = server
				.comment("The maximum amount of energy a Mark I Naquadah Generator can transfer at once")
				.defineInRange("server.naquadah_generator_mark_i_max_transfer", 100000, 1, 2147483647);
		
		
		
		naquadah_generator_mark_ii_reaction_time = server
				.comment("The amount of time in ticks for which the Mark II Naquadah Generator will generate energy from one piece of Weapons Grade Naquadah")
				.defineInRange("server.naquadah_generator_mark_ii_reaction_time", 150, 1, 2147483647);
		
		naquadah_generator_mark_ii_energy_per_tick = server
				.comment("The amount of FE generated per one tick of reaction by the Mark II Naquadah Generator")
				.defineInRange("server.naquadah_generator_mark_ii_energy_per_tick", 1200, 1, 2147483647);
		
		naquadah_generator_mark_ii_capacity = server
				.comment("The amount of energy a Mark II Naquadah Generator can hold")
				.defineInRange("server.naquadah_generator_mark_ii_capacity", 100000000, 1, 2147483647);
		
		naquadah_generator_mark_ii_max_transfer = server
				.comment("The maximum amount of energy a Mark II Naquadah Generator can transfer at once")
				.defineInRange("server.naquadah_generator_mark_ii_max_transfer", 1000000, 1, 2147483647);
	}
}
