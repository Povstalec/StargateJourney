package woldericz_junior.stargatejourney.entities;

import init.StargateEntities;
import init.StargateItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class JaffaPlasma extends ProjectileItemEntity
{
	public JaffaPlasma(EntityType<JaffaPlasma> type, World world) 
	{
		super(type, world);
	}
	
	public JaffaPlasma(LivingEntity entity, World world)
	{
		super(StargateEntities.jaffa_plasma.get(), entity, world);
	}
	
	public JaffaPlasma(double x, double y, double z, World world)
	{
		super(StargateEntities.jaffa_plasma.get(), x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() 
	{
		return StargateItems.jaffa_plasma;
	}
	
	@Override
	public IPacket<?> createSpawnPacket() 
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void onImpact(RayTraceResult result) 
	{
		if(result.getType() == RayTraceResult.Type.ENTITY) 
		{
			Entity entity = ((EntityRayTraceResult)result).getEntity();
			int damage;
			if(entity instanceof CowEntity)
			{
				damage = 5;
			}
			else
			{
				damage = 0;
			}
			entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)damage);
		}
		if(!world.isRemote)
		{
			this.remove();
		}
	}
}
