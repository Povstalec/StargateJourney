package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.Map;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.stargate.Address;

public class StargateFilterMethods
{
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getFilterType().getIntegerValue()};
			});
			
			return result;
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
			int filter = arguments.getInt(0);
			
			return MethodResult.of(stargate.setFilterType(filter).getIntegerValue());
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
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				
				if(addressArray.length < 6)
					throw new LuaException("Array is too short (minimum length: 6)");
				
				else if(addressArray.length > 8)
					throw new LuaException("Array is too long (maximum length: 8)");
				
				else if(!ArrayHelper.differentNumbers(addressArray))
					throw new LuaException("Array contains duplicate numbers");
				
				else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
					throw new LuaException("Array contains numbers which are out of bounds <1,47>");
				
				if(stargate.addToWhitelist(new Address(addressArray).immutable()))
					return new Object[] {"Address whitelisted successfully"};
				else
					return new Object[] {"Address is already whitelisted"};
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
				
				if(addressArray.length < 6)
					throw new LuaException("Array is too short (minimum length: 6)");
				
				else if(addressArray.length > 8)
					throw new LuaException("Array is too long (maximum length: 8)");
				
				else if(!ArrayHelper.differentNumbers(addressArray))
					throw new LuaException("Array contains duplicate numbers");
				
				else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
					throw new LuaException("Array contains numbers which are out of bounds <1,47>");
				
				if(stargate.removeFromWhitelist(new Address(addressArray).immutable()))
					return new Object[] {"Address removed from whitelist successfully"};
				else
					return new Object[] {"Address is not whitelisted"};
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				stargate.clearWhitelist();
				
				return new Object[] {"Whitelist cleared"};
			});
			
			return result;
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
				
				int[] addressArray = ArrayHelper.tableToArray(addressMap);
				
				if(addressArray.length < 6)
					throw new LuaException("Array is too short (minimum length: 6)");
				
				else if(addressArray.length > 8)
					throw new LuaException("Array is too long (maximum length: 8)");
				
				else if(!ArrayHelper.differentNumbers(addressArray))
					throw new LuaException("Array contains duplicate numbers");
				
				else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
					throw new LuaException("Array contains numbers which are out of bounds <1,47>");
				
				if(stargate.addToBlacklist(new Address(addressArray).immutable()))
					return new Object[] {"Address blacklisted successfully"};
				else
					return new Object[] {"Address is already blacklisted"};
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
				
				if(addressArray.length < 6)
					throw new LuaException("Array is too short (minimum length: 6)");
				
				else if(addressArray.length > 8)
					throw new LuaException("Array is too long (maximum length: 8)");
				
				else if(!ArrayHelper.differentNumbers(addressArray))
					throw new LuaException("Array contains duplicate numbers");
				
				else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
					throw new LuaException("Array contains numbers which are out of bounds <1,47>");
				
				if(stargate.removeFromBlacklist(new Address(addressArray).immutable()))
					return new Object[] {"Address removed from blacklist successfully"};
				else
					return new Object[] {"Address is not blacklisted"};
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				stargate.clearBlacklist();
				
				return new Object[] {"Blacklist cleared"};
			});
			
			return result;
		}
	}
}
