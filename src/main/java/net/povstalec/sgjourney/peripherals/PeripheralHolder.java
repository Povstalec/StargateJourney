package net.povstalec.sgjourney.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;

public class PeripheralHolder
{
	private BasicInterfacePeripheral basicInterfacePeripheral;
	private LazyOptional<IPeripheral> peripheral;
	
	public PeripheralHolder()
	{
	}
	
	public void resetPeripheral()
	{
		System.out.println("Resetting");
		peripheral.invalidate();
	}
	
	public LazyOptional<IPeripheral> newPeripheral(BasicInterfaceEntity basicInterface, EnergyBlockEntity energyBlockEntity)
	{
		StargateJourney.LOGGER.info("1");
		if(energyBlockEntity != null)
			StargateJourney.LOGGER.info(energyBlockEntity.toString());
		else
			StargateJourney.LOGGER.info("null");
		
		this.basicInterfacePeripheral = new BasicInterfacePeripheral(basicInterface);

		StargateJourney.LOGGER.info("1a");
		if(energyBlockEntity != null)
			StargateJourney.LOGGER.info(energyBlockEntity.toString());
		else
			StargateJourney.LOGGER.info("null");
		
		if(energyBlockEntity instanceof AbstractStargateEntity stargate)
		{
			StargateJourney.LOGGER.info("2");
			StargateJourney.LOGGER.info(energyBlockEntity.toString());
			if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			{
				StargateJourney.LOGGER.info("3");
				StargateJourney.LOGGER.info(energyBlockEntity.toString());
				basicInterfacePeripheral = new MilkyWayStargatePeripheral(basicInterface, milkyWayStargate);
			}
			else
				basicInterfacePeripheral = new BasicStargatePeripheral(basicInterface, stargate);
		}
		
		
		if(basicInterfacePeripheral != null)
			StargateJourney.LOGGER.info(basicInterfacePeripheral.toString());
		
		peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
		return peripheral;
	}
}
