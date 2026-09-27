package net.povstalec.sgjourney.client.render.block_entity.zpm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.zpm.AbstractZPMHolderEntity;
import net.povstalec.sgjourney.common.items.ZeroPointModule;

public abstract class AbstractZPMHolderRenderer<ZPMHolder extends AbstractZPMHolderEntity> implements BlockEntityRenderer<ZPMHolder>
{
	public static final int WORKING_ZPM_LIGHT = 15728880;
	
	protected final ItemRenderer itemRenderer;
	
	public AbstractZPMHolderRenderer(BlockEntityRendererProvider.Context context)
	{
		itemRenderer = context.getItemRenderer();
	}
	
	protected void renderZPM(ItemStack itemStack, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BakedModel bakedmodel = itemRenderer.getModel(itemStack, Minecraft.getInstance().level, null, 0);
		itemRenderer.render(itemStack, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false, stack, source,
			ZeroPointModule.hasEnergy(itemStack) ? WORKING_ZPM_LIGHT : combinedLight, combinedOverlay, bakedmodel);
	}
}
