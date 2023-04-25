package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundDHDUpdatePacket;

public abstract class AbstractDHDMenu extends AbstractContainerMenu
{
    protected final AbstractDHDEntity blockEntity;
    protected final Level level;
    public String symbolsType = "sgjourney:milky_way";
    
    public AbstractDHDMenu(MenuType<?> menu, int containerId, Inventory inv, FriendlyByteBuf extraData)
    {
        this(menu, containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public AbstractDHDMenu(MenuType<?> menu, int containerId, Inventory inv, BlockEntity entity)
    {
        super(menu, containerId);
        checkContainerSize(inv, 6);
        blockEntity = ((AbstractDHDEntity) entity);
        this.level = inv.player.level;
    }
    
    public void engageChevron(int symbol)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundDHDUpdatePacket(this.blockEntity.getBlockPos(), symbol));
    }
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) 
    {
    	return null;
    }
	
}
