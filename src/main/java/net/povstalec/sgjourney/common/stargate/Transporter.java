package net.povstalec.sgjourney.common.stargate;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Transporter
{
	public static final String DIMENSION = "Dimension";
	public static final String COORDINATES = "Coordinates";
	
	private final UUID id;
	private final ResourceKey<Level> dimension;
	private final BlockPos blockPos;
	
	public Transporter(UUID id, ResourceKey<Level> dimension, BlockPos blockPos)
	{
		this.id = id;
		this.dimension = dimension;
		this.blockPos = blockPos;
	}
	
	public Transporter(AbstractTransporterEntity transporterEntity)
	{
		this(UUID.fromString(transporterEntity.getID()), transporterEntity.getLevel().dimension(), transporterEntity.getBlockPos());
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
	
	
	
	@Override
	public String toString()
	{
		return "[ " + id + " | Pos: " + blockPos.toString() + " ]";
	}
	
	
	
	public CompoundTag serialize()
	{
		CompoundTag transporterTag = new CompoundTag();
		ResourceKey<Level> level = this.getDimension();
		BlockPos pos = this.getBlockPos();
		
		transporterTag.putString(DIMENSION, level.location().toString());
		transporterTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		
		return transporterTag;
	}
	
	public static Transporter deserialize(MinecraftServer server, String idString, CompoundTag tag)
	{
		UUID id;
		ResourceKey<Level> dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		try
		{
			id = UUID.fromString(idString);
		}
		catch(IllegalArgumentException e)
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			{
				transporter.setID(transporter.generateID());
				return new Transporter(transporter);
			}
			else
				return null;
		}
		
		return new Transporter(id, dimension, blockPos);
	}
}
