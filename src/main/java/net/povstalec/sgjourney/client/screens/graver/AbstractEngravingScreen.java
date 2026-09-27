package net.povstalec.sgjourney.client.screens.graver;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.misc.ButtonTooltip;

import java.util.function.Consumer;

public abstract class AbstractEngravingScreen<M extends AbstractContainerMenu> extends SGJourneyContainerScreen<M>
{
	public enum Selected
	{
		BOTH(new TranslatableComponent("screen.sgjourney.both.description"), true, true),
		POINT_OF_ORIGIN(new TranslatableComponent("screen.sgjourney.point_of_origin.description"), true, false),
		SYMBOLS(new TranslatableComponent("screen.sgjourney.symbols.description"), false, true);
		
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
	protected final ButtonTooltip engravingButtonTooltip = new ButtonTooltip()
	{
		@Override
		public void onTooltip(Button button, PoseStack stack, int mouseX, int mouseY)
		{
			renderTooltip(stack, getComponent(), mouseX, mouseY);
		}
	};
	
	public AbstractEngravingScreen(M menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}
}
