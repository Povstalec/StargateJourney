package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class NaquadahGeneratorMenu extends InventoryMenu
{
    private final NaquadahGeneratorEntity blockEntity;
    private final Level level;
    
    public NaquadahGeneratorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public NaquadahGeneratorMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(MenuInit.NAQUADAH_GENERATOR.get(), containerId);
        checkContainerSize(inv, 1);
        blockEntity = ((NaquadahGeneratorEntity) entity);
        this.level = inv.player.level();

        addPlayerInventory(inv, 8, 86);
        addPlayerHotbar(inv, 8, 144);
        
        IItemHandler cap = this.level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        if(cap != null)
            this.addSlot(new SlotItemHandler(cap, 0, 62, 35));
    }
    
    public int getReactionProgress()
    {
    	return this.blockEntity.getReactionProgress();
    }
    
    public long getReactionTime()
    {
    	return this.blockEntity.getReactionTime();
    }
    
    public long getEnergy()
    {
    	return this.blockEntity.getEnergyStored();
    }
    
    public long getMaxEnergy()
    {
    	return this.blockEntity.getEnergyCapacity();
    }
	
	@Override
	public boolean stillValid(Player player)
    {
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.NAQUADAH_GENERATOR_MARK_I.get()) || 
				stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
	}
	
	@Override
	protected int blockEntitySlotCount()
	{
		return 1;
	}
}
