package net.povstalec.sgjourney.common.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
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

import javax.annotation.Nullable;

public class Human extends AgeableMob
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/player/wide/makena.png");
	
	public Human(EntityType<? extends AgeableMob> type, Level level)
	{
		super(type, level);
	}
	
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return AgeableMob.createMobAttributes()
				.add(ForgeMod.ENTITY_GRAVITY.get(), 0.5f)
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.75D);
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
	{
		return null;
	}
}
