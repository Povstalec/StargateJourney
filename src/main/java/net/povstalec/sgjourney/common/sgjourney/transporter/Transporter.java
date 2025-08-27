package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface Transporter
{
	String DIMENSION = "Dimension";
	String COORDINATES = "Coordinates";
	
	String CUSTOM_NAME = "CustomName";
	
	UUID getID();
	
	ResourceKey<Level> getDimension();
	
	BlockPos getBlockPos();
	
	Component getName();
	
	int getTimeOffset(MinecraftServer server);
	
	@Nullable
	List<Entity> entitiesToTransport(MinecraftServer server);
	
	@Nullable
	BlockPos transportPos(MinecraftServer server);
	
	void connect(MinecraftServer server, UUID connectionID);
	
	void disconnect(MinecraftServer server);
	
	void reset(MinecraftServer server);
	
	boolean isConnected(MinecraftServer server);
	
	void updateTicks(MinecraftServer server, int connectionTime);
	
	CompoundTag serializeNBT(HolderLookup.Provider registries);
	
	void deserializeNBT(MinecraftServer server, UUID uuid, CompoundTag tag, HolderLookup.Provider registries);
}
