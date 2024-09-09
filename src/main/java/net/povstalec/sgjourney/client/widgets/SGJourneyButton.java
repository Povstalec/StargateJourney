package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class SGJourneyButton extends Button
{
	private Minecraft minecraft = Minecraft.getInstance();
	protected final ResourceLocation texture;
	
	protected final Component tooltip;
	
	protected final int xOffset;
	protected final int yOffset;
	
	public SGJourneyButton(ResourceLocation texture, int x, int y, int xSize, int ySize, int xOffset, int yOffset, Component message, Component tooltip, OnPress press)
	{
		super(x, y, xSize, ySize, message, press, Button.NO_TOOLTIP);
		
		this.texture = texture;
		
		this.tooltip = tooltip;
		
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
		return x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
	}
	
	@Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, xOffset, yOffset + i * this.height, this.width, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2 , this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        
        Screen screen = minecraft.screen;
        if(screen != null && isHovered(mouseX, mouseY))
        	screen.renderTooltip(stack, tooltip, mouseX, mouseY);
     }
}
