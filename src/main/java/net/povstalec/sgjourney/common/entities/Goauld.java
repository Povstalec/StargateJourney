package net.povstalec.sgjourney.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;
import net.povstalec.sgjourney.common.entities.goals.NearestHostGoal;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.GoauldItem;

import javax.annotation.Nullable;

public class Goauld extends AgeableMob
{
	public static final float MAX_HEALTH = 6.0F;
	
	public Goauld(EntityType<? extends AgeableMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(3, new TryFindWaterGoal(this));
		
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestHostGoal<>(this, Human.class));
		//this.targetSelector.addGoal(3, new NearestHostGoal<>(this, Villager.class));
		//this.targetSelector.addGoal(3, new NearestHostGoal<>(this, WanderingTrader.class));
		this.targetSelector.addGoal(4, new NearestHostGoal<>(this, Player.class));
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob)
	{
		return null;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, MAX_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.4D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D);
	}
	
	@Override
	public boolean doHurtTarget(Entity entity)
	{
		float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		boolean damaged = entity.hurt(this.damageSources().mobAttack(this), damage);
		
		if(damaged && entity instanceof Mob mob)
		{
			if(entity.getClass() == Human.class)
				mob.getCapability(GoauldHostProvider.GOAULD_HOST).ifPresent(cap -> cap.takeOverHost(this, mob));
		}
		
		return damaged;
	}
	
	@Override
	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.SILVERFISH_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return SoundEvents.SILVERFISH_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.SILVERFISH_DEATH;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state)
	{
		this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if(!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
			return InteractionResult.PASS;
		
		// Catching the Goa'uld with an empty hand
		ItemStack stack = saveToItem();
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		remove(RemovalReason.DISCARDED);
		
		return InteractionResult.SUCCESS;
	}
	
	
	
	public Info goauldInfo()
	{
		return new Goauld.Info(getCustomName(), getHealth(), getAge());
	}
	
	public void setFromInfo(Info goauldInfo)
	{
		setCustomName(goauldInfo.name());
		setHealth(goauldInfo.health());
		setAge(goauldInfo.age());
	}
	
	public ItemStack saveToItem()
	{
		return goauldInfo().toItemStack();
	}
	
	public void loadFromItem(ItemStack stack)
	{
		setFromInfo(Info.fromItemStack(stack));
	}
	
	
	
	public static class Info implements INBTSerializable<CompoundTag>
	{
		public static final String GOAULD_INFO = "goauld_info";
		public static final String NAME = "name";
		public static final String HEALTH = "health";
		public static final String AGE = "age";
		
		@Nullable
		private Component name = null;
		private float health;
		private int age;
		
		public Info(@Nullable Component name, float health, int age)
		{
			this.name = name;
			this.health = health;
			this.age = age;
			// Goa'uld faction
			// Goa'uld rank?
		}
		
		public Info()
		{
			this(null, MAX_HEALTH, 0);
		}
		
		@Nullable
		public Component name()
		{
			return name;
		}
		
		public float health()
		{
			return health;
		}
		
		public int age()
		{
			return age;
		}
		
		@Override
		public CompoundTag serializeNBT()
		{
			CompoundTag tag = new CompoundTag();
			
			if(this.name != null)
				tag.putString(NAME, Component.Serializer.toJson(this.name));
			
			tag.putFloat(HEALTH, health);
			tag.putInt(AGE, age);
			
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundTag tag)
		{
			if(tag.contains(NAME, CompoundTag.OBJECT_HEADER))
				this.name = Component.Serializer.fromJson(tag.getString(NAME));
			
			this.health = tag.getFloat(HEALTH);
			this.age = tag.getInt(AGE);
		}
		
		public Info copy()
		{
			return new Info(this.name, this.health, this.age);
		}
		
		public static Info fromItemStack(ItemStack stack)
		{
			Info goauldInfo = new Info();
			
			if(stack.getItem() instanceof GoauldItem && stack.getTag() != null && stack.getTag().contains(GOAULD_INFO, CompoundTag.TAG_COMPOUND))
				goauldInfo.deserializeNBT(stack.getTag().getCompound(GOAULD_INFO));
			
			if(stack.hasCustomHoverName())
				goauldInfo.name = stack.getHoverName();
			
			return goauldInfo;
		}
		
		public ItemStack toItemStack()
		{
			ItemStack goauldStack = new ItemStack(ItemInit.GOAULD.get());
			goauldStack.getOrCreateTag().put(GOAULD_INFO, this.serializeNBT());
			
			if(this.name != null)
				goauldStack.setHoverName(this.name);
			
			return goauldStack;
		}
	}
}
