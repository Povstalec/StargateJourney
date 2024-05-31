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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(!interfaceEntity.getInterfaceType().hasAdvancedCrystalMethods() && !stargate.isWormholeOpen())
					return new Object[] {false};
				
				String messageString = arguments.getString(0);
				
				return new Object[] {stargate.sendStargateMessage(messageString)};
			});
			
			return result;
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getVariant()};
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
				
				stargate.setEngagedChevrons(configurationArray);
				
				return new Object[] {"Chevron configuration set successfully"};
			});
			
			return result;
		}
	}

	/*public static class SetCFDStatus implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "callForward";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			return context.executeMainThreadTask(() -> {
				boolean input = arguments.getBoolean(0);

				blockEntity.setCFD(input);
				return new Object[]{"Call Forwarding successfully"};
			});
		}
	}

	public static class SetCFDTarget implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "setCallForwardingTarget";
		}

		@SuppressWarnings("unchecked")
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException {

			MethodResult result = context.executeMainThreadTask(() -> {

				Map<Double, Double> chevronConfiguration = (Map<Double, Double>) arguments.getTable(0);

				int[] configurationArray = ArrayHelper.tableToArray(chevronConfiguration);


				if(configurationArray.length == 0)
					stargate.setCFDTarget(new Address());
				else if(configurationArray.length < 8)
					throw new LuaException("Array is too short (required length: 8)");
				else if(configurationArray.length > 8)
					throw new LuaException("Array is too long (required length: 8)");
				else if(!ArrayHelper.differentNumbers(configurationArray))
					throw new LuaException("Array contains duplicate numbers");
				else if(!ArrayHelper.isArrayInBounds(configurationArray, 1, 38))
					throw new LuaException("Array contains numbers which are out of bounds <1,38>");

				stargate.setCFDTarget(new Address().fromArray(configurationArray));

				return new Object[] {"Call Forwarding target set successfully"};
			});

			return result;
		}
	}

	public static class GetCFDStatus implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "shouldCallForward";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			return context.executeMainThreadTask(() -> new Object[]{blockEntity.getCFD()});
		}
	}

	public static class GetCFDTarget implements InterfaceMethod<AbstractStargateEntity>
	{

		@Override
		public String getName() {
			return "getCallForwardingTarget";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity blockEntity, IArguments arguments) throws LuaException {
			return context.executeMainThreadTask(() -> {
				List<Integer> address = Arrays.stream(blockEntity.getCFDTarget().toArray()).boxed().toList();
				return new Object[] {address};
			});
		}
	}*/

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
				List<Integer> dialedAddress = Arrays.stream(stargate.get9ChevronAddress().toArray()).boxed().toList();
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
			int network = arguments.getInt(0);
			stargate.setNetwork(network);
			
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
			boolean restrictNetwork = arguments.getBoolean(0);
			stargate.setRestrictNetwork(restrictNetwork);
			
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
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getRestrictNetwork()};
			});
			
			return result;
		}
	}
}
