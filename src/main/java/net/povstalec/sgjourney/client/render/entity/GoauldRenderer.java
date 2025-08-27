package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.entity.GoauldModel;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.Goauld;

public class GoauldRenderer extends MobRenderer<Goauld, GoauldModel<Goauld>>
{
	private static final ResourceLocation LARVA_LOCATION = StargateJourney.sgjourneyLocation("textures/entity/goauld/goauld_larva.png");
	private static final ResourceLocation ADULT_LOCATION = StargateJourney.sgjourneyLocation("textures/entity/goauld/goauld.png");
	
	public GoauldRenderer(EntityRendererProvider.Context context)
	{
		super(context, new GoauldModel<>(context.bakeLayer(Layers.GOAULD)), 0.3F);
	}
	
	@Override
	protected float getFlipDegrees(Goauld goauld) {
		return 180.0F;
	}
	   
	@Override
	public ResourceLocation getTextureLocation(Goauld goauld) 
	{
		if(goauld.isBaby())
			return LARVA_LOCATION;
		else
			return ADULT_LOCATION;
	}
}
