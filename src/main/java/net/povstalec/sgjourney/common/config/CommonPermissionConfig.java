package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonPermissionConfig
{
	public static SGJourneyConfigValue.IntValue protected_stargate_permissions;
	public static SGJourneyConfigValue.IntValue protected_dhd_permissions;
	
	public static void init(ModConfigSpec.Builder server)
	{
		protected_stargate_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_stargate_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected Stargates");
		
		protected_dhd_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_dhd_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected DHDs");
	}
}