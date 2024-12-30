package net.povstalec.sgjourney.common.entities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;
import net.povstalec.sgjourney.common.init.TagInit;

public class PlasmaProjectile extends Projectile
{
	private float explosionPower = 0;
	
	public PlasmaProjectile(EntityType<? extends Projectile> type, Level level)
	{
		super(type, level);
	}
	
	public PlasmaProjectile(EntityType<? extends Projectile> type, LivingEntity shooter, Level level, float explosionPower)
	{
		super(type, level);
		
		this.explosionPower = explosionPower;
		this.setOwner(shooter);
	}
	
	protected void onHit(HitResult hitResult)
	{
		super.onHit(hitResult);
		if(!this.level().isClientSide())
		{
			boolean canDestroy = EventHooks.canEntityGrief(this.level(), this.getOwner());
			this.level().explode((Entity)this.getOwner(), this.getX(), this.getY(), this.getZ(), this.explosionPower, canDestroy,
					canDestroy ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
			this.discard();
		}
		
	}
	
	protected void onHitEntity(EntityHitResult hitResult)
	{
		super.onHitEntity(hitResult);
		if(!this.level().isClientSide())
		{
			Entity entity = hitResult.getEntity();
			Entity attacker = this.getOwner();
			
			DamageSource source = level().damageSources().explosion(entity, attacker);
			entity.hurt(source, 14.0F);
			
			if(attacker instanceof LivingEntity)
				EnchantmentHelper.doPostAttackEffects((ServerLevel) this.level(), entity, source);
		}
	}
	
	protected void onHitBlock(BlockHitResult result)
	{
		super.onHitBlock(result);
		
		if(!this.level().isClientSide())
		{
			Entity entity = this.getOwner();
			if(!(entity instanceof Mob) || EventHooks.canEntityGrief(this.level(), entity))
			{
				BlockPos blockpos = result.getBlockPos().relative(result.getDirection());
				
				if(this.level().isEmptyBlock(blockpos))
				{
					for(Direction direction : Direction.values())
					{
						trySetFireToBlock(blockpos, blockpos.relative(direction));
					}
				}
			}

		}
	}
	
	private boolean trySetFireToBlock(BlockPos blockpos, BlockPos nearbyPos)
	{
		if(this.level().getBlockState(nearbyPos).is(TagInit.Blocks.PLASMA_FLAMMABLE))
		{
			this.level().setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level(), blockpos));
			return true;
		}
		return false;
	}
	
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
	{
	
	}
}
