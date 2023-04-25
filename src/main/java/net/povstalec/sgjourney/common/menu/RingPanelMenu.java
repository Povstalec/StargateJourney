package net.povstalec.sgjourney.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;

public class RingPanelMenu extends AbstractContainerMenu
{
    private final RingPanelEntity blockEntity;
    private final Level level;
    
    public RingPanelMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public RingPanelMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(MenuInit.RING_PANEL.get(), containerId);
        checkContainerSize(inv, 6);
        blockEntity = ((RingPanelEntity) entity);
        this.level = inv.player.level;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 5, 36));
            this.addSlot(new SlotItemHandler(handler, 1, 23, 36));
            this.addSlot(new SlotItemHandler(handler, 2, 5, 54));
            this.addSlot(new SlotItemHandler(handler, 3, 23, 54));
            this.addSlot(new SlotItemHandler(handler, 4, 5, 72));
            this.addSlot(new SlotItemHandler(handler, 5, 23, 72));
        });
    }
    
    public String getRingsPos(int i)
    {
    	BlockPos coords = null;
    	
    	switch(i)
    	{
    	case 1:
    		coords = blockEntity.ringsPos[0];
    		break;
    	case 2:
    		coords = blockEntity.ringsPos[1];
    		break;
    	case 3:
    		coords = blockEntity.ringsPos[2];
    		break;
    	case 4:
    		coords = blockEntity.ringsPos[3];
    		break;
    	case 5:
    		coords = blockEntity.ringsPos[4];
    		break;
    	case 6:
    		coords = blockEntity.ringsPos[5];
    		break;
    	}
    	
    	if(coords == null)
    		return "-";
    	
    	return coords.getX() + " " + coords.getY() + " " + coords.getZ();
    }
    
    public int getRingsFound()
    {
    	return blockEntity.ringsPos.length;
    }
    
    public void activateRings(int number)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundRingPanelUpdatePacket(this.blockEntity.getBlockPos(), number));
    }
    
    public int[] getTargetCoords(int chosenNumber)
    {
    	return blockEntity.getTargetCoords(chosenNumber);
    }
	
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.RING_PANEL.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 140 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
    }
    
	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 6;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) 
    {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
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
            System.out.println("Invalid slotIndex:" + index);
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
