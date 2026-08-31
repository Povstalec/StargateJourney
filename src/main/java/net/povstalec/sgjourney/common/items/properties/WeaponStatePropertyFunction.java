package net.povstalec.sgjourney.common.items.properties;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;
import org.jetbrains.annotations.NotNull;

public class WeaponStatePropertyFunction implements ClampedItemPropertyFunction
{
	@Override
	public float unclampedCall(@NotNull ItemStack stack, ClientLevel level, LivingEntity entity, int layer)
	{
		if(entity != null && stack.getItem() instanceof StaffWeaponItem)
			return StaffWeaponItem.isOpen(stack) ? 1 : 0;
		
		return 0;
	}

}
