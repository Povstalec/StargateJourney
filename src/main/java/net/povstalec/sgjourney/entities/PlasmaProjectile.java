package net.povstalec.sgjourney.entities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PlasmaProjectile extends ThrowableProjectile
{
	private int explosionPower = 0;
	
	public PlasmaProjectile(EntityType<? extends ThrowableProjectile> type, Level worldIn)
	{
        super(type, worldIn);
    }

    public PlasmaProjectile(EntityType<? extends ThrowableProjectile> type, double x, double y, double z, Level worldIn)
    {
        super(type, x, y, z, worldIn);
    }

    public PlasmaProjectile(EntityType<? extends ThrowableProjectile> type, LivingEntity livingEntityIn, Level worldIn)
    {
        super(type, livingEntityIn, worldIn);
    }

	protected void onHit(HitResult p_37218_)
	{
	      super.onHit(p_37218_);
	      if (!this.level.isClientSide)
	      {
	         boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner());
	         this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, flag, flag ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
	         this.discard();
	      }

	   }

	protected void onHitEntity(EntityHitResult p_37216_)
	{
		super.onHitEntity(p_37216_);
		if (!this.level.isClientSide)
		{
			Entity entity = p_37216_.getEntity();
			Entity entity1 = this.getOwner();
			entity.hurt(DamageSource.explosion((Player)entity1, entity), 14.0F);
			if (entity1 instanceof LivingEntity)
			{
				this.doEnchantDamageEffects((LivingEntity)entity1, entity);
			}

		}
	}

	@Override
	protected void defineSynchedData()
	{
		
	}
}
