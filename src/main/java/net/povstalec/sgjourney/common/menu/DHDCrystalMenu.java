package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.energy_cores.IEnergyCore;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class DHDCrystalMenu<T extends CrystalDHDEntity> extends InventoryMenu<T>
{
	protected int largeControlCrystalIndex;
	protected int[] crystalSlotIndex = new int[8];
	protected int energySlotIndex;
	protected int energyFeederSlotIndex;
	
	public DHDCrystalMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
	{
		super(type, containerId, inventory, blockEntity);
		
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
		return this.blockEntity.maxEnergyTransfer();
	}
	
	public int getMaxDistance()
	{
		return this.blockEntity.getMaxConnectionDistance();
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
	
	public Set<Integer> getNetworks()
	{
		return this.blockEntity.stargateCache.returnCachedOrDefault(AbstractStargateEntity::getCachedNetworks, this.blockEntity.getNetworks());
	}
	
	public boolean hasNetworkRestrictions()
	{
		return this.blockEntity.stargateCache.returnCachedOrDefault(AbstractStargateEntity::hasNetworkRestrictions, false);
	}
	
	
	
	public static class MilkyWay extends DHDCrystalMenu<MilkyWayDHDEntity>
	{
		public MilkyWay(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (MilkyWayDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public MilkyWay(int containerId, Inventory inventory, MilkyWayDHDEntity blockEntity)
		{
			super(MenuInit.MILKY_WAY_DHD_CRYSTAL.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.MILKY_WAY_DHD.get());
		}
	}
	
	public static class Pegasus extends DHDCrystalMenu<PegasusDHDEntity>
	{
		public Pegasus(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (PegasusDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Pegasus(int containerId, Inventory inventory, PegasusDHDEntity blockEntity)
		{
			super(MenuInit.PEGASUS_DHD_CRYSTAL.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.PEGASUS_DHD.get());
		}
	}
	
	public static class Classic extends DHDCrystalMenu<ClassicDHDEntity>
	{
		public Classic(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (ClassicDHDEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Classic(int containerId, Inventory inventory, ClassicDHDEntity blockEntity)
		{
			super(MenuInit.CLASSIC_DHD_CRYSTAL.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.CLASSIC_DHD.get());
		}
	}
}
