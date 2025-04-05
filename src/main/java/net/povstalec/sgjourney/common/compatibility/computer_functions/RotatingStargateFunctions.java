package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.stargate.RotatingStargateEntity;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public class RotatingStargateFunctions
{
	//============================================================================================
	//**************************************Basic Interface***************************************
	//============================================================================================
	
	public static int getCurrentSymbol(RotatingStargateEntity stargate)
	{
		return stargate.getCurrentSymbol();
	}
	
	public static boolean isCurrentSymbol(RotatingStargateEntity stargate, int symbol)
	{
		return stargate.isCurrentSymbol(symbol);
	}
	
	public static StargateInfo.Feedback encodeChevron(RotatingStargateEntity stargate)
	{
		return stargate.encodeChevron();
	}
	
	public static int getRotation(RotatingStargateEntity stargate)
	{
		return stargate.getRotation();
	}
	
	public static double getRotationDegrees(RotatingStargateEntity stargate)
	{
		return stargate.getRotationDegrees();
	}
	
	public static StargateInfo.Feedback rotateClockwise(RotatingStargateEntity stargate, int desiredSymbol)
	{
		// Should handle following exception
		/*if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
			throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");*/
		
		return stargate.startRotation(desiredSymbol, true);
	}
	
	public static StargateInfo.Feedback rotateAntiClockwise(RotatingStargateEntity stargate, int desiredSymbol)
	{
		// Should handle following exception
		/*if(desiredSymbol != -1 && stargate.isSymbolOutOfBounds(desiredSymbol))
			throw new LuaException("Symbol out of bounds <-1, " + (stargate.totalSymbols() - 1) + ">");*/
		
		return stargate.startRotation(desiredSymbol, false);
	}
	
	public static StargateInfo.Feedback endRotation(RotatingStargateEntity stargate)
	{
		return stargate.endRotation(true);
	}
}
