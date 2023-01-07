package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.DHDButton;
import net.povstalec.sgjourney.menu.AbstractDHDMenu;

public abstract class DHDScreen extends AbstractContainerScreen<AbstractDHDMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd_gui.png");
	
	public DHDScreen(AbstractDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 230;
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
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
    
    private void tooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY, String number)
    {
    	if(mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16)
	    {
	    	renderTooltip(matrixStack, Component.literal(number), mouseX - 156, mouseY);
	    }
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
	    //this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY + 63.0F, 4210752);
	    
	    tooltip(matrixStack, x + 44, y + 6, mouseX, mouseY, "1");
	    tooltip(matrixStack, x + 64, y + 6, mouseX, mouseY, "2");
	    tooltip(matrixStack, x + 84, y + 6, mouseX, mouseY, "3");
	    tooltip(matrixStack, x + 104, y + 6, mouseX, mouseY, "4");
	    tooltip(matrixStack, x + 124, y + 6, mouseX, mouseY, "5");

	    tooltip(matrixStack, x + 10, y + 26, mouseX, mouseY, "6");
	    tooltip(matrixStack, x + 30, y + 26, mouseX, mouseY, "7");
	    tooltip(matrixStack, x + 50, y + 26, mouseX, mouseY, "8");
	    tooltip(matrixStack, x + 70, y + 26, mouseX, mouseY, "9");
	    tooltip(matrixStack, x + 90, y + 26, mouseX, mouseY, "10");
	    tooltip(matrixStack, x + 110, y + 26, mouseX, mouseY, "11");
	    tooltip(matrixStack, x + 130, y + 26, mouseX, mouseY, "12");
	    tooltip(matrixStack, x + 150, y + 26, mouseX, mouseY, "13");

	    tooltip(matrixStack, x + 10, y + 46, mouseX, mouseY, "14");
	    tooltip(matrixStack, x + 30, y + 46, mouseX, mouseY, "15");
	    tooltip(matrixStack, x + 50, y + 46, mouseX, mouseY, "16");

	    tooltip(matrixStack, x + 110, y + 46, mouseX, mouseY, "17");
	    tooltip(matrixStack, x + 130, y + 46, mouseX, mouseY, "18");
	    tooltip(matrixStack, x + 150, y + 46, mouseX, mouseY, "19");

	    tooltip(matrixStack, x + 10, y + 66, mouseX, mouseY, "20");
	    tooltip(matrixStack, x + 30, y + 66, mouseX, mouseY, "21");
	    tooltip(matrixStack, x + 50, y + 66, mouseX, mouseY, "22");

	    tooltip(matrixStack, x + 110, y + 66, mouseX, mouseY, "23");
	    tooltip(matrixStack, x + 130, y + 66, mouseX, mouseY, "24");
	    tooltip(matrixStack, x + 150, y + 66, mouseX, mouseY, "25");

	    tooltip(matrixStack, x + 10, y + 86, mouseX, mouseY, "26");
	    tooltip(matrixStack, x + 30, y + 86, mouseX, mouseY, "27");
	    tooltip(matrixStack, x + 50, y + 86, mouseX, mouseY, "28");
	    tooltip(matrixStack, x + 70, y + 86, mouseX, mouseY, "29");
	    tooltip(matrixStack, x + 90, y + 86, mouseX, mouseY, "30");
	    tooltip(matrixStack, x + 110, y + 86, mouseX, mouseY, "31");
	    tooltip(matrixStack, x + 130, y + 86, mouseX, mouseY, "32");
	    tooltip(matrixStack, x + 150, y + 86, mouseX, mouseY, "33");

	    tooltip(matrixStack, x + 44, y + 106, mouseX, mouseY, "34");
	    tooltip(matrixStack, x + 64, y + 106, mouseX, mouseY, "35");
	    tooltip(matrixStack, x + 84, y + 106, mouseX, mouseY, "36");
	    tooltip(matrixStack, x + 104, y + 106, mouseX, mouseY, "37");
	    tooltip(matrixStack, x + 124, y + 106, mouseX, mouseY, "38");
	    
    }
    
    
}
