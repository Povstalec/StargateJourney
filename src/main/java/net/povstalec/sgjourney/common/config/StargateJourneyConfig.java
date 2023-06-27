package net.povstalec.sgjourney.common.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;

@Mod.EventBusSubscriber
public class StargateJourneyConfig
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec COMMON_CONFIG;
	
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec CLIENT_CONFIG;
	
	public static SGJourneyConfigValue.BooleanValue disable_energy_use;
	public static SGJourneyConfigValue.BooleanValue disable_smooth_animations;
	
	static
	{
		COMMON_BUILDER.push("Stargate Journey Common Config");
		
		generalServerConfig(COMMON_BUILDER);
		CommonZPMConfig.init(COMMON_BUILDER);
		CommonInterfaceConfig.init(COMMON_BUILDER);
		CommonStargateConfig.init(COMMON_BUILDER);
		CommonNaquadahGeneratorConfig.init(COMMON_BUILDER);
		CommonStargateNetworkConfig.init(COMMON_BUILDER);
		CommonTechConfig.init(COMMON_BUILDER);
		CommonGeneticConfig.init(COMMON_BUILDER);
		
		COMMON_BUILDER.pop();
		COMMON_CONFIG = COMMON_BUILDER.build();
		
		

		CLIENT_BUILDER.push("Stargate Journey Client Config");
		
		generalClientConfig(CLIENT_BUILDER);
		ClientStargateConfig.init(CLIENT_BUILDER);

		CLIENT_BUILDER.pop();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path)
	{
		StargateJourney.LOGGER.info("Loading Config: " + path);
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		StargateJourney.LOGGER.info("Built config: " + path);
		file.load();
		StargateJourney.LOGGER.info("Loaded Config: " + path);
		config.setConfig(file);
	}
	
	private static void generalServerConfig(ForgeConfigSpec.Builder server)
	{
		server.comment("Stargate Journey General Config");
		
		disable_energy_use = new SGJourneyConfigValue.BooleanValue(server, "server.disable_energy_requirements", 
				true, 
				"Disable energy requirements for blocks added by Stargate Journey");
	}
	
	private static void generalClientConfig(ForgeConfigSpec.Builder client)
	{
		client.comment("Stargate Journey Client Config");
		
		disable_smooth_animations = new SGJourneyConfigValue.BooleanValue(client, "client.disable_smooth_animations", 
				false, 
				"Disables smooth animations of Stargate Journey Block Entities");
	}
}
