package net.povstalec.sgjourney.common.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.entities.goals.StaffWeaponAttackGoal;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import javax.annotation.Nullable;

public class Human extends Anthropoid
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/player/wide/makena.png");
	
	public Human(EntityType<? extends Human> type, Level level)
	{
		super(type, level);
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return AgeableMob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.FOLLOW_RANGE, 32.0D)
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D);
	}
	
	@Override
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
}
