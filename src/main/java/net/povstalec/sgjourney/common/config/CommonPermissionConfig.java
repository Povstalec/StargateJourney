package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonPermissionConfig
{
	public static SGJourneyConfigValue.IntValue protected_stargate_permissions;
	public static SGJourneyConfigValue.IntValue protected_dhd_permissions;
	public static SGJourneyConfigValue.IntValue protected_zpm_hub_permissions;
	
	public static SGJourneyConfigValue.BooleanValue protected_inventory_access;
	public static SGJourneyConfigValue.BooleanValue protected_blocks_ignore_explosions;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		protected_stargate_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_stargate_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected Stargates");
		
		protected_dhd_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_dhd_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected DHDs");
		
		protected_zpm_hub_permissions = new SGJourneyConfigValue.IntValue(server, "server.protected_zpm_hub_permissions",
				0, 0, 4,
				"Decides the player permission level required to modify or break protected ZPM Hubs");
		
		
		
		protected_inventory_access = new SGJourneyConfigValue.BooleanValue(server, "server.protected_inventory_access",
				true,
				"If false, it will be impossible for other blocks (like Hoppers) to access the inventories of protected blocks");
		
		protected_blocks_ignore_explosions = new SGJourneyConfigValue.BooleanValue(server, "server.protected_blocks_ignore_explosions",
				false,
				"If true, protected blocks won't be destroyed during any explosion");
	}
}