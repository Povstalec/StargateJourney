package net.povstalec.sgjourney.common.sgjourney.stargate.classic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

import javax.annotation.Nullable;

public class ClassicBlockEntityStargate extends ClassicStargate implements BlockEntityStargate<ClassicStargateEntity>
{
	// Stargate BlockEntity cache
	protected boolean stargateCacheRequiresUpdate = true;
	protected ClassicStargateEntity cachedStargateEntity = null;
	
	protected BlockPos blockPos;
	
	// Direction cache
	@Nullable
	protected Vec3 forward = null;
	@Nullable
	protected Vec3 up = null;
	@Nullable
	protected Vec3 right = null;
	
	public ClassicBlockEntityStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	protected void cacheStargateEntity(@Nullable ClassicStargateEntity stargate)
	{
		this.stargateCacheRequiresUpdate = false;
		this.cachedStargateEntity = stargate;
	}
	
	public void invalidateCachedStargateEntity()
	{
		if(!this.stargateCacheRequiresUpdate)
		{
			this.stargateCacheRequiresUpdate = true;
			this.cachedStargateEntity = null;
		}
	}
	
	protected @Nullable ClassicStargateEntity fetchStargateEntity()
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof ClassicStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	@Override
	public @Nullable ClassicStargateEntity getStargateEntity()
	{
		if(this.stargateCacheRequiresUpdate)
			cacheStargateEntity(fetchStargateEntity());
		
		return this.cachedStargateEntity;
	}
	
	@Override
	public void onStargateEntityUnloaded()
	{
		invalidateCachedStargateEntity();
	}
	
	@Override
	public void onStargateEntityRemoved()
	{
		invalidateCachedStargateEntity();
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
	public StargateInfo.FeedbackMessage resetStargate(StargateInfo.FeedbackMessage feedback)
	{
		invalidateCachedStargateEntity();
		return BlockEntityStargate.super.resetStargate(feedback);
	}
	
	@Override
	public @Nullable Vec3 getForward()
	{
		if(forward == null)
		{
			forward = stargateReturn(stargate ->
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
			up = stargateReturn(stargate ->
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
		stargateRun(stargate ->
		{
			// When it comes to the DHD, this update method should only take cached values, as the cache changing is what causes the update in the first place
			this.hasDHD = stargate.dhdCache.isCached();
			
			this.timesOpened = stargate.getTimesOpened();
			
			// Retrieving cached ones here too because they're influenced by the DHD
			this.hasNetworkRestrictions = stargate.hasCachedNetworkRestrictions();
			this.networks = stargate.getCachedNetworks();
		});
		
		// Make sure any changes are saved to the Block Entity List
		updateBlockEntityList();
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
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof ClassicStargateEntity stargate)
				loadFromBlockEntity(stargate);
		}
	}
}
