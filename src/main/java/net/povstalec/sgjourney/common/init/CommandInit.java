package net.povstalec.sgjourney.common.init;

import java.util.List;
import java.util.Map;

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
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.common.command.AddressArgumentType;
import net.povstalec.sgjourney.common.command.AddressArgumentInfo;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.data.*;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.Galaxy;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class CommandInit
{
	private static final String STARGATE_NETWORK = "stargateNetwork";
	private static final String TRANSPORTER_NETWORK = "transporterNetwork";
	private static final String GENE = "gene";
	
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, StargateJourney.MODID);
	
	public static final RegistryObject<ArgumentTypeInfo<AddressArgumentType, AddressArgumentInfo.Template>> ADDRESS_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("address",
			() -> ArgumentTypeInfos.registerByClass(AddressArgumentType.class, new AddressArgumentInfo()));
	
	public static void register(IEventBus eventBus)
	{
		COMMAND_ARGUMENT_TYPES.register(eventBus);
	}
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		// Stargate Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("address").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getAddress)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("extragalacticAddress").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getExtragalacticAddress)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("getAllStargates").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getStargates)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("version").requires(commandSourceStack -> commandSourceStack.hasPermission(0))
								.executes(CommandInit::getStargateNetworkVersion))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("forceStellarUpdate").requires(commandSourceStack -> commandSourceStack.hasPermission(CommonPermissionConfig.stellar_update_permissions.get()))
								.executes(CommandInit::forceStellarUpdate))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("get").requires(commandSourceStack -> commandSourceStack.hasPermission(0))
										.executes(CommandInit::getSettings)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("randomizeAddresses").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("set")
												.then(Commands.argument("randomizeAddresses", BoolArgumentType.bool())
														.executes(CommandInit::randomizeAddresses)))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("generateRandomSolarSystems").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("set")
												.then(Commands.argument("generateRandomSolarSystems", BoolArgumentType.bool())
														.executes(CommandInit::generateRandomSolarSystems)))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("randomAddressFromSeed").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("set")
												.then(Commands.argument("randomAddressFromSeed", BoolArgumentType.bool())
														.executes(CommandInit::randomAddressFromSeed)))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("primaryStargatePriority").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("set")
												.then(Commands.argument("prioritizePrimaryStargates", BoolArgumentType.bool())
														.executes(CommandInit::prioritizePrimaryStargate)))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("primaryStargate").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("set")
												.then(Commands.argument("dimension", DimensionArgument.dimension())
														.then(Commands.argument("address", new AddressArgumentType(Address.Type.ADDRESS_9_CHEVRON))
																.executes(CommandInit::setPrimaryStargate))))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("primaryStargate").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("unset")
												.then(Commands.argument("dimension", DimensionArgument.dimension())
														.executes(CommandInit::unsetPrimaryStargate)))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("settings")
								.then(Commands.literal("primaryStargate").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
										.then(Commands.literal("get")
												.then(Commands.argument("dimension", DimensionArgument.dimension())
														.executes(CommandInit::getPrimaryStargate)))))));
		
		
		
		// Transporter Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(TRANSPORTER_NETWORK)
						.then(Commands.literal("getAllTransporters").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getTransporters)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(TRANSPORTER_NETWORK)
						.then(Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
								.executes(CommandInit::reloadTransporterNetwork))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(TRANSPORTER_NETWORK)
						.then(Commands.literal("version").requires(commandSourceStack -> commandSourceStack.hasPermission(0))
								.executes(CommandInit::getTransportereNetworkVersion))));
		
		
		
		//Gene commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE).requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("ancient").executes(CommandInit::setAncientGene))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE).requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("inherited").executes(CommandInit::setInheritedGene))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE).requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("add")
										.then(Commands.literal("artificial").executes(CommandInit::setArtificialGene))))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(GENE).requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.argument("target", EntityArgument.entity())
								.then(Commands.literal("remove").executes(CommandInit::removeGene)))));
		
		
		
		//Protection commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("protection").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.literal("set")
								.then(Commands.argument("pos", BlockPosArgument.blockPos())
										.executes(CommandInit::setProtected)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("protection").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.then(Commands.literal("unset")
								.then(Commands.argument("pos", BlockPosArgument.blockPos())
										.executes(CommandInit::unsetProtected)))));
		
		
		
		//Dev commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("debugInfo").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
						.executes(CommandInit::printStargateNetworkInfo)));
	}
	
	private static Component dimensionComponent(ResourceKey<Level> dimension)
	{
		return Component.literal(dimension.location().toString()).withStyle(ChatFormatting.GREEN);
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{try{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		ResourceKey<Level> currentDimension = level.dimension();
		
		Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxyMap = Universe.get(level).getGalaxiesFromDimension(currentDimension);
		if(galaxyMap == null || galaxyMap.isEmpty())
		{
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.no_galaxy").withStyle(ChatFormatting.DARK_RED));
			return Command.SINGLE_SUCCESS;
		}
		
		Universe universe = Universe.get(level);
		for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxyEntry : galaxyMap.entrySet())
		{
			Galaxy galaxy = universe.getGalaxy(galaxyEntry.getKey());
			if(galaxy != null)
			{
				Address.Immutable address = universe.getAddressInGalaxyFromDimension(galaxy.getResourceKey(), dimension);
				
				if(address == null)
					context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.no_address", dimensionComponent(dimension), galaxy.toComponent()).withStyle(ChatFormatting.DARK_RED));
				else
					context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.address", dimensionComponent(dimension), galaxy.toComponent(), address.toComponent(true)));
			}
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getExtragalacticAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		
		Address.Immutable address = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		
		if(address == null)
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_extragalactic_address.none", dimensionComponent(dimension)), false);
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_extragalactic_address.address", dimensionComponent(dimension), address.toComponent(true)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		
		List<Stargate> stargates = StargateNetwork.get(level).getStargatesInDimension(dimension);
		
		if(!stargates.isEmpty())
		{
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_stargates.stargates", dimensionComponent(dimension)), false);
			context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
			
			stargates.forEach(stargate ->
			{
				ResourceKey<Level> stargateDimension = stargate.getDimension();
				Vec3 stargatePos = stargate.getPosition();
				
				if(dimension.equals(stargateDimension) && stargatePos != null)
					context.getSource().sendSuccess(() -> stargate.get9ChevronAddress().toComponent(true).append(" ").append(ComponentHelper.coordinate(stargatePos)), false);
			});
			context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
		}
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_stargates.no_stargates", dimensionComponent(dimension)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargateNetworkVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		int version = StargateNetwork.get(level).getVersion();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_version").append(Component.literal(": " + version)).withStyle(ChatFormatting.GREEN), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int forceStellarUpdate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		StargateNetwork.get(level).stellarUpdate();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stellar_update").withStyle(ChatFormatting.RED), true);
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		boolean randomizeAddresses = StargateNetworkSettings.get(level).randomizeAddresses();
		boolean generateRandomSolarSystems = StargateNetworkSettings.get(level).generateRandomAddressRegions();
		boolean randomAddressFromSeed = StargateNetworkSettings.get(level).randomAddressFromSeed();
		boolean primaryStargatePriority = StargateNetworkSettings.get(level).prioritizePrimaryStargates();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.randomize_addresses").append(": " + randomizeAddresses).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.generate_random_solar_systems").append(": " + generateRandomSolarSystems).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.random_addresses_from_seed").append(": " + randomAddressFromSeed).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.primary_stargate_priority").append(": " + primaryStargatePriority).withStyle(ChatFormatting.GOLD), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomizeAddresses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "randomizeAddresses");
		
		StargateNetworkSettings.get(level).setRandomizeAddresses(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int generateRandomSolarSystems(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "generateRandomSolarSystems");
		
		StargateNetworkSettings.get(level).setGenerateRandomAddressRegions(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomAddressFromSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "randomAddressFromSeed");
		
		StargateNetworkSettings.get(level).setRandomAddressFromSeed(setting);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int prioritizePrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean prioritize = BoolArgumentType.getBool(context, "prioritizePrimaryStargates");
		
		StargateNetworkSettings.get(level).setPrioritizePrimaryStargates(prioritize);
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Address.Immutable address = AddressArgumentType.getAddress(context, "address");
		
		Level level = context.getSource().getLevel();
		
		if(StargateNetwork.get(level).setPrimaryAddressForDimension(dimension, address))
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.primary_stargate_set").withStyle(ChatFormatting.DARK_GREEN), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int unsetPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		
		if(StargateNetwork.get(level).setPrimaryAddressForDimension(dimension, null))
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.primary_stargate_unset").withStyle(ChatFormatting.GREEN), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		
		Address.Immutable address = StargateNetwork.get(level).getPrimaryAddressFromDimension(dimension);
		if(address != null)
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.primary_stargate").append(Component.literal(": ").append(address.toComponent(true))).withStyle(ChatFormatting.AQUA), true);
		else
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.primary_stargate_none").withStyle(ChatFormatting.RED), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getTransporters(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		
		List<Transporter> transporters = TransporterNetwork.get(level).getTransportersInDimension(dimension);
		
		if(!transporters.isEmpty())
		{
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.get_transporters.transporters", dimensionComponent(dimension)), false);
			context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
			
			for(Transporter transporter : transporters)
			{
				Vec3 coords = transporter.getPosition();
				context.getSource().sendSuccess(() -> transporter.getID().toComponent(true).append(" ").append(ComponentHelper.coordinate(coords)), false);
			}
			context.getSource().sendSuccess(() -> Component.literal("-------------------------"), false);
		}
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_transporters.no_transporters", dimensionComponent(dimension)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int reloadTransporterNetwork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		TransporterNetwork.get(level).reloadNetwork();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.transporter_network_reload").withStyle(ChatFormatting.RED), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getTransportereNetworkVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		int version = TransporterNetwork.get(level).getVersion();
		
		context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.transporter_network_version").append(Component.literal(": " + version)).withStyle(ChatFormatting.GREEN), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setAncientGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.setGene(AncientGene.ATAGene.ANCIENT));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setInheritedGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.setGene(AncientGene.ATAGene.INHERITED));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setArtificialGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.setGene(AncientGene.ATAGene.ARTIFICIAL));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int removeGene(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Entity entity = EntityArgument.getEntity(context, "target");
		
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.setGene(AncientGene.ATAGene.NONE));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setProtected(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ServerLevel level = context.getSource().getLevel();
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof ProtectedBlock protectedBlock)
		{
			ProtectedBlockEntity blockEntity = protectedBlock.getProtectedBlockEntity(level, pos, state);
			
			if(context.getSource().isPlayer() && blockEntity.hasPermissions(context.getSource().getPlayer(), true))
			{
				blockEntity.setProtected(true);
				context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.protected_block_set").withStyle(ChatFormatting.LIGHT_PURPLE), true);
			}
		}
		else
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.not_protected_block").withStyle(ChatFormatting.RED), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int unsetProtected(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ServerLevel level = context.getSource().getLevel();
		BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
		
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof ProtectedBlock protectedBlock)
		{
			ProtectedBlockEntity blockEntity = protectedBlock.getProtectedBlockEntity(level, pos, state);
			
			if(context.getSource().isPlayer() && blockEntity.hasPermissions(context.getSource().getPlayer(), true))
			{
				blockEntity.setProtected(false);
				context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.protected_block_unset").withStyle(ChatFormatting.LIGHT_PURPLE), true);
			}
		}
		else
			context.getSource().sendSuccess(() -> Component.translatable("message.sgjourney.command.not_protected_block").withStyle(ChatFormatting.RED), true);
		
		return Command.SINGLE_SUCCESS;
	}
	
	//Only used for console checks
	private static int printStargateNetworkInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		MinecraftServer server = context.getSource().getServer();

		System.out.println("===============Universe===============");
		SpaceLocation.printSpaceLocations();
		Universe.get(server).printAddressRegions();
		Universe.get(server).printGalaxies();
		
		System.out.println("===============Stargate Network===============");
		BlockEntityList.get(server).printStargates();
		StargateNetwork.get(server).printRegionStargates();
		StargateNetwork.get(server).printConnections();

		System.out.println("===============Transporter Network===============");
		BlockEntityList.get(server).printTransporters();
		TransporterNetwork.get(server).printDimensions();
		
		System.out.println("===============Conduit Networks===============");
		ConduitNetworks.get(server).printConduits();
		
		context.getSource().sendSuccess(() -> Component.literal("Printed info onto the console"), false);
		
		return Command.SINGLE_SUCCESS;
	}
}
