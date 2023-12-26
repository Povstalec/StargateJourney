package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.client.models.AbstractStargateModel;
import net.povstalec.sgjourney.client.models.ShieldModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public abstract class AbstractStargateRenderer
{
	protected final WormholeModel wormholeModel;
	protected final ShieldModel shieldModel;
	
	public AbstractStargateRenderer(BlockEntityRendererProvider.Context context, int red, int green, int blue, int alpha, float maxDefaultDistortion)
	{
		this.shieldModel = new ShieldModel();
		this.wormholeModel = new WormholeModel(red, green, blue, alpha, maxDefaultDistortion);
	}
	
	protected void renderWormhole(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, @SuppressWarnings("rawtypes") @Nullable AbstractStargateModel model, int combinedLight, int combinedOverlay)
	{
		Optional<Stargate.RGBA> rgba = Optional.empty();
		
		if(model != null)
		{
			Optional<StargateVariant> variantOptional = AbstractStargateModel.getVariant(stargate);

			if(variantOptional.isPresent())
			{
				StargateVariant variant = variantOptional.get();
				if(model.canUseVariant(variant))
					rgba = variant.getEventHorizonRGBA();
			}
		}
		
		if(stargate.isConnected())
	    	this.wormholeModel.renderEventHorizon(stargate, stack, source, rgba, combinedLight, combinedOverlay);
	}
	
	protected void renderCover(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft:red_sand"));
		
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.LEFT2, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.LEFT, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.BASE, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.RIGHT, stack, source, combinedOverlay);
	    renderCoverBlock(stargate, block.defaultBlockState(), StargatePart.RIGHT2, stack, source, combinedOverlay);
	}
	
	protected void renderCoverBlock(AbstractStargateEntity stargate, BlockState state, StargatePart part, PoseStack stack, MultiBufferSource source, int combinedOverlay)
	{
		Level level = stargate.getLevel();
		Direction direction = stargate.getDirection();
		Orientation orientation = stargate.getOrientation();
		
		if(direction != null && orientation != null)
		{
			Vec3 relativeBlockPos = part.getRelativeRingPos(stargate.getBlockPos(), direction, orientation);
			BlockPos absolutePos = part.getRingPos(stargate.getBlockPos(), stargate.getDirection(), stargate.getOrientation());
			
			stack.pushPose();
			
			stack.translate(relativeBlockPos.x(), relativeBlockPos.y(), relativeBlockPos.z());
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
			dispatcher.renderSingleBlock(state, stack, source, LevelRenderer.getLightColor(level, absolutePos), combinedOverlay, ModelData.EMPTY, null);
			
			stack.popPose();
		}
	}
}
