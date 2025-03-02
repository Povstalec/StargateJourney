package net.povstalec.sgjourney.common.stargate.info;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.items.StargateShieldItem;

import javax.annotation.Nonnull;

public class ShieldInfo
{
	protected AbstractStargateEntity stargate;
	
	protected short shieldProgress;
	protected short oldShieldProgress;
	protected final ItemStackHandler shieldItemHandler;
	
	public ShieldInfo(AbstractStargateEntity stargate)
	{
		this.stargate = stargate;
		
		this.shieldProgress = 0;
		this.oldShieldProgress = 0;
		this.shieldItemHandler = createStargateShieldHandler();
	}
	
	protected ItemStackHandler createStargateShieldHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				stargate.setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getItem() instanceof StargateShieldItem;
			}
			
			// Limits the number of items per slot
			public int getSlotLimit(int slot)
			{
				return 1;
			}
			
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
			{
				if(!isItemValid(slot, stack))
					return stack;
				
				return super.insertItem(slot, stack, simulate);
				
			}
		};
	}
	
	
	
	public interface Interface
	{
		String SHIELD_PROGRESS = "ShieldProgress";
		String SHIELD_INVENTORY = "ShieldInventory";
		
		ShieldInfo shieldInfo();
	}
}
