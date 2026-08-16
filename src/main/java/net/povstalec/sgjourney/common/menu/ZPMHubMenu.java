package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.ZPMHubEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class ZPMHubMenu extends InventoryMenu<ZPMHubEntity>
{
    public ZPMHubMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, (ZPMHubEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ZPMHubMenu(int containerId, Inventory inventory, ZPMHubEntity zpmHub)
    {
        super(MenuInit.ZPM_HUB.get(), containerId, inventory, zpmHub);
        checkContainerSize(inventory, 1);
        
        addPlayerInventory(inventory, 8, 86);
        addPlayerHotbar(inventory, 8, 144);
        
        this.blockEntity.getItemHandler().ifPresent(handler ->
        {
            this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 80, 35));
        });
    }
	
    @Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ZPM_HUB.get());
    }
}
