package net.povstalec.sgjourney.client.render;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.povstalec.sgjourney.client.models.entity.FalconArmorModel;

public class FalconArmorRenderProperties implements IClientItemExtensions
{
	public static final FalconArmorRenderProperties INSTANCE = new FalconArmorRenderProperties();

    private FalconArmorRenderProperties() 
    {
    	
    }
    
    @Override
    public @Nullable HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> defaultModel)
    {
        return FalconArmorModel.INSTANCE;
    }
}
