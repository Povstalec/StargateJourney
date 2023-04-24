package net.povstalec.sgjourney.block_entities.dhd;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.ItemInit;

public class ClassicDHDEntity extends AbstractDHDEntity
{
	public ClassicDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CLASSIC_DHD.get(), pos, state);
	}
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		return side == Direction.DOWN;
	}
	
	@Override
	protected boolean isSlotValid(int slot, @Nonnull ItemStack stack)
	{
		if(slot == 0)
			return stack.getItem() == ItemInit.LARGE_CONTROL_CRYSTAL.get();
		else
			return isValidCrystal(stack);
	}
	
	protected boolean isValidCrystal(ItemStack stack)
	{
		if(stack.getItem() == ItemInit.MEMORY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ENERGY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.COMMUNICATION_CRYSTAL.get())
			return true;
		
		return false;
	}
}
