package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class MilkyWayDHDMenu extends AbstractDHDMenu<MilkyWayDHDEntity>
{
	
	public MilkyWayDHDMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		super(MenuInit.MILKY_WAY_DHD.get(), containerId, inventory, (MilkyWayDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
	}

    public MilkyWayDHDMenu(int containerId, Inventory inventory, MilkyWayDHDEntity dhd)
    {
        super(MenuInit.MILKY_WAY_DHD.get(), containerId, inventory, dhd);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_DHD.get());
    }

}
