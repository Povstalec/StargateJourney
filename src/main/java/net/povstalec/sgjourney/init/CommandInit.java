package net.povstalec.sgjourney.init;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.data.RingsNetwork;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.stargate.Addressing;
import net.povstalec.sgjourney.stargate.Galaxy;
import net.povstalec.sgjourney.stargate.SolarSystem;

public class CommandInit
{
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		// Stargate Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("getAddress")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getAddress)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("getAllStargates")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getStargates)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("regenerate")
								.executes(CommandInit::regenerateNetwork))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("reload")
								.executes(CommandInit::reloadNetwork))));
		
		// Rings Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("ringsNetwork")
						.then(Commands.literal("getAllRings")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getTransportRings)))));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID).then(Commands.literal("galaxies").executes(CommandInit::getGalaxies)));
		
		/*
		 * sgjourney
		 * 				stargateNetwork
		 * 									getAddress
		 * 									getAllStargates
		 * 									regenerate
		 * 				ringsNetwork
		 * 									getAllRings
		 */
	}
	
	private static int getGalaxies(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
        final Registry<SolarSystem> planetRegistry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
        Set<Entry<ResourceKey<Galaxy>, Galaxy>> set = galaxyRegistry.entrySet();
        
        set.forEach((galaxy) -> 
        {
        	context.getSource().getPlayer().sendSystemMessage(Component.literal(galaxy.getValue().getName()).withStyle(ChatFormatting.LIGHT_PURPLE));
        	galaxy.getValue().getPlanets().forEach((planet) -> 
        		context.getSource().getPlayer().sendSystemMessage(Component.literal("-" + planetRegistry.get(planet).getName()).withStyle(ChatFormatting.AQUA))
        	);
        }
        );
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level;
		
		int[] address = StargateNetwork.get(level).getPlanets().getCompound(dimension).getIntArray("Address");
		String addressString = Addressing.addressIntArrayToString(address);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("The address of " + dimension + " is:"));
		context.getSource().getPlayer().sendSystemMessage(Component.literal(addressString).withStyle(ChatFormatting.AQUA));

		int galaxy = StargateNetwork.get(level).getPlanets().getCompound(dimension).getInt("Galaxy");
		String extragalactic = Addressing.addressIntArrayToString(Addressing.convertTo8chevronAddress(galaxy, address));
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("with an extragalactic address:"));
		context.getSource().getPlayer().sendSystemMessage(Component.literal(extragalactic).withStyle(ChatFormatting.AQUA));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level;
		
		CompoundTag stargates = StargateNetwork.get(level).getStargatesInDimension(level, dimension);
		
		if(stargates.isEmpty())
		{
			context.getSource().getPlayer().sendSystemMessage(Component.literal("Dimension has no registered Stargates"));
			return Command.SINGLE_SUCCESS;
		}
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargates in " + dimension));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		stargates.getAllKeys().forEach((stargate) ->
		{
			int[] coords = stargates.getCompound(stargate).getIntArray("Coordinates");
			
			context.getSource().getPlayer().sendSystemMessage(Component.literal(" X: " + coords[0] + " Y: " + coords[1] + " Z: " + coords[2] + " | ").withStyle(ChatFormatting.AQUA).append(stargate));
		});
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int regenerateNetwork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		
		StargateNetwork.get(level).regenerateNetwork(level);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Regenerated Stargate Network").withStyle(ChatFormatting.DARK_RED));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int reloadNetwork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level;
		
		StargateNetwork.get(level).reloadNetwork(level);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Reloaded Stargate Network").withStyle(ChatFormatting.RED));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getTransportRings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level;

		context.getSource().getPlayer().sendSystemMessage(Component.literal("Transport Rings"));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		CompoundTag ringsNetwork = RingsNetwork.get(level).getRings(dimension);
		System.out.println(ringsNetwork);
		List<String> ringsNList = ringsNetwork.getAllKeys().stream().toList();
		for(int i = 0; i < ringsNList.size(); i++)
		{
			int[] coords = ringsNetwork.getCompound(ringsNList.get(i)).getIntArray("Coordinates");
			context.getSource().getPlayer().sendSystemMessage(Component.literal("X: " + coords[0] + " Y: " + coords[1] + " Z: " + coords[2]).withStyle(ChatFormatting.AQUA));
		}
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		return Command.SINGLE_SUCCESS;
	}
}
