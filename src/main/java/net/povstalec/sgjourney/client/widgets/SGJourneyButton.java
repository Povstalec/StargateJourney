package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class SGJourneyButton extends Button
{
	protected final ResourceLocation texture;
	
	protected final int xOffset;
	protected final int yOffset;
	
	public SGJourneyButton(ResourceLocation texture, int x, int y, int xSize, int ySize, int xOffset, int yOffset, Component component, OnPress press)
	{
		super(x, y, xSize, ySize, component, press, Button.DEFAULT_NARRATION);
		
		this.texture = texture;
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public SGJourneyButton(ResourceLocation texture, int x, int y, int xSize, int ySize, Component component, OnPress press)
	{
		this(texture, x, y, xSize, ySize, 0, 0, component, press);
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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
    	this.isHovered = isHovered(mouseX, mouseY);
		
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(stack, this.getX(), this.getY(), xOffset, yOffset + i * this.height, this.width, this.height);
        blit(stack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        int j = getFGColor();
        drawCenteredString(stack, font, this.getMessage(), this.getX() + this.width / 2 , this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
}
