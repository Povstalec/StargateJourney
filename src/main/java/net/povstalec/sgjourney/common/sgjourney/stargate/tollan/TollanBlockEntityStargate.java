package net.povstalec.sgjourney.common.sgjourney.stargate.tollan;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

import javax.annotation.Nullable;

public class TollanBlockEntityStargate extends TollanStargate implements SGJourneyBlockEntityStargate<TollanStargateEntity>
{
	// Stargate BlockEntity cache
	protected boolean stargateCacheRequiresUpdate = true;
	protected TollanStargateEntity stargateBlockEntity = null;
	
	protected BlockPos blockPos;
	
	// Direction cache
	@Nullable
	protected Vec3 forward = null;
	@Nullable
	protected Vec3 up = null;
	@Nullable
	protected Vec3 right = null;
	
	public TollanBlockEntityStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	public TollanBlockEntityStargate(StargateType<?> type, MinecraftServer server, TollanStargateEntity stargate)
	{
		this(type, server);
		
		loadFromBlockEntity(stargate);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	protected void cacheStargateEntity(@Nullable TollanStargateEntity stargate)
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
	
	protected @Nullable TollanStargateEntity fetchStargateEntity()
	{
		Level level = getLevel();
		if(level != null && level.getBlockEntity(blockPos) instanceof TollanStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	@Override
	public @Nullable TollanStargateEntity getStargateBlockEntity()
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
	public void updateFromBlockEntity(TollanStargateEntity stargate)
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
	public void serializeNBT(CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putIntArray(COORDINATES, Conversion.blockPosToIntArray(blockPos));
		
		super.serializeNBT(tag, registries);
	}
	
	@Override
	public void deserializeNBT(Address.Immutable id9ChevronAddress, CompoundTag tag, HolderLookup.Provider registries)
	{
		if(tag.contains(COORDINATES, Tag.TAG_INT_ARRAY))
			blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		else if(tag.contains("Coordinates", Tag.TAG_INT_ARRAY)) //TODO Keeping this here for the time being for legacy reasons
			blockPos = Conversion.intArrayToBlockPos(tag.getIntArray("Coordinates"));
		
		super.deserializeNBT(id9ChevronAddress, tag, registries);
		
		if(!tag.contains(HAS_DHD) || !tag.contains(TIMES_OPENED) || !tag.contains(NETWORKS))
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof TollanStargateEntity stargate)
				loadFromBlockEntity(stargate);
		}
	}
}
