package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

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
		ResourceKey<Symbols> symbols = Conversion.stringToSymbols(symbolString);
		if(symbols != null)
			stargate.symbolInfo().setSymbols(symbols);
	}
	
	public static void overridePointOfOrigin(PegasusStargateEntity stargate, String pointOfOriginString)
	{
		ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(pointOfOriginString);
		if(pointOfOrigin != null)
			stargate.symbolInfo().setPointOfOrigin(pointOfOrigin);
	}
}
