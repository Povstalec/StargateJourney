package net.povstalec.sgjourney.client.render.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.models.entity.HumanModel;
import net.povstalec.sgjourney.common.entities.Human;

public class HumanRenderer<T extends Human> extends HumanoidMobRenderer<T, HumanModel<T>>
{
	public HumanRenderer(EntityRendererProvider.Context context, HumanModel model, HumanModel innerModel, HumanModel outerModel)
	{
		super(context, model, 0.5F);
		this.addLayer(new HumanoidArmorLayer(this, innerModel, outerModel, context.getModelManager()));
	}
	
	public HumanRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, ModelLayerLocation innerArmorLayer, ModelLayerLocation outerArmorLayer)
	{
		this(context, new HumanModel(context.bakeLayer(layer)), new HumanModel(context.bakeLayer(innerArmorLayer)), new HumanModel(context.bakeLayer(outerArmorLayer)));
	}
	
	public HumanRenderer(EntityRendererProvider.Context context)
	{
		this(context, ModelLayers.PLAYER, ModelLayers.PLAYER_INNER_ARMOR, ModelLayers.PLAYER_OUTER_ARMOR);
	}
	
	@Override
	public ResourceLocation getTextureLocation(T entity)
	{
		return entity.texture();
	}
}
