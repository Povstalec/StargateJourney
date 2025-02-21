package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.computer_functions.IrisFunctions;

public class IrisMethods
{
	public static class GetIris implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIris";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.getIris(stargate)});
		}
	}
	
	public static class CloseIris implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "closeIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.closeIris(interfaceEntity)});
		}
	}
	
	public static class OpenIris implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "openIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.openIris(interfaceEntity)});
		}
	}
	
	public static class StopIris implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "stopIris";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.stopIris(interfaceEntity)});
		}
	}
	
	public static class GetIrisProgress implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisProgress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.getIrisProgress(stargate)});
		}
	}
	
	public static class GetIrisProgressPercentage implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisProgressPercentage";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.getIrisProgressPercentage(stargate)});
		}
	}
	
	public static class GetIrisDurability implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisDurability";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.getIrisDurability(stargate)});
		}
	}
	
	public static class GetIrisMaxDurability implements InterfaceMethod<IrisStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getIrisMaxDurability";
		}
		
		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractInterfaceEntity interfaceEntity, IrisStargateEntity stargate, IArguments arguments) throws LuaException
		{
			return context.executeMainThreadTask(() -> new Object[] {IrisFunctions.getIrisMaxDurability(stargate)});
		}
	}
}
