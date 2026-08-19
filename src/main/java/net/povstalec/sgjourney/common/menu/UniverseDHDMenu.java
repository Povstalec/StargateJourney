package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.povstalec.sgjourney.common.block_entities.dhd.UniverseDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class UniverseDHDMenu extends AbstractDHDMenu<UniverseDHDEntity>
{
	
	public UniverseDHDMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		super(MenuInit.UNIVERSE_DHD.get(), containerId, inventory, (UniverseDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
	}

    public UniverseDHDMenu(int containerId, Inventory inventory, UniverseDHDEntity dhd)
    {
        super(MenuInit.UNIVERSE_DHD.get(), containerId, inventory, dhd);
    }

	@Override
    public boolean stillValid(Player player)
    {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.UNIVERSE_DHD.get());
    }

}
