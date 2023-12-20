package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;

public class StargatePeripheralWrapper
{
	protected StargatePeripheral peripheral;
	AbstractInterfaceEntity.InterfaceType type;
	
	public StargatePeripheralWrapper(StargatePeripheral peripheral, AbstractInterfaceEntity.InterfaceType type)
	{
		this.peripheral = peripheral;
		this.type = type;
	}
	
	public StargatePeripheral getPeripheral()
	{
		return this.peripheral;
	}
	
	public AbstractInterfaceEntity.InterfaceType getType()
	{
		return this.type;
	}
}
