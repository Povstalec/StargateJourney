package net.povstalec.sgjourney.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.init.MenuInit;

public class PegasusDHDMenu extends AbstractDHDMenu
{
	
	public PegasusDHDMenu(int containerId, Inventory inv, FriendlyByteBuf extraData)
	{
		super(MenuInit.PEGASUS_DHD.get(), containerId, inv, extraData);
	}

    public PegasusDHDMenu(int containerId, Inventory inv, BlockEntity entity)
    {
        super(MenuInit.PEGASUS_DHD.get(), containerId, inv, entity);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.PEGASUS_DHD.get());
    }

}
