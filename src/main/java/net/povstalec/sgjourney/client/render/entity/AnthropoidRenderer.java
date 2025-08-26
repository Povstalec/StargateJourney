package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.models.entity.AnthropoidModel;
import net.povstalec.sgjourney.common.entities.Anthropoid;

public class AnthropoidRenderer<T extends Anthropoid> extends HumanoidMobRenderer<T, AnthropoidModel<T>>
{
	public AnthropoidRenderer(EntityRendererProvider.Context context, AnthropoidModel model, AnthropoidModel innerModel, AnthropoidModel outerModel)
	{
		super(context, model, 0.5F);
		this.addLayer(new HumanoidArmorLayer(this, innerModel, outerModel, context.getModelManager()));
	}
	
	public AnthropoidRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, ModelLayerLocation innerArmorLayer, ModelLayerLocation outerArmorLayer)
	{
		this(context, new AnthropoidModel(context.bakeLayer(layer)), new AnthropoidModel(context.bakeLayer(innerArmorLayer)), new AnthropoidModel(context.bakeLayer(outerArmorLayer)));
	}
	
	public AnthropoidRenderer(EntityRendererProvider.Context context)
	{
		this(context, ModelLayers.PLAYER, ModelLayers.PLAYER_INNER_ARMOR, ModelLayers.PLAYER_OUTER_ARMOR);
	}
	
	@Override
	public ResourceLocation getTextureLocation(T entity)
	{
		return entity.texture();
	}
}
