package net.povstalec.sgjourney.common.cctweaked.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public class CrystalPeripheralWrapper
{
	CrystalInterfaceEntity crystalInterface;
	private CrystalInterfacePeripheral crystalInterfacePeripheral;
	private LazyOptional<IPeripheral> peripheral;
	
	public CrystalPeripheralWrapper(CrystalInterfaceEntity crystalInterface)
	{
		this.crystalInterface = crystalInterface;
	}
	
	public static CrystalInterfacePeripheral createPeripheral(CrystalInterfaceEntity crystalInterface, EnergyBlockEntity energyBlockEntity)
	{
		if(energyBlockEntity instanceof AbstractStargateEntity stargate)
			return new CrystalStargatePeripheral(crystalInterface, stargate);

		return new CrystalInterfacePeripheral(crystalInterface);
	}
	
	public boolean resetInterface()
	{
		CrystalInterfacePeripheral newPeripheral = createPeripheral(crystalInterface, crystalInterface.findEnergyBlockEntity());
		if (crystalInterfacePeripheral != null && crystalInterfacePeripheral.equals(newPeripheral))
		{
			// Peripheral is same as before, no changes needed.
			return false;
		}

		// Peripheral has changed, invalidate the capability and trigger a block update.
		crystalInterfacePeripheral = newPeripheral;
		if (peripheral != null)
		{
			peripheral.invalidate();
			peripheral = LazyOptional.of(() -> newPeripheral);
		}
		return true;
	}
	
	public LazyOptional<IPeripheral> newPeripheral()
	{
		crystalInterfacePeripheral = createPeripheral(crystalInterface, crystalInterface.energyBlockEntity);
		peripheral = LazyOptional.of(() -> crystalInterfacePeripheral);
		
		if (peripheral == null)
		{
			crystalInterfacePeripheral = createPeripheral(crystalInterface, crystalInterface.findEnergyBlockEntity());
			peripheral = LazyOptional.of(() -> crystalInterfacePeripheral);
		}
		return peripheral;
	}
}
