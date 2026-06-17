package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.GoauldTransportRingsEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

import javax.annotation.Nullable;

public class GoauldBlockEntityTransportRings extends GoauldTransportRings implements BlockEntityTransportRings<GoauldTransportRingsEntity>
{
	protected BlockPos blockPos;
	
	public GoauldBlockEntityTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
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
		
		this.hasNetworkRestrictions = transporterEntity.hasNetworkRestrictions();
		this.networks = transporterEntity.getNetworks();
		
		this.allowInterdimensionalTransport = transporterEntity.allowInterdimensionalTransport();
		this.maxTransportDistance = transporterEntity.maxTransportRange();
	}
	
	@Nullable
	public GoauldTransportRingsEntity getTransporterEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof GoauldTransportRingsEntity transporter)
			return transporter;
		
		return null;
	}
	
	@Override
	public void update(MinecraftServer server)
	{
		transporterRun(server, transporter ->
		{
			this.hasNetworkRestrictions = transporter.hasNetworkRestrictions();
			this.networks = transporter.getCachedNetworks();
			
			this.allowInterdimensionalTransport = transporter.allowInterdimensionalTransport();
			this.maxTransportDistance = transporter.maxTransportRange();
		});
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
