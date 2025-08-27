package net.povstalec.sgjourney.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.povstalec.sgjourney.common.entities.goals.StaffWeaponAttackGoal;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import javax.annotation.Nullable;

public abstract class Anthropoid extends AgeableMob implements RangedAttackMob
{
	private final StaffWeaponAttackGoal staffWeaponGoal = new StaffWeaponAttackGoal(this, 1.0D, 8.0F, 12.0F);
	private final RangedBowAttackGoal<Anthropoid> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
	private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false)
	{
		public void stop()
		{
			super.stop();
			Anthropoid.this.setAggressive(false);
		}
		
		public void start()
		{
			super.start();
			Anthropoid.this.setAggressive(true);
		}
	};
	
	public Anthropoid(EntityType<? extends Anthropoid> type, Level level)
	{
		super(type, level);
		this.reassessWeaponGoal();
	}
	
	public abstract ResourceLocation texture();
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 5F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
	{
		return null;
	}
	
	public void reassessWeaponGoal()
	{
		if(this.level() == null || this.level().isClientSide())
			return;
		
		this.goalSelector.removeGoal(this.meleeGoal);
		this.goalSelector.removeGoal(this.bowGoal);
		this.goalSelector.removeGoal(this.staffWeaponGoal);
		
		ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> isWeapon(item)));
		
		if(itemstack.getItem() instanceof StaffWeaponItem)
			this.goalSelector.addGoal(2, staffWeaponGoal);
		else if(itemstack.getItem() instanceof BowItem)
		{
			int i = 20;
			
			if(this.level().getDifficulty() != Difficulty.HARD)
				i = 40;
			
			this.bowGoal.setMinAttackInterval(i);
			this.goalSelector.addGoal(4, this.bowGoal);
		}
		else
			this.goalSelector.addGoal(4, this.meleeGoal);
	}
	
	
	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData)
	{
		this.setCanPickUpLoot(true);
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
		
		reassessWeaponGoal();
		
		return spawnGroupData;
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag tag)
	{
		super.readAdditionalSaveData(tag);
		this.reassessWeaponGoal();
	}
	
	@Override
	public void setItemSlot(EquipmentSlot slot, ItemStack stack)
	{
		super.setItemSlot(slot, stack);
		
		if(!this.level().isClientSide())
			this.reassessWeaponGoal();
		
	}
	
	@Override
	public void performRangedAttack(LivingEntity entity, float distanceFactor)
	{
		if(getMainHandItem().getItem() instanceof StaffWeaponItem)
			performStaffWeaponAttack(entity, distanceFactor);
		else
			performBowAttack(entity, distanceFactor);
	}
	
	protected void performBowAttack(LivingEntity entity, float distanceFactor)
	{
		ItemStack bow = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof BowItem));
		ItemStack itemstack = this.getProjectile(bow);
		AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor, bow);
		
		if(this.getMainHandItem().getItem() instanceof BowItem)
			abstractarrow = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow, itemstack, bow);
		
		double d0 = entity.getX() - this.getX();
		double d1 = entity.getY(0.3333333333333333) - abstractarrow.getY();
		double d2 = entity.getZ() - this.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		
		abstractarrow.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
		
		this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.level().addFreshEntity(abstractarrow);
	}
	
	protected void performStaffWeaponAttack(LivingEntity entity, float distanceFactor)
	{
		ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, (item) -> item instanceof StaffWeaponItem));
		
		if(itemstack.getItem() instanceof StaffWeaponItem staffWeapon && staffWeapon.tryDepleteLiquidNaquadah(itemstack))
		{
			PlasmaProjectile plasmaProjectile = new PlasmaProjectile(EntityInit.JAFFA_PLASMA.get(), this, level(), staffWeapon.getExplosionPower(itemstack));
			
			double x = entity.getX() - this.getX();
			double y = entity.getY(0.3333333333333333) - plasmaProjectile.getY();
			double z = entity.getZ() - this.getZ();
			double distance = Math.sqrt(x * x + z * z);
			
			plasmaProjectile.shoot(x, y + distance * 0.125, z, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
			
			this.playSound(SoundInit.MATOK_FIRE.get(), 0.25F, 1.0F);
			level().addFreshEntity(plasmaProjectile);
		}
	}
	
	public static boolean isWeapon(Item item)
	{
		if(item instanceof SwordItem)
			return true;
		
		if(item instanceof StaffWeaponItem)
			return true;
		
		if(item instanceof BowItem)
			return true;
		
		if(item instanceof CrossbowItem)
			return true;
		
		return false;
	}
}
