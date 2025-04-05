package net.povstalec.sgjourney.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;
import net.povstalec.sgjourney.common.init.ItemInit;

import javax.annotation.Nullable;

public class Jaffa extends Human
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/player/wide/efe.png");
	
	public Jaffa(EntityType<? extends AgeableMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public ResourceLocation texture()
	{
		return TEXTURE;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return AgeableMob.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 32.0)
				.add(Attributes.MAX_HEALTH, 30.0D)
				.add(Attributes.ATTACK_DAMAGE, 2.0D);
	}
	
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag)
	{
		spawnGroupData = super.finalizeSpawn(level, difficulty, type, spawnGroupData, tag);
		
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemInit.MATOK.get()));
		
		setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemInit.JACKAL_HELMET.get()));
		setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemInit.JAFFA_CHESTPLATE.get()));
		setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemInit.JAFFA_LEGGINGS.get()));
		setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemInit.JAFFA_BOOTS.get()));
		
		return spawnGroupData;
	}
}
