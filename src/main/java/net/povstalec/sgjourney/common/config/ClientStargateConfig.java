package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientStargateConfig
{
	public static ForgeConfigSpec.BooleanValue use_movie_stargate_model;
	public static ForgeConfigSpec.BooleanValue milky_way_stargate_back_lights_up;
	public static ForgeConfigSpec.BooleanValue pegasus_stargate_back_lights_up;

	public static ForgeConfigSpec.IntValue universe_r;
	public static ForgeConfigSpec.IntValue universe_g;
	public static ForgeConfigSpec.IntValue universe_b;

	public static ForgeConfigSpec.IntValue milky_way_r;
	public static ForgeConfigSpec.IntValue milky_way_g;
	public static ForgeConfigSpec.IntValue milky_way_b;

	public static ForgeConfigSpec.IntValue pegasus_r;
	public static ForgeConfigSpec.IntValue pegasus_g;
	public static ForgeConfigSpec.IntValue pegasus_b;

	public static ForgeConfigSpec.IntValue classic_r;
	public static ForgeConfigSpec.IntValue classic_g;
	public static ForgeConfigSpec.IntValue classic_b;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		client.comment("Stargate Journey Client Stargate Config");
		
		use_movie_stargate_model = client
				.comment("Decide if Milky Way Stargate should use the Movie Stargate model")
				.define("client.use_movie_stargate_model", false);
		
		milky_way_stargate_back_lights_up = client
				.comment("Decide if Chevrons on the back of Milky Way Stargate should light up")
				.define("client.milky_way_stargate_back_lights_up", false);
		
		pegasus_stargate_back_lights_up = client
				.comment("Decide if Chevrons on the back of Pegasus Stargate should light up")
				.define("client.pegasus_stargate_back_lights_up", true);
		
		
		
		universe_r = client
				.comment("The amount of Red in Universe Stargate's Event Horizon")
				.defineInRange("client.universe_r", 225, 0, 255);
		
		universe_g = client
				.comment("The amount of Green in Universe Stargate's Event Horizon")
				.defineInRange("client.universe_g", 225, 0, 255);
		
		universe_b = client
				.comment("The amount of Blue in Universe Stargate's Event Horizon")
				.defineInRange("client.universe_b", 255, 0, 255);
		
		
		
		milky_way_r = client
				.comment("The amount of Red in Milky Way Stargate's Event Horizon")
				.defineInRange("client.milky_way_r", 55, 0, 255);
		
		milky_way_g = client
				.comment("The amount of Green in Milky Way Stargate's Event Horizon")
				.defineInRange("client.milky_way_g", 55, 0, 255);
		
		milky_way_b = client
				.comment("The amount of Blue in Milky Way Stargate's Event Horizon")
				.defineInRange("client.milky_way_b", 255, 0, 255);
		
		
		
		pegasus_r = client
				.comment("The amount of Red in Pegasus Stargate's Event Horizon")
				.defineInRange("client.pegasus_r", 55, 0, 255);
		
		pegasus_g = client
				.comment("The amount of Green in Pegasus Stargate's Event Horizon")
				.defineInRange("client.pegasus_g", 55, 0, 255);
		
		pegasus_b = client
				.comment("The amount of Blue in Pegasus Stargate's Event Horizon")
				.defineInRange("client.pegasus_b", 255, 0, 255);
		
		
		
		classic_r = client
				.comment("The amount of Red in Classic Stargate's Event Horizon")
				.defineInRange("client.classic_r", 55, 0, 255);
		
		classic_g = client
				.comment("The amount of Green in Classic Stargate's Event Horizon")
				.defineInRange("client.classic_g", 55, 0, 255);
		
		classic_b = client
				.comment("The amount of Blue in Classic Stargate's Event Horizon")
				.defineInRange("client.classic_b", 255, 0, 255);
	}
}
