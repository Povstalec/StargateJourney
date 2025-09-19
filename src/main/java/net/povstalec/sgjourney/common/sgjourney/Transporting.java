package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.advancements.WormholeTravelCriterion;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import java.util.*;

public class Transporting
{
	public static void startTransport(MinecraftServer server, Transporter initialTransporter, UUID targetUUID)
	{
		Transporter targetTransporter = TransporterNetwork.get(server).getTransporter(targetUUID);
		
		TransporterNetwork.get(server).createConnection(server, initialTransporter, targetTransporter);
	}
	
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
		if(traveler.level() != destinationLevel)
			traveler = traveler.changeDimension(new DimensionTransition(destinationLevel, destinationPosition, destinationMomentum,
					CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot(), false, DimensionTransition.DO_NOTHING));
		else
		{
			traveler.moveTo(destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot());
			traveler.setDeltaMovement(destinationMomentum);
		}
		
		//if(traveler != null)
		//	reconstructEvent(destinationLevel.getServer(), receivingTransporter, traveler); //TODO CC:Tweaked reconstruction event
		
		return traveler;
	}
	
	public static Entity transportPlayer(ServerLevel destinationLevel, Transporter receivingTransporter, ServerPlayer player, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		//Level initialLevel = player.getLevel();
		//Vec3 initialPos = player.position();
		
		player.teleportTo(destinationLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), player.getXRot());
		player.setDeltaMovement(destinationMomentum);
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		
		//reconstructEvent(destinationLevel.getServer(), receivingTransporter, player); //TODO CC:Tweaked reconstruction event
		
		//ResourceLocation initialDimension = initialLevel.dimension().location();
		//ResourceLocation targetDimension = destinationLevel.dimension().location();
		//long distanceTraveled = Math.round(DimensionType.getTeleportationScale(initialLevel.dimensionType(), destinationLevel.dimensionType()) * Math.sqrt(initialPos.distanceTo(player.position())));
		
		//TODO Add Advancements and Statistics
		//player.awardStat(StatisticsInit.TIMES_USED_WORMHOLE.get());
		//player.awardStat(StatisticsInit.DISTANCE_TRAVELED_BY_STARGATE.get(), (int) distanceTraveled * 100);
		//WormholeTravelCriterion.INSTANCE.trigger(player, initialDimension, targetDimension, distanceTraveled);
		
		return player;
	}
	
	public static Entity recursivePassengerTeleport(ServerLevel destinationLevel, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		Level initialLevel = traveler.level();
		ArrayList<Entity> passengers = new ArrayList<>();
		if(initialLevel != destinationLevel)
		{
			// Prepares passengers
			for(Entity passenger : traveler.getPassengers())
			{
				passengers.add(recursivePassengerTeleport(destinationLevel, receivingTransporter, passenger, destinationPosition, destinationMomentum, destinationLookAngle));
			}
		}
		
		// Teleports traveler
		if(traveler instanceof ServerPlayer player)
			traveler = transportPlayer(destinationLevel, receivingTransporter, player, destinationPosition, destinationMomentum, destinationLookAngle);
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
	
	public static boolean receiveTraveler(ServerLevel level, Transporter receivingTransporter, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		recursivePassengerTeleport(level, receivingTransporter, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		return true;
	}
}
