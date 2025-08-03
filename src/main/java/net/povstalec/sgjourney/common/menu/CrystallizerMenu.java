package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
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
        this(containerId, inventory, inventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public CrystallizerMenu(int containerId, Inventory inventory, BlockEntity blockEntity)
    {
        super(MenuInit.CRYSTALLIZER.get(), containerId);
        checkContainerSize(inventory, 5);
        this.blockEntity = ((AbstractCrystallizerEntity) blockEntity);
        this.level = inventory.player.level;
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
}
