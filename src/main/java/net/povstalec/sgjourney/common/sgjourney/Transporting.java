package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import java.util.UUID;

public class Transporting
{
	public static void startTransport(MinecraftServer server, Transporter initiatingTransporter, UUID targetUUID)
	{
		Transporter targetTransporter = TransporterNetwork.get(server).getTransporter(targetUUID);
		
		TransporterNetwork.get(server).createConnection(server, initiatingTransporter, targetTransporter);
	}
	
	
	
	private static void transportPlayer(ServerPlayer player, ServerLevel destinationLevel, double x, double y, double z)
	{
		player.teleportTo(destinationLevel, x, y, z, player.getYRot(), player.getXRot());
	}
	
	private static Entity transportEntity(Entity traveler, ServerLevel destinationLevel, double x, double y, double z)
	{
		if(traveler.getLevel() != destinationLevel)
			traveler = traveler.changeDimension(destinationLevel, new Wormhole.WormholeTeleporter(new Vec3(x, y, z), traveler.getDeltaMovement(), traveler.getYRot(), traveler.getXRot()));
		else
			traveler.moveTo(x, y, z, traveler.getYRot(), traveler.getXRot());
		
		return traveler;
	}
	
	public static Entity transportTraveler(Entity traveler, ServerLevel destinationLevel, double x, double y, double z)
	{
		if(traveler instanceof ServerPlayer player)
			transportPlayer(player, destinationLevel, x, y, z);
		else
			return transportEntity(traveler, destinationLevel, x, y, z);
		
		return traveler;
	}
	
	public static Entity transportTraveler(MinecraftServer server, Entity traveler, Transporter from, Transporter to)
	{
		double xOffset = traveler.getX() - from.getBlockPos().getX();
		double yOffset = traveler.getY() - from.getBlockPos().getY();
		double zOffset = traveler.getZ() - from.getBlockPos().getZ();
		
		return transportTraveler(traveler, to.getLevel(server), xOffset + to.getBlockPos().getX(), yOffset + to.getBlockPos().getY(), zOffset + to.getBlockPos().getZ());
	}
}
