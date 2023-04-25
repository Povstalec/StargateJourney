package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.RingPanelButton;
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
		if(menu.getRingsFound() >= 1)
			this.addRenderableWidget(new RingPanelButton(x + 51, y + 48, Component.literal(""), (n) -> {menu.activateRings(0);}));
		if(menu.getRingsFound() >= 2)
			this.addRenderableWidget(new RingPanelButton(x + 93, y + 48, Component.literal(""), (n) -> {menu.activateRings(1);}));
		if(menu.getRingsFound() >= 3)
			this.addRenderableWidget(new RingPanelButton(x + 51, y + 66, Component.literal(""), (n) -> {menu.activateRings(2);}));
		if(menu.getRingsFound() >= 4)
			this.addRenderableWidget(new RingPanelButton(x + 93, y + 66, Component.literal(""), (n) -> {menu.activateRings(3);}));
		if(menu.getRingsFound() >= 5)
			this.addRenderableWidget(new RingPanelButton(x + 51, y + 84, Component.literal(""), (n) -> {menu.activateRings(4);}));
		if(menu.getRingsFound() == 6)
			this.addRenderableWidget(new RingPanelButton(x + 93, y + 84, Component.literal(""), (n) -> {menu.activateRings(5);}));
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
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
        
	    renderButtonTooltip(pPoseStack, 1, 51, 48, mouseX, mouseY);
	    renderButtonTooltip(pPoseStack, 2, 93, 48, mouseX, mouseY);
	    renderButtonTooltip(pPoseStack, 3, 51, 66, mouseX, mouseY);
	    renderButtonTooltip(pPoseStack, 4, 93, 66, mouseX, mouseY);
	    renderButtonTooltip(pPoseStack, 5, 51, 84, mouseX, mouseY);
	    renderButtonTooltip(pPoseStack, 6, 93, 84, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
	    this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, 128.0F, 4210752);
	    
    }
    
    private void renderButtonTooltip(PoseStack matrixStack, int ringNumber, int xStart, int yStart, int mouseX, int mouseY)
    {
    	if(this.isHovering(xStart, yStart, 32, 16, (double) mouseX, (double) mouseY))
	    	renderTooltip(matrixStack, Component.literal(menu.getRingsPos(ringNumber)), mouseX, mouseY);
    }
    
    
}
