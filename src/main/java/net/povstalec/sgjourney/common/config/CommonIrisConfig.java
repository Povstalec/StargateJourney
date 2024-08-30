package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonIrisConfig
{
	public static ForgeConfigSpec.DoubleValue iris_breaking_strength;
	
	public static ForgeConfigSpec.IntValue copper_iris_durability;
	public static ForgeConfigSpec.IntValue iron_iris_durability;
	public static ForgeConfigSpec.IntValue gold_iris_durability;
	public static ForgeConfigSpec.IntValue diamond_iris_durability;
	public static ForgeConfigSpec.IntValue netherite_iris_durability;

	public static ForgeConfigSpec.IntValue naquadah_alloy_iris_durability;
	public static ForgeConfigSpec.IntValue trinium_iris_durability;

	public static ForgeConfigSpec.IntValue bronze_iris_durability;
	public static ForgeConfigSpec.IntValue steel_iris_durability;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		iris_breaking_strength = server
				.comment("The Iris can break any Blocks with Block Strength below the Iris Strength")
				.defineInRange("server.copper_iris_durability", 0.5, 0, Double.MAX_VALUE);
		
		//TODO Maybe different iris strengths?
		
		copper_iris_durability = server
				.comment("Durability of the Copper Iris")
				.defineInRange("server.copper_iris_durability", 1024, 1, Integer.MAX_VALUE);
		
		iron_iris_durability = server
				.comment("Durability of the Iron Iris")
				.defineInRange("server.iron_iris_durability", 2048, 1, Integer.MAX_VALUE);
		
		gold_iris_durability = server
				.comment("Durability of the Gold Iris")
				.defineInRange("server.gold_iris_durability", 1024, 1, Integer.MAX_VALUE);
		
		diamond_iris_durability = server
				.comment("Durability of the Diamond Iris")
				.defineInRange("server.diamond_iris_durability", 8192, 1, Integer.MAX_VALUE);
		
		netherite_iris_durability = server
				.comment("Durability of the Netherite Iris")
				.defineInRange("server.netherite_iris_durability", 16384, 1, Integer.MAX_VALUE);
		
		
		
		naquadah_alloy_iris_durability = server
				.comment("Durability of the Naquadah Alloy Iris")
				.defineInRange("server.naquadah_alloy_iris_durability", 8192, 1, Integer.MAX_VALUE);
		
		trinium_iris_durability = server
				.comment("Durability of the Trinium Iris")
				.defineInRange("server.trinium_iris_durability", 16384, 1, Integer.MAX_VALUE);
		
		
		
		bronze_iris_durability = server
				.comment("Durability of the Bronze Iris")
				.defineInRange("server.bronze_iris_durability", 4096, 1, Integer.MAX_VALUE);
		
		steel_iris_durability = server
				.comment("Durability of the Steel Iris")
				.defineInRange("server.steel_iris_durability", 4096, 1, Integer.MAX_VALUE);
	}
}
