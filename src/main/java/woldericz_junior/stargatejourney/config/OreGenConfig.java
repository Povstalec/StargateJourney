package woldericz_junior.stargatejourney.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class OreGenConfig 
{
	public static ForgeConfigSpec.BooleanValue generate_abydos;
	
	public static ForgeConfigSpec.BooleanValue generate_overworld;
	
	public static ForgeConfigSpec.IntValue naquadah_ore_chance;
	
	public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client)
	{
		server.comment("Oregen Config");
		
		generate_abydos = server
			.comment("Decide if ores added by Stargate Journey will generate in Abydos dimension")
			.define("oregen.generate_abydos", true);
		
		generate_overworld = server
				.comment("Decide if ores added by Stargate Journey will generate in Overworld")
				.define("oregen.generate_overworld", false);
		
		naquadah_ore_chance = server
			.comment("Maximum number of naquadah ore veins in one chunk.")
			.defineInRange("oregen.naquadah_ore_chance", 8, 0, 16);
	}
}
