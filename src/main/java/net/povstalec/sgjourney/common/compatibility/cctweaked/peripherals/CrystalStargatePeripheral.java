package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.HashMap;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import net.povstalec.sgjourney.common.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.InterfaceMethod;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.EndRotation;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.GetCurrentSymbol;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.GetRotation;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.IsCurrentSymbol;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.LowerChevron;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.RaiseChevron;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.RotateAntiClockwise;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods.RotateClockwise;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class CrystalStargatePeripheral extends CrystalInterfacePeripheral implements IDynamicPeripheral
{
	protected AbstractStargateEntity stargate;
	private HashMap<String, InterfaceMethod<AbstractStargateEntity>> methods = new HashMap<String,InterfaceMethod<AbstractStargateEntity>>();
	
	public CrystalStargatePeripheral(CrystalInterfaceEntity crystalInterface, AbstractStargateEntity stargate)
	{
		super(crystalInterface);
		this.crystalInterface = crystalInterface;
		this.stargate = stargate;
		
		if(stargate instanceof MilkyWayStargateEntity)
			registerMilkyWayStargateMethods();
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
		
		return methods.get(methodName).use(computer, context, this.stargate, arguments);
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public void inputSymbol(ILuaContext context, int symbol) throws LuaException
	{
		context.executeMainThreadTask(() ->
		{
			stargate.engageSymbol(symbol);
			return null;
		});
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
	public final MethodResult disconnectStargate(ILuaContext context) throws LuaException
	{
		MethodResult result = context.executeMainThreadTask(() ->
		{
			Stargate.Feedback feedback = stargate.disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
			return new Object[] {!feedback.isError()};
		});
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private <StargateEntity extends AbstractStargateEntity> void registerMilkyWayStargateMethod(InterfaceMethod<StargateEntity> function)
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
}
