package net.povstalec.sgjourney.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;

@Mod.EventBusSubscriber
public class StargateJourneyConfig
{
	private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec server_config;
	
	private static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec client_config;
	
	public static ForgeConfigSpec.BooleanValue disable_energy_use;
	public static ForgeConfigSpec.BooleanValue disable_smooth_animations;
	
	static
	{
		generalServerConfig(server_builder);
		generalClientConfig(client_builder);
		
		ServerZPMConfig.init(server_builder);
		ServerInterfaceConfig.init(server_builder);
		ServerStargateConfig.init(server_builder);
		ServerNaquadahGeneratorConfig.init(server_builder);
		ServerStargateNetworkConfig.init(server_builder);
		ServerTechConfig.init(server_builder);
		ServerGeneticConfig.init(server_builder);
		
		ClientStargateConfig.init(client_builder);
		
		server_config = server_builder.build();
		client_config = client_builder.build();
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
		
		// General Energy
		disable_energy_use = server
				.comment("Disable energy requirements for blocks added by Stargate Journey")
				.define("server.disable_energy_requirements", true);
	}
	
	private static void generalClientConfig(ForgeConfigSpec.Builder client)
	{
		client.comment("Stargate Journey Client Config");
		
		disable_smooth_animations = client
				.comment("Disables smooth animations of Stargate Journey Block Entities")
				.define("client.disable_smooth_animations", false);
	}
}
