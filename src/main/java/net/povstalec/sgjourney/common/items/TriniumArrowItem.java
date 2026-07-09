package net.povstalec.sgjourney.common.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.entities.TriniumArrow;
import org.jetbrains.annotations.NotNull;

public class TriniumArrowItem extends ArrowItem
{
	public TriniumArrowItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public @NotNull AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity owner)
	{
		return new TriniumArrow(level, owner);
	}
}
