package net.povstalec.sgjourney.common.sgjourney.info;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;

public class SymbolInfo
{
	@Nullable
	protected ResourceKey<PointOfOrigin> pointOfOrigin;
	@Nullable
	protected ResourceKey<Symbols> symbols;
	
	public SymbolInfo() {}
	
	//============================================================================================
	//**************************************Point of Origin***************************************
	//============================================================================================
	
	public void setPointOfOrigin(@Nullable ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
	}
	
	public ResourceKey<PointOfOrigin> pointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	public void setSymbols(@Nullable ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols;
	}
	
	public ResourceKey<Symbols> symbols()
	{
		return this.symbols;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	/**
	 * Saves the Symbol Info to a provided Compound Tag
	 * @param tag Compound Tag to save all the info to
	 * @param pointOfOriginName Name under which the Point of Origin info should be saved, or null if it shouldn't be saved
	 * @param symbolsName Name under which the Symbol info should be saved, or null if it shouldn't be saved
	 */
	public void saveToCompoundTag(CompoundTag tag, @Nullable String pointOfOriginName, @Nullable String symbolsName)
	{
		if(pointOfOriginName != null && pointOfOrigin != null)
			tag.putString(pointOfOriginName, pointOfOrigin.location().toString());
		
		if(symbolsName != null && symbols != null)
			tag.putString(symbolsName, symbols.location().toString());
	}
	
	/**
	 * Loads the Symbol Info from a provided Compound Tag
	 * @param tag Compound Tag to load all the info from
	 * @param pointOfOriginName Name under which the Point of Origin info should be loaded, or null if it shouldn't be loaded
	 * @param symbolsName Name under which the Symbol info should be loaded, or null if it shouldn't be loaded
	 */
	public void loadFromCompoundTag(CompoundTag tag, @Nullable String pointOfOriginName, @Nullable String symbolsName)
	{
		if(pointOfOriginName != null)
		{
			if(tag.contains(pointOfOriginName))
				pointOfOrigin = Conversion.stringToPointOfOrigin(tag.getString(pointOfOriginName));
			else
				pointOfOrigin = null;
		}
		
		if(symbolsName != null)
		{
			if(tag.contains(symbolsName))
				symbols = Conversion.stringToSymbols(tag.getString(symbolsName));
			else
				symbols = null;
		}
	}
	
	
	
	public interface Interface
	{
		String POINT_OF_ORIGIN = "point_of_origin";
		String SYMBOLS = "symbols";
		
		SymbolInfo symbolInfo();
	}
}
