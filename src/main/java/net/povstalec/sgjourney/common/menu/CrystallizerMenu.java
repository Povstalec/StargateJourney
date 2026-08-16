package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystallizerEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.packets.ServerboundCrystallizerUpdatePacket;
import org.jetbrains.annotations.NotNull;

public abstract class CrystallizerMenu<T extends AbstractCrystallizerEntity<?>> extends EnergyBlockMenu<T>
{
	protected FluidStack fluidStack;
	
	protected int crystalBaseSlotIndex;
	protected int leftIngredientSlotIndex;
	protected int rightIngredientSlotIndex;
	protected int outputSlotIndex;
	protected int liquidContainerDumpSlotIndex;
	protected int liquidContainerInputSlotIndex;
	protected int energySlotIndex;
	
	public CrystallizerMenu(MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
	{
		super(type, containerId, inventory, blockEntity);
		checkContainerSize(inventory, 7);
		this.fluidStack = this.blockEntity.getFluidStack();
	
		addPlayerInventory(inventory, 8, 84);
		addPlayerHotbar(inventory, 8, 142);
		
		this.crystalBaseSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalBaseHandler, 0, 71, 17)).index;
		
		this.leftIngredientSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.primaryIngredientHandler, 0, 55, 53)).index;
		
		this.rightIngredientSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.secondaryIngredientHandler, 0, 87, 53)).index;
		
		this.outputSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.outputHandler, 0, 112, 36)).index;
		this.liquidContainerDumpSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.outputHandler, 1, 8, 53)).index;
		
		this.liquidContainerInputSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.fluidInputHandler, 0, 8, 17)).index;
		
		this.energySlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 0, 142, 17)).index;
	}
	
	public void pressDumpButton()
	{
		PacketDistributor.sendToServer(new ServerboundCrystallizerUpdatePacket(this.blockEntity.getBlockPos()));
	}
	
	public void setFluid(FluidStack fluidStack)
	{
		this.fluidStack = fluidStack;
	}
	
	public FluidStack getFluidStack()
	{
		return this.blockEntity.getFluidStack();
	}
	
	public int getMaxProgress()
	{
		return this.blockEntity.getMaxProgress();
	}
	
	public int getProgress()
	{
		return this.blockEntity.getProgress();
	}
	
	/**
	 * Checks if the ItemStack has the required liquid for the liquidizer.
	 * @return true if the ItemStack has the required liquid, false otherwise.
	 */
	private boolean hasRequiredLiquid(ItemStack itemStack)
	{
		IFluidHandlerItem fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
			return blockEntity.isDesiredInputFluid(fluidHandler.getFluidInTank(0));
		
		return false;
	}
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		if(sourceStack.getCapability(Capabilities.EnergyStorage.ITEM) != null && moveItemStackTo(sourceStack, energySlotIndex, energySlotIndex + 1, false))
			return true;
		
		// Try moving it to Liquid input slot
		if(hasRequiredLiquid(sourceStack) && moveItemStackTo(sourceStack, liquidContainerInputSlotIndex, liquidContainerInputSlotIndex + 1, false))
			return true;
		
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
	
	
	
	public static class Crystallizer extends CrystallizerMenu<CrystallizerEntity>
	{
		public Crystallizer(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (CrystallizerEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Crystallizer(int containerId, Inventory inventory, CrystallizerEntity blockEntity)
		{
			super(MenuInit.CRYSTALLIZER.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CRYSTALLIZER.get());
		}
	}
	
	public static class AdvancedCrystallizer extends CrystallizerMenu<AdvancedCrystallizerEntity>
	{
		public AdvancedCrystallizer(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (AdvancedCrystallizerEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public AdvancedCrystallizer(int containerId, Inventory inventory, AdvancedCrystallizerEntity blockEntity)
		{
			super(MenuInit.ADVANCED_CRYSTALLIZER.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ADVANCED_CRYSTALLIZER.get());
		}
	}
}
