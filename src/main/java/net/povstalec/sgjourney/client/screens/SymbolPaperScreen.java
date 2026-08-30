package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.items.SymbolPaperItem;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SymbolPaperScreen extends Screen
{
	public static final ResourceLocation TEXTURE = StargateJourney.sgjourneyLocation("textures/gui/symbol_paper_gui.png");
	
	private static final int IMAGE_WIDTH = 192;
	private static final int IMAGE_HEIGHT = 192;
	
	private static final int POINT_OF_ORIGIN_SIZE = 64;
	private static final int SYMBOL_SIZE = 16;
	
	private static final ColorUtil.RGBA RGBA = new ColorUtil.RGBA(0, 0, 0);
	
	@Nullable
	private final ResourceKey<PointOfOrigin> pointOfOrigin;
	@Nullable
	private final ResourceKey<Symbols> symbols;
	
	public SymbolPaperScreen(InteractionHand interactionHand)
	{
		super(Component.empty());
		
		ItemStack stack = Minecraft.getInstance().player.getItemInHand(interactionHand);
		
		pointOfOrigin = SymbolPaperItem.getPointOfOrigin(stack);
		symbols = SymbolPaperItem.getSymbols(stack);
	}
	
	@Override
	public boolean isPauseScreen()
	{
		return false;
	}
	
	public void renderPointOfOrigin(PoseStack stack, ClientPointOfOrigin pointOfOrigin)
	{
		float xStart = (width - POINT_OF_ORIGIN_SIZE) / 2F;
		float yStart = (height - POINT_OF_ORIGIN_SIZE) / 2F;
		float xEnd = xStart + POINT_OF_ORIGIN_SIZE;
		float yEnd = yStart + POINT_OF_ORIGIN_SIZE;
		
		ClientUtil.renderPointOfOrigin(stack.last().pose(), xStart, yStart, xEnd, yEnd, pointOfOrigin, RGBA);
	}
	
	public void renderSymbols(PoseStack stack, ClientSymbols symbols)
	{
		float xStart = -SYMBOL_SIZE / 2F;
		float yStart = -SYMBOL_SIZE / 2F;
		float xEnd = xStart + SYMBOL_SIZE;
		float yEnd = yStart + SYMBOL_SIZE;
		
		float angle = 360F / symbols.size();
		
		stack.pushPose();
		stack.translate(width / 2D, height / 2D, 0);
		
		for(int i = 0; i < symbols.size(); i++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(i * -angle));
			stack.translate(0, -88, 0);
			ClientUtil.renderSymbol(stack.last().pose(), xStart, yStart, xEnd, yEnd, symbols, i + 1, RGBA);
			stack.popPose();
		}
		
		stack.popPose();
	}
	
	@Override
	public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float delta)
	{
		renderBackground(stack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - IMAGE_WIDTH) / 2;
		int y = (height - IMAGE_HEIGHT) / 2;
		blit(stack, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		super.render(stack, mouseX, mouseY, delta);
		
		if(pointOfOrigin != null)
			renderPointOfOrigin(stack, ClientPointOfOrigin.getPointOfOrigin(pointOfOrigin));
		if(symbols != null)
			renderSymbols(stack, ClientSymbols.getSymbols(symbols));
	}
}
