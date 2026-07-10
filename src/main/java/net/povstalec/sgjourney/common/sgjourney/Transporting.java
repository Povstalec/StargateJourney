package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.advancements.TransporterTravelCriterion;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import java.util.*;

public class Transporting
{
	public static final String EVENT_DECONSTRUCTING_ENTITY = "transporter_deconstructing_entity";
	public static final String EVENT_RECONSTRUCTING_ENTITY = "transporter_reconstructing_entity";
	
	//============================================================================================
	//***************************************Transport out****************************************
	//============================================================================================
	
	public static boolean transportTraveler(MinecraftServer server, TransporterConnection connection, Transporter initialTransporter, Transporter receivingTransporter, Entity traveler)
	{
		Vec3 relativePosition = initialTransporter.toTransporterCoords(traveler.position().subtract(initialTransporter.transportPos()), true);
		Vec3 relativeMomentum = initialTransporter.toTransporterCoords(traveler.getDeltaMovement(), false);
		Vec3 relativeLookAngle = initialTransporter.toTransporterCoords(traveler.getLookAngle(), false);
		
		if(relativePosition.lengthSqr() <= initialTransporter.getInnerRadius() * initialTransporter.getInnerRadius())
		{
			if(!SGJourneyEvents.onTransporterTransport(server, initialTransporter, receivingTransporter, traveler) && receivingTransporter.receiveTraveler(connection, initialTransporter, traveler, relativePosition, relativeMomentum, relativeLookAngle))
			{
				deconstructEvent(initialTransporter, traveler, false);
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
		if(traveler.level() != destinationLevel)
			traveler = traveler.changeDimension(destinationLevel, new Wormhole.WormholeTeleporter(destinationPosition, destinationMomentum,
					CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot()));
		else
		{
			traveler.moveTo(destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot());
			traveler.setDeltaMovement(destinationMomentum);
		}
		
		if(traveler != null)
			reconstructEvent(receivingTransporter, traveler);
		
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
		
		reconstructEvent(receivingTransporter, player);
		
		long distanceTraveled = Math.round(CoordinateHelper.distanceAcrossDimensions(initialLevel.dimensionType(), initialPos, destinationLevel.dimensionType(), destinationPosition));
		
		player.awardStat(StatisticsInit.TIMES_USED_TRANSPORTER.get());
		player.awardStat(StatisticsInit.DISTANCE_TRAVELED_BY_TRANSPORTER.get(), (int) distanceTraveled * 100);
		triggerAdvancement(player, connection, initialLevel, destinationLevel, distanceTraveled);
		
		return player;
	}
	
	public static Entity recursivePassengerTeleport(TransporterConnection connection, ServerLevel destinationLevel, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		Level initialLevel = traveler.level();
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
	
	//============================================================================================
	//*******************************************Events*******************************************
	//============================================================================================
	
	public static void deconstructEvent(Transporter initialTransporter, Entity traveler, boolean disintegrated)
	{
		String travelerType = EntityType.getKey(traveler.getType()).toString();
		String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
		String uuid = traveler.getUUID().toString();
		
		initialTransporter.updateInterfaceBlocks(null, EVENT_DECONSTRUCTING_ENTITY, travelerType, displayName, uuid, disintegrated);
	}
	
	public static void reconstructEvent(Transporter receivingTransporter, Entity traveler)
	{
		String travelerType = EntityType.getKey(traveler.getType()).toString();
		String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
		String uuid = traveler.getUUID().toString();
		
		receivingTransporter.updateInterfaceBlocks(null, EVENT_RECONSTRUCTING_ENTITY, travelerType, displayName, uuid);
	}
}
