package net.povstalec.sgjourney.common.menu.graver;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.GraverItem;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.menu.InventoryMenu;
import net.povstalec.sgjourney.common.misc.SimpleTempContainer;
import org.jetbrains.annotations.NotNull;

public abstract class StargateEngravingMenu<S extends AbstractStargateEntity<?>> extends InventoryMenu<S>
{
	public final SimpleTempContainer<StargateEngravingMenu<S>> tempContainer = new SimpleTempContainer<>(this, 1, 1)
	{
		@Override
		public boolean canPlaceItem(int slot, @NotNull ItemStack stack)
		{
			return stack.isEmpty() || stack.getItem() instanceof SymbolPaperItem;
		}
	};
	private final int tempSlotIndex;
	private final ContainerLevelAccess access;
	
    public StargateEngravingMenu(MenuType<?> type, int containerId, Inventory inventory, S blockEntity, ContainerLevelAccess access)
    {
        super(type, containerId, inventory, blockEntity);
		
		addPlayerInventory(inventory, 8, 82);
		addPlayerHotbar(inventory, 8, 140);
		
		this.tempSlotIndex = addBlockEntitySlot(new Slot(tempContainer, 0, 124, 24)
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
	
	protected boolean stargateStillValid(ContainerLevelAccess containerLevelAccess, Player player, Block block)
	{
		return containerLevelAccess.evaluate((level, pos) -> level.getBlockState(pos).is(block) &&
			player.distanceToSqr(blockEntity.getCenter()) <= 256D, true);
	}
	
	
	
	public static class Universe extends StargateEngravingMenu<UniverseStargateEntity>
	{
		public Universe(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (UniverseStargateEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public Universe(int containerId, Inventory inventory, UniverseStargateEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_UNIVERSE_STARGATE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stargateStillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.UNIVERSE_STARGATE.get());
		}
	}
	
	public static class MilkyWay extends StargateEngravingMenu<MilkyWayStargateEntity>
	{
		public MilkyWay(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (MilkyWayStargateEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
		}
		
		public MilkyWay(int containerId, Inventory inventory, MilkyWayStargateEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_MILKY_WAY_STARGATE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stargateStillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_STARGATE.get());
		}
	}
	
	public static class Classic extends StargateEngravingMenu<ClassicStargateEntity>
    {
        public Classic(int containerId, Inventory inventory, FriendlyByteBuf extraData)
        {
            this(containerId, inventory, (ClassicStargateEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()), ContainerLevelAccess.NULL);
        }

		public Classic(int containerId, Inventory inventory, ClassicStargateEntity blockEntity, ContainerLevelAccess containerLevelAccess)
		{
			super(MenuInit.ENGRAVING_CLASSIC_STARGATE.get(), containerId, inventory, blockEntity, containerLevelAccess);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stargateStillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_STARGATE.get());
		}
    }
}
