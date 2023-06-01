package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.symbols.SandstoneSymbolBlockEntity;

public class SandstoneSymbolRenderer extends SymbolBlockRenderer implements BlockEntityRenderer<SandstoneSymbolBlockEntity>
{
	private static final ResourceLocation ERROR = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public SandstoneSymbolRenderer(Context context)
	{
		super(context);
	}

	@Override
	public void render(SandstoneSymbolBlockEntity symbol, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.translate(0.5D, 0.5D, 0.5D);
		
		symbol_part.xScale = 1.01F;
		symbol_part.yScale = 1.01F;
		symbol_part.zScale = 1.01F;
		
		VertexConsumer symbol_texture;
		
		if(getPointOfOrigin(symbol) != null)
			symbol_texture = source.getBuffer(RenderType.entityNoOutline(getPointOfOrigin(symbol).texture()));
		else
			symbol_texture = source.getBuffer(RenderType.entityNoOutline(ERROR));
		
		symbol_part.render(stack, symbol_texture, combinedLight, combinedOverlay, 198.0F/255.0F, 174.0F/255.0F, 113.0F/255.0F, 1.0F);
	}
	
}
