package net.povstalec.sgjourney.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.povstalec.sgjourney.client.models.entity.FalconArmorModel;
import org.jetbrains.annotations.NotNull;

public class FalconArmorRenderProperties implements IItemRenderProperties
{
	public static final FalconArmorRenderProperties INSTANCE = new FalconArmorRenderProperties();
	
	@Override
	public @NotNull HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default)
	{
		return FalconArmorModel.INSTANCE;
	}
}
