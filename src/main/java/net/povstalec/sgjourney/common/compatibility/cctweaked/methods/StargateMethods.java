package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.Arrays;
import java.util.List;
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
import net.povstalec.sgjourney.common.stargate.Stargate;

public class StargateMethods
{
	public static Object[] returnedFeedback(AbstractInterfaceEntity interfaceEntity, Stargate.Feedback feedback)
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Stargate.Feedback feedback = stargate.getRecentFeedback();
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
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
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				Stargate.Feedback feedback = stargate.engageSymbol(desiredSymbol);
				return StargateMethods.returnedFeedback(interfaceEntity, feedback);
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				// Will only display the dialed Address
				int[] stargateAddress = !stargate.isConnected() || (stargate.isConnected() && stargate.isDialingOut()) ? stargate.getAddress().toArray() : new int[] {};
				List<Integer> address = Arrays.stream(stargateAddress).boxed().toList();
				return new Object[] {address};
			});
			
			return result;
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
			context.executeMainThreadTask(() ->
			{
				Map<Double, Double> chevronConfiguration = (Map<Double, Double>) arguments.getTable(0);
				
				int[] configurationArray = ArrayHelper.tableToArray(chevronConfiguration);

				
				if(configurationArray.length != 9)
					throw new LuaException("Array is too short");
				else if(!ArrayHelper.differentNumbers(configurationArray))
					throw new LuaException("Array contains duplicate numbers");
				else if(!ArrayHelper.differentNumbers(configurationArray))
					throw new LuaException("Array contains duplicate numbers");
				else if(!ArrayHelper.isArrayPositive(configurationArray, true))
					throw new LuaException("Array contains negative numbers");
				
				stargate.setEngagedChevrons(configurationArray);
				
				return null;
			});
			
			return MethodResult.of();
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				List<Integer> address = Arrays.stream(stargate.getAddress().toArray()).boxed().toList();
				return new Object[] {address};
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				List<Integer> dialedAddress = Arrays.stream(new Address(stargate.getID()).toArray()).boxed().toList();
				return new Object[] {dialedAddress};
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getNetwork()};
			});
			
			return result;
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
			context.executeMainThreadTask(() ->
			{
				int network = arguments.getInt(0);
				stargate.setNetwork(network);
				return null;
			});
			
			return MethodResult.of();
		}
	}
	
	public static class SetRestrictNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "setRestrictNetwork";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			context.executeMainThreadTask(() ->
			{
				boolean restrictNetwork = arguments.getBoolean(0);
				stargate.setRestrictNetwork(restrictNetwork);
				return null;
			});
			
			return MethodResult.of();
		}
	}
	
	public static class GetRestrictNetwork implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getRestrictNetwork";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getRestrictNetwork()};
			});
			
			return result;
		}
	}
}
