package net.povstalec.sgjourney.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyBlockMenu<T extends EnergyBlockEntity> extends InventoryMenu<T>
{
	public EnergyBlockMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
	{
		super(type, containerId, inventory, blockEntity);
	}
	
	public long getEnergy()
	{
		return this.blockEntity.energyStorage.getTrueEnergyStored();
	}
	
	public long getEnergyCapacity()
	{
		return this.blockEntity.energyStorage.getTrueMaxEnergyStored();
	}
}
