package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;

public class InterfacePeripheralWrapper
{
	private AbstractInterfaceEntity interfaceEntity;
	private InterfacePeripheral interfacePeripheral;
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
		if(interfacePeripheral != null && interfacePeripheral.equals(newPeripheral))
		{
			// Peripheral is same as before, no changes needed.
			return false;
		}

		// Peripheral has changed, invalidate the capability and trigger a block update.
		interfacePeripheral = newPeripheral;
		if(peripheral != null)
		{
			peripheral.invalidate();
			peripheral = LazyOptional.of(() -> newPeripheral);
		}
		return true;
	}
	
	public LazyOptional<IPeripheral> newPeripheral()
	{
		interfacePeripheral = createPeripheral(interfaceEntity, interfaceEntity.findEnergyBlockEntity());
		peripheral = LazyOptional.of(() -> interfacePeripheral);
		
		if(peripheral == null)
		{
			interfacePeripheral = createPeripheral(interfaceEntity, interfaceEntity.findEnergyBlockEntity());
			peripheral = LazyOptional.of(() -> interfacePeripheral);
		}
		return peripheral;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(this.interfacePeripheral instanceof StargatePeripheral stargatePeripheral)
		{
			stargatePeripheral.queueEvent(eventName, objects);
		}
	}
	
	public InterfacePeripheral getPeripheral()
	{
		return this.interfacePeripheral;
	}
}
