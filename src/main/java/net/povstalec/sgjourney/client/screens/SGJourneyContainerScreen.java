package net.povstalec.sgjourney.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

import java.util.List;

public abstract class SGJourneyContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
	public SGJourneyContainerScreen(T menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
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
	
	protected void tooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, Component... components)
	{
		if(this.isHovering(x, y, width, height, mouseX, mouseY))
		{
			if(components.length == 1)
				graphics.renderTooltip(this.font, components[0], mouseX, mouseY);
			else
			{
				List<Component> tooltips = List.of(components);
				graphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
			}
		}
	}
	
	protected void itemTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int slot, Component... components)
	{
		if(!hasItem(slot))
			tooltip(graphics, mouseX, mouseY, x, y, 16, 16, components);
	}
	
	protected void energyTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, String name, long energy, long maxEnergy)
	{
		tooltip(graphics, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(name, energy, maxEnergy));
	}
	
	protected void energyTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, long energy, long maxEnergy)
	{
		tooltip(graphics, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(energy, maxEnergy));
	}
	
	protected void energyTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, String name, long energy)
	{
		tooltip(graphics, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(name, energy));
	}
	
	protected void energyTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, long energy)
	{
		tooltip(graphics, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(energy));
	}
	
	protected void renderEnergyHorizontal(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int energyTexturePosX, int energyTexturePosY, long energy, long maxEnergy)
	{
		float percentage = (float) energy / maxEnergy;
		int actual = Math.round(width * percentage);
		graphics.blit(texture, x, y, energyTexturePosX, energyTexturePosY, actual, height);
	}
	
	protected void renderEnergyVertical(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int energyTexturePosX, int energyTexturePosY, long energy, long maxEnergy)
	{
		float percentage = (float) energy / maxEnergy;
		int actual = Math.round(height * percentage);
		graphics.blit(texture, x, y + height - actual, energyTexturePosX, energyTexturePosY, width, actual);
	}
}
