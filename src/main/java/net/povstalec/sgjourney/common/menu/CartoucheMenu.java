package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.misc.SimpleTempContainer;
import org.jetbrains.annotations.NotNull;

public abstract class CartoucheMenu<C extends CartoucheEntity> extends InventoryMenu<C>
{
	public final SimpleTempContainer<CartoucheMenu<C>> tempContainer = new SimpleTempContainer<>(this, 1, 1)
	{
		@Override
		public boolean canPlaceItem(int slot, @NotNull ItemStack stack)
		{
			return stack.isEmpty() || stack.getItem() instanceof SymbolPaperItem;
		}
	};
	private final int tempSlotIndex;
	private final ContainerLevelAccess access;
	
    public CartoucheMenu(MenuType<?> type, int containerId, Inventory inventory, C blockEntity, ContainerLevelAccess access)
    {
        super(type, containerId, inventory, blockEntity);
		
		addPlayerInventory(inventory, 8, 170);
		addPlayerHotbar(inventory, 8, 228);
		
		this.tempSlotIndex = addBlockEntitySlot(new Slot(tempContainer, 0, 124, 57)
		{
			@Override
			public boolean mayPlace(@NotNull ItemStack stack)
			{
				return container.canPlaceItem(index, stack);
			}
		}).index;
		this.access = access;
    }
	
	@Override
	public void removed(@NotNull Player player)
	{
		super.removed(player);
		this.access.execute((level, pos) -> clearContainer(player, this.tempContainer));
	}
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		if(sourceStack.getItem() instanceof SymbolPaperItem && moveItemStackTo(sourceStack, tempSlotIndex, tempSlotIndex + 1, false))
			return true;
		
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
	
	
	
	public static class Stone extends CartoucheMenu<CartoucheEntity.Stone>
	{
		public Stone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (CartoucheEntity.Stone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public Stone(int containerId, Inventory inventory, CartoucheEntity.Stone blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.STONE_CARTOUCHE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.STONE_CARTOUCHE.get());
		}
	}
	
	public static class Sandstone extends CartoucheMenu<CartoucheEntity.Sandstone>
    {
        public Sandstone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (CartoucheEntity.Sandstone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public Sandstone(int containerId, Inventory inventory, CartoucheEntity.Sandstone blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.SANDSTONE_CARTOUCHE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.SANDSTONE_CARTOUCHE.get());
		}
    }
	
    public static class RedSandstone extends CartoucheMenu<CartoucheEntity.RedSandstone>
    {
        public RedSandstone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (CartoucheEntity.RedSandstone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public RedSandstone(int containerId, Inventory inventory, CartoucheEntity.RedSandstone blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.RED_SANDSTONE_CARTOUCHE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.RED_SANDSTONE_CARTOUCHE.get());
		}
    }
}
