package net.povstalec.sgjourney.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundDHDUpdatePacket;

public abstract class AbstractDHDMenu<T extends AbstractDHDEntity> extends SGJourneyMenu<T>
{
    public AbstractDHDMenu(MenuType<?> menu, int containerId, Inventory inventory, T blockEntity)
    {
        super(menu, containerId, inventory, blockEntity);
        checkContainerSize(inventory, 9);
    }
	
	public void engageStargate()
	{
		PacketHandlerInit.INSTANCE.sendToServer(new ServerboundDHDUpdatePacket(this.blockEntity.getBlockPos(), -1));
	}
    
    public void encodeSymbol(int symbol)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundDHDUpdatePacket(this.blockEntity.getBlockPos(), symbol));
    }
    
    public boolean isSymbolEngaged(int symbol)
    {
    	return blockEntity.isSymbolEncoded(symbol);
    }
	
	public boolean isSymbolRemapped(int symbol)
	{
		return blockEntity.isSymbolRemapped(symbol);
	}
	
	public int getRemappedOriginalSymbol(int symbol)
	{
		return blockEntity.getRemappedOriginalSymbol(symbol);
	}
    
    public boolean isCenterButtonEngaged()
    {
    	return blockEntity.isCenterButtonEngaged();
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
    	return ItemStack.EMPTY;
    }
}
