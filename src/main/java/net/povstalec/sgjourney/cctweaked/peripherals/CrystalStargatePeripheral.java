package net.povstalec.sgjourney.cctweaked.peripherals;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;

public class CrystalStargatePeripheral extends CrystalInterfacePeripheral
{
	protected CrystalInterfaceEntity crystalInterface;
	protected AbstractStargateEntity stargate;
	
	public CrystalStargatePeripheral(CrystalInterfaceEntity crystalInterface, AbstractStargateEntity stargate)
	{
		super(crystalInterface);
		this.crystalInterface = crystalInterface;
		this.stargate = stargate;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	@LuaFunction
	public void inputSymbol(ILuaContext context, int symbol) throws LuaException
	{
		//This needs to be executed on the main thread, otherwise you won't be able to dial
		context.executeMainThreadTask(() ->
		{
			stargate.engageSymbol(symbol);
			return null;
		});
	}
}
