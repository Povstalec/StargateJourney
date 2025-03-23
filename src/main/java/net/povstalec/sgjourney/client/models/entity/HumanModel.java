package net.povstalec.sgjourney.client.models.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.povstalec.sgjourney.common.entities.Human;

public class HumanModel<T extends Human> extends HumanoidModel<T>
{
	public HumanModel(ModelPart root)
	{
		super(root);
	}
}
