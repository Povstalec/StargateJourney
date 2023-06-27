package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonGeneticConfig
{
	public static SGJourneyConfigValue.IntValue player_ata_gene_inheritance_chance;
	public static SGJourneyConfigValue.IntValue villager_player_ata_gene_inheritance_chance;

	public static SGJourneyConfigValue.IntValue prototype_ata_gene_therapy_success_rate;
	public static SGJourneyConfigValue.IntValue ata_gene_therapy_success_rate;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Genetic Config");
		
		player_ata_gene_inheritance_chance = new SGJourneyConfigValue.IntValue(server, "server.player_ata_gene_inheritance_chance", 
				30, 0, 100, 
				"Percentage of Players who will inherit the Ancient Gene");
		
		villager_player_ata_gene_inheritance_chance = new SGJourneyConfigValue.IntValue(server, "server.villager_player_ata_gene_inheritance_chance", 
				30, 0, 100, 
				"Percentage of Villagers who will inherit the Ancient Gene");
		
		
		
		prototype_ata_gene_therapy_success_rate = new SGJourneyConfigValue.IntValue(server, "server.prototype_ata_gene_therapy_success_rate", 
				50, 0, 100, 
				"Probability of the Prototype Ancient Gene Therapy working");
		
		ata_gene_therapy_success_rate = new SGJourneyConfigValue.IntValue(server, "server.ata_gene_therapy_success_rate", 
				100, 0, 100, 
				"Probability of the Ancient Gene Therapy working");
	}
}
