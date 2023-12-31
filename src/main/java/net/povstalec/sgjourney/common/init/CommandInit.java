package net.povstalec.sgjourney.common.init;

import java.util.List;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.StargateNetworkSettings;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.data.Universe;

public class CommandInit
{
	private static final String EMPTY = StargateJourney.EMPTY;
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
				.then(Commands.literal("info").executes(CommandInit::printStargateNetworkInfo))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().getLevel();
		
		String currentDimension = level.dimension().location().toString();
		
		// List of Galaxies the dialing Dimension is located in
		ListTag galaxies = Universe.get(level).getGalaxiesFromDimension(currentDimension);
		
		if(galaxies.isEmpty())
			context.getSource().getPlayer().sendSystemMessage(Component.literal("You are not located in any Galaxy").withStyle(ChatFormatting.DARK_RED));
		else
		{
			// Makes a chat message for each galaxy the Dimension is located in
			for(int i = 0; i < galaxies.size(); i++)
			{
				String galaxy = galaxies.getCompound(i).getAllKeys().iterator().next();
				String address = Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension);
				if(address.equals(EMPTY))
					context.getSource().getPlayer().sendSystemMessage(Component.literal(dimension + " ").withStyle(ChatFormatting.GOLD)
							.append(Component.translatable("message.sgjourney.command.get_address.located"))
							.append(Component.literal(" " + galaxy).withStyle(ChatFormatting.LIGHT_PURPLE)));
				else
				{
					context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.address")
							.append(Component.literal(" " + dimension + " ").withStyle(ChatFormatting.GOLD)).append(Component.translatable("message.sgjourney.command.get_address.in_galaxy"))
							.append(Component.literal(" " + galaxy + " ").withStyle(ChatFormatting.LIGHT_PURPLE))
							.append(Component.translatable("message.sgjourney.command.get_address.is")));
					context.getSource().getPlayer().sendSystemMessage(Component.literal(address).withStyle(ChatFormatting.GOLD));
				}
			}
			
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getExtragalacticAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().getLevel();
		
		String address = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_extragalactic_address.address")
				.append(Component.literal(" " + dimension + " ").withStyle(ChatFormatting.GOLD))
				.append(Component.translatable("message.sgjourney.command.get_extragalactic_address.is")));
		context.getSource().getPlayer().sendSystemMessage(Component.literal(address).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().getLevel();
		String solarSystem = Universe.get(level).getSolarSystemFromDimension(dimension);
		
		if(!solarSystem.isEmpty())
		{
			context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_stargates")
					.append(Component.literal(" " + dimension).withStyle(ChatFormatting.GOLD)));
			context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
			CompoundTag stargates = StargateNetwork.get(level).getSolarSystem(solarSystem);
			stargates.getAllKeys().forEach(stargateID ->
			{
				CompoundTag stargate = stargates.getCompound(stargateID);
				String stargateDimension = stargate.getString("Dimension");
				
				if(stargateDimension.equals(dimension))
				{
					int[] coordinates = stargate.getIntArray("Coordinates");
					context.getSource().getPlayer().sendSystemMessage(Component.literal(stargateID).withStyle(ChatFormatting.AQUA)
							.append(Component.literal(" X: " + coordinates[0] + " Y: " + coordinates[1] + " Z: " + coordinates[2]).withStyle(ChatFormatting.BLUE)));
				}
			});
			context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		}
		else
			context.getSource().getPlayer().sendSystemMessage(Component.literal("No Stargates could be located in " + dimension).withStyle(ChatFormatting.RED));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().getLevel();
		
		int version = StargateNetwork.get(level).getVersion();
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_version").append(Component.literal(": " + version)).withStyle(ChatFormatting.GREEN));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int forceStellarUpdate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().getLevel();
		
		StargateNetwork.get(level).stellarUpdate(level.getServer(), true);
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stellar_update").withStyle(ChatFormatting.RED));
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		
		boolean useDatapackAddresses = StargateNetworkSettings.get(level).useDatapackAddresses();
		boolean generateRandomSolarSystems = StargateNetworkSettings.get(level).generateRandomSolarSystems();
		boolean randomAddressFromSeed = StargateNetworkSettings.get(level).randomAddressFromSeed();
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.use_datapack_addresses").append(Component.literal(": " + useDatapackAddresses)).withStyle(ChatFormatting.GOLD));
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.generate_random_solar_systems").append(Component.literal(": " + generateRandomSolarSystems)).withStyle(ChatFormatting.GOLD));
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.random_addresses_from_seed").append(Component.literal(": " + randomAddressFromSeed)).withStyle(ChatFormatting.GOLD));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int useDatapackAddresses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		boolean setting = BoolArgumentType.getBool(context, "useDatapackAddresses");
		
		StargateNetworkSettings.get(level).setUseDatapackAddresses(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int generateRandomSolarSystems(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		boolean setting = BoolArgumentType.getBool(context, "generateRandomSolarSystems");
		
		StargateNetworkSettings.get(level).setGenerateRandomSolarSystems(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomAddressFromSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		boolean setting = BoolArgumentType.getBool(context, "randomAddressFromSeed");
		
		StargateNetworkSettings.get(level).setRandomAddressFromSeed(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getTransporters(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().getLevel();

		context.getSource().getPlayer().sendSystemMessage(Component.translatable("message.sgjourney.command.get_transporters")
				.append(Component.literal(" " + dimension).withStyle(ChatFormatting.GOLD)));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		CompoundTag ringsNetwork = TransporterNetwork.get(level).getRings(dimension);
		System.out.println(ringsNetwork);
		List<String> ringsNList = ringsNetwork.getAllKeys().stream().toList();
		for(int i = 0; i < ringsNList.size(); i++)
		{
			int[] coords = ringsNetwork.getCompound(ringsNList.get(i)).getIntArray("Coordinates");
			context.getSource().getPlayer().sendSystemMessage(Component.literal("X: " + coords[0] + " Y: " + coords[1] + " Z: " + coords[2]).withStyle(ChatFormatting.BLUE));
		}
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
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
		Level level = context.getSource().getPlayer().getLevel();

		System.out.println("Dimensions:\n" + Universe.get(level).getDimensions());
		System.out.println("Solar Systems:\n" + Universe.get(level).getSolarSystems());
		System.out.println("Galaxies:\n" + Universe.get(level).getGalaxies());
		System.out.println("Extragalactic Addresses:\n" + Universe.get(level).getExtragalacticAddressInfo());
		System.out.println("=============================");
		System.out.println("Stargates:\n" + StargateNetwork.get(level).getStargates());
		System.out.println("Stargates in Solar Systems:\n" + StargateNetwork.get(level).getSolarSystems());
		//System.out.println("Connections:\n" + StargateNetwork.get(level).getConnections());

		context.getSource().getPlayer().sendSystemMessage(Component.literal("Printed info onto the console"));
		
		return Command.SINGLE_SUCCESS;
	}
}
