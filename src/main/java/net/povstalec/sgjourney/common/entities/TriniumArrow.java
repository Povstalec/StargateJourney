package net.povstalec.sgjourney.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.TagInit;
import org.jetbrains.annotations.NotNull;

public class TriniumArrow extends AbstractArrow
{
	public static final double MIN_BREAK_SPEED = 2; // Minimum speed needed to break blocks
	public static final double MIN_BREAK_SPEED_SQR = MIN_BREAK_SPEED * MIN_BREAK_SPEED;
	
	public TriniumArrow(EntityType<? extends AbstractArrow> type, Level level)
	{
		super(type, level);
	}
	
	public TriniumArrow(Level level, LivingEntity owner)
	{
		super(EntityInit.TRINIUM_ARROW.get(), owner, level);
	}
	
	@Override
	protected @NotNull ItemStack getPickupItem()
	{
		return new ItemStack(ItemInit.TRINIUM_ARROW.get());
	}
	
	@Override
	protected void onHitBlock(BlockHitResult blockHitResult)
	{
		if(this.level.getBlockState(blockHitResult.getBlockPos()).is(TagInit.Blocks.TRINIUM_ARROW_CAN_BREAK) && this.getDeltaMovement().lengthSqr() >= MIN_BREAK_SPEED_SQR)
			this.level.destroyBlock(blockHitResult.getBlockPos(), true);
		else
			super.onHitBlock(blockHitResult);
	}
}
