package woldericz_junior.stargatejourney.items;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import woldericz_junior.stargatejourney.StargateJourney;

public class NaquadahHorusArmor extends HorusArmor 
{

	public final EquipmentSlotType type;
    
    public NaquadahHorusArmor(IArmorMaterial mat, EquipmentSlotType type, Properties props) {
        super(mat, type, props);
        this.type = type;
    }
    
    @Nonnull
    @Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
	{
    	return StargateJourney.MODID + "/textures/models/armor/horus_layer_1.png";
	}

}
