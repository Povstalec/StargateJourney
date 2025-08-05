package net.povstalec.sgjourney.common.menu;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public abstract class LiquidizerMenu extends InventoryMenu
{
    protected final AbstractNaquadahLiquidizerEntity blockEntity;
    protected final Level level;
    protected FluidStack fluidStack1;
    protected FluidStack fluidStack2;
    
    public LiquidizerMenu(MenuType<LiquidizerMenu> type, int containerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(type, containerId);
        checkContainerSize(inventory, 3);
        this.blockEntity = ((AbstractNaquadahLiquidizerEntity) blockEntity);
        this.level = inventory.player.level();
        this.fluidStack1 = this.blockEntity.getFluid1();
        this.fluidStack2 = this.blockEntity.getFluid2();
		
		addPlayerInventory(inventory, 8, 84);
		addPlayerHotbar(inventory, 8, 142);
		
		IItemHandler cap0 = this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), Direction.UP);
		if(cap0 != null)
			this.addSlot(new SlotItemHandler(cap0, 0, 80, 20));
		
		IItemHandler cap1 = this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), Direction.NORTH);
		if(cap1 != null)
			this.addSlot(new SlotItemHandler(cap1, 0, 34, 20));
		
		IItemHandler cap2 = this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), Direction.DOWN);
		if(cap2 != null)
			this.addSlot(new SlotItemHandler(cap2, 0, 126, 58));
    }
    
    public void setFluid1(FluidStack fluidStack)
	{
		this.fluidStack1 = fluidStack;
	}
    
    public void setFluid2(FluidStack fluidStack)
	{
		this.fluidStack2 = fluidStack;
	}
	
	public FluidStack getFluid1()
	{
		return this.blockEntity.getFluid1();
	}
	
	public FluidStack getFluid2()
	{
		return this.blockEntity.getFluid2();
	}
	
	public Fluid getDesiredFluid1()
	{
		return this.blockEntity.getDesiredFluid1();
	}
	
	public Fluid getDesiredFluid2()
	{
		return this.blockEntity.getDesiredFluid2();
	}
	
	public int getProgress()
	{
		return this.blockEntity.progress;
	}
    
    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.NAQUADAH_LIQUIDIZER.get()) ||
        		stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
    }
	
	@Override
	protected int blockEntitySlotCount()
	{
		return 3;
	}
    
	private static final int TE_INVENTORY_INPUT_BUCKET_SLOT_INDEX = 1;
    private static final int TE_INVENTORY_OUTPUT_BUCKET_SLOT_INDEX = 2;

    /**
     * Checks if the ItemStack has the required liquid for the liquidizer.
     * @return true if the ItemStack has the required liquid, false otherwise.
     */
    private boolean hasRequiredLiquid(ItemStack itemStack)
	{
		IFluidHandlerItem fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
			return fluidHandler.getFluidInTank(0).is(blockEntity.getDesiredFluid1());
		
		return false;
    }

    /**
     * Checks if the ItemStack can accept the resulting liquid from the liquidizer.
     * @return true if the ItemStack has fluid tank with matching fluid type or is empty, false otherwise.
     */
    private boolean canAcceptResultingLiquid(ItemStack itemStack)
    {
		IFluidHandlerItem fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
			return fluidHandler.getFluidInTank(0).isEmpty() || fluidHandler.getFluidInTank(0).is(blockEntity.getDesiredFluid2());
		
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
                moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_INPUT_BUCKET_SLOT_INDEX,
                        TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_INPUT_BUCKET_SLOT_INDEX + 1, false);
                // we are trying a single slot which might fail
            }
            else if (canAcceptResultingLiquid(sourceSlot.getItem())) {
                // try an empty bucket for the output slot
                moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_OUTPUT_BUCKET_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_OUTPUT_BUCKET_SLOT_INDEX + 1, false);
            }

            // if nothing was moved before, try all slots
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
            // Do not "overflow" to the next slot
            return ItemStack.EMPTY;
        }
        return copyOfSourceStack;
    }
	
    public static class LiquidNaquadah extends LiquidizerMenu
    {
        public LiquidNaquadah(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
        }

		public LiquidNaquadah(int containerId, Inventory inventory, BlockEntity blockEntity)
		{
			super(MenuInit.NAQUADAH_LIQUIDIZER.get(), containerId, inventory, blockEntity);
		}
    	
    }
	
    public static class HeavyLiquidNaquadah extends LiquidizerMenu
    {
        public HeavyLiquidNaquadah(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
        }

		public HeavyLiquidNaquadah(int containerId, Inventory inventory, BlockEntity blockEntity)
		{
			super(MenuInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), containerId, inventory, blockEntity);
		}
    	
    }
}
