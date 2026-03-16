package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.TransportRingsEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

import javax.annotation.Nullable;

public class TransportRings extends SGJourneyTransporter implements BlockEntityTransporter<TransportRingsEntity>
{
	protected BlockPos blockPos;
	
	public TransportRings(TransporterType<?> type)
	{
		super(type);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	@Override
	public void loadFromBlockEntity(AbstractTransporterEntity<?> transporterEntity)
	{
		this.transporterID = transporterEntity.getID();
		
		this.dimension = transporterEntity.getLevel().dimension();
		this.blockPos = transporterEntity.getBlockPos();
		
		this.name = transporterEntity.getCustomName();
	}
	
	@Nullable
	public TransportRingsEntity getTransporterEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof TransportRingsEntity transporter)
			return transporter;
		
		return null;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		tag.putIntArray(COORDINATES, Conversion.blockPosToIntArray(blockPos));
		
		super.serializeNBT(tag);
	}
	
	public void deserializeNBT(MinecraftServer server, TransporterID transporterID, CompoundTag tag)
	{
		blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		super.deserializeNBT(server, transporterID, tag);
	}
}
