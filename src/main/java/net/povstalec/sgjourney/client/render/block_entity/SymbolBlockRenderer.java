package net.povstalec.sgjourney.client.render.block_entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.blocks.SymbolBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class SymbolBlockRenderer
{
	private static final ResourceLocation ERROR = StargateJourney.sgjourneyLocation("textures/symbols/error.png");

	protected static final float SYMBOL_OFFSET = 0.51F;
	protected static final float SYMBOL_SIZE = 1;
	protected static final float SYMBOL_START = -SYMBOL_SIZE / 2;
	protected static final float SYMBOL_END = SYMBOL_SIZE / 2;
	
	protected int red;
	protected int green;
	protected int blue;
	
	public SymbolBlockRenderer(BlockEntityRendererProvider.Context context) {}
	
	protected PointOfOrigin getPointOfOrigin(SymbolBlockEntity symbolBlock)
	{
		return ClientUtil.getPointOfOrigin(symbolBlock.pointOfOrigin);
	}
	
	protected Symbols getSymbols(SymbolBlockEntity symbolBlock)
	{
		return ClientUtil.getSymbols(symbolBlock.symbols);
	}
	
	protected void renderSymbol(VertexConsumer consumer, Matrix4f matrix4, PoseStack.Pose pose, int light, float textureSize, float textureOffset)
	{
		float textureHalf = 1F / textureSize / 2;
		//TOP LEFT
		consumer.addVertex(matrix4, SYMBOL_START, SYMBOL_END, SYMBOL_OFFSET).setColor((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).setUv(textureOffset - textureHalf, 0)
		.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light >> 16).setNormal(pose, 0.0F, 0.0F, 1.0F);
		//BOTTOM LEFT
		consumer.addVertex(matrix4, SYMBOL_START, SYMBOL_START, SYMBOL_OFFSET).setColor((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).setUv(textureOffset - textureHalf, 1)
		.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light >> 16).setNormal(pose, 0.0F, 0.0F, 1.0F);
		//BOTTOM RIGHT
		consumer.addVertex(matrix4, SYMBOL_END, SYMBOL_START, SYMBOL_OFFSET).setColor((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).setUv(textureOffset + textureHalf, 1)
		.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light >> 16).setNormal(pose, 0.0F, 0.0F, 1.0F);
		//TOP RIGHT
		consumer.addVertex(matrix4, SYMBOL_END, SYMBOL_END, SYMBOL_OFFSET).setColor((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).setUv(textureOffset + textureHalf, 0)
		.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light >> 16).setNormal(pose, 0.0F, 0.0F, 1.0F);
	}
	
	protected void renderSymbolBlock(SymbolBlockEntity symbolBlock, PoseStack stack, MultiBufferSource source, int light)
	{
		BlockState blockstate = symbolBlock.getBlockState();
		float facing = blockstate.getValue(SymbolBlock.FACING).toYRot();
		Direction direction = blockstate.getValue(SymbolBlock.FACING);
		Orientation orientation = blockstate.getValue(SymbolBlock.ORIENTATION);
		BlockPos pos = symbolBlock.getBlockPos().relative(Orientation.getEffectiveDirection(direction, orientation));
		
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));

        if(symbolBlock != null)
        {
        	VertexConsumer consumer;
    		Matrix4f matrix4 = stack.last().pose();
			PoseStack.Pose pose = stack.last();
            light = LevelRenderer.getLightColor(symbolBlock.getLevel(), pos);
    		
        	if(symbolBlock.symbolNumber == 0)
        	{
            	PointOfOrigin pointOfOrigin = getPointOfOrigin(symbolBlock);
            	ResourceLocation texture = pointOfOrigin != null ? pointOfOrigin.texture() : ERROR;
            	
                consumer = source.getBuffer(SGJourneyRenderTypes.symbol(texture));
                renderSymbol(consumer, matrix4, pose, light, 1, 0.5F);
        	}
        	else
        	{
        		Symbols symbols = getSymbols(symbolBlock);
        		
        		if(symbols != null)
            	{
        			ResourceLocation texture = symbols.getSymbolTexture();
                	
                    consumer = source.getBuffer(SGJourneyRenderTypes.symbol(texture));
                    renderSymbol(consumer, matrix4, pose, light, symbols.getSize(), symbols.getTextureOffset(symbolBlock.symbolNumber));
                    
            	}
        	}
        }
        
		stack.popPose();
	}
	
	
	
	public static class Stone extends SymbolBlockRenderer implements BlockEntityRenderer<SymbolBlockEntity.Stone>
	{
		public Stone(Context context)
		{
			super(context);
			this.red = 90;
			this.green = 89;
			this.blue = 90;
		}

		@Override
		public void render(SymbolBlockEntity.Stone symbol, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderSymbolBlock(symbol, stack, source, combinedLight);
		}
		
	}
	
	public static class Sandstone extends SymbolBlockRenderer implements BlockEntityRenderer<SymbolBlockEntity.Sandstone>
	{
		public Sandstone(Context context)
		{
			super(context);
			this.red = 198;
			this.green = 174;
			this.blue = 113;
		}

		@Override
		public void render(SymbolBlockEntity.Sandstone symbol, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderSymbolBlock(symbol, stack, source, combinedLight);
		}
		
	}
	
	public static class RedSandstone extends SymbolBlockRenderer implements BlockEntityRenderer<SymbolBlockEntity.RedSandstone>
	{
		public RedSandstone(Context context)
		{
			super(context);
			this.red = 159;
			this.green = 78;
			this.blue = 11;
		}
		
		@Override
		public void render(SymbolBlockEntity.RedSandstone symbol, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderSymbolBlock(symbol, stack, source, combinedLight);
		}
		
	}
}
