package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.HeavyNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundLiquidizerUpdatePacket;
import org.jetbrains.annotations.NotNull;

public abstract class LiquidizerMenu<T extends AbstractNaquadahLiquidizerEntity<?>> extends EnergyBlockMenu<T>
{
    protected FluidStack fluidStack1;
    protected FluidStack fluidStack2;
	
	protected int itemInputSlotIndex;
	protected int fluidItemInputSlotIndex;
	protected int fluidItemInputDumpSlotIndex;
	protected int fluidItemOutputSlotIndex;
	protected int fluidItemOutputDumpSlotIndex;
	protected int energySlotIndex;

    public LiquidizerMenu(MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
    {
        super(type, containerId, inventory, blockEntity);
		
        checkContainerSize(inventory, 6);
        this.fluidStack1 = this.blockEntity.getInputFluidStack();
        this.fluidStack2 = this.blockEntity.getOutputFluidStack();

        addPlayerInventory(inventory, 8, 84);
        addPlayerHotbar(inventory, 8, 142);
		
		this.itemInputSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.itemInputHandler, 0, 62, 17)).index;
		
		this.fluidItemInputSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.fluidItemInputHandler, 0, 8, 17)).index;
		this.fluidItemOutputSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.fluidItemInputHandler, 1, 116, 17)).index;
		
		this.fluidItemOutputDumpSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.fluidItemOutputHandler, 0, 116, 53)).index;
		this.fluidItemInputDumpSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.fluidItemOutputHandler, 1, 8, 53)).index;
		
		this.energySlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 0, 142, 17)).index;
		
    }
	
	public void pressDumpButton(boolean inputTank)
	{
		PacketHandlerInit.INSTANCE.sendToServer(new ServerboundLiquidizerUpdatePacket(this.blockEntity.getBlockPos(), inputTank));
	}
	
	public FluidStack getInputFluidStack()
	{
		return this.blockEntity.getInputFluidStack();
	}
	
	public FluidStack getOutputFluidStack()
	{
		return this.blockEntity.getOutputFluidStack();
	}
	
	public int getMaxProgress()
	{
		return this.blockEntity.getMaxProgress();
	}
	
	public int getProgress()
	{
		return this.blockEntity.getProgress();
	}
    
	/**
     * Checks if the ItemStack has the required liquid for the liquidizer.
     * @return true if the ItemStack has the required liquid, false otherwise.
     */
    private boolean hasRequiredLiquid(ItemStack itemStack)
	{
		IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		if(fluidHandler != null)
			return blockEntity.isDesiredInputFluid(fluidHandler.getFluidInTank(0));
		
		return false;
    }

    /**
     * Checks if the ItemStack can accept the resulting liquid from the liquidizer.
     * @return true if the ItemStack has fluid tank with matching fluid type or is empty, false otherwise.
     */
    private boolean canAcceptResultingLiquid(ItemStack itemStack)
    {
		IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		if(fluidHandler != null)
			return fluidHandler.getFluidInTank(0).isEmpty() || fluidHandler.getFluidInTank(0).getFluid().isSame(blockEntity.getOutputFluidStack().getFluid());
		
		return false;
    }

    private static boolean countEquals(ItemStack first, ItemStack second)
    {
        return first.getCount() == second.getCount();
    }
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		if(sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent() && moveItemStackTo(sourceStack, energySlotIndex, energySlotIndex + 1, false))
			return true;
		
		// Try moving it to Liquid input slot
		if(hasRequiredLiquid(sourceStack) && moveItemStackTo(sourceStack, fluidItemInputSlotIndex, fluidItemInputSlotIndex + 1, false))
			return true;
		
		// Try moving it to Liquid output slot
		if(canAcceptResultingLiquid(sourceStack) && moveItemStackTo(sourceStack, fluidItemOutputSlotIndex, fluidItemOutputSlotIndex + 1, false))
			return true;
		
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
	
	
	
    public static class LiquidNaquadah extends LiquidizerMenu<NaquadahLiquidizerEntity>
    {
        public LiquidNaquadah(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (NaquadahLiquidizerEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
        }

		public LiquidNaquadah(int containerId, Inventory inventory, NaquadahLiquidizerEntity blockEntity)
		{
			super(MenuInit.NAQUADAH_LIQUIDIZER.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.NAQUADAH_LIQUIDIZER.get());
		}
    }
	
    public static class HeavyLiquidNaquadah extends LiquidizerMenu<HeavyNaquadahLiquidizerEntity>
    {
        public HeavyLiquidNaquadah(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (HeavyNaquadahLiquidizerEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
        }

		public HeavyLiquidNaquadah(int containerId, Inventory inventory, HeavyNaquadahLiquidizerEntity blockEntity)
		{
			super(MenuInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get());
		}
    }
}
