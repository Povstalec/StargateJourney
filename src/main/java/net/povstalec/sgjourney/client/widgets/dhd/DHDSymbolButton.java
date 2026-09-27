package net.povstalec.sgjourney.client.widgets.dhd;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import org.jetbrains.annotations.NotNull;

public abstract class DHDSymbolButton<M extends IDHDMenu> extends DHDButton
{
	protected M menu;
	protected ResourceLocation overlay;
	
	protected final int symbol;
	
	protected int textureX = 0;
	protected int textureY = 0;
	
	protected final ColorUtil.RGBA hoverColor;
	protected final ColorUtil.RGBA disengagedColor;
	protected final ColorUtil.RGBA engagedColor;
	
	protected boolean isRemapped = false;
	
    public DHDSymbolButton(int x, int y, int width, int height, M menu, int symbol, ResourceLocation widgets, ResourceLocation overlay,
						   ColorUtil.RGBA hoverColor, ColorUtil.RGBA disengagedColor, ColorUtil.RGBA engagedColor, Button.OnPress onPress)
	{
		super(widgets, x, y, width, height, Component.empty(), onPress);
		
		this.menu = menu;
		this.overlay = overlay;
		
		this.symbol = symbol;
		
		this.hoverColor = hoverColor;
		this.disengagedColor = disengagedColor;
		this.engagedColor = engagedColor;
	}
	
	protected void updateRemapping()
	{
		if(this.menu.isSymbolRemapped(getSymbol()) != isRemapped)
		{
			isRemapped = this.menu.isSymbolRemapped(getSymbol());
			
			if(isRemapped)
				setTooltip(remappedSymbolComponent());
			else
				setTooltip(symbolComponent());
		}
	}
	
	public int getSymbol()
	{
		return this.symbol;
	}
	
	public Component symbolComponent()
	{
		return Component.literal(Integer.toString(getSymbol())).withStyle(ChatFormatting.WHITE);
	}
	
	public Component remappedSymbolComponent()
	{
		return Component.literal(menu.getRemappedOriginalSymbol(getSymbol()) + " -> ").withStyle(ChatFormatting.DARK_GRAY).append(symbolComponent());
	}
	
	public boolean isEngaged()
	{
		return this.menu.isSymbolEngaged(getSymbol());
	}
	
	public void renderPointOfOrigin(Matrix4f matrix4f, float xCenter, float yCenter, float xSize, float ySize, ClientPointOfOrigin pointOfOrigin, ColorUtil.RGBA rgba)
	{
		float xStart = xCenter - (xSize / 2F);
		float yStart = yCenter - (ySize / 2F);
		float xEnd = xCenter + (xSize / 2F);
		float yEnd = yCenter + (ySize / 2F);
		
		ClientUtil.renderPointOfOrigin(matrix4f, xStart, yStart, xEnd, yEnd, pointOfOrigin, rgba);
	}
	
	public void renderSymbol(Matrix4f matrix4f, float xCenter, float yCenter, float xSize, float ySize, ClientSymbols symbols, int symbol, ColorUtil.RGBA rgba)
	{
		float xStart = xCenter - (xSize / 2F);
		float yStart = yCenter - (ySize / 2F);
		float xEnd = xCenter + (xSize / 2F);
		float yEnd = yCenter + (ySize / 2F);
		
		ClientUtil.renderSymbol(matrix4f, xStart, yStart, xEnd, yEnd, symbols, symbol, rgba);
	}
	
	public abstract void renderSymbol(PoseStack poseStack);
	
	public void renderNumber(PoseStack poseStack, Minecraft minecraft)
	{
		Font font = minecraft.font;
		int j = getFGColor();
		drawCenteredString(poseStack, font, symbolComponent(getSymbol()), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(poseStack, this.x, this.y, textureX, textureY, this.width, this.height);
		
		if(isEngaged())
		{
			RenderSystem.setShaderTexture(0, overlay);
			RenderSystem.setShaderColor(engagedColor.red(), engagedColor.green(), engagedColor.blue(), engagedColor.alpha());
			this.blit(poseStack, this.x, this.y, textureX, textureY, this.width, this.height);
		}
		else if(this.isHoveredOrFocused())
		{
			RenderSystem.setShaderTexture(0, overlay);
			RenderSystem.setShaderColor(hoverColor.red(), hoverColor.green(), hoverColor.blue(), hoverColor.alpha());
			this.blit(poseStack, this.x, this.y, textureX, textureY, this.width, this.height);
		}
		
		this.renderBg(poseStack, minecraft, mouseX, mouseY);
		
		if(ClientDHDConfig.dhd_symbols_numbers.get() == SGJourneyContainerScreen.isShiftDown())
			renderNumber(poseStack, minecraft);
		else
			renderSymbol(poseStack);
		
		Screen screen = minecraft.screen;
		if(screen != null && isHovered)
			screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
	}
	
    private static Component symbolComponent(int index)
    {
    	return Component.literal(String.valueOf(index));
    }
    
}
