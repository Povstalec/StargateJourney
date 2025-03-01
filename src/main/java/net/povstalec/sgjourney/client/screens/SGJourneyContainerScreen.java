package net.povstalec.sgjourney.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class SGJourneyContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
	public SGJourneyContainerScreen(T menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}
	
	protected void tooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, Component component)
	{
		if(this.isHovering(x, y, width, height, mouseX, mouseY))
			graphics.renderTooltip(this.font, component, mouseX, mouseY);
	}
	
	protected boolean hasItem(int slot)
	{
		return true;
	}
	
	protected void itemHint(GuiGraphics graphics, ResourceLocation texture, int x, int y, int hintTexturePosX, int hintTexturePosY, int slot)
	{
		if(!hasItem(slot))
			graphics.blit(texture, x, y, hintTexturePosX, hintTexturePosY, 16, 16);
	}
}
