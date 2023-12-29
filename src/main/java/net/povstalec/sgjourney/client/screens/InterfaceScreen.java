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
import net.povstalec.sgjourney.common.menu.InterfaceMenu;

public class InterfaceScreen extends AbstractContainerScreen<InterfaceMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/interface_gui.png");

    public InterfaceScreen(InterfaceMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 88;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        this.renderEnergy(graphics, x + 52, y + 54);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        this.energyTooltip(graphics, 52, 54, mouseX, mouseY);
    }
    
    protected void renderEnergy(GuiGraphics graphics, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(106 * percentage);
    	graphics.blit(TEXTURE, x, y, 0, 88, actual, 16);
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
    	graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
    
    protected void energyTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 106, 16, (double) mouseX, (double) mouseY))
	    {
    		graphics.renderTooltip(this.font, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + this.menu.getEnergy() + "/" + this.menu.getMaxEnergy() + " FE")).withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
	    }
    }
}
