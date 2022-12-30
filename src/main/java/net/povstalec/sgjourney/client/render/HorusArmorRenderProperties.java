package net.povstalec.sgjourney.client.render;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.povstalec.sgjourney.client.models.HorusArmorModel;

public class HorusArmorRenderProperties implements IClientItemExtensions
{
	public static final HorusArmorRenderProperties INSTANCE = new HorusArmorRenderProperties();

    private HorusArmorRenderProperties() 
    {
    	
    }
    
    @Override
    public @Nullable HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> defaultModel)
    {
        return HorusArmorModel.INSTANCE;
    }
}
