package net.povstalec.sgjourney.common.sgjourney.transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;

public class SGJourneyTransporter implements Transporter
{
	private UUID id;
	private ResourceKey<Level> dimension;
	private BlockPos blockPos;
	
	@Nullable
	private Component name;
	
	public SGJourneyTransporter() {}
	
	public SGJourneyTransporter(UUID id, ResourceKey<Level> dimension, BlockPos blockPos, Component name)
	{
		this.id = id;
		this.dimension = dimension;
		this.blockPos = blockPos;
		
		this.name = name;
	}
	
	public SGJourneyTransporter(AbstractTransporterEntity transporterEntity)
	{
		this(transporterEntity.getID(), transporterEntity.getLevel().dimension(), transporterEntity.getBlockPos(), transporterEntity.getCustomName());
	}
	
	@Override
	public UUID getID()
	{
		return id;
	}
	
	@Override
	public ResourceKey<Level> getDimension()
	{
		return dimension;
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return blockPos;
	}
	
	@Override
	public Component getName()
	{
		return name != null ? name : Component.empty();
	}
	
	@Nullable
	public AbstractTransporterEntity getTransporterEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			return transporter;
		
		return null;
	}
	
	@Override
	public int getTimeOffset(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.getTimeOffset();
		
		return 0;
	}
	
	@Override
	@Nullable
	public List<Entity> entitiesToTransport(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.entitiesToTransport();
		
		return new ArrayList<>();
	}
	
	@Override
	@Nullable
	public BlockPos transportPos(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.transportPos();
		
		return null;
	}
	
	@Override
	public void connect(MinecraftServer server, UUID connectionID)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.connectTransporter(connectionID);
	}
	
	@Override
	public void disconnect(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.disconnectTransporter();
	}
	
	@Override
	public void reset(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.resetTransporter();
	}
	
	@Override
	public boolean isConnected(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.isConnected();
		
		return false;
	}
	
	@Override
	public void updateTicks(MinecraftServer server, int connectionTime)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.updateTicks(connectionTime);
	}
	
	
	
	@Override
	public String toString()
	{
		String nameString = name != null ? name.getString() : id.toString();
		
		return "[ " + nameString + " | Pos: " + blockPos.toString() + " ]";
	}
	
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag transporterTag = new CompoundTag();
		ResourceKey<Level> level = this.getDimension();
		BlockPos pos = this.getBlockPos();
		
		transporterTag.putString(DIMENSION, level.location().toString());
		transporterTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		
		if(this.name != null)
			transporterTag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name));
		
		return transporterTag;
	}
	
	public void deserializeNBT(MinecraftServer server, UUID uuid, CompoundTag tag)
	{
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		this.blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		if(tag.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
			this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		this.id = uuid;
	}
}
