package net.povstalec.sgjourney.common.items.properties;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.items.FluidItem;
import org.jetbrains.annotations.NotNull;

public class FluidPropertyFunction implements ClampedItemPropertyFunction
{
	@Override
	public float unclampedCall(@NotNull ItemStack stack, ClientLevel level, LivingEntity entity, int layer)
	{
		if(entity != null && stack.getItem() instanceof FluidItem fluidItem)
			return fluidItem.getFluidPercentage(stack);
		
		return 0;
	}

}
