package net.povstalec.sgjourney.client.render.block_entity.zpm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMHubEntity;
import org.jetbrains.annotations.NotNull;

public class ZPMHubRenderer extends AbstractZPMHolderRenderer<ZPMHubEntity>
{
	private final BlockPos.MutableBlockPos abovePos = new BlockPos.MutableBlockPos();
	
	public ZPMHubRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(@NotNull ZPMHubEntity zpmHub, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.75, 0.5);
		
		abovePos.setWithOffset(zpmHub.getBlockPos(), Direction.UP);
		int combinedLightAbove = LevelRenderer.getLightColor(zpmHub.getLevel(), abovePos);
		renderZPM(zpmHub.getHeldItemStack(), stack, source, combinedLightAbove, combinedOverlay);
		
		stack.popPose();
	}
}
