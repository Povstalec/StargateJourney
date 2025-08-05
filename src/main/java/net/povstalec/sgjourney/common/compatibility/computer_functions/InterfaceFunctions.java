package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;

import java.util.Map;

/**
 * Class with all functions that can be called on any of the Interface blocks
 */
public class InterfaceFunctions
{
	//============================================================================================
	//**************************************Basic Interface***************************************
	//============================================================================================
	
	public static long getEnergy(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.getEnergyStored();
	}
	
	public static long getEnergyCapacity(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.getEnergyCapacity();
	}
	
	public static long getEnergyTarget(AbstractInterfaceEntity interfaceEntity)
	{
		return interfaceEntity.getEnergyTarget();
	}
	
	public static void setEnergyTarget(AbstractInterfaceEntity interfaceEntity, long energyTarget)
	{
		interfaceEntity.setEnergyTarget(energyTarget);
	}
	
	public static String addressToString(Map<Double, Double> addressTable)
	{
		return new Address(addressTable).toString();
	}
}
