package net.povstalec.sgjourney.common.blocks.symbols;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.symbols.StoneSymbolBlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;

public class StoneSymbolBlock extends SymbolBlock
{

	public StoneSymbolBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		StoneSymbolBlockEntity entity = new StoneSymbolBlockEntity(pos, state);
		
		return entity;
	}
    
    @Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof StoneSymbolBlockEntity)
		{
			if (!level.isClientSide)
			{
				ItemStack itemstack = new ItemStack(BlockInit.STONE_SYMBOL.get());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
}
