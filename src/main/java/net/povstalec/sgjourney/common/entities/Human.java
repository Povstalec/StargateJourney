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

public class Human extends AgeableMob implements RangedAttackMob
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
		this.goalSelector.addGoal(0, new FloatGoal(this));
		
		//this.goalSelector.addGoal(2, new StaffWeaponAttackGoal(this, 1.0D, 10.0F, 32.0F));
		
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 5F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
		
		//this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return AgeableMob.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 32.0)
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D);
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
	{
		return null;
	}
	
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions)
	{
		return this.isBaby() ? 0.93F : 1.74F;
	}
	
	@Override
	public void performRangedAttack(LivingEntity entity, float distanceFactor)
	{
		//TODO Humans should be able to use other weapons as well
		/*ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof BowItem)));
		AbstractArrow abstractarrow = this.getArrow(itemstack, distanceFactor);
		
		if(this.getMainHandItem().getItem() instanceof BowItem)
			abstractarrow = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow);
		
		double d0 = entity.getX() - this.getX();
		double d1 = entity.getY(0.3333333333333333) - abstractarrow.getY();
		double d2 = entity.getZ() - this.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		
		abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
		
		this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.level.addFreshEntity(abstractarrow);*/
		
		performStaffWeaponAttack(entity, distanceFactor);
	}
	
	public void performStaffWeaponAttack(LivingEntity entity, float distanceFactor)
	{
		ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof StaffWeaponItem));
		
		if(itemstack.getItem() instanceof StaffWeaponItem staffWeapon)
		{
			PlasmaProjectile plasmaProjectile = new PlasmaProjectile(EntityInit.JAFFA_PLASMA.get(), this, level, staffWeapon.getExplosionPower(itemstack));
			
			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333) - plasmaProjectile.getY();
			double z = entity.getZ() - this.getZ();
			double distance = Math.sqrt(x * x + z * z);
			
			plasmaProjectile.shoot(x, y + distance * 0.125, z, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
			
			this.playSound(SoundInit.MATOK_FIRE.get(), 0.25F, 1.0F);
			level.addFreshEntity(plasmaProjectile);
		}
	}
}
