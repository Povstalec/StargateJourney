package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;

public class PegasusStargateFunctions
{
	//============================================================================================
	//*********************************Advanced Crystal Interface*********************************
	//============================================================================================
	
	public static void dynamicSymbols(PegasusStargateEntity stargate, boolean dynamicSymbols)
	{
		stargate.dynamicSymbols(dynamicSymbols);
	}
	
	public static void overrideSymbols(PegasusStargateEntity stargate, String symbolString)
	{
		ResourceLocation symbols = ResourceLocation.tryParse(symbolString);
		if(symbols != null)
			stargate.symbolInfo().setSymbols(symbols);
	}
	
	public static void overridePointOfOrigin(PegasusStargateEntity stargate, String pointOfOriginString)
	{
		ResourceLocation pointOfOrigin = ResourceLocation.tryParse(pointOfOriginString);
		if(pointOfOrigin != null)
			stargate.symbolInfo().setPointOfOrigin(pointOfOrigin);
	}
}
