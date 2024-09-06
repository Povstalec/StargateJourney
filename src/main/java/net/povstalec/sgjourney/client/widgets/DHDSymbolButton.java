package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class DHDSymbolButton extends DHDButton
{
	protected AbstractDHDMenu menu;
	protected ResourceLocation widgets;
	
	protected int symbol;
	protected int widgetTextureOffsetX = 0;
	protected int widgetTextureOffsetY = 0;
	
    public DHDSymbolButton(int x, int y, int xSize, int ySize, AbstractDHDMenu menu, int symbol, ResourceLocation widgets)
	{
		super(x, y, xSize, ySize, symbol(menu.symbolsType, symbol), (button) -> {menu.engageChevron(symbol);});
		
		this.menu = menu;
		this.widgets = widgets;
		
		this.symbol = symbol;
	}
    
    @Override
    protected int getYImage(boolean isHovering)
    {
    	if(this.menu.isSymbolEngaged(this.symbol))
    		return 2;
    	
    	if(isHovering)
    		return 1;
    	
    	return 0;
    }
	
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, widgets);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, widgetTextureOffsetX, widgetTextureOffsetY + i * this.getHeight(), this.width, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
     }
	
    private static Component symbol(String symbolsType, int i)
    {
    	MutableComponent symbols = Component.literal(String.valueOf(i));
    	
		return symbols;
    }
    
}
