package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class CrystallizerMenu extends InventoryMenu
{
    protected final AbstractCrystallizerEntity blockEntity;
    protected final Level level;
    protected FluidStack fluidStack;
    
    public CrystallizerMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CrystallizerMenu(int containerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(MenuInit.CRYSTALLIZER.get(), containerId);
        checkContainerSize(inventory, 5);
        this.blockEntity = ((AbstractCrystallizerEntity) blockEntity);
        this.level = inventory.player.level();
        this.fluidStack = this.blockEntity.getFluid();

        addPlayerInventory(inventory, 8, 84);
        addPlayerHotbar(inventory, 8, 142);
        
        IItemHandler cap0 = this.blockEntity.getItemHandler(0).get();
        if(cap0 != null)
            this.addSlot(new SlotItemHandler(cap0, 0, 80, 20));
        
        IItemHandler cap1 = this.blockEntity.getItemHandler(1).get();
        if(cap1 != null)
            this.addSlot(new SlotItemHandler(cap1, 0, 67, 50));
        
        IItemHandler cap2 = this.blockEntity.getItemHandler(2).get();
        if(cap2 != null)
            this.addSlot(new SlotItemHandler(cap2, 0, 93, 50));
        
        IItemHandler cap3 = this.blockEntity.getItemHandler(3).get();
        if(cap3 != null)
            this.addSlot(new SlotItemHandler(cap3, 0, 130, 36));
        
        IItemHandler cap4 = this.blockEntity.getItemHandler(4).get();
        if(cap4 != null)
            this.addSlot(new SlotItemHandler(cap4, 0, 34, 20));
    }
    
    public void setFluid(FluidStack fluidStack)
	{
		this.fluidStack = fluidStack;
	}
	
	public FluidStack getFluid()
	{
		return this.blockEntity.getFluid();
	}
	
	public Fluid getDesiredFluid()
	{
		return this.blockEntity.getDesiredFluid();
	}
	
	public int getProgress()
	{
		return this.blockEntity.progress;
	}
    
    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CRYSTALLIZER.get()) ||
        		stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ADVANCED_CRYSTALLIZER.get());
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
    private static final int TE_INVENTORY_SLOT_COUNT = 5;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS

    /**
     * Checks if the ItemStack has the required liquid for the liquidizer.
     * @return true if the ItemStack has the required liquid, false otherwise.
     */
    private boolean hasRequiredLiquid(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(cap ->
                cap.getFluidInTank(0).getFluid().isSame(blockEntity.getDesiredFluid())
        ).orElse(false);
    }

    private static boolean countEquals(ItemStack first, ItemStack second)
    {
        return first.getCount() == second.getCount();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        boolean stopQuickMove = false;

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT)
        {
            // This is a vanilla container slot so merge the stack into the tile inventory
            stopQuickMove = true;

            // try to move it to the bucket slot first if it has the required liquid
            if(hasRequiredLiquid(sourceStack))
            {
                moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + 4, TE_INVENTORY_FIRST_SLOT_INDEX + 5, false);
                // we are trying a single slot which might fail
            }

            if (countEquals(sourceStack, copyOfSourceStack) && !moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false))
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
        if (stopQuickMove) {
            return ItemStack.EMPTY;
        }
        return copyOfSourceStack;
    }
	
}
