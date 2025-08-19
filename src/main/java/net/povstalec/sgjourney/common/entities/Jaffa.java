package net.povstalec.sgjourney.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.entities.goals.NearestThreatGoal;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;
import net.povstalec.sgjourney.common.items.VialItem;

import javax.annotation.Nullable;

public class Jaffa extends Human
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/player/wide/efe.png");
	
	private static final ResourceLocation ABYDOS = new ResourceLocation(StargateJourney.MODID,"abydos");
	
	//protected Goauld.Info goauldLarva;
	
	public Jaffa(EntityType<? extends Jaffa> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
	
	@Override
	protected void registerGoals()
	{
		super.registerGoals();
		
		this.targetSelector.addGoal(1, new NearestThreatGoal<>(this, Player.class, true));
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Human.createAttributes()
				.add(Attributes.ATTACK_DAMAGE, 2.0D);
	}
	
	protected void setupHelmet()
	{
		setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemInit.JAFFA_HELMET.get()));
	}
	
	protected void setupDimensionHelmet(ServerLevelAccessor level, RandomSource randomSource)
	{
		if(level.getLevel().dimension().location().equals(ABYDOS))
			setItemSlot(EquipmentSlot.HEAD, randomSource.nextFloat() > 0.7F ? new ItemStack(ItemInit.JACKAL_HELMET.get()) : new ItemStack(ItemInit.FALCON_HELMET.get()));
		else
			setupHelmet();
	}
	
	protected void setupArmor()
	{
		setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemInit.JAFFA_CHESTPLATE.get()));
		setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemInit.JAFFA_LEGGINGS.get()));
		setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemInit.JAFFA_BOOTS.get()));
	}
	
	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag)
	{
		RandomSource randomSource = level.getRandom();
		
		spawnGroupData = super.finalizeSpawn(level, difficulty, type, spawnGroupData, tag);
		
		setItemInHand(InteractionHand.MAIN_HAND, StaffWeaponItem.filledStaffWeapon(randomSource.nextFloat() > difficulty.getDifficulty().getId() / 3F, (int) (randomSource.nextFloat() * CommonTechConfig.vial_capacity.get())));
		
		if(type != MobSpawnType.EVENT)
		{
			if(type == MobSpawnType.NATURAL)
				setupDimensionHelmet(level, randomSource);
			else
				setupHelmet();
			setupArmor();
		}
		
		return spawnGroupData;
	}
}
