package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class PegasusDHDMenu extends AbstractDHDMenu<PegasusDHDEntity>
{
	
	public PegasusDHDMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		super(MenuInit.PEGASUS_DHD.get(), containerId, inventory, (PegasusDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
	}

    public PegasusDHDMenu(int containerId, Inventory inventory, PegasusDHDEntity dhd)
    {
        super(MenuInit.PEGASUS_DHD.get(), containerId, inventory, dhd);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.PEGASUS_DHD.get());
    }

}
