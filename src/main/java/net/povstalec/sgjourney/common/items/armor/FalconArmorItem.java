package net.povstalec.sgjourney.common.items.armor;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;

public class FalconArmorItem extends ArmorItem
{
	public static final ResourceLocation TEXTURE = StargateJourney.sgjourneyLocation("textures/models/armor/falcon_helmet.png");
	
	public FalconArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties props)
	{
		super(material, type, props);
	}
	 
	 @Nullable
	 @Override
	 public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel)
	 {
		 return TEXTURE;
	 }
}
