package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;

public class DHDCrystalScreen extends AbstractContainerScreen<DHDCrystalMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd_crystal_gui.png");
	
	public DHDCrystalScreen(DHDCrystalMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
        super(pMenu, pPlayerInventory, pTitle);
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
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
    	this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }
    
    
}
