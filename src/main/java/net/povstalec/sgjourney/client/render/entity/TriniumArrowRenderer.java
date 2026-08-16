package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.TriniumArrow;

public class TriniumArrowRenderer extends ArrowRenderer<TriniumArrow>
{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/trinium_arrow.png");
	
	public TriniumArrowRenderer(EntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public ResourceLocation getTextureLocation(TriniumArrow triniumArrow)
	{
		return TEXTURE_LOCATION;
	}
}
