package net.povstalec.sgjourney.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.povstalec.sgjourney.client.models.JackalArmorModel;

public class JackalArmorRenderProperties implements IClientItemExtensions
{
	public static final JackalArmorRenderProperties INSTANCE = new JackalArmorRenderProperties();

    private JackalArmorRenderProperties() 
    {
    	
    }

    @Override
    public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> defaultModel)
    {
        return JackalArmorModel.INSTANCE;
    }
}
