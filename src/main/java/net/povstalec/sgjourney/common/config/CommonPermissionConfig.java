package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonPermissionConfig
{
	public static SGJourneyConfigValue.IntValue protected_stargate_permissions;
	public static SGJourneyConfigValue.IntValue protected_dhd_permissions;
	public static SGJourneyConfigValue.IntValue protected_zpmHub_permissions;
	public static SGJourneyConfigValue.IntValue protected_block_permissions;

	public static void init(ModConfigSpec.Builder server)
	{
		protected_stargate_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_stargate_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected Stargates");
		
		protected_dhd_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_dhd_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected DHDs");


		protected_zpmHub_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_zpmHub_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected ZPM Nubs");

		protected_block_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_block_permissions",
				0, 0, 4,
				"Decides the player permission level required to break protected blocks that do not have a protected inventory.");
	}
}