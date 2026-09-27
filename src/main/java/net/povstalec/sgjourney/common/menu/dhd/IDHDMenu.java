package net.povstalec.sgjourney.common.menu.dhd;

import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;

public interface IDHDMenu
{
	AbstractDHDEntity getDHD();
	
	default boolean isSymbolEngaged(int symbol)
	{
		return getDHD().isSymbolEncoded(symbol);
	}
	
	default boolean isSymbolRemapped(int symbol)
	{
		return getDHD().isSymbolRemapped(symbol);
	}
	
	default int getRemappedOriginalSymbol(int symbol)
	{
		return getDHD().getRemappedOriginalSymbol(symbol);
	}
	
	default boolean isCenterButtonEngaged()
	{
		return getDHD().isCenterButtonEngaged();
	}
}
