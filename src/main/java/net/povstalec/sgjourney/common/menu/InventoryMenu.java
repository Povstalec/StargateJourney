package net.povstalec.sgjourney.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryMenu<T extends BlockEntity> extends SGJourneyMenu<T>
{
	private int blockEntityInventorySlotCount = 0;
	
	protected InventoryMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
	{
		super(type, containerId, inventory, blockEntity);
	}
	
	protected void addPlayerInventory(Inventory playerInventory, int x, int y)
	{
		for(int i = 0; i < 3; ++i)
		{
			for(int l = 0; l < 9; ++l)
			{
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, x + l * 18, y + i * 18));
			}
		}
	}
	
	protected void addPlayerHotbar(Inventory playerInventory, int x, int y)
	{
		for(int i = 0; i < 9; ++i)
		{
			this.addSlot(new Slot(playerInventory, i, x + i * 18, y));
		}
	}
	
	protected Slot addBlockEntitySlot(Slot slot)
	{
		this.blockEntityInventorySlotCount++;
		return this.addSlot(slot);
	}
	
	public int blockEntityInventorySlotCount()
	{
		return blockEntityInventorySlotCount;
	}
	
	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
	// --- Code and description below was altered for the purposes of this project ---
	// Must assign a slot number to each of the slots used by the GUI.
	// For this container, we can see both the Block Entity inventory slots as well as the player inventory slots and the hotbar.
	// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
	//  0 - 8 = hotbar slots (which will map to the Player Inventory slot numbers 0 - 8)
	//  9 - 35 = player inventory slots (which map to the Player Inventory slot numbers 9 - 35)
	//  36 - 44 = Block Entity slots, which map to our Block Entity slot numbers 0 - 8)
	protected static final int HOTBAR_SLOT_COUNT = 9;
	protected static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	protected static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	protected static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	protected static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	protected static final int VANILLA_FIRST_SLOT_INDEX = 0;
	protected static final int BLOCK_ENTITY_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	
	protected boolean isHotbarSlot(int index)
	{
		return index < VANILLA_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;
	}
	
	protected boolean isPlayerInventorySlot(int index)
	{
		return index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	}
	
	protected boolean isBlockEntitySlot(int index)
	{
		return index < BLOCK_ENTITY_INVENTORY_FIRST_SLOT_INDEX + blockEntityInventorySlotCount();
	}
	
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack, int startIndex, int endIndex, boolean reverseDirection)
	{
		return moveItemStackTo(sourceStack, BLOCK_ENTITY_INVENTORY_FIRST_SLOT_INDEX + startIndex, BLOCK_ENTITY_INVENTORY_FIRST_SLOT_INDEX + endIndex, reverseDirection);
	}
	
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
	
	protected boolean moveItemStackToPlayerInventory(ItemStack sourceStack)
	{
		return moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false);
	}
	
	@Override
	public @NotNull ItemStack quickMoveStack(Player playerIn, int index)
	{
		Slot sourceSlot = slots.get(index);
		if(!sourceSlot.hasItem())
			return ItemStack.EMPTY;
		
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();
		
		if(isPlayerInventorySlot(index))
		{
			if(!moveItemStackToBlockEntity(sourceStack))
				return ItemStack.EMPTY;
		}
		else if(isBlockEntitySlot(index))
		{
			if(!moveItemStackToPlayerInventory(sourceStack))
				return ItemStack.EMPTY;
		}
		else
		{
			StargateJourney.LOGGER.error("Invalid slotIndex:" + index);
			return ItemStack.EMPTY;
		}
		
		// If stack size == 0 (the entire stack was moved) set slot contents to empty
		if(sourceStack.getCount() == 0)
			sourceSlot.set(ItemStack.EMPTY);
		else
			sourceSlot.setChanged();
		
		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}
}
