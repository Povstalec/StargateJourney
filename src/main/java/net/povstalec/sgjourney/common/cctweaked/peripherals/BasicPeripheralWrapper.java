package net.povstalec.sgjourney.common.cctweaked.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public class BasicPeripheralWrapper
{
	BasicInterfaceEntity basicInterface;
	private BasicInterfacePeripheral basicInterfacePeripheral;
	private LazyOptional<IPeripheral> peripheral;
    protected final List<IComputerAccess> computerList = new LinkedList<>();
	
	public BasicPeripheralWrapper(BasicInterfaceEntity basicInterface)
	{
		this.basicInterface = basicInterface;
	}
	
	public static BasicInterfacePeripheral createPeripheral(BasicInterfaceEntity basicInterface, EnergyBlockEntity energyBlockEntity)
	{
		if(energyBlockEntity instanceof AbstractStargateEntity stargate)
			return new BasicStargatePeripheral(basicInterface, stargate);

		return new BasicInterfacePeripheral(basicInterface);
	}
	
	public boolean resetInterface()
	{
		BasicInterfacePeripheral newPeripheral = createPeripheral(basicInterface, basicInterface.findEnergyBlockEntity());
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
		basicInterfacePeripheral = createPeripheral(basicInterface, basicInterface.energyBlockEntity);
		peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
		
		if (peripheral == null)
		{
			basicInterfacePeripheral = createPeripheral(basicInterface, basicInterface.findEnergyBlockEntity());
			peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
		}
		return peripheral;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(this.basicInterfacePeripheral instanceof BasicStargatePeripheral stargatePeripheral)
		{
			stargatePeripheral.queueEvent(eventName, objects);
		}
	}
}
