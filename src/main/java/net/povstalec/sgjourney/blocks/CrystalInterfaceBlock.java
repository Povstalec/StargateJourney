package net.povstalec.sgjourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;

public class CrystalInterfaceBlock extends BasicInterfaceBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public CrystalInterfaceBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new CrystalInterfaceEntity(pos, state);
	}
	
	public Block getDroppedBlock()
	{
		return BlockInit.CRYSTAL_INTERFACE.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CRYSTAL_INTERFACE.get(), CrystalInterfaceEntity::tick);
    }
	
	public long getCapacity()
	{
		return 50000000;
	}
}
