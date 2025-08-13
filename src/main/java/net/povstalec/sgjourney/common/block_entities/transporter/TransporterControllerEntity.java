package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransporterControllerEntity extends EnergyBlockEntity
{
	public TransporterControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, false);
	}
	
	protected List<TransportRingsEntity> getNearbyTransportRings(int maxDistance)
	{
		List<TransportRingsEntity> transportRingsList = new ArrayList<TransportRingsEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof TransportRingsEntity transportRings)
						transportRingsList.add(transportRings);
				});
			}
		}
		
		return transportRingsList;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long capacity()
	{
		return 0;
	}
	
	@Override
	protected long maxReceive()
	{
		return 0;
	}
	
	@Override
	protected long maxExtract()
	{
		return 0;
	}
}
