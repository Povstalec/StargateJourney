package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public class MilkyWayStargateFunctions
{
	//============================================================================================
	//**************************************Basic Interface***************************************
	//============================================================================================
	
	// rotateClockwise()
	// Should handle following exceptions
	/*if(stargate.isChevronOpen())
			throw new LuaException("Can't rotate while chevron is open");
	else if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
		throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");*/
	
	// rotateCounterClockwise()
	// Should handle following exceptions
	/*if(stargate.isChevronOpen())
			throw new LuaException("Can't rotate while chevron is open");
	else if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
		throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");*/
	
	public static StargateInfo.Feedback openChevron(MilkyWayStargateEntity stargate)
	{
		return stargate.openChevron();
	}
	
	public static StargateInfo.Feedback closeChevron(MilkyWayStargateEntity stargate)
	{
		return stargate.closeChevron();
	}
	
	public static boolean isChevronOpen(MilkyWayStargateEntity stargate)
	{
		return stargate.isChevronOpen();
	}
}
