package woldericz_junior.stargatejourney.items;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import woldericz_junior.stargatejourney.StargateJourney;

public class HorusArmor extends ArmorItem
{

	 public final EquipmentSlotType type;
	
	 public HorusArmor(IArmorMaterial mat, EquipmentSlotType type, Properties props) {
	        super(mat, type, props);
	        this.type = type;
	    }

	    @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
	    @OnlyIn(Dist.CLIENT)
	    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel original) 
	    {
	    		BipedModel armorModel = StargateJourney.armorModels.get(this);

					armorModel.bipedHead.showModel = armorSlot == EquipmentSlotType.HEAD;
					
			return armorModel;
	    }
}
