package net.povstalec.sgjourney.client.screens.graver;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;

public abstract class AbstractEngravingScreen<M extends AbstractContainerMenu> extends SGJourneyContainerScreen<M>
{
	public enum Selected
	{
		BOTH(Component.translatable("screen.sgjourney.both.description"), true, true),
		POINT_OF_ORIGIN(Component.translatable("screen.sgjourney.point_of_origin.description"), true, false),
		SYMBOLS(Component.translatable("screen.sgjourney.symbols.description"), false, true);
		
		public final Component tooltip;
		public final boolean engravePointOfOrigin;
		public final boolean engraveSymbols;
		
		Selected(Component tooltip, boolean engravePointOfOrigin, boolean engraveSymbols)
		{
			this.tooltip = tooltip;
			this.engravePointOfOrigin = engravePointOfOrigin;
			this.engraveSymbols = engraveSymbols;
		}
	}
	
	protected Button engravingButton;
	
	public AbstractEngravingScreen(M menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}
}
