package net.povstalec.sgjourney.common.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.StargateNetworkSettings;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.Galaxy.Serializable;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Transporter;

public class CommandInit
{
	private static final String STARGATE_NETWORK = "stargateNetwork";
	private static final String TRANSPORTER_NETWORK = "transporterNetwork";
	private static final String GENE = "gene";
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		// Stargate Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("address")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getAddress))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("extragalacticAddress")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getExtragalacticAddress))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("getAllStargates")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getStargates))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("version")
								.executes(CommandInit::getVersion)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(0)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("forceStellarUpdate")
								.executes(CommandInit::forceStellarUpdate)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("get")
										.executes(CommandInit::getSettings))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(0)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("set")
										.then(Commands.literal("useDatapackAddresses")
												.then(Commands.argument("useDatapackAddresses", BoolArgumentType.bool())
														.executes(CommandInit::useDatapackAddresses))))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("set")
										.then(Commands.literal("generateRandomSolarSystems")
												.then(Commands.argument("generateRandomSolarSystems", BoolArgumentType.bool())
														.executes(CommandInit::generateRandomSolarSystems))))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("set")
										.then(Commands.literal("randomAddressFromSeed")
												.then(Commands.argument("randomAddressFromSeed", BoolArgumentType.bool())
														.executes(CommandInit::randomAddressFromSeed))))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		
		
		// Rings Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(TRANSPORTER_NETWORK)
						.then(Commands.literal("getAllTransporters")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getTransporters))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(TRANSPORTER_NETWORK)
						.then(Commands.literal("reload")
								.executes(CommandInit::reloadTransporterNetwork)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		
		
		//Gene commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE)
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("ancient").executes(CommandInit::setAncientGene)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE)
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("inherited").executes(CommandInit::setInheritedGene)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE)
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("artificial").executes(CommandInit::setArtificialGene)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE)
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("remove").executes(CommandInit::removeGene))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		
		
		//Dev commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("debugInfo").executes(CommandInit::printStargateNetworkInfo))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		ServerPlayer player = context.getSource().getPlayer();
		
		if(player == null || dimension == null)
		{
			//TODO For each galaxy
			
			return Command.SINGLE_SUCCESS;
		}
		
		Level level = player.level();
		
		ResourceKey<Level> currentDimension = level.dimension();
		
		// List of Galaxies the dialing Dimension is located in
		Optional<HashMap<Serializable, Address.Immutable>> galaxiesOptional = Universe.get(level).getGalaxiesFromDimension(currentDimension);
		
		if(galaxiesOptional.isPresent())
		{
			List<Entry<Serializable, Address.Immutable>> galaxies = galaxiesOptional.get().entrySet().stream().toList();

			if(!galaxies.isEmpty())
			{
				// Creates a chat message for each galaxy the Dimension is located in
				for(int i = 0; i < galaxies.size(); i++)
				{
					Entry<Serializable, Address.Immutable> galaxyEntry = galaxies.get(i);
					Galaxy.Serializable galaxy = galaxyEntry.getKey();
					
					Optional<Address.Immutable> addressOptional = Universe.get(level).getAddressInGalaxyFromDimension(galaxy.getKey().location().toString(), dimension);
					
					if(addressOptional.isEmpty())
						context.getSource().getPlayer().sendSystemMessage(Component.literal(dimension.location().toString() + " ").withStyle(ChatFormatting.GREEN)
								.append(Component.translatable("message.sgjourney.command.get_address.located").withStyle(ChatFormatting.WHITE))
								.append(Component.literal(" ").append(galaxy.getTranslationName()).withStyle(ChatFormatting.LIGHT_PURPLE)));
					else
					{
						Address.Immutable address = addressOptional.get();
						context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.address")
								.append(Component.literal(" " + dimension.location().toString() + " ").withStyle(ChatFormatting.GREEN)).append(Component.translatable("message.sgjourney.command.get_address.in_galaxy"))
								.append(Component.literal(" ").append(galaxy.getTranslationName()).append(Component.literal(" ")).withStyle(ChatFormatting.LIGHT_PURPLE))
								.append(Component.translatable("message.sgjourney.command.get_address.is")));
						
						Style style = Style.EMPTY;
						style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
						style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, address.toString()));
						context.getSource().getPlayer().sendSystemMessage(Component.literal(address.toString()).setStyle(style.applyFormat(ChatFormatting.GOLD)));
					}
				}
				return Command.SINGLE_SUCCESS;
			}
		}
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.no_galaxy").withStyle(ChatFormatting.DARK_RED));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getExtragalacticAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getPlayer().level();
		
		Optional<Address.Immutable> addressOptional = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		
		if(addressOptional.isPresent())
		{
			Address.Immutable address = addressOptional.get();
			
			Style style = Style.EMPTY;
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
			style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, address.toString()));
			
			final Style formatStyle = style;
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_extragalactic_address.address")
					.append(Component.literal(" " + dimension.location().toString() + " ").withStyle(ChatFormatting.GREEN))
					.append(Component.translatable("message.sgjourney.command.get_extragalactic_address.is")), false);
			context.getSource().sendSuccess(() -> Component.literal(address.toString()).setStyle(formatStyle.applyFormat(ChatFormatting.LIGHT_PURPLE)), false);
		}
		else
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_extragalactic_address.none").withStyle(ChatFormatting.DARK_RED), false);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getPlayer().level();
		Optional<SolarSystem.Serializable> solarSystemOptional = Universe.get(level).getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
		{
			SolarSystem.Serializable solarSystem = solarSystemOptional.get();
			if(!solarSystem.getStargates().isEmpty())
			{
				context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_stargates")
						.append(Component.literal(" " + dimension.location().toString()).withStyle(ChatFormatting.GOLD)), false);
				context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
				
				solarSystem.getStargates().stream().forEach(stargate ->
				{
					ResourceKey<Level> stargateDimension = stargate.getDimension();
					BlockPos stargatePos = stargate.getBlockPos();
					
					if(stargateDimension.equals(dimension))
					{
						Style style = Style.EMPTY;
						style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
						style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, stargate.get9ChevronAddress().toString()));
						
						final Style formatStyle = style;
						context.getSource().sendSuccess(() -> Component.literal(stargate.get9ChevronAddress().toString()).setStyle(formatStyle.applyFormat(ChatFormatting.AQUA))
								.append(Component.literal(" X: " + stargatePos.getX() + " Y: " + stargatePos.getY() + " Z: " + stargatePos.getZ()).withStyle(ChatFormatting.BLUE)), false);
					}
				});
				context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
				
				return Command.SINGLE_SUCCESS;
			}
		}
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("No Stargates could be located in " + dimension.location().toString()).withStyle(ChatFormatting.RED));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		int version = StargateNetwork.get(level).getVersion();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_version").append(Component.literal(": " + version)).withStyle(ChatFormatting.GREEN), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int forceStellarUpdate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		StargateNetwork.get(level).stellarUpdate(level.getServer(), true);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stellar_update").withStyle(ChatFormatting.RED), true);
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		boolean useDatapackAddresses = StargateNetworkSettings.get(level).useDatapackAddresses();
		boolean generateRandomSolarSystems = StargateNetworkSettings.get(level).generateRandomSolarSystems();
		boolean randomAddressFromSeed = StargateNetworkSettings.get(level).randomAddressFromSeed();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.use_datapack_addresses").append(Component.literal(": " + useDatapackAddresses)).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.generate_random_solar_systems").append(Component.literal(": " + generateRandomSolarSystems)).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.random_addresses_from_seed").append(Component.literal(": " + randomAddressFromSeed)).withStyle(ChatFormatting.GOLD), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int useDatapackAddresses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "useDatapackAddresses");
		
		StargateNetworkSettings.get(level).setUseDatapackAddresses(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int generateRandomSolarSystems(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "generateRandomSolarSystems");
		
		StargateNetworkSettings.get(level).setGenerateRandomSolarSystems(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomAddressFromSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "randomAddressFromSeed");
		
		StargateNetworkSettings.get(level).setRandomAddressFromSeed(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getTransporters(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getPlayer().level();

		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_transporters")
				.append(Component.literal(" " + dimension.location().toString()).withStyle(ChatFormatting.GOLD)), false);
		context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
		
		Optional<List<Transporter>> transportersOptional = TransporterNetwork.get(level).getTransportersFromDimension(dimension);
		
		if(transportersOptional.isPresent())
		{
			List<Transporter> transporters = transportersOptional.get();
			
			for(int i = 0; i < transporters.size(); i++)
			{
				BlockPos coords = transporters.get(i).getBlockPos();
				context.getSource().sendSuccess(() -> Component.literal("X: " + coords.getX() + " Y: " + coords.getY() + " Z: " + coords.getZ()).withStyle(ChatFormatting.BLUE), false);
			}
		}
		context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int reloadTransporterNetwork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		TransporterNetwork.get(level).reloadNetwork(level.getServer(), true);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.transporter_network_reload").withStyle(ChatFormatting.RED), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setAncientGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.giveGene());
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setInheritedGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.inheritGene());
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setArtificialGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.implantGene());
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int removeGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.removeGene());
		
		return Command.SINGLE_SUCCESS;
	}
	
	//Only used for console checks
	private static int printStargateNetworkInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();

		System.out.println("===============Universe===============");
		Universe.get(level).printDimensions();
		Universe.get(level).printSolarSystems();
		Universe.get(level).printGalaxies();
		
		System.out.println("===============Stargate Network===============");
		BlockEntityList.get(level).printStargates();
		StargateNetwork.get(level).printConnections();

		System.out.println("===============Transporter Network===============");
		BlockEntityList.get(level).printTransporters();
		TransporterNetwork.get(level).printDimensions();

		context.getSource().sendSuccess(() -> Component.literal("Printed info onto the console"), false);
		
		return Command.SINGLE_SUCCESS;
	}
}
