package net.povstalec.sgjourney.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryMenu extends AbstractContainerMenu
{
	protected InventoryMenu(@Nullable MenuType<?> type, int containerId)
	{
		super(type, containerId);
	}
	
	protected void addPlayerInventory(Inventory playerInventory, int x, int y)
	{
		for (int i = 0; i < 3; ++i)
		{
			for (int l = 0; l < 9; ++l)
			{
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, x + l * 18, y + i * 18));
			}
		}
	}
	
	protected void addPlayerHotbar(Inventory playerInventory, int x, int y)
	{
		for (int i = 0; i < 9; ++i)
		{
			this.addSlot(new Slot(playerInventory, i, x + i * 18, y));
		}
	}
	
	protected abstract int blockEntitySlotCount();
	
	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
	// must assign a slot number to each of the slots used by the GUI.
	// For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
	// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
	//  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
	//  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
	//  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
	protected static final int HOTBAR_SLOT_COUNT = 9;
	protected static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	protected static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	protected static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	protected static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	protected static final int VANILLA_FIRST_SLOT_INDEX = 0;
	protected static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	
	// THIS YOU HAVE TO DEFINE!
	protected final int TE_INVENTORY_SLOT_COUNT = blockEntitySlotCount();
	
	@Override
	public @NotNull ItemStack quickMoveStack(Player playerIn, int index)
	{
		Slot sourceSlot = slots.get(index);
		if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM // Removed the null check because it was was redundant
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();
		
		// Check if the slot clicked is one of the vanilla container slots
		if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
		{
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
					+ TE_INVENTORY_SLOT_COUNT, false))
			{
				return ItemStack.EMPTY;  // EMPTY_ITEM
			}
		}
		else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT)
		{
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
			{
				return ItemStack.EMPTY;
			}
		} else
		{
			StargateJourney.LOGGER.error("Invalid slotIndex:" + index); // Edited to use Stargate Journey's Logger
			return ItemStack.EMPTY;
		}
		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0)
		{
			sourceSlot.set(ItemStack.EMPTY);
		} else
		{
			sourceSlot.setChanged();
		}
		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}
}
