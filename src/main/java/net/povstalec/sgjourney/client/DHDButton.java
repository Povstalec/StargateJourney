package net.povstalec.sgjourney.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class DHDButton extends Button
{
	
	public static final ResourceLocation WIDGETS_LOCATION = StargateJourney.sgjourneyLocation("textures/gui/milky_way_dhd_widgets.png");
	
    public DHDButton(int x, int y, AbstractDHDMenu menu, int i)
	{
		super(x, y, 16, 16, symbol(menu.symbolsType, i), (n) -> {menu.engageChevron(i);}, Button.DEFAULT_NARRATION);
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
    public void render(GuiGraphics graphics, int x, int y, float p_93679_)
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
        graphics.blit(WIDGETS_LOCATION, this.getX(), this.getY(), 0, i * 16, this.width, this.height);
        graphics.blit(WIDGETS_LOCATION, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        int j = getFGColor();
        graphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
	
    private static Component symbol(String symbolsType, int i)
    {
    	MutableComponent symbols = Component.literal("" + i); //Symbols.unicode(i)
		//Style style = symbols.getStyle().withFont(new ResourceLocation(symbolsType));
		//symbols = symbols.withStyle(style);
		return symbols;
    }
    
}
