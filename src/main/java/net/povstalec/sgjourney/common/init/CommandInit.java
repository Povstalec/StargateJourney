package net.povstalec.sgjourney.common.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
import net.povstalec.sgjourney.common.data.*;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.Galaxy.Serializable;
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
						.then(Commands.literal("primaryStargate")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.then(Commands.literal("set")
												.then(Commands.argument("address", new AddressArgumentType(Address.Type.ADDRESS_9_CHEVRON))
											.executes(CommandInit::setPrimaryStargate))))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("primaryStargate")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.then(Commands.literal("unset")
														.executes(CommandInit::unsetPrimaryStargate)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal(STARGATE_NETWORK)
						.then(Commands.literal("primaryStargate")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.then(Commands.literal("get")
												.executes(CommandInit::getPrimaryStargate)))))
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
										.then(Commands.literal("randomizeAddresses")
												.then(Commands.argument("randomizeAddresses", BoolArgumentType.bool())
														.executes(CommandInit::randomizeAddresses))))))
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
		
		
		
		//Protection commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("protection")
						.then(Commands.literal("set")
								.then(Commands.argument("pos", BlockPosArgument.blockPos())
										.executes(CommandInit::setProtected))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("protection")
						.then(Commands.literal("unset")
								.then(Commands.argument("pos", BlockPosArgument.blockPos())
										.executes(CommandInit::unsetProtected))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		
		
		//Dev commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("debugInfo").executes(CommandInit::printStargateNetworkInfo))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
	}
	
	private static Component dimensionComponent(ResourceKey<Level> dimension)
	{
		return Component.literal(dimension.location().toString()).withStyle(ChatFormatting.GREEN);
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		ResourceKey<Level> currentDimension = level.dimension();
		
		HashMap<Serializable, Address.Immutable> galaxyMap = Universe.get(level).getGalaxiesFromDimension(currentDimension);
		if(galaxyMap == null || galaxyMap.isEmpty())
		{
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.no_galaxy").withStyle(ChatFormatting.DARK_RED));
			return Command.SINGLE_SUCCESS;
		}
		
		for(Entry<Serializable, Address.Immutable> galaxyEntry : galaxyMap.entrySet())
		{
			Galaxy.Serializable galaxy = galaxyEntry.getKey();
			Address.Immutable address = Universe.get(level).getAddressInGalaxyFromDimension(galaxy.getKey(), dimension);
			
			if(address == null)
				context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.no_address", dimensionComponent(dimension), galaxy.toComponent()));
			else
				context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_address.address", dimensionComponent(dimension), galaxy.toComponent(), address.toComponent(true)));
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getExtragalacticAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		
		Address.Immutable address = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		
		if(address == null)
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.get_extragalactic_address.none", dimensionComponent(dimension)), false);
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_extragalactic_address.address", dimensionComponent(dimension), address.toComponent(true)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		AddressRegion.Serializable addressRegion = Universe.get(level).getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null && !addressRegion.getStargates(stargate -> dimension.equals(stargate.getDimension())).isEmpty())
		{
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.get_stargates.stargates", dimensionComponent(dimension)), false);
			context.getSource().sendSuccess(Component.literal("-------------------------"), false);
			
			addressRegion.getStargates().forEach(stargate ->
			{
				ResourceKey<Level> stargateDimension = stargate.getDimension();
				Vec3 stargatePos = stargate.getPosition(context.getSource().getServer());
				
				if(dimension.equals(stargateDimension) && stargatePos != null)
					context.getSource().sendSuccess(stargate.get9ChevronAddress().toComponent(true).append(Component.literal(" X: " + stargatePos.x() + " Y: " + stargatePos.y() + " Z: " + stargatePos.z()).withStyle(ChatFormatting.BLUE)), false);
			});
			context.getSource().sendSuccess(Component.literal("-------------------------"), false);
		}
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_stargates.no_stargates", dimensionComponent(dimension)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Address.Immutable address = AddressArgumentType.getAddress(context, "address");
		
		Level level = context.getSource().getLevel();
		AddressRegion.Serializable addressRegion = Universe.get(level).getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
		{
			addressRegion.setPrimaryStargate(address);
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.primary_stargate_set").withStyle(ChatFormatting.DARK_GREEN), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int unsetPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		AddressRegion.Serializable addressRegion = Universe.get(level).getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
		{
			addressRegion.setPrimaryStargate(null);
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.primary_stargate_unset").withStyle(ChatFormatting.GREEN), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getPrimaryStargate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		
		Level level = context.getSource().getLevel();
		AddressRegion.Serializable addressRegion = Universe.get(level).getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
		{
			Address.Immutable address = addressRegion.primaryAddress();
			
			if(address != null)
				context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.primary_stargate").append(Component.literal(": ").append(address.toComponent(true))).withStyle(ChatFormatting.AQUA), true);
			else
				context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.primary_stargate_none").withStyle(ChatFormatting.RED), true);
			return Command.SINGLE_SUCCESS;
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		int version = StargateNetwork.get(level).getVersion();
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_version").append(Component.literal(": " + version)).withStyle(ChatFormatting.GREEN), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int forceStellarUpdate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		StargateNetwork.get(level).stellarUpdate(level.getServer());
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stellar_update").withStyle(ChatFormatting.RED), true);
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		boolean randomizeAddresses = StargateNetworkSettings.get(level).randomizeAddresses();
		boolean generateRandomSolarSystems = StargateNetworkSettings.get(level).generateRandomAddressRegions();
		boolean randomAddressFromSeed = StargateNetworkSettings.get(level).randomAddressFromSeed();
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.randomize_addresses").append(Component.literal(": " + randomizeAddresses)).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.generate_random_solar_systems").append(Component.literal(": " + generateRandomSolarSystems)).withStyle(ChatFormatting.GOLD), false);
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.random_addresses_from_seed").append(Component.literal(": " + randomAddressFromSeed)).withStyle(ChatFormatting.GOLD), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomizeAddresses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "randomizeAddresses");
		
		StargateNetworkSettings.get(level).randomizeAddresses(setting);
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int generateRandomSolarSystems(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "generateRandomSolarSystems");
		
		StargateNetworkSettings.get(level).setGenerateRandomSolarSystems(setting);
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomAddressFromSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		boolean setting = BoolArgumentType.getBool(context, "randomAddressFromSeed");
		
		StargateNetworkSettings.get(level).setRandomAddressFromSeed(setting);
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.stargate_network_settings.changed").withStyle(ChatFormatting.YELLOW), false);
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getTransporters(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		ResourceKey<Level> dimension = DimensionArgument.getDimension(context, "dimension").dimension();
		Level level = context.getSource().getLevel();
		
		List<Transporter> transporters = TransporterNetwork.get(level).getTransportersFromDimension(dimension);
		
		if(transporters != null && !transporters.isEmpty())
		{
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.get_transporters.transporters", dimensionComponent(dimension)), false);
			context.getSource().sendSuccess(Component.literal("-------------------------"), false);
			
			for(Transporter transporter : transporters)
			{
				Vec3 coords = transporter.getPosition(level.getServer());
				context.getSource().sendSuccess(transporter.getID().toComponent(true).append(Component.literal(" X: " + (int) Math.floor(coords.x()) + " Y: " + (int) Math.floor(coords.y()) + " Z: " + (int) Math.floor(coords.z())).withStyle(ChatFormatting.BLUE)), false);
			}
			context.getSource().sendSuccess(Component.literal("-------------------------"), false);
		}
		else
			context.getSource().sendSystemMessage(Component.translatable("message.sgjourney.command.get_transporters.no_transporters", dimensionComponent(dimension)));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int reloadTransporterNetwork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getLevel();
		
		TransporterNetwork.get(level).reloadNetwork(level.getServer(), true);
		
		context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.transporter_network_reload").withStyle(ChatFormatting.RED), true);
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
				context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.protected_block_set").withStyle(ChatFormatting.LIGHT_PURPLE), true);
			}
		}
		else
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.not_protected_block").withStyle(ChatFormatting.RED), true);
		
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
				context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.protected_block_unset").withStyle(ChatFormatting.LIGHT_PURPLE), true);
			}
		}
		else
			context.getSource().sendSuccess(Component.translatable("message.sgjourney.command.not_protected_block").withStyle(ChatFormatting.RED), true);
		
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
		StargateNetwork.get(server).printConnections();

		System.out.println("===============Transporter Network===============");
		BlockEntityList.get(server).printTransporters();
		TransporterNetwork.get(server).printDimensions();
		
		System.out.println("===============Conduit Networks===============");
		ConduitNetworks.get(server).printConduits();

		context.getSource().sendSuccess(Component.literal("Printed debug info to console"), false);
		
		return Command.SINGLE_SUCCESS;
	}
}
