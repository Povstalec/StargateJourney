package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.LinkedList;
import java.util.List;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;

import javax.annotation.Nonnull;

public class InterfacePeripheralWrapper
{
	private final AbstractInterfaceEntity interfaceEntity;
	@Nonnull
	private LazyOptional<InterfacePeripheral> peripheral;
    protected final List<IComputerAccess> computerList = new LinkedList<>();
	
	public InterfacePeripheralWrapper(AbstractInterfaceEntity interfaceEntity)
	{
		this.interfaceEntity = interfaceEntity;
		this.peripheral = createPeripheralLazy();
	}

	private LazyOptional<InterfacePeripheral> createPeripheralLazy() {
		return LazyOptional.of(() -> createPeripheral(this.interfaceEntity, this.interfaceEntity.findEnergyBlockEntity()));
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
		final InterfacePeripheral currentPeripheral = peripheral.resolve().orElse(null);
		final BlockEntity oldEntity = currentPeripheral == null ? null : currentPeripheral.targetEntity;
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
		peripheral.ifPresent(p -> p.queueEvent(eventName, objects));
	}
	
	public LazyOptional<IPeripheral> getPeripheral()
	{
		return peripheral.cast();
	}
}
