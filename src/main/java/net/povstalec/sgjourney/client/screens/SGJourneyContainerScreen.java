package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

public abstract class SGJourneyContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
	public SGJourneyContainerScreen(T menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}
	
	protected void tooltip(PoseStack poseStack, int mouseX, int mouseY, int x, int y, int width, int height, Component... components)
	{
		if(this.isHovering(x, y, width, height, mouseX, mouseY))
		{
			if(components.length == 0)
				renderTooltip(poseStack, components[0], mouseX, mouseY);
			else
			{
				List<Component> tooltips = List.of(components);
				renderComponentTooltip(poseStack, tooltips, mouseX, mouseY);
			}
		}
	}
	
	protected boolean hasItem(int slot)
	{
		return true;
	}
	
	protected void itemHint(PoseStack poseStack, int x, int y, int hintTexturePosX, int hintTexturePosY, int slot)
	{
		if(!hasItem(slot))
			this.blit(poseStack, x, y, hintTexturePosX, hintTexturePosY, 16, 16);
	}
}
