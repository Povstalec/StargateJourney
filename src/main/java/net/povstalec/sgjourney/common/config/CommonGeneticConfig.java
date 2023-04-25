package net.povstalec.sgjourney.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonGeneticConfig
{
	public static ForgeConfigSpec.IntValue player_ata_gene_inheritance_chance;
	public static ForgeConfigSpec.IntValue villager_player_ata_gene_inheritance_chance;

	public static ForgeConfigSpec.IntValue prototype_ata_gene_therapy_success_rate;
	public static ForgeConfigSpec.IntValue ata_gene_therapy_success_rate;
	
	public static void init(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey Genetic Config");
		
		player_ata_gene_inheritance_chance = server
				.comment("Percentage of Players who will inherit the Ancient Gene.")
				.defineInRange("server.player_ata_gene_inheritance_chance", 30, 0, 100);
		
		villager_player_ata_gene_inheritance_chance = server
				.comment("Percentage of Villagers who will inherit the Ancient Gene.")
				.defineInRange("server.villager_player_ata_gene_inheritance_chance", 30, 0, 100);
		
		
		
		prototype_ata_gene_therapy_success_rate = server
				.comment("Probability of the Prototype Gene Therapy working.")
				.defineInRange("server.prototype_ata_gene_therapy_success_rate", 50, 0, 100);
		
		ata_gene_therapy_success_rate = server
				.comment("Probability of the Gene Therapy working.")
				.defineInRange("server.ata_gene_therapy_success_rate", 100, 0, 100);
	}
}
