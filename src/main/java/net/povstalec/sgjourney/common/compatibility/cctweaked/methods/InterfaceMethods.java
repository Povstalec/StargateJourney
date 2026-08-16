package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.Map;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.InterfaceFunctions;

public class InterfaceMethods
{
	// Basic Interface
	public static class SetEnergyTarget implements InterfaceMethod<BlockEntity>
	{
		@Override
		public String getName()
		{
			return "setEnergyTarget";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, BlockEntity stargate, IArguments arguments) throws LuaException
		{
			long target = arguments.getLong(0);
			InterfaceFunctions.setEnergyTarget(interfaceEntity, target);
			
			return MethodResult.of();
		}
	}
	
	public static class AddressToString implements InterfaceMethod<BlockEntity>
	{
		@Override
		public String getName()
		{
			return "addressToString";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, BlockEntity stargate, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressTable = (Map<Double, Double>) arguments.getTable(0);
			
			return MethodResult.of(InterfaceFunctions.addressToString(addressTable));
		}
	}
	
	
	
	//TODO
	// Crystal Interface
	/*public static class AddressToDimension implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "calculateSolarSystem";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressTable = (Map<Double, Double>) arguments.getTable(0);
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Address address = new Address(addressTable);
				
				String dimension = stargate.getLevel().dimension().location().toString();
				String galaxy = Universe.get(stargate.getLevel()).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys().iterator().next();
				
				String solarSystem = Universe.get(stargate.getLevel()).getSolarSystemInGalaxy(galaxy, address.toString());
				
				//TODO What if the Dimension is not located inside a Galaxy
				return new Object[] {solarSystem};
			});
			
			return result;
		}
	}*/
}
