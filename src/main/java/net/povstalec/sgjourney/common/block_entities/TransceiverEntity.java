package net.povstalec.sgjourney.common.block_entities;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.blocks.TransceiverBlock;
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
	
	@Override
	public void receiveTransmission(int transmissionJumps, int frequency, String transmission)
	{
		Level level = getLevel();
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		
		if(state.getBlock() instanceof TransceiverBlock transceiver)
			transceiver.receiveTransmission(state, level, pos);
	}
}
