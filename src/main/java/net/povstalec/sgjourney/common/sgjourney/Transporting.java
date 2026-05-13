package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.advancements.TransporterTravelCriterion;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import java.util.*;

public class Transporting
{
	//============================================================================================
	//***************************************Transport out****************************************
	//============================================================================================
	
	public static boolean transportTraveler(MinecraftServer server, TransporterConnection connection, Transporter initialTransporter, Transporter receivingTransporter, Entity traveler)
	{
		Vec3 relativePosition = initialTransporter.toTransporterCoords(server,traveler.position().subtract(initialTransporter.transportPos(server)), true);
		Vec3 relativeMomentum = initialTransporter.toTransporterCoords(server, traveler.getDeltaMovement(), false);
		Vec3 relativeLookAngle = initialTransporter.toTransporterCoords(server, traveler.getLookAngle(), false);
		
		if(relativePosition.lengthSqr() <= initialTransporter.getInnerRadius() * initialTransporter.getInnerRadius())
		{
			//TODO Transporter Event
			if(receivingTransporter.receiveTraveler(server, connection, initialTransporter, traveler, relativePosition, relativeMomentum, relativeLookAngle))
			{
				//TODO CC:Tweaked deconstruction event
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean transportTravelers(MinecraftServer server, TransporterConnection connection, Transporter initialTransporter, Transporter receivingTransporter, List<Entity> travelers)
	{
		boolean used = false;
		
		for(Entity traveler : travelers)
		{
			if(transportTraveler(server, connection, initialTransporter, receivingTransporter, traveler))
				used = true;
		}
		
		return used;
	}
	
	//============================================================================================
	//*************************************Receive transport**************************************
	//============================================================================================
	
	public static Entity transportEntity(ServerLevel destinationLevel, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		if(traveler.getLevel() != destinationLevel)
			traveler = traveler.changeDimension(destinationLevel, new Wormhole.WormholeTeleporter(destinationPosition, destinationMomentum,
					CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot()));
		else
		{
			traveler.moveTo(destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot());
			traveler.setDeltaMovement(destinationMomentum);
		}
		
		//if(traveler != null)
		//	reconstructEvent(destinationLevel.getServer(), receivingTransporter, traveler); //TODO CC:Tweaked reconstruction event
		
		return traveler;
	}
	
	private static void triggerDestinationGalaxyAdvancement(ServerPlayer player, TransporterConnection connection, ServerLevel initialLevel, ServerLevel destinationLevel, ResourceKey<AddressRegion> initialRegion, ResourceKey<Galaxy>  initialGalaxy, long distanceTraveled)
	{
		SpaceLocation destinationLocation = SpaceLocation.fromDimension(destinationLevel.getServer(), destinationLevel.dimension());
		AddressRegion destinationRegion = destinationLocation.getAddressRegion();
		
		if(destinationRegion != null)
		{
			Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> destinationGalaxyMap = destinationRegion.getGalacticAddresses();
			if(destinationGalaxyMap == null || destinationGalaxyMap.isEmpty()) // Destination Region but no Galaxies
				TransporterTravelCriterion.INSTANCE.trigger(player, connection.getConnectionType(), initialLevel.dimension(), destinationLevel.dimension(),
						initialRegion, destinationRegion.getResourceKey(), initialGalaxy, null, distanceTraveled);
			else // Destination Region with Galaxies
			{
				for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> entry : destinationGalaxyMap.entrySet())
				{
					TransporterTravelCriterion.INSTANCE.trigger(player, connection.getConnectionType(), initialLevel.dimension(), destinationLevel.dimension(),
							initialRegion, destinationRegion.getResourceKey(), initialGalaxy, entry.getKey(), distanceTraveled);
				}
			}
		}
		else // No destination Region
		{
			TransporterTravelCriterion.INSTANCE.trigger(player, connection.getConnectionType(), initialLevel.dimension(), destinationLevel.dimension(),
					initialRegion, null, initialGalaxy, null, distanceTraveled);
		}
	}
	
	public static void triggerAdvancement(ServerPlayer player, TransporterConnection connection, ServerLevel initialLevel, ServerLevel destinationLevel, long distanceTraveled)
	{
		SpaceLocation destinationLocation = SpaceLocation.fromDimension(destinationLevel.getServer(), destinationLevel.dimension());
		AddressRegion destinationRegion = destinationLocation.getAddressRegion();
		
		if(destinationRegion != null)
		{
			Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> destinationGalaxyMap = destinationRegion.getGalacticAddresses();
			if(destinationGalaxyMap == null || destinationGalaxyMap.isEmpty()) // Initial Region but no Galaxies
				triggerDestinationGalaxyAdvancement(player, connection, initialLevel, destinationLevel, destinationRegion.getResourceKey(), null, distanceTraveled);
			else // Initial Region with Galaxies
			{
				for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> entry : destinationGalaxyMap.entrySet())
				{
					triggerDestinationGalaxyAdvancement(player, connection, initialLevel, destinationLevel, destinationRegion.getResourceKey(), entry.getKey(), distanceTraveled);
				}
			}
		}
		else // No initial Region
			triggerDestinationGalaxyAdvancement(player, connection, initialLevel, destinationLevel, null, null, distanceTraveled);
	}
	
	public static Entity transportPlayer(TransporterConnection connection, ServerLevel destinationLevel, Transporter receivingTransporter, ServerPlayer player, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		ServerLevel initialLevel = player.getLevel();
		Vec3 initialPos = player.position();
		
		player.teleportTo(destinationLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), player.getXRot());
		player.setDeltaMovement(destinationMomentum);
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		
		//reconstructEvent(destinationLevel.getServer(), receivingTransporter, player); //TODO CC:Tweaked reconstruction event
		
		long distanceTraveled = Math.round(DimensionType.getTeleportationScale(initialLevel.dimensionType(), destinationLevel.dimensionType()) * Math.sqrt(initialPos.distanceTo(player.position())));
		
		player.awardStat(StatisticsInit.TIMES_USED_TRANSPORTER.get());
		player.awardStat(StatisticsInit.DISTANCE_TRAVELED_BY_TRANSPORTER.get(), (int) distanceTraveled * 100);
		triggerAdvancement(player, connection, initialLevel, destinationLevel, distanceTraveled);
		
		return player;
	}
	
	public static Entity recursivePassengerTeleport(TransporterConnection connection, ServerLevel destinationLevel, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		Level initialLevel = traveler.getLevel();
		ArrayList<Entity> passengers = new ArrayList<>();
		if(initialLevel != destinationLevel)
		{
			// Prepares passengers
			for(Entity passenger : traveler.getPassengers())
			{
				passengers.add(recursivePassengerTeleport(connection, destinationLevel, receivingTransporter, passenger, destinationPosition, destinationMomentum, destinationLookAngle));
			}
		}
		
		// Teleports traveler
		if(traveler instanceof ServerPlayer player)
			traveler = transportPlayer(connection, destinationLevel, receivingTransporter, player, destinationPosition, destinationMomentum, destinationLookAngle);
		else
			traveler = transportEntity(destinationLevel, receivingTransporter, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		
		if(initialLevel != destinationLevel)
		{
			// Brings passengers
			for(Entity passenger : passengers)
			{
				passenger.startRiding(traveler, true);
			}
		}
		
		return traveler;
	}
	
	public static boolean receiveTraveler(TransporterConnection connection, ServerLevel level, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		recursivePassengerTeleport(connection, level, receivingTransporter, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		return true;
	}
}
