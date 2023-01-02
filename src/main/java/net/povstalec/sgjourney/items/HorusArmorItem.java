package net.povstalec.sgjourney.items;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.HorusArmorRenderProperties;

public class HorusArmorItem extends ArmorItem
{
	public final EquipmentSlot type;
	
	 public HorusArmorItem(ArmorMaterial mat, EquipmentSlot type, Properties props) {
	        super(mat, type, props);
	        this.type = type;
	    }
	 
	 @Override
	 public void initializeClient(Consumer<IClientItemExtensions> consumer) 
	 {
		 consumer.accept(HorusArmorRenderProperties.INSTANCE);
	 }
	 
	 @Nullable
	 @Override
	 public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		
		 return StargateJourney.MODID + ":textures/models/armor/horus_helmet.png";
	 }
}
