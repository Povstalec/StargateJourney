package net.povstalec.sgjourney.common.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.init.EntityInit;
import org.jetbrains.annotations.Nullable;

public class Mastadge extends Animal
{
	public int tailCounter; //TODO
	private float eatAnim; //TODO
	private float eatAnimO; //TODO
	private float standAnim; //TODO
	private float standAnimO; //TODO
	private float mouthAnim; //TODO
	private float mouthAnimO; //TODO
	
	public Mastadge(EntityType<? extends Animal> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public boolean isFood(ItemStack stack)
	{
		return stack.is(ItemTags.HORSE_FOOD); //TODO Make a custom tag
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT, Items.CARROT, Items.POTATO, Items.COOKIE), false));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
	{
		return EntityInit.MASTADGE.get().create(level);
	}
	
	public float getEatAnim(float partialTicks)
	{
		return Mth.lerp(partialTicks, this.eatAnimO, this.eatAnim); //TODO
	}
	
	public float getStandAnim(float partialTicks)
	{
		return Mth.lerp(partialTicks, this.standAnimO, this.standAnim); //TODO
	}
	
	public float getMouthAnim(float partialTicks)
	{
		return Mth.lerp(partialTicks, this.mouthAnimO, this.mouthAnim); //TODO
	}
	
	public boolean isSaddled()
	{
		return false; //TODO
	}
}
