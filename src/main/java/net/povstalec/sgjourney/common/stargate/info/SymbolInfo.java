package net.povstalec.sgjourney.common.stargate.info;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class SymbolInfo
{
	protected ResourceLocation pointOfOrigin;
	protected ResourceLocation symbols;
	
	public SymbolInfo()
	{
		pointOfOrigin = StargateJourney.EMPTY_LOCATION;
		symbols = StargateJourney.EMPTY_LOCATION;
	}
	
	public void setPointOfOrigin(ResourceLocation pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
	}
	
	public ResourceLocation pointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(ResourceLocation symbols)
	{
		this.symbols = symbols;
	}
	
	
	
	public ResourceLocation symbols()
	{
		return this.symbols;
	}
	
	public interface Interface
	{
		String POINT_OF_ORIGIN = "PointOfOrigin";
		String SYMBOLS = "Symbols";
		
		SymbolInfo symbolInfo();
	}
}
