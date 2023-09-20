package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.blocks.SymbolBlock;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;

public abstract class SymbolBlockRenderer
{
	private static final ResourceLocation ERROR = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");

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
		String pointOfOrigin = symbolBlock.symbol;
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return registry.get(new ResourceLocation(pointOfOrigin));
	}
	
	protected void renderSymbol(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light)
	{
		//TOP LEFT
		consumer.vertex(matrix4, SYMBOL_START, SYMBOL_END, SYMBOL_OFFSET).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(0, 0)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//BOTTOM LEFT
		consumer.vertex(matrix4, SYMBOL_START, SYMBOL_START, SYMBOL_OFFSET).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(0, 1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//BOTTOM RIGHT
		consumer.vertex(matrix4, SYMBOL_END, SYMBOL_START, SYMBOL_OFFSET).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(1, 1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		//TOP RIGHT
		consumer.vertex(matrix4, SYMBOL_END, SYMBOL_END, SYMBOL_OFFSET).color((float) red / 255, (float) green / 255, (float) blue / 255, 1.0F).uv(1, 0)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
	}
	
	protected void renderSymbolBlock(SymbolBlockEntity symbol, PoseStack stack, MultiBufferSource source, int light)
	{
		BlockState blockstate = symbol.getBlockState();
		float facing = blockstate.getValue(SymbolBlock.FACING).toYRot();
		Direction direction = blockstate.getValue(SymbolBlock.FACING);
		Orientation orientation = blockstate.getValue(SymbolBlock.ORIENTATION);
		BlockPos pos = symbol.getBlockPos().relative(Orientation.getEffectiveDirection(direction, orientation));
		
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
        stack.mulPose(Vector3f.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

        if(symbol != null)
        {
    		Matrix4f matrix4 = stack.last().pose();
    		Matrix3f matrix3 = stack.last().normal();
        	PointOfOrigin pointOfOrigin = getPointOfOrigin(symbol);
        	ResourceLocation texture = pointOfOrigin != null ? pointOfOrigin.texture() : ERROR;
            light = LevelRenderer.getLightColor(symbol.getLevel(), pos);
        	
            VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.symbol(texture));
            renderSymbol(consumer, matrix4, matrix3, light);
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
}
