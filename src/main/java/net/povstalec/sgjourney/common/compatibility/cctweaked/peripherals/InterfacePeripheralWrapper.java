package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.Lazy;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;

import java.util.LinkedList;
import java.util.List;

public class InterfacePeripheralWrapper
{
	private final AbstractInterfaceEntity interfaceEntity;
	private InterfacePeripheral interfacePeripheral;
	private Lazy<IPeripheral> peripheral;
    protected final List<IComputerAccess> computerList = new LinkedList<>();
	
	public InterfacePeripheralWrapper(AbstractInterfaceEntity interfaceEntity)
	{
		this.interfaceEntity = interfaceEntity;
		this.peripheral = createPeripheralLazy();
	}

	private Lazy<IPeripheral> createPeripheralLazy()
	{
		return Lazy.of(() -> createPeripheral(this.interfaceEntity, this.interfaceEntity.findEnergyBlockEntity()));
	}

	private static InterfacePeripheral createPeripheral(AbstractInterfaceEntity interfaceEntity, EnergyBlockEntity energyBlockEntity)
	{
		if(energyBlockEntity instanceof AbstractStargateEntity<?> stargate)
			return new StargatePeripheral(interfaceEntity, stargate);
		else if(energyBlockEntity instanceof AbstractTransporterEntity<?> transporter)
			return new TransporterPeripheral(interfaceEntity, transporter);

		return new InterfacePeripheral(interfaceEntity, energyBlockEntity);
	}
	
	public boolean resetInterface()
	{
		final IPeripheral currentPeripheral = peripheral.get();
		final BlockEntity oldEntity = ((InterfacePeripheral) currentPeripheral).targetEntity;
		final BlockEntity newEntity = interfaceEntity.findEnergyBlockEntity();

		// if the peripheral was initialized for a different BE than the interface is currently targeting
		if (oldEntity != newEntity) {
			// invalidate the previous peripheral
			peripheral.invalidate();
			// create new peripheral initialized, yes Forge (or CC:T) really wants new LazyOptional instance
			peripheral = createPeripheralLazy();
			return true;
		}

		return false;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(interfacePeripheral != null)
			interfacePeripheral.queueEvent(eventName, objects);
	}
	
	public Lazy<IPeripheral> getPeripheral()
	{
		return peripheral;
	}
}
