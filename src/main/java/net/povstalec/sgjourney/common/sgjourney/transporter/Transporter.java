package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface Transporter
{
	String DIMENSION = "Dimension";
	String COORDINATES = "Coordinates";
	
	String CUSTOM_NAME = "CustomName";
	
	UUID getID();
	
	@Nullable
	ResourceKey<Level> getDimension();
	
	/**
	 * @param server Current Minecraft Server
	 * @return Level the Stargate is currently located in, null if it's not located in any Level
	 */
	@Nullable
	default ServerLevel getLevel(MinecraftServer server)
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return server.getLevel(dimension);
	}
	
	@Nullable
	BlockPos getBlockPos(); //TODO Replace this with vector
	
	@Nullable
	Component getName();
	
	int getTimeOffset(MinecraftServer server);
	
	@Nullable
	List<Entity> entitiesToTransport(MinecraftServer server); //TODO introduce a transporter send and receive functions instead
	
	//void transportTravelers(MinecraftServer server, TransporterConnection connection, Transporter receivingTransporter);
	
	//boolean receiveTraveler(MinecraftServer server, TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	@Nullable
	BlockPos transportPos(MinecraftServer server);
	
	void connect(MinecraftServer server, UUID connectionID);
	
	void disconnect(MinecraftServer server);
	
	void reset(MinecraftServer server);
	
	boolean isConnected(MinecraftServer server);
	
	void updateTicks(MinecraftServer server, int connectionTime);
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(MinecraftServer server, UUID uuid, CompoundTag tag);
}
