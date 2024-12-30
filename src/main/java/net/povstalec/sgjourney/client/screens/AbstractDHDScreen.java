package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen extends AbstractContainerScreen<AbstractDHDMenu>
{
	private ResourceLocation texture;
	
	public AbstractDHDScreen(AbstractDHDMenu menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
        this.imageWidth = 192;
        this.imageHeight = 192;
        
        this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics, mouseX, mouseY, delta);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
	    
	    /*tooltip(graphics, 44, 6, mouseX, mouseY, "1");
	    tooltip(graphics, 64, 6, mouseX, mouseY, "2");
	    tooltip(graphics, 84, 6, mouseX, mouseY, "3");
	    tooltip(graphics, 104, 6, mouseX, mouseY, "4");
	    tooltip(graphics, 124, 6, mouseX, mouseY, "5");

	    tooltip(graphics, 10, 26, mouseX, mouseY, "6");
	    tooltip(graphics, 30, 26, mouseX, mouseY, "7");
	    tooltip(graphics, 50, 26, mouseX, mouseY, "8");
	    tooltip(graphics, 70, 26, mouseX, mouseY, "9");
	    tooltip(graphics, 90, 26, mouseX, mouseY, "10");
	    tooltip(graphics, 110, 26, mouseX, mouseY, "11");
	    tooltip(graphics, 130, 26, mouseX, mouseY, "12");
	    tooltip(graphics, 150, 26, mouseX, mouseY, "13");

	    tooltip(graphics, 10, 46, mouseX, mouseY, "14");
	    tooltip(graphics, 30, 46, mouseX, mouseY, "15");
	    tooltip(graphics, 50, 46, mouseX, mouseY, "16");

	    tooltip(graphics, 110, 46, mouseX, mouseY, "17");
	    tooltip(graphics, 130, 46, mouseX, mouseY, "18");
	    tooltip(graphics, 150, 46, mouseX, mouseY, "19");

	    tooltip(graphics, 10, 66, mouseX, mouseY, "20");
	    tooltip(graphics, 30, 66, mouseX, mouseY, "21");
	    tooltip(graphics, 50, 66, mouseX, mouseY, "22");

	    tooltip(graphics, 110, 66, mouseX, mouseY, "23");
	    tooltip(graphics, 130, 66, mouseX, mouseY, "24");
	    tooltip(graphics, 150, 66, mouseX, mouseY, "25");

	    tooltip(graphics, 10, 86, mouseX, mouseY, "26");
	    tooltip(graphics, 30, 86, mouseX, mouseY, "27");
	    tooltip(graphics, 50, 86, mouseX, mouseY, "28");
	    tooltip(graphics, 70, 86, mouseX, mouseY, "29");
	    tooltip(graphics, 90, 86, mouseX, mouseY, "30");
	    tooltip(graphics, 110, 86, mouseX, mouseY, "31");
	    tooltip(graphics, 130, 86, mouseX, mouseY, "32");
	    tooltip(graphics, 150, 86, mouseX, mouseY, "33");

	    tooltip(graphics, 44, 106, mouseX, mouseY, "34");
	    tooltip(graphics, 64, 106, mouseX, mouseY, "35");
	    tooltip(graphics, 84, 106, mouseX, mouseY, "36");
	    tooltip(graphics, 104, 106, mouseX, mouseY, "37");
	    tooltip(graphics, 124, 106, mouseX, mouseY, "38");*/
    }
    
    private void tooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, String number)
    {
    	if(this.isHovering(x, y, 16, 16, (double) mouseX, (double) mouseY))
    		graphics.renderTooltip(this.font, Component.literal(number), mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
    	
    }
    
    
}
