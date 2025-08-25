package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
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
	
	protected void itemHint(PoseStack poseStack, int mouseX, int mouseY, int hintTexturePosX, int hintTexturePosY, int slot)
	{
		if(!hasItem(slot))
			this.blit(poseStack, mouseX, mouseY, hintTexturePosX, hintTexturePosY, 16, 16);
	}
	
	protected void tooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, Component... components)
	{
		if(this.isHovering(x, y, width, height, mouseX, mouseY))
		{
			if(components.length == 1)
				renderTooltip(poseStack, components[0], mouseX, mouseY);
			else
			{
				List<Component> tooltips = List.of(components);
				renderComponentTooltip(poseStack, tooltips, mouseX, mouseY);
			}
		}
	}
	
	protected void itemTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int slot, Component... components)
	{
		if(!hasItem(slot))
			tooltip(poseStack, mouseX, mouseY, x, y, 16, 16, components);
	}
	
	protected void energyTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, String name, long energy, long maxEnergy)
	{
		tooltip(poseStack, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(name, energy, maxEnergy));
	}
	
	protected void energyTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, long energy, long maxEnergy)
	{
		tooltip(poseStack, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(energy, maxEnergy));
	}
	
	protected void energyTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, String name, long energy)
	{
		tooltip(poseStack, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(name, energy));
	}
	
	protected void energyTooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, long energy)
	{
		tooltip(poseStack, mouseX, mouseY, x, y, width, height, ComponentHelper.energy(energy));
	}
	
	protected void renderEnergyHorizontal(PoseStack matrixStack, int x, int y, int width, int height, int energyTexturePosX, int energyTexturePosY, long energy, long maxEnergy)
	{
		float percentage = (float) energy / maxEnergy;
		int actual = Math.round(width * percentage);
		this.blit(matrixStack, x, y, energyTexturePosX, energyTexturePosY, actual, height);
	}
	
	protected void renderEnergyVertical(PoseStack matrixStack, int x, int y, int width, int height, int energyTexturePosX, int energyTexturePosY, long energy, long maxEnergy)
	{
		float percentage = (float) energy / maxEnergy;
		int actual = Math.round(height * percentage);
		this.blit(matrixStack, x, y + height - actual, energyTexturePosX, energyTexturePosY, width, actual);
	}
}
