package net.povstalec.sgjourney.client.screens.dhd;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public abstract class AbstractDHDScreen<T extends AbstractDHDMenu<?>> extends SGJourneyContainerScreen<T>
{
	private final ResourceLocation texture;
	
	public static final Component SYMBOLS_TO_NUMBERS = Component.translatable("tooltip.sgjourney.dhd.symbols_to_numbers");
	public static final Component NUMBERS_TO_SYMBOLS = Component.translatable("tooltip.sgjourney.dhd.numbers_to_symbols");
	
	public AbstractDHDScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture)
	{
        super(menu, playerInventory, title);
        this.imageWidth = 192;
        this.imageHeight = 192;
        
        this.texture = texture;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
	{
		GuiComponent.drawCenteredString(poseStack, font, ClientDHDConfig.dhd_symbols_numbers.get() ? SYMBOLS_TO_NUMBERS : NUMBERS_TO_SYMBOLS, imageWidth / 2, imageHeight + 1, 0xFFFFFF);
    }
}
