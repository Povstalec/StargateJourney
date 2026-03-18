package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Random;

/**
 * Class representing the way a Stargate's symbols are mapped internally
 */
public class SymbolMap
{
	public static final String SYMBOL_MAP = "symbol_map";
	
	public static final int MAX_SYMBOLS = 48;
	
	private final int totalSymbols;
	// Null by default to avoid wasting memory because most Stargates won't be using the symbol map at all
	private int[] symbolMap = null;
	private int[] reverseSymbolMap = null; // Should ALWAYS be larger than symbolMap
	
	public SymbolMap(int totalSymbols)
	{
		this.totalSymbols = totalSymbols;
	}
	
	public void reset()
	{
		if(isRemapped())
		{
			symbolMap = null;
			reverseSymbolMap = null;
		}
	}
	
	private boolean isRemapped()
	{
		return symbolMap != null;
	}
	
	public boolean isSymbolOutOfBounds(int symbol)
	{
		if(symbol < 0)
			return true;
		
		return symbol >= totalSymbols;
	}
	
	public boolean isSymbolRemapped(int symbol)
	{
		if(!isRemapped()) // No symbols are remapped
			return false;
		
		if(symbol < 0 || symbol >= MAX_SYMBOLS)
			return false;
		
		return reverseSymbolMap[symbol] != symbol;
	}
	
	public boolean isSymbolMapped(int symbol)
	{
		if(!isRemapped())
			return !isSymbolOutOfBounds(symbol);
		
		if(!isSymbolOutOfBounds(symbol) && symbolMap[symbol] == symbol) // Checks if the symbol is in its rightful place
			return true;
		
		return reverseSymbolMap[symbol] > 0 && reverseSymbolMap[symbol] < totalSymbols;
	}
	
	private void fillDefaultSymbolMap()
	{
		symbolMap = new int[totalSymbols];
		for(int i = 0; i < totalSymbols; i++)
		{
			symbolMap[i] = i;
		}
	}
	
	private void fillDefaultReverseSymbolMap()
	{
		reverseSymbolMap = new int[MAX_SYMBOLS];
		// Clean all ordinary symbols
		for(int i = 0; i < totalSymbols; i++)
		{
			reverseSymbolMap[i] = i;
		}
		// Set leftover symbols to unreachable
		for(int i = totalSymbols; i < reverseSymbolMap.length; i++)
		{
			reverseSymbolMap[i] = -1;
		}
	}
	
	public boolean remapSymbol(int originalSymbol, int newSymbol)
	{
		if(isSymbolOutOfBounds(originalSymbol))
			return false;
		
		if(newSymbol < 0 || newSymbol >= MAX_SYMBOLS)
			return false;
		
		if(!isRemapped())
		{
			fillDefaultSymbolMap();
			fillDefaultReverseSymbolMap();
		}
		
		symbolMap[originalSymbol] = newSymbol;
		reverseSymbolMap[newSymbol] = originalSymbol;
		
		return true;
	}
	
	private boolean shouldAvoidSymbol(int[] avoidedSymbols, int symbol)
	{
		for(int avoidedSymbol : avoidedSymbols)
		{
			if(avoidedSymbol == symbol)
				return true;
		}
		
		return false;
	}
	
	public int remapToRandomSymbol(int symbol, int... avoidedSymbols)
	{
		Random random = new Random(0);
		int remapCandidate;
		
		// Try and remap the symbol to some symbol that isn't remapped
		do
		{
			remapCandidate = random.nextInt(1, totalSymbols);
		}
		while(isSymbolRemapped(remapCandidate) && !shouldAvoidSymbol(avoidedSymbols, symbol));
		
		remapSymbol(remapCandidate, symbol);
		
		return remapCandidate;
	}
	
	public int getMappedSymbol(int symbol)
	{
		if(symbol < 0 || symbol >= totalSymbols)
			return -1;
		
		return isRemapped() ? symbolMap[symbol] : symbol;
	}
	
	public int getOriginalSymbol(int symbol)
	{
		if(symbol < 0 || symbol >= MAX_SYMBOLS)
			return -1;
		
		return isRemapped() ? reverseSymbolMap[symbol] : symbol;
	}
	
	public void saveToCompoundTag(CompoundTag tag)
	{
		if(isRemapped())
			tag.putIntArray(SYMBOL_MAP, symbolMap);
	}
	
	public void loadFromCompoundTag(CompoundTag tag)
	{
		if(tag.contains(SYMBOL_MAP, Tag.TAG_INT_ARRAY))
		{
			symbolMap = tag.getIntArray(SYMBOL_MAP);
			fillDefaultReverseSymbolMap();
			
			for(int i = 0; i < symbolMap.length; i++)
			{
				if(symbolMap[i] != i)
					reverseSymbolMap[symbolMap[i]] = i;
			}
		}
	}
	
	public Address.Mutable remapAddress(Address original)
	{
		int[] addressArray = original.toArray();
		for(int i = 0; i < addressArray.length; i++)
		{
			addressArray[i] = getMappedSymbol(addressArray[i]);
		}
		
		return new Address.Mutable(addressArray);
	}
}
