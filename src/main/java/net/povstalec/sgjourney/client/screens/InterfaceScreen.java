package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
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
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
        
        this.renderEnergy(pPoseStack, x + 52, y + 54);
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
        
        this.energyTooltip(pPoseStack, 52, 54, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) 
	{
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }
    
    protected void renderEnergy(PoseStack matrixStack, int x, int y)
    {
    	float percentage = (float) this.menu.getEnergy() / this.menu.getMaxEnergy();
    	int actual = Math.round(106 * percentage);
    	this.blit(matrixStack, x, y, 0, 88, actual, 16);
    }
    
    protected void energyTooltip(PoseStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
    	if(this.isHovering(x, y, 106, 16, (double) mouseX, (double) mouseY))
	    {
	    	renderTooltip(matrixStack, Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(this.menu.getEnergy(), this.menu.getMaxEnergy()))).withStyle(ChatFormatting.DARK_RED), mouseX, mouseY);
	    }
    }
}
