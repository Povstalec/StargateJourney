package net.povstalec.sgjourney.common.blocks.symbols;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.symbols.SandstoneSymbolBlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;

public class SandstoneSymbolBlock extends SymbolBlock
{

	public SandstoneSymbolBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		SandstoneSymbolBlockEntity entity = new SandstoneSymbolBlockEntity(pos, state);
		
		return entity;
	}
    
    @Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof SandstoneSymbolBlockEntity)
		{
			if (!level.isClientSide)
			{
				ItemStack itemstack = new ItemStack(BlockInit.SANDSTONE_SYMBOL.get());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
}
