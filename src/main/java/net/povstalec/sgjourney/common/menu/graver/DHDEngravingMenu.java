package net.povstalec.sgjourney.common.menu.graver;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.UniverseDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.GraverItem;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.InventoryMenu;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.misc.SimpleTempContainer;
import org.jetbrains.annotations.NotNull;

public abstract class DHDEngravingMenu<S extends AbstractDHDEntity> extends InventoryMenu<S> implements IDHDMenu
{
	public final SimpleTempContainer<DHDEngravingMenu<S>> tempContainer = new SimpleTempContainer<>(this, 1, 1)
	{
		@Override
		public boolean canPlaceItem(int slot, @NotNull ItemStack stack)
		{
			return stack.isEmpty() || stack.getItem() instanceof SymbolPaperItem;
		}
	};
	private final int tempSlotIndex;
	private final ContainerLevelAccess access;
	
    public DHDEngravingMenu(MenuType<?> type, int containerId, Inventory inventory, S blockEntity, ContainerLevelAccess access)
    {
        super(type, containerId, inventory, blockEntity);
		
		addPlayerInventory(inventory, 8, 140);
		addPlayerHotbar(inventory, 8, 198);
		
		this.tempSlotIndex = addBlockEntitySlot(new Slot(tempContainer, 0, 8, 108)
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
	public AbstractDHDEntity getDHD()
	{
		return blockEntity;
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
	
	
	
	public static class Universe extends DHDEngravingMenu<UniverseDHDEntity>
	{
		public Universe(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (UniverseDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public Universe(int containerId, Inventory inventory, UniverseDHDEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_UNIVERSE_DHD.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.UNIVERSE_DHD.get());
		}
	}
	
	public static class MilkyWay extends DHDEngravingMenu<MilkyWayDHDEntity>
	{
		public MilkyWay(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (MilkyWayDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public MilkyWay(int containerId, Inventory inventory, MilkyWayDHDEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_MILKY_WAY_DHD.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_DHD.get());
		}
	}
	
	public static class Classic extends DHDEngravingMenu<ClassicDHDEntity>
    {
        public Classic(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (ClassicDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public Classic(int containerId, Inventory inventory, ClassicDHDEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_CLASSIC_DHD.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_DHD.get());
		}
    }
}
