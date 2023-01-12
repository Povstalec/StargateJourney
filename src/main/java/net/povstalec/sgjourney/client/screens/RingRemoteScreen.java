package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class RingRemoteScreen extends Screen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/ring_remote_gui.png");
	
	protected int imageWidth = 176;
	protected int imageHeight = 167;

    public RingRemoteScreen(Component pTitle)
    {
        super(pTitle);
    }
	
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight + 1);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        renderBackground(stack);
        renderBg(stack, partialTick, mouseX, mouseY);
        super.render(stack, mouseX, mouseY, partialTick);
        
    }
    
    @Override
    public boolean isPauseScreen()
    {
    	return false;
    }
}
