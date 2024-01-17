package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.NaquadahGeneratorMenu;

public class NaquadahGeneratorScreen extends AbstractContainerScreen<NaquadahGeneratorMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/naquadah_generator_gui.png");

    public NaquadahGeneratorScreen(NaquadahGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight + 1);
        
        this.renderProgress(graphics, x + 62, y + 35);
        this.renderEnergy(graphics, x + 8, y + 62);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        this.energyTooltip(graphics, 8, 62, mouseX, mouseY);
    }
    
    /*@Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	    this.font.draw(matrixStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
    }*/
    
    protected void renderProgress(GuiGraphics graphics, int x, int y)
    {

    	float percentage = (float) this.menu.getReactionProgress() / this.menu.getReactionTime();
    	int actual = Math.round(52 * percentage);
    	
    	graphics.blit(TEXTURE, x, y, 0, 174, actual, 16);
    }
    
    protected void renderEnergy(GuiGraphics graphics, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(160 * percentage);
    	graphics.blit(TEXTURE, x, y, 0, 168, actual, 6);
    }
    
    protected void energyTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 160, 6, (double) mouseX, (double) mouseY))
    		graphics.renderTooltip(this.font, Component.literal("Energy: " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE").withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
    }
}
