package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;

public class TransceiverPeripheralWrapper
{
	private TransceiverEntity transceiver;
	private TransceiverPeripheral transceiverPeripheral;
	private LazyOptional<IPeripheral> peripheral;
    protected final List<IComputerAccess> computerList = new LinkedList<>();
	
	public TransceiverPeripheralWrapper(TransceiverEntity transceiver)
	{
		this.transceiver = transceiver;
	}
	
	public static TransceiverPeripheral createPeripheral(TransceiverEntity transceiver)
	{
		return new TransceiverPeripheral(transceiver);
	}
	
	public LazyOptional<IPeripheral> newPeripheral()
	{
		transceiverPeripheral = createPeripheral(transceiver);
		peripheral = LazyOptional.of(() -> transceiverPeripheral);
		
		return peripheral;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(transceiverPeripheral != null)
			transceiverPeripheral.queueEvent(eventName, objects);
	}
	
	public TransceiverPeripheral getPeripheral()
	{
		return this.transceiverPeripheral;
	}
}
