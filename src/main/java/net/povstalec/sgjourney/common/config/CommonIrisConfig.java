package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonIrisConfig
{
	public static ModConfigSpec.BooleanValue creative_ignores_iris;
	
	public static ModConfigSpec.DoubleValue iris_breaking_strength;

	public static ModConfigSpec.IntValue copper_iris_durability;
	public static ModConfigSpec.IntValue iron_iris_durability;
	public static ModConfigSpec.IntValue gold_iris_durability;
	public static ModConfigSpec.IntValue diamond_iris_durability;
	public static ModConfigSpec.IntValue netherite_iris_durability;

	public static ModConfigSpec.IntValue naquadah_alloy_iris_durability;
	public static ModConfigSpec.IntValue trinium_iris_durability;

	public static ModConfigSpec.IntValue bronze_iris_durability;
	public static ModConfigSpec.IntValue steel_iris_durability;
	
	public static void init(ModConfigSpec.Builder server)
	{
		creative_ignores_iris = server
				.comment("If true, players in Creative Mode will be able to pass through the Stargate even when the Iris is closed on the other side")
				.define("server.creative_ignores_iris", false);
		
		
		
		iris_breaking_strength = server
				.comment("The Iris can break any Blocks with Block Strength below the Iris Strength")
				.defineInRange("server.iris_breaking_strength", 0.5, 0, Double.MAX_VALUE);
		
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
