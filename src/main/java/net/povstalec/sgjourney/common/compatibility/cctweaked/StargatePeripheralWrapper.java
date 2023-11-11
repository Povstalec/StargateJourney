package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.BasicStargatePeripheral;

public class StargatePeripheralWrapper
{
	protected BasicStargatePeripheral peripheral;
	
	public StargatePeripheralWrapper(BasicStargatePeripheral peripheral)
	{
		this.peripheral = peripheral;
	}
	
	public BasicStargatePeripheral getPeripheral()
	{
		return this.peripheral;
	}
}
