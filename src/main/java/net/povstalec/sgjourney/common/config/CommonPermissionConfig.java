package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonPermissionConfig
{
	public static SGJourneyConfigValue.IntValue protected_stargate_permissions;
	public static SGJourneyConfigValue.IntValue protected_dhd_permissions;
	public static SGJourneyConfigValue.BooleanValue protected_blocks_ignore_explosions;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		protected_stargate_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_stargate_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected Stargates");
		
		protected_dhd_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_dhd_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected DHDs");
		
		protected_blocks_ignore_explosions = new SGJourneyConfigValue.BooleanValue(server, "server.protected_blocks_ignore_explosions",
				false,
				"Decides a protected block should ignore all explosions");
	}
}