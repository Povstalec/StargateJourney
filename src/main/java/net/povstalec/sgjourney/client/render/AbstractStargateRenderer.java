package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStargateRenderer
{
	public AbstractStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		
	}
	
	protected void renderCover(PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
	    renderCoverBlock(Blocks.STONE_BRICKS.defaultBlockState(), -2.0D, 0.0D, stack, source, combinedLight, combinedOverlay);
	    renderCoverBlock(Blocks.STONE_BRICKS.defaultBlockState(), -1.0D, 0.0D, stack, source, combinedLight, combinedOverlay);
	    renderCoverBlock(Blocks.STONE_BRICKS.defaultBlockState(), 0.0D, 0.0D, stack, source, combinedLight, combinedOverlay);
	    renderCoverBlock(Blocks.STONE_BRICKS.defaultBlockState(), 1.0D, 0.0D, stack, source, combinedLight, combinedOverlay);
	    renderCoverBlock(Blocks.STONE_BRICKS.defaultBlockState(), 2.0D, 0.0D, stack, source, combinedLight, combinedOverlay);
	}
	
	protected void renderCoverBlock(BlockState state, double x, double y, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(x, y, 0.0D);
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		dispatcher.renderSingleBlock(state, stack, source, combinedLight, combinedOverlay, ModelData.EMPTY, null);
		stack.popPose();
	}
}
