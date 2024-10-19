package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class CartoucheRenderer
{
	protected static final float MAX_WIDTH = 10F / 16;
	protected static final float MAX_HEIGHT = 26F / 16;
	protected static final float SYMBOL_OFFSET = 0.51F;
	
	protected int red;
	protected int green;
	protected int blue;
	
	public CartoucheRenderer(BlockEntityRendererProvider.Context context) {}
	
	protected Symbols getSymbols(CartoucheEntity cartouche)
	{
		return ClientUtil.getSymbols(cartouche.getSymbols());
	}
	
	protected void renderSymbol(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light,
			float size, float x, float y, float z, float textureSize, float textureOffset)
	{
		float halfsize = size / 2;
		float textureHalf = 1F / textureSize / 2;
		//TOP LEFT
		consumer.vertex(matrix4, x - halfsize, y + halfsize, z).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(textureOffset - textureHalf, 0)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//BOTTOM LEFT
		consumer.vertex(matrix4, x - halfsize, y - halfsize, z).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(textureOffset - textureHalf, 1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//BOTTOM RIGHT
		consumer.vertex(matrix4, x + halfsize, y - halfsize, z).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(textureOffset + textureHalf, 1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//TOP RIGHT
		consumer.vertex(matrix4, x + halfsize, y + halfsize, z).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(textureOffset + textureHalf, 0)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
	}
	
	protected void renderCartoucheBlock(CartoucheEntity cartouche, PoseStack stack, MultiBufferSource source, int light)
	{
		BlockState blockstate = cartouche.getBlockState();
		
		if(blockstate.getValue(CartoucheBlock.HALF) == DoubleBlockHalf.UPPER)
			return;
		
		Direction direction = blockstate.getValue(CartoucheBlock.FACING);
		float facing = direction.toYRot();
		Orientation orientation = blockstate.getValue(CartoucheBlock.ORIENTATION);
		BlockPos pos = cartouche.getBlockPos().relative(Orientation.getEffectiveDirection(direction, orientation));
		
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
        stack.mulPose(Vector3f.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

        if(cartouche != null)
        {
    		Matrix4f matrix4 = stack.last().pose();
    		Matrix3f matrix3 = stack.last().normal();
        	Symbols symbols = getSymbols(cartouche);
            light = LevelRenderer.getLightColor(cartouche.getLevel(), pos);
        	
        	Address address = cartouche.getAddress();
            
            if(address != null)
            {
            	float symbolSize = MAX_HEIGHT / address.getLength();
                if(symbolSize > MAX_WIDTH)
                	symbolSize = MAX_WIDTH;
                
            	if(symbols != null)
            	{
                	ResourceLocation texture = symbols.getSymbolTexture();
            		
            		for(int i = 0; i < address.getLength(); i++)
                    {
                    	VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.symbol(texture));
                    	
                    	float yStart = 0.5F + symbolSize * address.getLength() / 2;
                    	if(yStart > 0.5F + MAX_HEIGHT / 2)
                    		yStart = 0.5F + MAX_HEIGHT / 2;
                    	
                    	float yPos = yStart - symbolSize / 2 - symbolSize * i;
                    	
                        renderSymbol(consumer, matrix4, matrix3, light, symbolSize, 0, yPos, SYMBOL_OFFSET, symbols.getSize(), symbols.getTextureOffset(address.getSymbol(i)));
                    }
            	}
            }
        }
        
		stack.popPose();
	}
	
	
	
	public static class Stone extends CartoucheRenderer implements BlockEntityRenderer<CartoucheEntity.Stone>
	{
		public Stone(Context context)
		{
			super(context);
			this.red = 90;
			this.green = 89;
			this.blue = 90;
		}

		@Override
		public void render(CartoucheEntity.Stone cartouche, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderCartoucheBlock(cartouche, stack, source, combinedLight);
		}
		
	}
	
	public static class Sandstone extends CartoucheRenderer implements BlockEntityRenderer<CartoucheEntity.Sandstone>
	{
		public Sandstone(Context context)
		{
			super(context);
			this.red = 198;
			this.green = 174;
			this.blue = 113;
		}

		@Override
		public void render(CartoucheEntity.Sandstone cartouche, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderCartoucheBlock(cartouche, stack, source, combinedLight);
		}
		
	}
	
	public static class RedSandstone extends CartoucheRenderer implements BlockEntityRenderer<CartoucheEntity.RedSandstone>
	{
		public RedSandstone(Context context)
		{
			super(context);
			this.red = 159;
			this.green = 78;
			this.blue = 11;
		}
		
		@Override
		public void render(CartoucheEntity.RedSandstone cartouche, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
		{
			renderCartoucheBlock(cartouche, stack, source, combinedLight);
		}
		
	}
}
