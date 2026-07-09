package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.entity.AbydosLizardModel;
import net.povstalec.sgjourney.client.models.entity.GoauldModel;
import net.povstalec.sgjourney.common.entities.AbydosLizard;
import net.povstalec.sgjourney.common.entities.Goauld;

public class AbydosLizardRenderer extends MobRenderer<AbydosLizard, AbydosLizardModel>
{
	private static final ResourceLocation ABYDOS_LIZARD_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/abydos_lizard.png");
	
	public AbydosLizardRenderer(EntityRendererProvider.Context context)
	{
		super(context, new AbydosLizardModel(context.bakeLayer(Layers.GOAULD)), 0.3F);
	}
	
	@Override
	protected float getFlipDegrees(AbydosLizard abydosLizard) {
		return 180.0F;
	}
	   
	@Override
	public ResourceLocation getTextureLocation(AbydosLizard abydosLizard)
	{
		return ABYDOS_LIZARD_LOCATION;
	}
}
