package net.povstalec.sgjourney.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientStargateConfig
{
	public static ForgeConfigSpec.BooleanValue use_movie_stargate_model;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		client.comment("Stargate Journey Client Stargate Config");
		
		use_movie_stargate_model = client
				.comment("Decide if Milky Way Stargate should use the Movie Stargate model(true/false)")
				.define("client.use_movie_stargate_model", false);
	}
}
