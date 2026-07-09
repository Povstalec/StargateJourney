package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientDHDConfig
{
	public static SGJourneyConfigValue.BooleanValue dhd_symbols_numbers;
	
	public static SGJourneyConfigValue.BooleanValue milky_way_dhd_canon_button_layout;
	public static SGJourneyConfigValue.BooleanValue pegasus_dhd_canon_button_layout;
	public static SGJourneyConfigValue.BooleanValue classic_dhd_canon_button_layout;
	
	public static void init(ForgeConfigSpec.Builder client)
	{
		dhd_symbols_numbers = new SGJourneyConfigValue.BooleanValue(client, "client.dhd_symbols_numbers",
				true,
				"If true, DHD buttons will render symbols by default, otherwise they'll be render numbers by default");
		
		
		
		milky_way_dhd_canon_button_layout = new SGJourneyConfigValue.BooleanValue(client, "client.milky_way_dhd_canon_button_layout",
				false,
				"If true, Milky Way DHD symbol positions won't be ordered and instead be based on their canon positions");
		
		pegasus_dhd_canon_button_layout = new SGJourneyConfigValue.BooleanValue(client, "client.pegasus_dhd_canon_button_layout",
				false,
				"If true, Pegasus DHD symbol positions won't be ordered and instead be based on their canon positions");
		
		classic_dhd_canon_button_layout = new SGJourneyConfigValue.BooleanValue(client, "client.classic_dhd_canon_button_layout",
				false,
				"If true, Classic DHD symbol positions won't be ordered and instead be based on the canon symbol positions of the Milky Way DHD");
	}
}
