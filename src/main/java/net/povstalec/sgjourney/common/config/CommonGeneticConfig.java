package net.povstalec.sgjourney.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CommonGeneticConfig
{
	public static SGJourneyConfigValue.IntValue player_ata_gene_inheritance_chance;
	public static SGJourneyConfigValue.IntValue villager_ata_gene_inheritance_chance;
	public static SGJourneyConfigValue.IntValue human_ata_gene_inheritance_chance;

	public static SGJourneyConfigValue.IntValue prototype_ata_gene_therapy_success_rate;
	public static SGJourneyConfigValue.IntValue ata_gene_therapy_success_rate;
	
	public static ModConfigSpec.ConfigValue<List<? extends String>> ancient_players;
	public static ModConfigSpec.ConfigValue<List<? extends String>> inherited_ancient_gene_players;
	public static ModConfigSpec.ConfigValue<List<? extends String>> artificial_ancient_gene_players;
	public static ModConfigSpec.ConfigValue<List<? extends String>> no_ancient_gene_players;
	
	public static void init(ModConfigSpec.Builder server)
	{
		ancient_players = server.comment("A list of Player names who will receive the Ancient Gene when they join the world for the first time")
				.defineList("server.ancient_players", () -> List.of("Dev", "Woldericz_junior", "cookta2012", "mistersecret312", "_MaGistR____", "Redangel121"), name -> true);
		
		inherited_ancient_gene_players = server.comment("A list of Player names who will inherit the Ancient Gene when they join the world for the first time")
				.defineList("server.inherited_ancient_gene_players", List::of, name -> true);
		
		artificial_ancient_gene_players = server.comment("A list of Player names who will be implanted with the artificial Ancient Gene when they join the world for the first time")
				.defineList("server.artificial_ancient_gene_players", List::of, name -> true);
		
		no_ancient_gene_players = server.comment("A list of Player names who will not be able to inherit Ancient Gene when they join the world for the first time")
				.defineList("server.no_ancient_gene_players", List::of, name -> true);
		
		
		
		player_ata_gene_inheritance_chance = new SGJourneyConfigValue.IntValue(server, "server.player_ata_gene_inheritance_chance", 
				30, 0, 100, 
				"Percentage of Players who will inherit the Ancient Gene");
		
		villager_ata_gene_inheritance_chance = new SGJourneyConfigValue.IntValue(server, "server.villager_ata_gene_inheritance_chance",
				30, 0, 100, 
				"Percentage of Villagers who will inherit the Ancient Gene");
		
		human_ata_gene_inheritance_chance = new SGJourneyConfigValue.IntValue(server, "server.human_ata_gene_inheritance_chance",
				30, 0, 100,
				"Percentage of Humans who will inherit the Ancient Gene");
		
		
		
		prototype_ata_gene_therapy_success_rate = new SGJourneyConfigValue.IntValue(server, "server.prototype_ata_gene_therapy_success_rate", 
				50, 0, 100, 
				"Probability of the Prototype Ancient Gene Therapy working");
		
		ata_gene_therapy_success_rate = new SGJourneyConfigValue.IntValue(server, "server.ata_gene_therapy_success_rate", 
				100, 0, 100, 
				"Probability of the Ancient Gene Therapy working");
	}
}
