package net.povstalec.sgjourney.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.init.EntityInit;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class GoauldItem extends Item
{
	public GoauldItem(Properties properties)
	{
		super(properties);
	}
	
	/*@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
	{
		//TODO Age
	}*/
	
	@Nullable
	public CompoundTag getInfoTag(ItemStack stack)
	{
		if(stack.getTag() != null && stack.getTag().contains(Goauld.Info.GOAULD_INFO, CompoundTag.TAG_COMPOUND))
			return stack.getTag().getCompound(Goauld.Info.GOAULD_INFO);
		
		return null;
	}
	
	public float getHealth(ItemStack stack)
	{
		CompoundTag tag = getInfoTag(stack);
		
		if(tag != null && tag.contains(Goauld.Info.HEALTH, CompoundTag.TAG_FLOAT))
			return tag.getFloat(Goauld.Info.HEALTH);
		
		return 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		float health = getHealth(stack);
		return health > 0 && health < Goauld.MAX_HEALTH;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * getHealth(stack) / Goauld.MAX_HEALTH);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, getHealth(stack) / Goauld.MAX_HEALTH);
		
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		
		if(!(level instanceof ServerLevel))
			return InteractionResult.SUCCESS;
		
		ItemStack itemstack = context.getItemInHand();
		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockState blockstate = level.getBlockState(blockpos);
		
		BlockPos spawnPos;
		if(blockstate.getCollisionShape(level, blockpos).isEmpty())
			spawnPos = blockpos;
		else
			spawnPos = blockpos.relative(direction);
		
		Entity entity = EntityInit.GOAULD.get().spawn((ServerLevel) level, itemstack, context.getPlayer(), spawnPos, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, spawnPos) && direction == Direction.UP);
		if(entity instanceof Goauld goauld)
		{
			goauld.loadFromItem(itemstack);
			itemstack.shrink(1);
			level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
		}
		
		return InteractionResult.CONSUME;
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
	{
		if(player.getLevel().isClientSide())
			return super.interactLivingEntity(stack, player, target, hand);
		
		Optional<GoauldHost> cap = target.getCapability(GoauldHostProvider.GOAULD_HOST).resolve();
		
		if(!cap.isPresent() || !(target instanceof Mob mob) || !cap.get().takeOverHost(stack, mob))
			return InteractionResult.FAIL;
		
		target.hurt(DamageSource.GENERIC, 1); //TODO Add Goa'uld damage source
		stack.shrink(1);
		return InteractionResult.CONSUME;
	}
	
	@Override
	public boolean hasCustomEntity(ItemStack stack)
	{
		return true;
	}
	
	@Override
	@Nullable
	public Entity createEntity(Level level, Entity location, ItemStack stack)
	{
		Goauld goauld = EntityInit.GOAULD.get().create(level);
		goauld.loadFromItem(stack);
		
		if(goauld == null)
			return null;
		
		goauld.setDeltaMovement(location.getDeltaMovement());
		goauld.setPos(location.getX(), location.getY(), location.getZ());
		goauld.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(goauld.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData) null, (CompoundTag) null);
		
		return goauld;
	}
}
