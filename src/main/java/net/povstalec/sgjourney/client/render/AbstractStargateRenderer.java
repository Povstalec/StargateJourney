package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateRenderer
{
	public AbstractStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		
	}
	
	protected void renderCover(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:stone_bricks"));
		
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.LEFT2, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.LEFT, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.BASE, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.RIGHT, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.RIGHT2, stack, source, combinedOverlay);
	}
	
	protected void renderCoverBlock(AbstractStargateEntity stargate, BlockState state, StargatePart part, PoseStack stack, MultiBufferSource source, int combinedOverlay)
	{
		Level level = stargate.getLevel();
		Vec3 relativeBlockPos = part.getRelativeRingPos(stargate.getBlockPos(), stargate.getDirection(), stargate.getOrientation());
		BlockPos absolutePos = part.getRingPos(stargate.getBlockPos(), stargate.getDirection(), stargate.getOrientation());
		stack.pushPose();
		stack.translate(relativeBlockPos.x(), relativeBlockPos.y(), relativeBlockPos.z());
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		dispatcher.renderSingleBlock(state, stack, source, LevelRenderer.getLightColor(level, absolutePos), combinedOverlay, ModelData.EMPTY, null);
		stack.popPose();
	}
}
