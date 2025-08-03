package net.povstalec.sgjourney.common.menu;

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
import net.povstalec.sgjourney.common.block_entities.tech.ZPMHubEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class ZPMHubMenu extends InventoryMenu
{
    private final ZPMHubEntity blockEntity;
    private final Level level;
    
    public ZPMHubMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ZPMHubMenu(int containerId, Inventory inventory, BlockEntity entity)
    {
        super(MenuInit.ZPM_HUB.get(), containerId);
        checkContainerSize(inventory, 1);
        blockEntity = ((ZPMHubEntity) entity);
        this.level = inventory.player.level();
        
        addPlayerInventory(inventory, 8, 86);
        addPlayerHotbar(inventory, 8, 144);
        
        this.blockEntity.getItemHandler().ifPresent(handler ->
        {
            this.addSlot(new SlotItemHandler(handler, 0, 80, 35));
        });
    }
	
    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ZPM_HUB.get());
    }
}
