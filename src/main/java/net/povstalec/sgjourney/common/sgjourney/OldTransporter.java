package net.povstalec.sgjourney.common.sgjourney;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;

public class OldTransporter
{
	public static final String DIMENSION = "Dimension";
	public static final String COORDINATES = "Coordinates";

	public static final String CUSTOM_NAME = "CustomName";
	
	private final UUID id;
	private final ResourceKey<Level> dimension;
	private final BlockPos blockPos;
	
	@Nullable
	private final Component name;
	
	public OldTransporter(UUID id, ResourceKey<Level> dimension, BlockPos blockPos, Component name)
	{
		this.id = id;
		this.dimension = dimension;
		this.blockPos = blockPos;
		
		this.name = name;
	}
	
	public OldTransporter(AbstractTransporterEntity transporterEntity)
	{
		this(transporterEntity.getID(), transporterEntity.getLevel().dimension(), transporterEntity.getBlockPos(), transporterEntity.getCustomName());
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public ResourceKey<Level> getDimension()
	{
		return dimension;
	}
	
	public BlockPos getBlockPos()
	{
		return blockPos;
	}
	
	public Component getName()
	{
		return name != null ? name : Component.empty();
	}
	
	public Optional<AbstractTransporterEntity> getTransporterEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			return Optional.of(transporter);
		
		return Optional.empty();
	}
	
	public int getTimeOffset(MinecraftServer server)
	{
		Optional<AbstractTransporterEntity> transporter = getTransporterEntity(server);
		
		if(transporter.isPresent())
			return transporter.get().getTimeOffset();
		
		return 0;
	}
	
	
	
	@Override
	public String toString()
	{
		String nameString = name != null ? name.getString() : id.toString();
		
		return "[ " + nameString + " | Pos: " + blockPos.toString() + " ]";
	}
	
	
	
	public CompoundTag serialize()
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
	
	public static OldTransporter deserialize(MinecraftServer server, String idString, CompoundTag tag)
	{
		UUID id;
		ResourceKey<Level> dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		Component name = null;
		
		if(tag.contains(CUSTOM_NAME, 8))
	         name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		try
		{
			id = UUID.fromString(idString);
		}
		catch(IllegalArgumentException e)
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			{
				transporter.setID(transporter.generateID());
				return new OldTransporter(transporter);
			}
			else
				return null;
		}
		
		return new OldTransporter(id, dimension, blockPos, name);
	}
}
