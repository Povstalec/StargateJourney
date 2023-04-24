package net.povstalec.sgjourney.block_entities.dhd;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.ItemInit;

public class MilkyWayDHDEntity extends AbstractDHDEntity
{
	public MilkyWayDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_DHD.get(), pos, state);
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
