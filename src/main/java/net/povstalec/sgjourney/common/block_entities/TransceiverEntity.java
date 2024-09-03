package net.povstalec.sgjourney.common.block_entities;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.common.blocks.TransceiverBlock;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.stargate.ITransmissionReceiver;

public class TransceiverEntity extends BlockEntity implements ITransmissionReceiver
{
	public TransceiverEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.TRANSCEIVER.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
	}
	
	public float transmissionRadius()
	{
		return CommonTransmissionConfig.max_transceiver_transmission_distance.get();
	}
	
	public String getCurrentCode()
	{
		return "1234";
	}
	
	@Override
	public void receiveTransmission(int transmissionJumps, int frequency, String transmission)
	{
		if(!getCurrentCode().equals(transmission))
			return;
		
		Level level = getLevel();
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		
		if(state.getBlock() instanceof TransceiverBlock transceiver)
			transceiver.receiveTransmission(state, level, pos);
	}
	
	public void sendTransmission()
	{
		int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
		
		for(int x = -roundedRadius; x <= roundedRadius; x++)
		{
			for(int z = -roundedRadius; z <= roundedRadius; z++)
			{
				ChunkAccess chunk = level.getChunk(getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					BlockEntity blockEntity = level.getBlockEntity(pos);
					
					if(blockEntity instanceof ITransmissionReceiver receiver && blockEntity != this)
						receiver.receiveTransmission(0, 0, getCurrentCode());
				});
			}
		}
	}
}
