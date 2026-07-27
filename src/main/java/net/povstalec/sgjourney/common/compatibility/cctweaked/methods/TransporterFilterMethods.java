package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.TransporterFilterFunctions;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransporterFilterMethods
{
	public static void checkTransporterIDArray(int[] idArray) throws LuaException
	{
		if(idArray.length != TransporterID.FULL_ID_LENGTH)
			throw new LuaException("Array length should be exactly 7");
		
		else if(!ArrayHelper.isArrayInBounds(idArray, TransporterID.MIN_SYMBOL, TransporterID.MAX_SYMBOL))
			throw new LuaException("Array contains numbers which are out of bounds <1,8>");
	}
	
	public static ArrayList<List<Integer>> transporterIDListToIntList(List<TransporterID.Immutable> addressList)
	{
		ArrayList<List<Integer>> integerList = new ArrayList<>();
		for(TransporterID.Immutable transporterID : addressList)
		{
			integerList.add(transporterID.toList());
		}
		
		return integerList;
	}
	
	public static class GetFilterType implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "getFilterType";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return MethodResult.of(TransporterFilterFunctions.getFilterType(transporter));
		}
	}
	
	public static class SetFilterType implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "setFilterType";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			int filterType = arguments.getInt(0);
			return MethodResult.of(TransporterFilterFunctions.setFilterType(transporter, filterType));
		}
	}
	
	
	
	public static class AddToWhitelist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "addToWhitelist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
			int[] addressArray = ArrayHelper.tableToArray(addressMap);
			checkTransporterIDArray(addressArray);
			
			try
			{
				boolean isVisible = arguments.getBoolean(1);
				return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.addToWhitelist(transporter, addressArray, isVisible)});
			}
			catch(LuaException e)
			{
				return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.addToWhitelist(transporter, addressArray, false)});
			}
		}
	}
	
	public static class RemoveFromWhitelist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "removeFromWhitelist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
			int[] addressArray = ArrayHelper.tableToArray(addressMap);
			checkTransporterIDArray(addressArray);
			
			return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.removeFromWhitelist(transporter, addressArray)});
		}
	}
	
	public static class GetWhitelist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "getPublicWhitelist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {transporterIDListToIntList(TransporterFilterFunctions.getPublicWhitelist(transporter))});
		}
	}
	
	public static class ClearWhitelist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "clearWhitelist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.clearWhitelist(transporter)});
		}
	}
	
	
	
	public static class AddToBlacklist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "addToBlacklist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
			int[] addressArray = ArrayHelper.tableToArray(addressMap);
			checkTransporterIDArray(addressArray);
			
			try
			{
				boolean isVisible = arguments.getBoolean(1);
				return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.addToBlacklist(transporter, addressArray, isVisible)});
			}
			catch(LuaException e)
			{
				return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.addToBlacklist(transporter, addressArray, false)});
			}
		}
	}
	
	public static class RemoveFromBlacklist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "removeFromBlacklist";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			Map<Double, Double> addressMap = (Map<Double, Double>) arguments.getTable(0);
			int[] addressArray = ArrayHelper.tableToArray(addressMap);
			checkTransporterIDArray(addressArray);
			
			return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.removeFromBlacklist(transporter, addressArray)});
		}
	}
	
	public static class GetBlacklist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "getPublicBlacklist";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {transporterIDListToIntList(TransporterFilterFunctions.getPublicBlacklist(transporter))});
		}
	}
	
	public static class ClearBlacklist implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "clearBlacklist";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {TransporterFilterFunctions.clearBlacklist(transporter)});
		}
	}
}
