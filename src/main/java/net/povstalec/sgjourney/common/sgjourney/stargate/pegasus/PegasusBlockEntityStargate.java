package net.povstalec.sgjourney.common.sgjourney.stargate.pegasus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class PegasusBlockEntityStargate extends PegasusStargate implements BlockEntityStargate<PegasusStargateEntity>
{
	protected WeakReference<PegasusStargateEntity> stargate;
	
	protected BlockPos blockPos;
	
	// Direction cache
	@Nullable
	protected Vec3 forward = null;
	@Nullable
	protected Vec3 up = null;
	@Nullable
	protected Vec3 right = null;
	
	public PegasusBlockEntityStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	private PegasusStargateEntity cacheStargateEntity(PegasusStargateEntity stargate)
	{
		//this.stargate = new WeakReference(stargate); //TODO Bring caching back once Stargates are more flexible
		
		return stargate;
	}
	
	private @Nullable PegasusStargateEntity tryCacheStargateEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof PegasusStargateEntity stargate)
			return cacheStargateEntity(stargate);
		
		return null;
	}
	
	@Override
	public @Nullable PegasusStargateEntity getStargateEntity(MinecraftServer server)
	{
		//if((this.stargate != null && this.stargate.get() != null) || server == null)
		//	return this.stargate.get();
		
		return tryCacheStargateEntity(server);
	}
	
	@Override
	public void loadFromBlockEntity(AbstractStargateEntity<?> stargate)
	{
		this.id9ChevronAddress = stargate.get9ChevronAddress();
		
		this.dimension = stargate.getLevel().dimension();
		this.blockPos = stargate.getBlockPos();
		
		this.hasDHD = stargate.dhdCache.isPresent();
		this.timesOpened = stargate.getTimesOpened();
		
		this.hasNetworkRestrictions = stargate.hasNetworkRestrictions();
		this.networks = stargate.getNetworks();
	}
	
	//============================================================================================
	//*************************************Basic functionality************************************
	//============================================================================================
	
	@Override
	public @Nullable Vec3 getForward()
	{
		if(forward == null)
		{
			forward = stargateReturn(server, stargate ->
			{
				Direction direction = stargate.getDirection();
				Orientation orientation = stargate.getOrientation();
				
				return Orientation.getForwardVector(direction, orientation);
			}, null);
		}
		
		return forward;
	}
	
	@Override
	public @Nullable Vec3 getUp()
	{
		if(up == null)
		{
			up = stargateReturn(server, stargate ->
			{
				Direction direction = stargate.getDirection();
				Orientation orientation = stargate.getOrientation();
				
				return Orientation.getUpVector(direction, orientation);
			}, null);
		}
		
		return up;
	}
	
	@Override
	public Vec3 getRight()
	{
		if(right == null)
		{
			if(getForward() != null && getUp() != null)
				right = CoordinateHelper.Relative.vecRight(getForward(), getUp());
		}
		
		return right;
	}
	
	// Updating
	
	@Override
	public void update()
	{
		stargateRun(server, stargate ->
		{
			// When it comes to the DHD, this update method should only take cached values, as the cache changing is what causes the update in the first place
			this.hasDHD = stargate.dhdCache.isCached();
			
			this.timesOpened = stargate.getTimesOpened();
			
			// Retrieving cached ones here too because they're influenced by the DHD
			this.hasNetworkRestrictions = stargate.hasCachedNetworkRestrictions();
			this.networks = stargate.getCachedNetworks();
		});
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		tag.putIntArray(COORDINATES, Conversion.blockPosToIntArray(blockPos));
		
		super.serializeNBT(tag);
	}
	
	@Override
	public void deserializeNBT(Address.Immutable id9ChevronAddress, CompoundTag tag)
	{
		blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		super.deserializeNBT(id9ChevronAddress, tag);
		
		if(!tag.contains(HAS_DHD) || !tag.contains(TIMES_OPENED) || !tag.contains(NETWORKS))
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof PegasusStargateEntity stargate)
				loadFromBlockEntity(stargate);
		}
	}
}
