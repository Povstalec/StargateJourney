package net.povstalec.sgjourney.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class Goauld extends PathfinderMob
{
	public Goauld(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
	      this.goalSelector.addGoal(1, new FloatGoal(this));
	      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
	      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
	      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
	      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return PathfinderMob.createMobAttributes()
				.add(ForgeMod.ENTITY_GRAVITY.get(), 0.5f)
				.add(Attributes.MAX_HEALTH, 5.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.75D);
	}
}
