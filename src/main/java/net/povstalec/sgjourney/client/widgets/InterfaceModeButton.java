package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.screens.InterfaceScreen;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;

public class InterfaceModeButton extends SGJourneyButton
{
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/interface_widgets.png");
	
	protected InterfaceScreen screen;
	
	public InterfaceModeButton(int x, int y, Component component, Component tooltip, OnPress press, InterfaceScreen screen)
	{
		super(WIDGETS_LOCATION, x, y, 16, 16, component, tooltip, press);
		
		this.screen = screen;
	}

    public InterfaceModeButton(int x, int y, Component component, OnPress press, InterfaceScreen screen)
	{
		this(x, y, component, component, press, screen);
	}
	
	protected int getXImage(InterfaceMode mode)
	{
		return mode.ordinal();
	}
	
	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick)
	{
		this.isHovered = isHovered(mouseX, mouseY);
		
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		int x = this.getXImage(this.screen.getMode());
		int y = this.getYImage(this.isHoveredOrFocused());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		graphics.blit(texture, this.getX(), this.getY(), xOffset + x * this.width, yOffset + y * this.height, this.width, this.height);
		graphics.blit(texture, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + y * 20, this.width / 2, this.height);
		int j = getFGColor();
		this.renderString(graphics, minecraft.font, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
}
