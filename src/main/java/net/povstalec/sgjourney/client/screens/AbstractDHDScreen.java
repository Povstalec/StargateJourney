package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.DHDButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen extends AbstractContainerScreen<AbstractDHDMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd_background.png");
	
	public AbstractDHDScreen(AbstractDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 176;
    }
    
    @Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new DHDButton(x + 44, y + 6, menu, 1));
		this.addRenderableWidget(new DHDButton(x + 64, y + 6, menu, 2));
		this.addRenderableWidget(new DHDButton(x + 84, y + 6, menu, 3));
		this.addRenderableWidget(new DHDButton(x + 104, y + 6, menu, 4));
		this.addRenderableWidget(new DHDButton(x + 124, y + 6, menu, 5));
		
		this.addRenderableWidget(new DHDButton(x + 10, y + 26, menu, 6));
		this.addRenderableWidget(new DHDButton(x + 30, y + 26, menu, 7));
		this.addRenderableWidget(new DHDButton(x + 50, y + 26, menu, 8));
		this.addRenderableWidget(new DHDButton(x + 70, y + 26, menu, 9));
		this.addRenderableWidget(new DHDButton(x + 90, y + 26, menu, 10));
		this.addRenderableWidget(new DHDButton(x + 110, y + 26, menu, 11));
		this.addRenderableWidget(new DHDButton(x + 130, y + 26, menu, 12));
		this.addRenderableWidget(new DHDButton(x + 150, y + 26, menu, 13));
		
		this.addRenderableWidget(new DHDButton(x + 10, y + 46, menu, 14));
		this.addRenderableWidget(new DHDButton(x + 30, y + 46, menu, 15));
		this.addRenderableWidget(new DHDButton(x + 50, y + 46, menu, 16));
		
		this.addRenderableWidget(new DHDButton(x + 110, y + 46, menu, 17));
		this.addRenderableWidget(new DHDButton(x + 130, y + 46, menu, 18));
		this.addRenderableWidget(new DHDButton(x + 150, y + 46, menu, 19));
		
		this.addRenderableWidget(new DHDButton(x + 10, y + 66, menu, 20));
		this.addRenderableWidget(new DHDButton(x + 30, y + 66, menu, 21));
		this.addRenderableWidget(new DHDButton(x + 50, y + 66, menu, 22));
		
		this.addRenderableWidget(new DHDButton(x + 110, y + 66, menu, 23));
		this.addRenderableWidget(new DHDButton(x + 130, y + 66, menu, 24));
		this.addRenderableWidget(new DHDButton(x + 150, y + 66, menu, 25));
		
		this.addRenderableWidget(new DHDButton(x + 10, y + 86, menu, 26));
		this.addRenderableWidget(new DHDButton(x + 30, y + 86, menu, 27));
		this.addRenderableWidget(new DHDButton(x + 50, y + 86, menu, 28));
		this.addRenderableWidget(new DHDButton(x + 70, y + 86, menu, 29));
		this.addRenderableWidget(new DHDButton(x + 90, y + 86, menu, 30));
		this.addRenderableWidget(new DHDButton(x + 110, y + 86, menu, 31));
		this.addRenderableWidget(new DHDButton(x + 130, y + 86, menu, 32));
		this.addRenderableWidget(new DHDButton(x + 150, y + 86, menu, 33));
		
		this.addRenderableWidget(new DHDButton(x + 44, y + 106, menu, 34));
		this.addRenderableWidget(new DHDButton(x + 64, y + 106, menu, 35));
		this.addRenderableWidget(new DHDButton(x + 84, y + 106, menu, 36));
		this.addRenderableWidget(new DHDButton(x + 104, y + 106, menu, 37));
		this.addRenderableWidget(new DHDButton(x + 124, y + 106, menu, 38));
	}

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
	    
	    tooltip(graphics, 44, 6, mouseX, mouseY, "1");
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
	    tooltip(graphics, 124, 106, mouseX, mouseY, "38");
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
