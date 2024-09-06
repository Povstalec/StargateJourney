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
import net.povstalec.sgjourney.StargateJourney;

public class RingPanelButton extends Button
{
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/widgets.png");
	
    public RingPanelButton(int x, int y, Component component, OnPress press)
	{
		super(x, y, 32, 16, component, press, Button.DEFAULT_NARRATION);
	}
    
    protected int getYImage(boolean p_93668_)
    {
    	int i = 1;
    	if (!this.active)
    	{
    		i = 0;
    	}
    	else if(p_93668_)
    	{
    		i = 2;
    	}
    	
    	return i;
	}
    
    @Override
    public void render(PoseStack p_93676_, int x, int y, float p_93679_)
    {
    	this.isHovered = x >= this.getX() && y >= this.getY() && x < this.getX() + this.width && y < this.getY() + this.height;
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(p_93676_, this.getX(), this.getY(), 0, i * 16, this.width, this.height);
        blit(p_93676_, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        int j = getFGColor();
        drawCenteredString(p_93676_, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
	
}
