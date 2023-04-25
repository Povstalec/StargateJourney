package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonTechConfig
{
	public static ForgeConfigSpec.BooleanValue disable_kara_kesh_requirements;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Tech Config");
		
		disable_kara_kesh_requirements = server
				.comment("If true Kara Kesh won't require its user to have Naquadah in their bloodstream")
				.define("server.disable_kara_kesh_requirements", true);
	}
}
