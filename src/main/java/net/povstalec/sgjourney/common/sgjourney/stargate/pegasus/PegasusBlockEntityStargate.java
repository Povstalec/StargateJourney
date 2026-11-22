package net.povstalec.sgjourney.common.sgjourney.stargate.pegasus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

import javax.annotation.Nullable;

public class PegasusBlockEntityStargate extends PegasusStargate implements SGJourneyBlockEntityStargate<PegasusStargateEntity>
{
	// Stargate BlockEntity cache
	protected boolean stargateCacheRequiresUpdate = true;
	protected PegasusStargateEntity stargateBlockEntity = null;
	
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
	
	public PegasusBlockEntityStargate(StargateType<?> type, MinecraftServer server, PegasusStargateEntity stargate)
	{
		this(type, server);
		
		loadFromBlockEntity(stargate);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	protected void cacheStargateEntity(@Nullable PegasusStargateEntity stargate)
	{
		this.stargateCacheRequiresUpdate = false;
		this.stargateBlockEntity = stargate;
	}
	
	public void invalidateCachedStargateEntity()
	{
		if(!this.stargateCacheRequiresUpdate)
		{
			this.stargateCacheRequiresUpdate = true;
			this.stargateBlockEntity = null;
		}
	}
	
	protected @Nullable PegasusStargateEntity fetchStargateEntity()
	{
		Level level = getLevel();
		if(level != null && level.getBlockEntity(blockPos) instanceof PegasusStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	@Override
	public @Nullable PegasusStargateEntity getStargateBlockEntity()
	{
		if(this.stargateCacheRequiresUpdate)
			cacheStargateEntity(fetchStargateEntity());
		
		return this.stargateBlockEntity;
	}
	
	@Override
	public void onStargateBlockEntityUnloaded()
	{
		if(getServer() != null)
			invalidateCachedStargateEntity();
	}
	
	@Override
	public void onStargateBlockEntityRemoved()
	{
		if(getServer() != null)
			invalidateCachedStargateEntity();
	}
	
	@Override
	public void loadFromBlockEntity(AbstractStargateEntity<?> stargate)
	{
		SGJourneyBlockEntityStargate.super.loadFromBlockEntity(stargate);
		
		this.id9ChevronAddress = stargate.get9ChevronAddress();
		
		this.level = stargate.getLevel();
		this.dimension = stargate.getLevel().dimension();
		this.blockPos = stargate.getBlockPos();
		
		this.hasDHD = dhdCache.isPresent();
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
		this.connectionTime = 0;
		this.kawooshTime = 0;
		this.openTime = 0;
		this.timeSinceLastTraveler = 0;
		
		invalidateCachedStargateEntity();
		return SGJourneyBlockEntityStargate.super.resetStargate(feedback);
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
	public void updateFromBlockEntity(PegasusStargateEntity stargate)
	{
		// When it comes to the DHD, this update method should only take cached values, as the cache changing is what causes the update in the first place
		this.hasDHD = dhdCache.isCached();
		
		this.timesOpened = stargate.getTimesOpened();
		
		// Retrieving cached ones here too because they're influenced by the DHD
		this.hasNetworkRestrictions = stargate.hasCachedNetworkRestrictions();
		this.networks = stargate.getCachedNetworks();
		
		// Make sure any changes are saved to the Block Entity List
		if(!stargate.getLevel().isClientSide())
			updateBlockEntityList();
	}
	
	@Override
	public void onClientUpdate(@Nullable CompoundTag tag)
	{
		super.onClientUpdate(tag);
		
		if(tag != null)
		{
			stargateRun(stargate ->
			{
				stargate.setKawooshTime(getKawooshTime());
				stargate.setOpenTime(getOpenTime());
				stargate.setTimeSinceLastTraveler(getTimeSinceLastTraveler());
			});
		}
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
