package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class SGJourneyButton extends Button
{
	protected final ResourceLocation texture;
	
	protected final int xOffset;
	protected final int yOffset;
	
	public SGJourneyButton(ResourceLocation texture, int x, int y, int xSize, int ySize, int xOffset, int yOffset, Component message, Component tooltip, OnPress press)
	{
		super(x, y, xSize, ySize, message, press, Button.DEFAULT_NARRATION);
		
		this.texture = texture;
		
		this.setTooltip(Tooltip.create(tooltip));
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public SGJourneyButton(ResourceLocation texture, int x, int y, int xSize, int ySize, Component message, Component tooltip, OnPress press)
	{
		this(texture, x, y, xSize, ySize, 0, 0, message, tooltip, press);
	}
	
	protected int getYImage(boolean isHovered)
    {
    	int i = 1;
    	
    	if (!this.active)
    		i = 0;
    	
    	else if(isHovered)
    		i = 2;
    	
    	return i;
	}
	
	protected boolean isHovered(int x, int y)
	{
		return x >= this.getX() && y >= this.getY() && x < this.getX() + this.width && y < this.getY() + this.height;
	}
	
	@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
		super.render(graphics, mouseX, mouseY, partialTick);
     }
	
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick)
	{
		this.isHovered = isHovered(mouseX, mouseY);
		
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(texture, this.getX(), this.getY(), xOffset, yOffset + i * height, this.width, this.height);
        graphics.blit(texture, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
		int j = getFGColor();
		this.renderString(graphics, minecraft.font, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
}
