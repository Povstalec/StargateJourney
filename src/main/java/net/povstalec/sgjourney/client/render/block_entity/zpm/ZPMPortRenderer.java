package net.povstalec.sgjourney.client.render.block_entity.zpm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMPortEntity;
import org.jetbrains.annotations.NotNull;

public class ZPMPortRenderer extends AbstractZPMHolderRenderer<ZPMPortEntity>
{
	public ZPMPortRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public void render(@NotNull ZPMPortEntity zpmPort, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.8125, 0.5);
		
		renderZPM(zpmPort.getHeldItemStack(), stack, source, combinedLight, combinedOverlay);
		
		stack.popPose();
	}
}
