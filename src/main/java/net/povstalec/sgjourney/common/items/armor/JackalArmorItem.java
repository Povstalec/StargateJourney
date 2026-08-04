package net.povstalec.sgjourney.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.JackalArmorRenderProperties;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class JackalArmorItem extends ArmorItem
{
	public final EquipmentSlot type;
	
	 public JackalArmorItem(ArmorMaterial mat, EquipmentSlot type, Properties props) {
	        super(mat, type, props);
	        this.type = type;
	    }
	 
	 @Override
	 public void initializeClient(Consumer<IItemRenderProperties> consumer)
	 {
		 consumer.accept(JackalArmorRenderProperties.INSTANCE);
	 }
	 
	 @Nullable
	 @Override
	 public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		 
		 return StargateJourney.MODID + ":textures/models/armor/jackal_helmet.png";
	 }
}
