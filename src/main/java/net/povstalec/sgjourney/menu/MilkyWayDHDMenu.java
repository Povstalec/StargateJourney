package net.povstalec.sgjourney.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.init.MenuInit;

public class MilkyWayDHDMenu extends AbstractDHDMenu
{
	
	public MilkyWayDHDMenu(int containerId, Inventory inv, FriendlyByteBuf extraData)
	{
		super(MenuInit.MILKY_WAY_DHD.get(), containerId, inv, extraData);
        this.symbolsType = "sgjourney:milky_way";
	}

    public MilkyWayDHDMenu(int containerId, Inventory inv, BlockEntity entity)
    {
        super(MenuInit.MILKY_WAY_DHD.get(), containerId, inv, entity);
        this.symbolsType = "sgjourney:milky_way";
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.MILKY_WAY_DHD.get());
    }

}
