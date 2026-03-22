package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.energy_cores.IEnergyCore;

public class DHDCrystalMenu extends InventoryMenu<CrystalDHDEntity>
{
	protected int largeControlCrystalIndex;
	protected int[] crystalSlotIndex = new int[8];
	protected int energySlotIndex;
	protected int energyFeederSlotIndex;
	
	public DHDCrystalMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(containerId, inventory, (CrystalDHDEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
	}
	
	public DHDCrystalMenu(int containerId, Inventory inventory, CrystalDHDEntity blockEntity)
	{
		super(MenuInit.DHD_CRYSTAL.get(), containerId, inventory, blockEntity);
		
		checkContainerSize(inventory, 9);
		addPlayerInventory(inventory, 8, 84);
		addPlayerHotbar(inventory, 8, 142);
		
		this.largeControlCrystalIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 0, 80, 35)).index;
		
		this.crystalSlotIndex[0] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 1, 80, 17)).index;
		this.crystalSlotIndex[1] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 2, 98, 17)).index;
		this.crystalSlotIndex[2] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 3, 98, 35)).index;
		this.crystalSlotIndex[3] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 4, 98, 53)).index;
		this.crystalSlotIndex[4] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 5, 80, 53)).index;
		this.crystalSlotIndex[5] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 6, 62, 53)).index;
		this.crystalSlotIndex[6] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 7, 62, 35)).index;
		this.crystalSlotIndex[7] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalHandler, 8, 62, 17)).index;
		
		this.energySlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 0, 134, 27)).index;
		this.energyFeederSlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 1, 134, 53)).index;
	}
	
	public long getStargateEnergy()
	{
		return this.blockEntity.getStargateEnergy();
	}
	
	public int getStargateOpenTime()
	{
		return this.blockEntity.getStargateOpenTime();
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		return this.blockEntity.getStargateTimeSinceLastTraveler();
	}
	
	public long getEnergy()
	{
		return this.blockEntity.energyStorage.getTrueEnergyStored();
	}
	
	public long getMaxEnergy()
	{
		return this.blockEntity.energyStorage.getTrueMaxEnergyStored();
	}
	
	public boolean enableAdvancedProtocols()
	{
		return this.blockEntity.enableAdvancedProtocols();
	}
	
	public long getEnergyTarget()
	{
		return this.blockEntity.getEnergyTarget();
	}
	
	public long maxEnergyDeplete()
	{
		return this.blockEntity.maxEnergyDeplete();
	}
	
	public int getMaxDistance()
	{
		return this.blockEntity.getMaxDistance();
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_DHD.get()) ||
				stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.PEGASUS_DHD.get()) ||
				stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_DHD.get());
	}
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		if((sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent() || sourceStack.getItem() instanceof IEnergyCore || sourceStack.getItem() instanceof ZeroPointModule) && moveItemStackTo(sourceStack, energySlotIndex, energySlotIndex + 1, false))
			return true;
		
		if(sourceStack.getItem() instanceof NaquadahFuelRodItem && moveItemStackTo(sourceStack, energyFeederSlotIndex, energyFeederSlotIndex + 1, false))
			return true;
		
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
}
