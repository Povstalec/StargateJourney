package net.povstalec.sgjourney.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.init.EntityInit;

import java.util.Objects;

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
		if(player.level().isClientSide())
			return super.interactLivingEntity(stack, player, target, hand);
		
		GoauldHost cap = target.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
		
		if(cap == null || !(target instanceof Mob mob) || !cap.takeOverHost(stack, mob))
			return InteractionResult.FAIL;
		
		target.hurt(player.damageSources().generic(), 1); //TODO Add Goa'uld damage source
		stack.shrink(1);
		return InteractionResult.CONSUME;
	}
}
