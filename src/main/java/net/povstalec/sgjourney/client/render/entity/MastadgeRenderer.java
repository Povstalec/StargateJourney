package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.entity.GoauldModel;
import net.povstalec.sgjourney.client.models.entity.MastadgeModel;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Mastadge;

public class MastadgeRenderer extends MobRenderer<Mastadge, MastadgeModel>
{
	private static final ResourceLocation MASTADGE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/mastadge.png");
	
	public MastadgeRenderer(EntityRendererProvider.Context context)
	{
		super(context, new MastadgeModel(context.bakeLayer(Layers.MASTADGE)), 0.3F);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Mastadge mastadge)
	{
		return MASTADGE_LOCATION;
	}
}
