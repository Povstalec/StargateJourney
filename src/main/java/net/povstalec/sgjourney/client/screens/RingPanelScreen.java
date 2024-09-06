package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.RingPanelButton;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;

public class RingPanelScreen extends AbstractContainerScreen<RingPanelMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/ring_panel_gui.png");

    public RingPanelScreen(RingPanelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 222;
    }
    
    @Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 48, Component.empty(), (n) -> {menu.activateRings(0);}));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 48, Component.empty(), (n) -> {menu.activateRings(1);}));
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 66, Component.empty(), (n) -> {menu.activateRings(2);}));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 66, Component.empty(), (n) -> {menu.activateRings(3);}));
		this.addRenderableWidget(new RingPanelButton(x + 51, y + 84, Component.empty(), (n) -> {menu.activateRings(4);}));
		this.addRenderableWidget(new RingPanelButton(x + 93, y + 84, Component.empty(), (n) -> {menu.activateRings(5);}));
	}

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
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
        
	    renderButtonTooltip(graphics, 1, 51, 48, mouseX, mouseY);
	    renderButtonTooltip(graphics, 2, 93, 48, mouseX, mouseY);
	    renderButtonTooltip(graphics, 3, 51, 66, mouseX, mouseY);
	    renderButtonTooltip(graphics, 4, 93, 66, mouseX, mouseY);
	    renderButtonTooltip(graphics, 5, 51, 84, mouseX, mouseY);
	    renderButtonTooltip(graphics, 6, 93, 84, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
    	graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, 128, 4210752, false);
	    
    }
    
    private void renderButtonTooltip(GuiGraphics graphics, int ringNumber, int xStart, int yStart, int mouseX, int mouseY)
    {
    	if(this.isHovering(xStart, yStart, 32, 16, (double) mouseX, (double) mouseY))
    		graphics.renderTooltip(this.font, menu.getRingsPos(ringNumber), mouseX, mouseY);
    }
    
    
}
