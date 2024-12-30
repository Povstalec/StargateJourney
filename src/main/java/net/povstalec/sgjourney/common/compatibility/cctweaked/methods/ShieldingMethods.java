package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class ShieldingMethods
{
	public static class GetIris implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIris";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				if(stargate.getIris().isEmpty())
					return new Object[] {null};
				
				return new Object[] {BuiltInRegistries.ITEM.getKey(stargate.getIris().getItem()).toString()};
			});
			
			return result;
		}
	}
	
	public static class CloseIris implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "closeIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {interfaceEntity.setIrisMotion(Stargate.IrisMotion.CLOSING_COMPUTER)};
			});
			
			return result;
		}
	}
	
	public static class OpenIris implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "openIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {interfaceEntity.setIrisMotion(Stargate.IrisMotion.OPENING_COMPUTER)};
			});
			
			return result;
		}
	}
	
	public static class StopIris implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "stopIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {interfaceEntity.setIrisMotion(Stargate.IrisMotion.IDLE)};
			});
			
			return result;
		}
	}
	
	public static class GetIrisProgress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisProgress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getIrisProgress()};
			});
			
			return result;
		}
	}
	
	public static class GetIrisProgressPercentage implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisProgressPercentage";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.checkIrisState()};
			});
			
			return result;
		}
	}
	
	public static class GetIrisDurability implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisDurability";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getIrisDurability()};
			});
			
			return result;
		}
	}
	
	public static class GetIrisMaxDurability implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisMaxDurability";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				return new Object[] {stargate.getIrisMaxDurability()};
			});
			
			return result;
		}
	}
}
