package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientDHDConfig
{
	public static ForgeConfigSpec.BooleanValue canon_symbol_positions;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		canon_symbol_positions = client
				.comment("If true, symbol positions won't be ordered and instead be based on the canon")
				.define("client.canon_symbol_positions", false);
	}
}
