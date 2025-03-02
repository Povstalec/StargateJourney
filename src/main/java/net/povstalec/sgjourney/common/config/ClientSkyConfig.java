package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientSkyConfig
{
	public static SGJourneyConfigValue.BooleanValue custom_abydos_sky;
	public static SGJourneyConfigValue.BooleanValue custom_chulak_sky;
	public static SGJourneyConfigValue.BooleanValue custom_cavum_tenebrae_sky;
	public static SGJourneyConfigValue.BooleanValue custom_unitas_sky;

	public static SGJourneyConfigValue.BooleanValue custom_lantea_sky;
	public static SGJourneyConfigValue.BooleanValue custom_athos_sky;
	
	public static void init(ModConfigSpec.Builder client)
	{
		custom_abydos_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_abydos_sky", 
				true, 
				"If true you will render a custom Abydos sky");
		
		custom_chulak_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_chulak_sky", 
				true, 
				"If true you will render a custom Chulak sky");
		
		custom_unitas_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_unitas_sky",
				true,
				"If true you will render a custom Unitas sky");
		
		custom_cavum_tenebrae_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_cavum_tenebrae_sky", 
				true, 
				"If true you will render a custom Cavum Tenebrae sky");
		
		
		
		custom_lantea_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_lantea_sky", 
				true, 
				"If true you will render a custom Lantea sky");
		
		custom_athos_sky = new SGJourneyConfigValue.BooleanValue(client, "client.custom_athos_sky", 
				true, 
				"If true you will render a custom Athos sky");
	}
}
