package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.Arrays;
import java.util.Map;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericStargateFunctions;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public class StargateMethods
{
	// Crystal Interfaces can display the actual feedback messages and not just their codes
	public static Object[] returnedFeedback(AbstractInterfaceEntity interfaceEntity, StargateInfo.Feedback feedback)
	{
		if(interfaceEntity.getInterfaceType().hasCrystalMethods())
			return new Object[] {feedback.getCode(), feedback.getMessage()};
		
		return new Object[] {feedback.getCode()};
	}
	
	// Basic Interface
	public static class GetRecentFeedback implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getRecentFeedback";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> returnedFeedback(interfaceEntity, GenericStargateFunctions.getRecentFeedback(stargate)));
		}
	}
	
	public static class SendStargateMessage implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "sendStargateMessage";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.sendStargateMessage(interfaceEntity, stargate, arguments.getString(0))});
		}
	}
	
	public static class GetStargateVariant implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getStargateVariant";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.getVariant(stargate)});
		}
	}
	
	public static class GetPointOfOrigin implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getPointOfOrigin";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.getPointOfOrigin(stargate)});
		}
	}
	
	public static class GetSymbols implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getSymbols";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.getSymbols(stargate)});
		}
	}
	
	// Crystal Interface
	public static class EngageSymbol implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "engageSymbol";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			try
			{
				boolean engageDirectly = arguments.getBoolean(1);
				return context.executeMainThreadTask(() -> returnedFeedback(interfaceEntity, GenericStargateFunctions.engageSymbol(interfaceEntity, stargate, desiredSymbol, engageDirectly)));
			}
			catch(LuaException e)
			{
				return context.executeMainThreadTask(() -> returnedFeedback(interfaceEntity, GenericStargateFunctions.engageSymbol(interfaceEntity, stargate, desiredSymbol, false)));
			}
			
		}
	}
	
	public static class DialedAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getDialedAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {Arrays.stream(GenericStargateFunctions.getDialedAddress(stargate).toArray()).boxed().toList()});
		}
	}
	
	public static class SetChevronConfiguration implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "setChevronConfiguration";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Map<Double, Double> chevronConfiguration = (Map<Double, Double>) arguments.getTable(0);
				
				int[] configurationArray = ArrayHelper.tableToArray(chevronConfiguration);

				
				if(configurationArray.length < 8)
					throw new LuaException("Array is too short (required length: 8)");
				else if(configurationArray.length > 8)
					throw new LuaException("Array is too long (required length: 8)");
				else if(!ArrayHelper.differentNumbers(configurationArray))
					throw new LuaException("Array contains duplicate numbers");
				else if(!ArrayHelper.isArrayInBounds(configurationArray, 1, 8))
					throw new LuaException("Array contains numbers which are out of bounds <1,8>");
				
				GenericStargateFunctions.setChevronConfiguration(stargate, configurationArray);
				
				return new Object[] {"Chevron configuration set successfully"};
			});
			
			return result;
		}
	}
	
	public static class RemapSymbol implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "remapSymbol";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.remapSymbol(stargate, arguments.getInt(0), arguments.getInt(1))});
		}
	}
	
	public static class GetMappedSymbol implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getMappedSymbol";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.getMappedSymbol(stargate, arguments.getInt(0))});
		}
	}
	
	public static class HasDHD implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "hasDHD";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.hasDHD(stargate)});
		}
	}

	// Advanced Crystal Interface
	public static class ConnectedAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getConnectedAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {Arrays.stream(GenericStargateFunctions.getConnectedAddress(stargate).toArray()).boxed().toList()});
		}
	}
	
	public static class LocalAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getLocalAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {Arrays.stream(GenericStargateFunctions.getLocalAddress(stargate).toArray()).boxed().toList()});
		}
	}
	
	public static class GetNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getNetwork";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.getNetwork(stargate)});
		}
	}
	
	public static class SetNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "setNetwork";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			GenericStargateFunctions.setNetwork(stargate, arguments.getInt(0));
			
			return MethodResult.of();
		}
	}
	
	public static class SetRestrictNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "restrictNetwork";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			arguments.escapes();
			GenericStargateFunctions.setRestrictNetwork(stargate, arguments.getBoolean(0));
			
			return MethodResult.of();
		}
	}
	
	public static class GetRestrictNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "isNetworkRestricted";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericStargateFunctions.isNetworkRestricted(stargate)});
		}
	}
}
