package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.packets.ServerboundInterfaceUpdatePacket;
import org.jetbrains.annotations.Nullable;

public abstract class InterfaceMenu<T extends AbstractInterfaceEntity> extends EnergyBlockMenu<T>
{
	protected int energySlotIndex;
	
    public InterfaceMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
    {
        super(type, containerId, inventory, blockEntity);
		checkContainerSize(inventory, 1);
		
		this.energySlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 0, 165, 18)).index;
		
		addPlayerInventory(inventory, 31, 140);
		addPlayerHotbar(inventory, 31, 198);
    }
	
	public InterfaceMode getMode()
	{
		return this.blockEntity.getMode();
	}
	
	public void setEnergyTargetAndMode(long energyTarget, InterfaceMode mode)
	{
		PacketDistributor.sendToServer(new ServerboundInterfaceUpdatePacket(this.blockEntity.getBlockPos(), energyTarget, mode));
	}
	
	public long getEnergyTarget()
	{
		return this.blockEntity.getEnergyTarget();
	}
	
	public long getEnergyBlockEnergy()
	{
		return this.blockEntity.getEnergyBlockEnergy();
	}
	
	public int getStargateOpenTime()
	{
		return this.blockEntity.getStargateOpenTime();
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		return this.blockEntity.getStargateTimeSinceLastTraveler();
	}
	
	public AbstractInterfaceEntity.InterfaceType getInterfaceType()
	{
		return this.blockEntity.getInterfaceType();
	}
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		return sourceStack.getCapability(Capabilities.EnergyStorage.ITEM) != null && moveItemStackTo(sourceStack, energySlotIndex, energySlotIndex + 1, false);
	}
	
	
	
	public static class Basic extends InterfaceMenu<BasicInterfaceEntity>
	{
		public Basic(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (BasicInterfaceEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Basic(int containerId, Inventory inventory, BasicInterfaceEntity blockEntity)
		{
			super(MenuInit.BASIC_INTERFACE.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.BASIC_INTERFACE.get());
		}
	}
	
	public static class Crystal extends InterfaceMenu<CrystalInterfaceEntity>
	{
		public Crystal(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (CrystalInterfaceEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Crystal(int containerId, Inventory inventory, CrystalInterfaceEntity blockEntity)
		{
			super(MenuInit.CRYSTAL_INTERFACE.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CRYSTAL_INTERFACE.get());
		}
	}
	
	public static class AdvancedCrystal extends InterfaceMenu<AdvancedCrystalInterfaceEntity>
	{
		public AdvancedCrystal(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (AdvancedCrystalInterfaceEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public AdvancedCrystal(int containerId, Inventory inventory, AdvancedCrystalInterfaceEntity blockEntity)
		{
			super(MenuInit.ADVANCED_CRYSTAL_INTERFACE.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());
		}
	}
}
