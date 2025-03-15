package net.povstalec.sgjourney.common.items.energy_cores;

import net.minecraft.world.item.ItemStack;

public interface IEnergyCore
{
	long maxGeneratedEnergy(ItemStack energyCore, ItemStack input);
	
	/**
	 * Generates energy
	 * @param energyCore Energy Core stack
	 * @return Energy generated
	 */
	long generateEnergy(ItemStack energyCore, ItemStack input);
}
