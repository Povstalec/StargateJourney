package net.povstalec.sgjourney.common.cctweaked.peripherals;

import java.util.HashMap;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import net.povstalec.sgjourney.common.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.cctweaked.methods.InterfaceMethod;
import net.povstalec.sgjourney.common.cctweaked.methods.MilkyWayStargateMethods.*;
import net.povstalec.sgjourney.common.cctweaked.methods.UniverseStargateMethods.*;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class BasicStargatePeripheral extends BasicInterfacePeripheral implements IDynamicPeripheral
{
	protected AbstractStargateEntity stargate;
	private HashMap<String, InterfaceMethod<AbstractStargateEntity>> methods = new HashMap<String,InterfaceMethod<AbstractStargateEntity>>();
	
	public BasicStargatePeripheral(BasicInterfaceEntity basicInterface, AbstractStargateEntity stargate)
	{
		super(basicInterface);
		this.stargate = stargate;
		
		if(stargate instanceof MilkyWayStargateEntity)
			registerMilkyWayStargateMethods();
		else if(stargate instanceof UniverseStargateEntity)
			registerUniverseStargateMethods();
	}

	@Override
	public String[] getMethodNames()
	{
		return methods.keySet().toArray(new String[0]);
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(context, this.stargate, arguments);
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public final int getRecentFeedback()
	{
		return stargate.getRecentFeedback().getCode();
	}
	
	@LuaFunction
	public final boolean isStargateConnected()
	{
		return stargate.isConnected();
	}
	
	@LuaFunction
	public final long getStargateEnergy()
	{
		return stargate.getEnergyStored();
	}
	
	@LuaFunction
	public final int getChevronsEngaged()
	{
		return stargate.getChevronsEngaged();
	}
	
	@LuaFunction
	public final int getOpenTime()
	{
		return stargate.getOpenTime();
	}
	
	@LuaFunction
	public final void disconnectStargate(ILuaContext context) throws LuaException
	{
		context.executeMainThreadTask(() ->
		{
			stargate.disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
			return null;
		});
	}
	
	@SuppressWarnings("unchecked")
	private <Stargate extends AbstractStargateEntity> void registerMilkyWayStargateMethod(InterfaceMethod<Stargate> function)
	{
		methods.put(function.getName(), (InterfaceMethod<AbstractStargateEntity>) function);
	}
	
	public void registerMilkyWayStargateMethods()
	{
		registerMilkyWayStargateMethod(new GetCurrentSymbol());
		registerMilkyWayStargateMethod(new IsCurrentSymbol());
		
		registerMilkyWayStargateMethod(new GetRotation());
		registerMilkyWayStargateMethod(new RotateClockwise());
		registerMilkyWayStargateMethod(new RotateAntiClockwise());
		registerMilkyWayStargateMethod(new EndRotation());

		registerMilkyWayStargateMethod(new RaiseChevron());
		registerMilkyWayStargateMethod(new LowerChevron());
	}
	
	@SuppressWarnings("unchecked")
	private <Stargate extends AbstractStargateEntity> void registerUniverseStargateMethod(InterfaceMethod<Stargate> function)
	{
		methods.put(function.getName(), (InterfaceMethod<AbstractStargateEntity>) function);
	}
	
	public void registerUniverseStargateMethods()
	{
		registerUniverseStargateMethod(new EngageSymbol());
	}
}
