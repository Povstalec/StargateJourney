package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class ClassicDHDMenu extends AbstractDHDMenu
{
	
	public ClassicDHDMenu(int containerId, Inventory inv, FriendlyByteBuf extraData)
	{
		super(MenuInit.CLASSIC_DHD.get(), containerId, inv, extraData);
        this.symbolsType = "sgjourney:milky_way";
	}

    public ClassicDHDMenu(int containerId, Inventory inv, BlockEntity entity)
    {
        super(MenuInit.CLASSIC_DHD.get(), containerId, inv, entity);
        this.symbolsType = "sgjourney:milky_way";
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.CLASSIC_DHD.get());
    }

}
