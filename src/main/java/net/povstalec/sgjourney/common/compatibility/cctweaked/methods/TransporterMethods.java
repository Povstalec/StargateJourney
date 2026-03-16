package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.GenericTransporterFunctions;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;

import java.util.Map;

public class TransporterMethods
{
	//TODO get name, set name
	
	// Crystal Interfaces can display the actual feedback messages and not just their codes
	public static Object[] returnedFeedback(AbstractInterfaceEntity interfaceEntity, TransporterInfo.Feedback feedback)
	{
		if(interfaceEntity.getInterfaceType().hasCrystalMethods())
			return new Object[] {feedback.getCode(), feedback.getMessage()};
		
		return new Object[] {feedback.getCode()};
	}
	
	// Basic Interface
	public static class GetRecentFeedback implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "getRecentFeedback";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> returnedFeedback(interfaceEntity, GenericTransporterFunctions.getRecentFeedback(transporter)));
		}
	}
	public static class DialCoords implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "dialCoords";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() ->
			{
				int x = arguments.getInt(0);
				int y = arguments.getInt(1);
				int z = arguments.getInt(2);
				
				return returnedFeedback(interfaceEntity, GenericTransporterFunctions.dialCoords(transporter, new Vec3i(x, y, z)));
			});
		}
	}
	
	// Crystal Interface
	public static class DialTransporterID implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "dialTransporterID";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() ->
			{
				Map<Double, Double> transporterIDTable = (Map<Double, Double>) arguments.getTable(0);
				
				return returnedFeedback(interfaceEntity, GenericTransporterFunctions.dialTransporterID(transporter, new TransporterID.Immutable(transporterIDTable)));
			});
		}
	}
	
	public static class LocalTransporterID implements InterfaceMethod<AbstractTransporterEntity<?>>
	{
		@Override
		public String getName()
		{
			return "getLocalTransporterID";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractTransporterEntity<?> transporter, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {GenericTransporterFunctions.getLocalTransporterID(transporter).toList()});
		}
	}
	
	/*public static class ConnectedAddress implements InterfaceMethod<AbstractStargateEntity>
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
			int network = arguments.getInt(0);
			GenericStargateFunctions.setNetwork(stargate, network);
			
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
			boolean restrict = arguments.getBoolean(0);
			GenericStargateFunctions.setRestrictNetwork(stargate, restrict);
			
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
	}*/
	
	// Advanced Crystal Interface
}
