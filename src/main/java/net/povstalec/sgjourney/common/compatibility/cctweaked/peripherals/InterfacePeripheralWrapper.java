package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;

public class InterfacePeripheralWrapper
{
	private AbstractInterfaceEntity interfaceEntity;
	private InterfacePeripheral basicInterfacePeripheral;
	private LazyOptional<IPeripheral> peripheral;
    protected final List<IComputerAccess> computerList = new LinkedList<>();
	
	public InterfacePeripheralWrapper(AbstractInterfaceEntity interfaceEntity)
	{
		this.interfaceEntity = interfaceEntity;
	}
	
	public static InterfacePeripheral createPeripheral(AbstractInterfaceEntity interfaceEntity, EnergyBlockEntity energyBlockEntity)
	{
		if(energyBlockEntity instanceof AbstractStargateEntity stargate)
			return new StargatePeripheral(interfaceEntity, stargate);

		return new InterfacePeripheral(interfaceEntity);
	}
	
	public boolean resetInterface()
	{
		InterfacePeripheral newPeripheral = createPeripheral(interfaceEntity, interfaceEntity.findEnergyBlockEntity());
		if (basicInterfacePeripheral != null && basicInterfacePeripheral.equals(newPeripheral))
		{
			// Peripheral is same as before, no changes needed.
			return false;
		}

		// Peripheral has changed, invalidate the capability and trigger a block update.
		basicInterfacePeripheral = newPeripheral;
		if (peripheral != null)
		{
			peripheral.invalidate();
			peripheral = LazyOptional.of(() -> newPeripheral);
		}
		return true;
	}
	
	public LazyOptional<IPeripheral> newPeripheral()
	{
		basicInterfacePeripheral = createPeripheral(interfaceEntity, interfaceEntity.energyBlockEntity);
		peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
		
		if(peripheral == null)
		{
			basicInterfacePeripheral = createPeripheral(interfaceEntity, interfaceEntity.findEnergyBlockEntity());
			peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
		}
		return peripheral;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(this.basicInterfacePeripheral instanceof StargatePeripheral stargatePeripheral)
		{
			stargatePeripheral.queueEvent(eventName, objects);
		}
	}
	
	public InterfacePeripheral getPeripheral()
	{
		return this.basicInterfacePeripheral;
	}
}
