package net.povstalec.sgjourney.client.render;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

//CREDIT: https://github.com/mezz/JustEnoughItems by mezz
//Under MIT-License: https://github.com/mezz/JustEnoughItems/blob/1.19/LICENSE.txt
//Includes major rewrites and methods from:
//https://github.com/mezz/JustEnoughItems/blob/1.20/Library/src/main/java/mezz/jei/library/render/FluidTankRenderer
public class FluidTankRenderer
{
	public static final Logger LOGGER = LogUtils.getLogger();
	
	private static final NumberFormat nf = NumberFormat.getIntegerInstance();
	private static final int TEXTURE_SIZE = 16;
	private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible
	
	 private final long capacity;
	private final TooltipMode tooltipMode;
	private final int width;
	private final int height;

	enum TooltipMode
	{
		SHOW_AMOUNT,
		SHOW_AMOUNT_AND_CAPACITY,
		ITEM_LIST
	}

	public FluidTankRenderer(long capacity, boolean showCapacity, int width, int height)
	{
		this(capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height);
	}

	private FluidTankRenderer(long capacity, TooltipMode tooltipMode, int width, int height)
	{
     Preconditions.checkArgument(capacity > 0, "capacity must be > 0");
     Preconditions.checkArgument(width > 0, "width must be > 0");
     Preconditions.checkArgument(height > 0, "height must be > 0");

     this.capacity = capacity;
     this.tooltipMode = tooltipMode;
     this.width = width;
     this.height = height;
 }

 public void render(GuiGraphics graphics, int x, int y, FluidStack fluidStack)
 {
     RenderSystem.enableBlend();
     
     PoseStack poseStack = graphics.pose();
     poseStack.pushPose();

     poseStack.translate(x, y, 0);
     drawFluid(graphics, width, height, fluidStack);
     
     poseStack.popPose();
     RenderSystem.setShaderColor(1, 1, 1, 1);
     RenderSystem.disableBlend();
 }

 private void drawFluid(GuiGraphics graphics, final int width, final int height, FluidStack fluidStack)
 {
     Fluid fluid = fluidStack.getFluid();
     if (fluid.isSame(Fluids.EMPTY)) {
         return;
     }

     TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);
     int fluidColor = getColorTint(fluidStack);

     long amount = fluidStack.getAmount();
     long scaledAmount = (amount * height) / capacity;

     if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
         scaledAmount = MIN_FLUID_HEIGHT;
     }
     if (scaledAmount > height) {
         scaledAmount = height;
     }

     drawTiledSprite(graphics, width, height, fluidColor, scaledAmount, fluidStillSprite);
 }

 private TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack)
 {
     Fluid fluid = fluidStack.getFluid();
     IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
     ResourceLocation fluidStill = renderProperties.getStillTexture(fluidStack);

     Minecraft minecraft = Minecraft.getInstance();
     return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
 }

 private int getColorTint(FluidStack ingredient) {
     Fluid fluid = ingredient.getFluid();
     IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
     return renderProperties.getTintColor(ingredient);
 }

 private static void drawTiledSprite(GuiGraphics graphics, final int tiledWidth, final int tiledHeight, int color, long scaledAmount, TextureAtlasSprite sprite)
 {
     RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
     Matrix4f matrix = graphics.pose().last().pose();
     setGLColorFromInt(color);

     final int xTileCount = tiledWidth / TEXTURE_SIZE;
     final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
     final long yTileCount = scaledAmount / TEXTURE_SIZE;
     final long yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

     final int yStart = tiledHeight;

     for (int xTile = 0; xTile <= xTileCount; xTile++) {
         for (int yTile = 0; yTile <= yTileCount; yTile++) {
             int width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
             long height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
             int x = (xTile * TEXTURE_SIZE);
             int y = yStart - ((yTile + 1) * TEXTURE_SIZE);
             if (width > 0 && height > 0) {
                 long maskTop = TEXTURE_SIZE - height;
                 int maskRight = TEXTURE_SIZE - width;

                 drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
             }
         }
     }
 }

 private static void setGLColorFromInt(int color)
 {
     float red = (color >> 16 & 0xFF) / 255.0F;
     float green = (color >> 8 & 0xFF) / 255.0F;
     float blue = (color & 0xFF) / 255.0F;
     float alpha = ((color >> 24) & 0xFF) / 255F;

     RenderSystem.setShaderColor(red, green, blue, alpha);
 }

 private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, long maskTop, long maskRight, float zLevel)
 {
     float uMin = textureSprite.getU0();
     float uMax = textureSprite.getU1();
     float vMin = textureSprite.getV0();
     float vMax = textureSprite.getV1();
     uMax = uMax - (maskRight / 16F * (uMax - uMin));
     vMax = vMax - (maskTop / 16F * (vMax - vMin));

     RenderSystem.setShader(GameRenderer::getPositionTexShader);

     Tesselator tesselator = Tesselator.getInstance();
     BufferBuilder bufferBuilder = tesselator.getBuilder();
     bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
     bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
     bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
     bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
     bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
     tesselator.end();
 }

 public List<Component> getTooltip(FluidStack fluidStack, TooltipFlag tooltipFlag)
 {
     List<Component> tooltip = new ArrayList<>();

     Fluid fluidType = fluidStack.getFluid();
     try {
         if (fluidType.isSame(Fluids.EMPTY)) {
             return tooltip;
         }

         Component displayName = fluidStack.getDisplayName();
         tooltip.add(displayName);

         long amount = fluidStack.getAmount();
         long milliBuckets = (amount * 1000) / FluidType.BUCKET_VOLUME;

         if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
             MutableComponent amountString = Component.translatable("tutorialmod.tooltip.liquid.amount.with.capacity", nf.format(milliBuckets), nf.format(capacity));
             tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
         } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
             MutableComponent amountString = Component.translatable("tutorialmod.tooltip.liquid.amount", nf.format(milliBuckets));
             tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
         }
     } catch (RuntimeException e) {
         LOGGER.error("Failed to get tooltip for fluid: " + e);
     }

     return tooltip;
 }

 public int getWidth()
 {
     return width;
 }

 public int getHeight()
 {
     return height;
 }
}
