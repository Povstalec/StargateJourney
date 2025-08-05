package net.povstalec.sgjourney.common.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;

public class RingPanelMenu extends InventoryMenu
{
    private final RingPanelEntity blockEntity;
    private final Level level;
    
    public RingPanelMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public RingPanelMenu(int containerId, Inventory inventory, BlockEntity entity)
    {
        super(MenuInit.RING_PANEL.get(), containerId);
        checkContainerSize(inventory, 6);
        blockEntity = ((RingPanelEntity) entity);
        this.level = inventory.player.level();
        
        addPlayerInventory(inventory, 8, 140);
        addPlayerHotbar(inventory, 8, 198);
        
        IItemHandler cap = this.blockEntity.getItemHandler();
        if(cap != null)
        {
            this.addSlot(new SlotItemHandler(cap, 0, 5, 36));
            this.addSlot(new SlotItemHandler(cap, 1, 23, 36));
            this.addSlot(new SlotItemHandler(cap, 2, 5, 54));
            this.addSlot(new SlotItemHandler(cap, 3, 23, 54));
            this.addSlot(new SlotItemHandler(cap, 4, 5, 72));
            this.addSlot(new SlotItemHandler(cap, 5, 23, 72));
        }
    }
    
    public Component getRingsPos(int i)
    {
    	if(i < blockEntity.ringsPos.size())
    	{
    		BlockPos coords = blockEntity.ringsPos.get(i);
    		
    		Component name = blockEntity.ringsName.get(i);
    		if(name.getString().length() == 0)
    			return Component.literal("[" + coords.getX() + " " + coords.getY() + " " + coords.getZ() + "]").withStyle(ChatFormatting.DARK_GREEN);
    		else
    			return Component.empty().append(name).withStyle(ChatFormatting.AQUA).append(Component.literal(" [" + coords.getX() + " " + coords.getY() + " " + coords.getZ() + "] ").withStyle(ChatFormatting.DARK_GREEN));
    	}
    	else
    		return Component.literal("-");
    }
    
    public void activateRings(int number)
    {
        PacketDistributor.sendToServer(new ServerboundRingPanelUpdatePacket(this.blockEntity.getBlockPos(), number));
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
	
	@Override
	protected int blockEntitySlotCount()
	{
		return 6;
	}
}
