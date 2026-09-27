package net.povstalec.sgjourney.client.render.block_entity.zpm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMHubEntity;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMPlugEntity;
import org.jetbrains.annotations.NotNull;

public class ZPMPlugRenderer extends AbstractZPMHolderRenderer<ZPMPlugEntity>
{
	public ZPMPlugRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(@NotNull ZPMPlugEntity zpmPlug, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.75, 0.5);
		
		renderZPM(zpmPlug.getHeldItemStack(), stack, source, combinedLight, combinedOverlay);
		
		stack.popPose();
	}
}
