package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.DHDSymbolButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen extends AbstractContainerScreen<AbstractDHDMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way_dhd_background.png");
	
	public AbstractDHDScreen(AbstractDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 192;
        this.imageHeight = 192;
    }
    
    @Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new DHDSymbolButton(x + 102, y + 140, menu, 1));
		this.addRenderableWidget(new DHDSymbolButton(x + 107, y + 160, menu, 2));
		this.addRenderableWidget(new DHDSymbolButton(x + 82, y + 162, menu, 3));
		this.addRenderableWidget(new DHDSymbolButton(x + 22, y + 124, menu, 4));
		this.addRenderableWidget(new DHDSymbolButton(x + 107, y + 16, menu, 5));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 58, y + 18, menu, 6));
		this.addRenderableWidget(new DHDSymbolButton(x + 51, y + 126, menu, 7));
		this.addRenderableWidget(new DHDSymbolButton(x + 38, y + 144, menu, 8));
		this.addRenderableWidget(new DHDSymbolButton(x + 102, y + 38, menu, 9));
		this.addRenderableWidget(new DHDSymbolButton(x + 148, y + 135, menu, 10));
		this.addRenderableWidget(new DHDSymbolButton(x + 164, y + 88, menu, 11));
		this.addRenderableWidget(new DHDSymbolButton.MilkyWay(x + 8, y + 68, menu, DHDSymbolButton.MilkyWay.MilkyWayButton.BUTTON_1));
		this.addRenderableWidget(new DHDSymbolButton(x + 130, y + 118, menu, 13));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 36, y + 80, menu, 14));
		this.addRenderableWidget(new DHDSymbolButton(x + 66, y + 38, menu, 15));
		this.addRenderableWidget(new DHDSymbolButton(x + 116, y + 132, menu, 16));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 138, y + 104, menu, 17));
		this.addRenderableWidget(new DHDSymbolButton.MilkyWay(x + 14, y + 43, menu, DHDSymbolButton.MilkyWay.MilkyWayButton.BUTTON_2));
		this.addRenderableWidget(new DHDSymbolButton(x + 58, y + 156, menu, 19));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 130, y + 150, menu, 20));
		this.addRenderableWidget(new DHDSymbolButton(x + 37, y + 33, menu, 21));
		this.addRenderableWidget(new DHDSymbolButton(x + 142, y + 88, menu, 22));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 148, y + 42, menu, 23));
		this.addRenderableWidget(new DHDSymbolButton(x + 83, y + 140, menu, 24));
		this.addRenderableWidget(new DHDSymbolButton(x + 138, y + 71, menu, 25));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 42, y + 112, menu, 26));
		this.addRenderableWidget(new DHDSymbolButton(x + 84, y + 36, menu, 27));
		this.addRenderableWidget(new DHDSymbolButton(x + 129, y + 25, menu, 28));
		this.addRenderableWidget(new DHDSymbolButton(x + 52, y + 48, menu, 29));
		this.addRenderableWidget(new DHDSymbolButton(x + 38, y + 97, menu, 30));
		this.addRenderableWidget(new DHDSymbolButton(x + 14, y + 100, menu, 31));
		this.addRenderableWidget(new DHDSymbolButton(x + 118, y + 44, menu, 32));
		this.addRenderableWidget(new DHDSymbolButton(x + 160, y + 64, menu, 33));
		
		this.addRenderableWidget(new DHDSymbolButton(x + 42, y + 64, menu, 34));
		this.addRenderableWidget(new DHDSymbolButton(x + 66, y + 135, menu, 35));
		this.addRenderableWidget(new DHDSymbolButton(x + 160, y + 112, menu, 36));
		this.addRenderableWidget(new DHDSymbolButton(x + 83, y + 12, menu, 37));
		this.addRenderableWidget(new DHDSymbolButton(x + 130, y + 58, menu, 38));
	}

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        renderTooltip(matrixStack, mouseX, mouseY);
	    
	    /*tooltip(matrixStack, 44, 6, mouseX, mouseY, "1");
	    tooltip(matrixStack, 64, 6, mouseX, mouseY, "2");
	    tooltip(matrixStack, 84, 6, mouseX, mouseY, "3");
	    tooltip(matrixStack, 104, 6, mouseX, mouseY, "4");
	    tooltip(matrixStack, 124, 6, mouseX, mouseY, "5");

	    tooltip(matrixStack, 10, 26, mouseX, mouseY, "6");
	    tooltip(matrixStack, 30, 26, mouseX, mouseY, "7");
	    tooltip(matrixStack, 50, 26, mouseX, mouseY, "8");
	    tooltip(matrixStack, 70, 26, mouseX, mouseY, "9");
	    tooltip(matrixStack, 90, 26, mouseX, mouseY, "10");
	    tooltip(matrixStack, 110, 26, mouseX, mouseY, "11");
	    tooltip(matrixStack, 130, 26, mouseX, mouseY, "12");
	    tooltip(matrixStack, 150, 26, mouseX, mouseY, "13");

	    tooltip(matrixStack, 10, 46, mouseX, mouseY, "14");
	    tooltip(matrixStack, 30, 46, mouseX, mouseY, "15");
	    tooltip(matrixStack, 50, 46, mouseX, mouseY, "16");

	    tooltip(matrixStack, 110, 46, mouseX, mouseY, "17");
	    tooltip(matrixStack, 130, 46, mouseX, mouseY, "18");
	    tooltip(matrixStack, 150, 46, mouseX, mouseY, "19");

	    tooltip(matrixStack, 10, 66, mouseX, mouseY, "20");
	    tooltip(matrixStack, 30, 66, mouseX, mouseY, "21");
	    tooltip(matrixStack, 50, 66, mouseX, mouseY, "22");

	    tooltip(matrixStack, 110, 66, mouseX, mouseY, "23");
	    tooltip(matrixStack, 130, 66, mouseX, mouseY, "24");
	    tooltip(matrixStack, 150, 66, mouseX, mouseY, "25");

	    tooltip(matrixStack, 10, 86, mouseX, mouseY, "26");
	    tooltip(matrixStack, 30, 86, mouseX, mouseY, "27");
	    tooltip(matrixStack, 50, 86, mouseX, mouseY, "28");
	    tooltip(matrixStack, 70, 86, mouseX, mouseY, "29");
	    tooltip(matrixStack, 90, 86, mouseX, mouseY, "30");
	    tooltip(matrixStack, 110, 86, mouseX, mouseY, "31");
	    tooltip(matrixStack, 130, 86, mouseX, mouseY, "32");
	    tooltip(matrixStack, 150, 86, mouseX, mouseY, "33");

	    tooltip(matrixStack, 44, 106, mouseX, mouseY, "34");
	    tooltip(matrixStack, 64, 106, mouseX, mouseY, "35");
	    tooltip(matrixStack, 84, 106, mouseX, mouseY, "36");
	    tooltip(matrixStack, 104, 106, mouseX, mouseY, "37");
	    tooltip(matrixStack, 124, 106, mouseX, mouseY, "38");*/
    }
    
    private void tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, String number)
    {
    	if(this.isHovering(x, y, 16, 16, (double) mouseX, (double) mouseY))
	    	renderTooltip(matrixStack, Component.literal(number), mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
    	
    }
    
    
}
