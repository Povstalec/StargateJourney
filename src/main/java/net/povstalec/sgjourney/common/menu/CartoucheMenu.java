package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.CartoucheBlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.GraverItem;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.misc.SimpleTempContainer;
import org.jetbrains.annotations.NotNull;

public abstract class CartoucheMenu<C extends CartoucheBlockEntity> extends InventoryMenu<C>
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
		
		addPlayerInventory(inventory, 8, 168);
		addPlayerHotbar(inventory, 8, 226);
		
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
	protected void addPlayerHotbar(Inventory playerInventory, int x, int y)
	{
		int selected = playerInventory.getSelected().getItem() instanceof GraverItem ? playerInventory.selected : -1;
		
		for(int i = 0; i < 9; ++i)
		{
			if(i == selected) // Lock the selected slot
			{
				this.addSlot(new Slot(playerInventory, i, x + i * 18, y)
				{
					@Override
					public boolean mayPlace(@NotNull ItemStack stack)
					{
						return true;
					}
					
					@Override
					public boolean mayPickup(@NotNull Player player)
					{
						return false;
					}
				});
			}
			else
				this.addSlot(new Slot(playerInventory, i, x + i * 18, y));
		}
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
	
	
	
	public static class Stone extends CartoucheMenu<CartoucheBlockEntity.Stone>
	{
		public Stone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (CartoucheBlockEntity.Stone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public Stone(int containerId, Inventory inventory, CartoucheBlockEntity.Stone blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.STONE_CARTOUCHE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.STONE_CARTOUCHE.get());
		}
	}
	
	public static class Sandstone extends CartoucheMenu<CartoucheBlockEntity.Sandstone>
    {
        public Sandstone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (CartoucheBlockEntity.Sandstone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public Sandstone(int containerId, Inventory inventory, CartoucheBlockEntity.Sandstone blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.SANDSTONE_CARTOUCHE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.SANDSTONE_CARTOUCHE.get());
		}
    }
	
    public static class RedSandstone extends CartoucheMenu<CartoucheBlockEntity.RedSandstone>
    {
        public RedSandstone(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (CartoucheBlockEntity.RedSandstone) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public RedSandstone(int containerId, Inventory inventory, CartoucheBlockEntity.RedSandstone blockEntity, ContainerLevelAccess containerLevelAccess)
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
