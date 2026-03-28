package net.povstalec.sgjourney.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.client.screens.SGJourneyContainerScreen;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.joml.Matrix4f;

public abstract class DHDSymbolButton extends DHDButton
{
	protected AbstractDHDMenu<?> menu;
	protected ResourceLocation widgets;
	protected ResourceLocation overlay;
	
	protected final int symbol;
	
	protected int textureX = 0;
	protected int textureY = 0;
	
	protected final ColorUtil.RGBA hoverColor;
	protected final ColorUtil.RGBA disengagedColor;
	protected final ColorUtil.RGBA engagedColor;
	
	protected boolean isRemapped = false;
	
    public DHDSymbolButton(int x, int y, int width, int height, AbstractDHDMenu<?> menu, int symbol, ResourceLocation widgets, ResourceLocation overlay,
						   ColorUtil.RGBA hoverColor, ColorUtil.RGBA disengagedColor, ColorUtil.RGBA engagedColor)
	{
		super(x, y, width, height, Component.empty(), (button) -> {});
		
		this.menu = menu;
		this.widgets = widgets;
		this.overlay = overlay;
		
		this.symbol = symbol;
		
		this.hoverColor = hoverColor;
		this.disengagedColor = disengagedColor;
		this.engagedColor = engagedColor;
		
		setTooltip(Tooltip.create(symbolComponent()));
	}
	
	protected void updateRemapping()
	{
		if(this.menu.isSymbolRemapped(getSymbol()) != isRemapped)
		{
			isRemapped = this.menu.isSymbolRemapped(getSymbol());
			
			if(isRemapped)
				setTooltip(Tooltip.create(remappedSymbolComponent()));
			else
				setTooltip(Tooltip.create(symbolComponent()));
		}
	}
	
	@Override
	public void onPress()
	{
		super.onPress();
		menu.encodeSymbol(getSymbol());
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
	
	public void renderPointOfOrigin(Matrix4f matrix4f, float xCenter, float yCenter, float xSize, float ySize, PointOfOrigin pointOfOrigin, ColorUtil.RGBA rgba)
	{
		float xStart = xCenter - (xSize / 2F);
		float yStart = yCenter - (ySize / 2F);
		float xEnd = xCenter + (xSize / 2F);
		float yEnd = yCenter + (ySize / 2F);
		
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha());
		RenderSystem.setShaderTexture(0, pointOfOrigin.texture());
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, xStart, yStart, 0F).uv(0F, 0F).endVertex();
		bufferbuilder.vertex(matrix4f, xStart, yEnd, 0F).uv(0F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yEnd, 0F).uv(1F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yStart, 0F).uv(1F, 0F).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}
	
	public void renderSymbol(Matrix4f matrix4f, float xCenter, float yCenter, float xSize, float ySize, Symbols symbols, int symbol, ColorUtil.RGBA rgba)
	{
		float xStart = xCenter - (xSize / 2F);
		float yStart = yCenter - (ySize / 2F);
		float xEnd = xCenter + (xSize / 2F);
		float yEnd = yCenter + (ySize / 2F);
		
		float symbolOffset = symbols.getTextureOffset(symbol);
		float symbolsSize = symbols.getSize();
		float symbolHalfSpace = 1 / (2 * symbolsSize);
		
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha());
		RenderSystem.setShaderTexture(0, symbols.getSymbolTexture());
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, xStart, yStart, 0F).uv(symbolOffset - symbolHalfSpace, 0F).endVertex();
		bufferbuilder.vertex(matrix4f, xStart, yEnd, 0F).uv(symbolOffset - symbolHalfSpace, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yEnd, 0F).uv(symbolOffset + symbolHalfSpace, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yStart, 0F).uv(symbolOffset + symbolHalfSpace, 0F).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}
	
	public abstract void renderSymbol(PoseStack poseStack);
	
	public void renderNumber(PoseStack poseStack, Minecraft minecraft)
	{
		Font font = minecraft.font;
		int j = getFGColor();
		drawCenteredString(poseStack, font, symbolComponent(getSymbol()), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
	}
	
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, widgets);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(poseStack, this.getX(), this.getY(), textureX, textureY, this.width, this.height);
		
		if(isEngaged())
		{
			RenderSystem.setShaderTexture(0, overlay);
			RenderSystem.setShaderColor(engagedColor.red(), engagedColor.green(), engagedColor.blue(), engagedColor.alpha());
			this.blit(poseStack, this.getX(), this.getY(), textureX, textureY, this.width, this.height);
		}
		else if(this.isHoveredOrFocused())
		{
			RenderSystem.setShaderTexture(0, overlay);
			RenderSystem.setShaderColor(hoverColor.red(), hoverColor.green(), hoverColor.blue(), hoverColor.alpha());
			this.blit(poseStack, this.getX(), this.getY(), textureX, textureY, this.width, this.height);
		}
		
		this.renderBg(poseStack, minecraft, mouseX, mouseY);
		
		if(ClientDHDConfig.dhd_symbols_numbers.get() == SGJourneyContainerScreen.isShiftDown())
			renderNumber(poseStack, minecraft);
		else
			renderSymbol(poseStack);
	}
	
    private static Component symbolComponent(int index)
    {
    	return Component.literal(String.valueOf(index));
    }
    
}
