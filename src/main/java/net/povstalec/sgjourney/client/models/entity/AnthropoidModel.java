package net.povstalec.sgjourney.client.models.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.povstalec.sgjourney.common.entities.Anthropoid;

public class AnthropoidModel<T extends Anthropoid> extends HumanoidModel<T>
{
	public AnthropoidModel(ModelPart root)
	{
		super(root);
	}
}
