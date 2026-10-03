package net.povstalec.sgjourney.common.menu.dhd;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.povstalec.sgjourney.common.block_entities.dhd.AndromedaDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class AndromedaDHDMenu extends AbstractDHDMenu<AndromedaDHDEntity>
{
	
	public AndromedaDHDMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		super(MenuInit.ANDROMEDA_DHD.get(), containerId, inventory, (AndromedaDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
	}

    public AndromedaDHDMenu(int containerId, Inventory inventory, AndromedaDHDEntity dhd)
    {
        super(MenuInit.ANDROMEDA_DHD.get(), containerId, inventory, dhd);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.ANDROMEDA_DHD.get());
    }

}
