package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.StargateFilterFunctions;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class StargateFilterMethods
{
	public static void checkAddressArray(int[] addressArray) throws LuaException
	{
		if(addressArray.length < 6)
			throw new LuaException("Array is too short (minimum length: 6)");
		
		else if(addressArray.length > 8)
			throw new LuaException("Array is too long (maximum length: 8)");
		
		else if(!ArrayHelper.differentNumbers(addressArray))
			throw new LuaException("Array contains duplicate numbers");
		
		else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
			throw new LuaException("Array contains numbers which are out of bounds <1,47>");
	}
	
	public static ArrayList<List<Integer>> addressListToIntList(ArrayList<Address.Immutable> addressList)
	{
		ArrayList<List<Integer>> integerList = new ArrayList<List<Integer>>();
		for(Address.Immutable address : addressList)
		{
			integerList.add(address.toList());
		}
		
		return integerList;
	}
	
	public static class GetFilterType implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getFilterType";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(StargateFilterFunctions.getFilterType(stargate));
		}
	}
	
	public static class SetFilterType implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "setFilterType";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return MethodResult.of(StargateFilterFunctions.setFilterType(stargate, arguments.getInt(0)));
		}
	}
	
	
	
	public static class AddToWhitelist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "addToWhitelist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
				boolean isVisible;
				
				try
				{
					isVisible = arguments.getBoolean(1);
				}
				catch(LuaException e)
				{
					isVisible = false;
				}
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				checkAddressArray(addressArray);
				
				return new Object[] {StargateFilterFunctions.addToWhitelist(stargate, addressArray, isVisible)};
			});
			
			return result;
		}
	}
	
	public static class RemoveFromWhitelist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "removeFromWhitelist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				checkAddressArray(addressArray);
				
				return new Object[] {StargateFilterFunctions.removeFromWhitelist(stargate, addressArray)};
			});
			
			return result;
		}
	}
	
	public static class GetWhitelist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getPublicWhitelist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {addressListToIntList(StargateFilterFunctions.getPublicWhitelist(stargate))});
		}
	}
	
	public static class ClearWhitelist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "clearWhitelist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {StargateFilterFunctions.clearWhitelist(stargate)});
		}
	}
	
	
	
	public static class AddToBlacklist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "addToBlacklist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
				boolean isVisible;
				
				try
				{
					isVisible = arguments.getBoolean(1);
				}
				catch(LuaException e)
				{
					isVisible = false;
				}
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				checkAddressArray(addressArray);
				
				return new Object[] {StargateFilterFunctions.addToBlacklist(stargate, addressArray, isVisible)};
			});
			
			return result;
		}
	}
	
	public static class RemoveFromBlacklist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "removeFromBlacklist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				checkAddressArray(addressArray);
				
				return new Object[] {StargateFilterFunctions.removeFromBlacklist(stargate, addressArray)};
			});
			
			return result;
		}
	}
	
	public static class GetBlacklist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getPublicBlacklist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {addressListToIntList(StargateFilterFunctions.getPublicBlacklist(stargate))});
		}
	}
	
	public static class ClearBlacklist implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "clearBlacklist";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {StargateFilterFunctions.clearBlacklist(stargate)});
		}
	}
}
