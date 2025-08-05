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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;
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
       // this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
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
        
        this.blockEntity.getItemHandler(0).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 80, 20));
        });
        this.blockEntity.getItemHandler(1).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 67, 50));
        });
        this.blockEntity.getItemHandler(2).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 93, 50));
        });
        this.blockEntity.getItemHandler(3).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 130, 36));
        });
        this.blockEntity.getItemHandler(4).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 34, 20));
        });
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
    
    @Override
    protected int blockEntitySlotCount()
    {
        return 5;
    }

    /**
     * Checks if the ItemStack has the required liquid for the liquidizer.
     * @return true if the ItemStack has the required liquid, false otherwise.
     */
    private boolean hasRequiredLiquid(ItemStack itemStack)
    {
        IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
        if(fluidHandler != null)
            return fluidHandler.getFluidInTank(0).getFluid().isSame(blockEntity.getDesiredFluid());
        
        return false;
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
