package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.InterfacePeripheral;

public class SGJourneyPeripheralWrapper<T extends InterfacePeripheral>
{
	protected T peripheral;
	protected AbstractInterfaceEntity.InterfaceType type;
	
	public SGJourneyPeripheralWrapper(T peripheral, AbstractInterfaceEntity.InterfaceType type)
	{
		this.peripheral = peripheral;
		this.type = type;
	}
	
	public T getPeripheral()
	{
		return this.peripheral;
	}
	
	public AbstractInterfaceEntity.InterfaceType getType()
	{
		return this.type;
	}
}
