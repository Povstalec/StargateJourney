package net.povstalec.sgjourney.client.screens.dhd;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.client.screens.SGJourneyMenuScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen<T extends AbstractDHDMenu<?>> extends SGJourneyMenuScreen<T>
{
	private final ResourceLocation texture;
	
	public static final Component SYMBOLS_TO_NUMBERS = Component.translatable("tooltip.sgjourney.dhd.symbols_to_numbers");
	public static final Component NUMBERS_TO_SYMBOLS = Component.translatable("tooltip.sgjourney.dhd.numbers_to_symbols");
	
	public AbstractDHDScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, title);
        this.imageWidth = 192;
        this.imageHeight = 192;
        
        this.texture = texture;
    }
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(poseStack);
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		
		this.blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		RenderSystem.disableDepthTest();
        super.render(poseStack, mouseX, mouseY, delta);
		
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((float) leftPos, (float) topPos, 0.0F);
		RenderSystem.applyModelViewMatrix();
		
		GuiComponent.drawCenteredString(poseStack, font, ClientDHDConfig.dhd_symbols_numbers.get() ? SYMBOLS_TO_NUMBERS : NUMBERS_TO_SYMBOLS, imageWidth / 2, imageHeight + 1, 0xFFFFFF);
		
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
    }
}
