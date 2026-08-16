package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class ClassicDHDMenu extends AbstractDHDMenu<ClassicDHDEntity>
{
	
	public ClassicDHDMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(containerId, inventory, (ClassicDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
	}

    public ClassicDHDMenu(int containerId, Inventory inventory, ClassicDHDEntity dhd)
    {
        super(MenuInit.CLASSIC_DHD.get(), containerId, inventory, dhd);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_DHD.get());
    }
}
