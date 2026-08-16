package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
	
	protected int getXImage()
	{
		return 0;
	}
	
	protected int getYImage(boolean isHovered)
    {
		if(!this.active)
			return 0;
		
    	return isHovered ? 2 : 1;
	}
	
	protected boolean isHovered(int x, int y)
	{
		return x >= this.getX() && y >= this.getY() && x < this.getX() + this.width && y < this.getY() + this.height;
	}
	
	@Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int x = this.getXImage();
        int y = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.getX(), this.getY(), xOffset + x * this.width, yOffset + y * this.height, this.width, this.height);
        this.renderBg(stack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(stack, font, this.getMessage(), this.getX() + this.width / 2 , this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
}
