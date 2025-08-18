package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.*;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SGJourneyStargate implements Stargate
{
	public static final double MIN_TRAVELER_SPEED = 0.4;
	public static final double INNER_RADIUS = Wormhole.INNER_RADIUS;
	
	protected Address.Immutable address;
	
	@Nullable
	protected WeakReference<AbstractStargateEntity> stargate;
	protected ResourceKey<Level> dimension;
	protected BlockPos blockPos;
	
	// Preferred Stargate decision
	protected boolean hasDHD;
	protected StargateInfo.Gen generation;
	protected int timesOpened;
	protected int network;
	
	protected Wormhole wormhole = new Wormhole();
	
	public SGJourneyStargate() {}
	
	public SGJourneyStargate(AbstractStargateEntity stargate)
	{
		this.address = stargate.get9ChevronAddress().immutable();
		
		this.dimension = stargate.getLevel().dimension();
		this.blockPos = stargate.getBlockPos();
		
		this.hasDHD = stargate.dhdInfo().hasDHD();
		this.generation = stargate.getGeneration();
		this.timesOpened = stargate.getTimesOpened();
		this.network = stargate.getNetwork();
		
		cacheStargateEntity(stargate);
	}
	
	@Override
	public Address.Immutable get9ChevronAddress()
	{
		return this.address;
	}
	
	
	@Override
	public @Nullable ResourceKey<Level> getDimension()
	{
		return this.dimension;
	}
	
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	@Override
	public @Nullable Vec3 getPosition()
	{
		return getBlockPos().getCenter();
	}
	
	@Override
	public @Nullable SolarSystem.Serializable getSolarSystem(MinecraftServer server)
	{
		return Universe.get(server).getSolarSystemFromDimension(getDimension());
	}
	
	
	
	@Override
	public boolean hasDHD()
	{
		return this.hasDHD;
	}
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return this.generation;
	}
	
	@Override
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	@Override
	public int getNetwork()
	{
		return this.network;
	}
	
	@Override
	public Address getAddress(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.getAddress(), new Address());
	}
	
	private AbstractStargateEntity cacheStargateEntity(AbstractStargateEntity stargate)
	{
		//this.stargate = new WeakReference(stargate); //TODO Bring caching back once Stargates are more flexible
		
		return stargate;
	}
	
	private @Nullable AbstractStargateEntity tryCacheStargateEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractStargateEntity stargate)
			return cacheStargateEntity(stargate);
		
		return null;
	}
	
	public @Nullable AbstractStargateEntity getStargateEntity(MinecraftServer server)
	{
		//if((this.stargate != null && this.stargate.get() != null) || server == null)
		//	return this.stargate.get();
		
		return tryCacheStargateEntity(server);
	}
	
	
	
	@Override
	public StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces)
	{
		AbstractStargateEntity stargateEntity = getStargateEntity(server);
		
		this.stargate = null;
		
		if(stargateEntity != null)
			return stargateEntity.resetStargate(feedback, updateInterfaces);
		else
			StargateJourney.LOGGER.error("Failed to reset Stargate as it does not exist");
		
		return feedback;
	}
	
	@Override
	public boolean isConnected(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isConnected(), false);
	}
	
	@Override
	public boolean isObstructed(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isConnected(), false);
	}
	
	@Override
	public boolean isPrimary(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isPrimary(), false);
	}
	
	@Override
	public void update(MinecraftServer server)
	{
		stargateRun(server, stargate->
		{
			this.hasDHD = stargate.dhdInfo().hasDHD();
			this.generation = stargate.getGeneration();
			this.timesOpened = stargate.getTimesOpened();
			this.network = stargate.getNetwork();
		});
	}
	
	@Override
	public boolean isValid(MinecraftServer server)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate != null)
		{
			stargate.checkStargate();
			return true;
		}
		else
		{
			StargateJourney.LOGGER.error("Stargate not found");
			return false;
		}
	}
	
	@Override
	public void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration)
	{
		stargateRun(server, stargate -> stargate.setEngagedChevrons(chevronConfiguration));
	}
	
	// Client Connection
	
	@Override
	public void updateClient(MinecraftServer server)
	{
		stargateRun(server, stargate -> stargate.updateClient());
	}
	
	// Communication
	
	@Override
	public void receiveStargateMessage(MinecraftServer server, String message)
	{
		stargateRun(server, stargate -> stargate.receiveStargateMessage(message));
	}
	
	@Override
	public void forwardTransmission(MinecraftServer server, int transmissionJumps, int frequency, String transmission)
	{
		stargateRun(server, stargate -> stargate.forwardTransmission(transmissionJumps, frequency, transmission));
	}
	
	@Override
	public float checkStargateShieldingState(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate instanceof IrisStargateEntity irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F, 0F);
	}
	
	@Override
	public void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects)
	{
		stargateRun(server, stargate ->
		{
			if(type == null)
				stargate.updateInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.BASIC)
				stargate.updateBasicInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.CRYSTAL)
				stargate.updateCrystalInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.ADVANCED_CRYSTAL)
				stargate.updateAdvancedCrystalInterfaceBlocks(eventName, objects);
		});
	}
	
	@Override
	public int autoclose(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.dhdInfo().autoclose(), 0);
	}
	
	// Energy
	
	@Override
	public long getEnergyStored(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.getEnergyStored(), 0L);
	}
	
	@Override
	public boolean canExtractEnergy(MinecraftServer server, long energy)
	{
		return stargateReturn(server, stargate -> stargate.canExtractEnergy(energy), false);
	}
	
	@Override
	public void depleteEnergy(MinecraftServer server, long energy, boolean simulate)
	{
		stargateRun(server, stargate -> stargate.depleteEnergy(energy, simulate));
	}
	
	// Stargate Connection
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.getChevronLockSpeed(), StargateInfo.ChevronLockSpeed.SLOW);
	}
	
	@Override
	public StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		return stargateReturn(server, stargate ->
		{
			// If last Stargate is obstructed
			if(stargate.isObstructed())
				return StargateInfo.Feedback.TARGET_OBSTRUCTED;
			
			// If last Stargate is restricted
			if(stargate.isRestricted(dialingStargate.getNetwork()))
				return StargateInfo.Feedback.TARGET_RESTRICTED;
			
			// If last Stargate has a blacklist
			if(stargate.addressFilterInfo().getFilterType().isBlacklist() && stargate.addressFilterInfo().isAddressBlacklisted(dialingStargate.getConnectionAddress(server, getSolarSystem(server), addressType.byteValue()).immutable()))
				return StargateInfo.Feedback.BLACKLISTED_BY_TARGET;
			
			// If last Stargate has a whitelist
			if(stargate.addressFilterInfo().getFilterType().isWhitelist() && !stargate.addressFilterInfo().isAddressWhitelisted(dialingStargate.getConnectionAddress(server, getSolarSystem(server), addressType.byteValue()).immutable()))
				return StargateInfo.Feedback.NOT_WHITELISTED_BY_TARGET;
			
			return Dialing.connectStargates(server, dialingStargate, this, addressType, doKawoosh);
		},
		StargateInfo.Feedback.UNKNOWN_ERROR);
	}
	
	@Override
	public void connectStargate(MinecraftServer server, UUID connectionID, StargateConnection.State connectionState)
	{
		stargateRun(server, stargate -> stargate.connectStargate(connectionID, connectionState));
	}
	
	@Override
	public void doWhileConnecting(MinecraftServer server, boolean incoming, boolean doKawoosh, int kawooshStartTicks, int openTime)
	{
		stargateRun(server, stargate -> stargate.doWhileConnecting(incoming, doKawoosh, kawooshStartTicks, openTime));
	}
	
	@Override
	public void doWhileDialed(MinecraftServer server, Address connectedAddress, int kawooshStartTicks, StargateInfo.ChevronLockSpeed chevronLockSpeed, int openTime)
	{
		stargateRun(server, stargate -> stargate.doWhileDialed(connectedAddress, kawooshStartTicks, chevronLockSpeed, openTime));
	}
	
	@Override
	public void setKawooshTickCount(MinecraftServer server, int kawooshTick)
	{
		stargateRun(server, stargate -> stargate.setKawooshTickCount(kawooshTick));
	}
	
	@Override
	public void doKawoosh(MinecraftServer server, int kawooshTime)
	{
		stargateRun(server, stargate -> stargate.doKawoosh(kawooshTime));
	}
	
	@Override
	public void doWhileConnected(MinecraftServer server, boolean incoming, int openTime)
	{
		stargateRun(server, stargate -> stargate.doWhileConnected(incoming, openTime));
	}
	
	protected void wormholeEntities(MinecraftServer server, StargateConnection connection, Stargate destinationStargate, boolean incoming, StargateInfo.WormholeTravel wormholeTravel, List<Entity> wormholeCandidates)
	{
		stargateRun(server, stargate ->
		{
			Direction direction = stargate.getDirection();
			Orientation orientation = stargate.getOrientation();
			
			Vec3 forward = Orientation.getForwardVector(direction, orientation);
			Vec3 up = Orientation.getUpVector(direction, orientation);
			Vec3 right = forward.cross(up);
			
			if(this.wormhole.wormholeEntities(server, this, destinationStargate, wormholeTravel, stargate.getCenter(), forward, up, right, wormholeCandidates))
				connection.setUsed(true);
		});
	}
	
	@Override
	public void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		stargateRun(server, stargate ->
		{
			List<Entity> wormholeCandidates = stargate.findWormholeCandidates();
			
			// If this Stargate has its iris closed, then there's no point in trying to transport Entities
			if(stargate instanceof IrisStargateEntity irisStargate && irisStargate.irisInfo().isIrisClosed())
				return;
			
			Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
			
			if(!wormholeCandidates.isEmpty() && connection.used())
				connection.setTimeSinceLastTraveler(0);
			
			//TODO Call Forwarding
			wormholeEntities(server, connection, connectedStargate, incoming, wormholeTravel, wormholeCandidates);
		});
	}
	
	@Override
	public boolean receiveTraveler(MinecraftServer server, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		return stargateReturn(server, stargate ->
		{
			Direction direction = stargate.getDirection();
			Orientation orientation = stargate.getOrientation();
			
			// Multiplied some elements by -1 to make the traveler exit the Stargate in a mirrored position
			Vec3 forward = Orientation.getForwardVector(direction, orientation);
			Vec3 up = Orientation.getUpVector(direction, orientation);
			Vec3 right = forward.cross(up);
			right = right.multiply(-1, -1, -1);
			forward = forward.multiply(-1, -1, -1);
			
			// TODO Tie this to Advanced Protocols
			Vec3 tempMomentum = relativeMomentum.x() > -MIN_TRAVELER_SPEED ? new Vec3(-MIN_TRAVELER_SPEED, relativeMomentum.y(), relativeMomentum.z()) : relativeMomentum;
			
			Vec3 destinationPosition = CoordinateHelper.StargateCoords.toStargateCoords(relativePosition, forward, up, right, INNER_RADIUS).add(stargate.getCenter());
			Vec3 destinationMomentum = CoordinateHelper.StargateCoords.toStargateCoords(tempMomentum, forward, up, right);
			Vec3 destinationLookAngle = CoordinateHelper.StargateCoords.toStargateCoords(relativeLookAngle, forward, up, right);
			
			if(stargate instanceof IrisStargateEntity irisStargate && !this.wormhole.checkShielding(irisStargate, destinationPosition, destinationMomentum, traveler))
			{
				this.wormhole.handleShielding(server, irisStargate, traveler);
				return true;
			}
			
			return this.wormhole.receiveTraveler((ServerLevel) stargate.getLevel(), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		},
		false);
	}
	
	// Saving and loading
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag stargateTag = new CompoundTag();
		ResourceKey<Level> level = this.getDimension();
		BlockPos pos = this.getBlockPos();
		
		stargateTag.putString(DIMENSION, level.location().toString());
		stargateTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		
		stargateTag.putBoolean(HAS_DHD, hasDHD);
		stargateTag.putInt(GENERATION, generation.getGen());
		stargateTag.putInt(TIMES_OPENED, timesOpened);
		stargateTag.putInt(NETWORK, network);
		
		return stargateTag;
	}
	
	@Override
	public void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag)
	{
		this.address = address;
		
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		this.blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		if(!tag.contains(HAS_DHD) || !tag.contains(GENERATION) || !tag.contains(TIMES_OPENED) || !tag.contains(NETWORK))
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractStargateEntity stargate)
			{
				this.hasDHD = stargate.dhdInfo().hasDHD();
				this.generation = stargate.getGeneration();
				this.timesOpened = stargate.getTimesOpened();
				this.network = stargate.getNetwork();
				
				cacheStargateEntity(stargate);
			}
			else
				StargateJourney.LOGGER.info("Failed to deserialize Stargate " + address.toString());
		}
		else
		{
			this.hasDHD = tag.getBoolean(HAS_DHD);
			this.generation = StargateInfo.Gen.intToGen(tag.getInt(GENERATION));
			this.timesOpened = tag.getInt(TIMES_OPENED);
			this.network = tag.getInt(NETWORK);
			
			this.stargate = null;
		}
	}
	
	
	
	@Override
	public String toString()
	{
		return "[ " + this.address.toString() + " | DHD: " + this.hasDHD + " | Generation: " + this.generation + " | Times Opened: " + this.timesOpened + " ]";
	}
	
	
	
	public interface StargateConsumer
	{
		void run(AbstractStargateEntity stargate);
	}
	
	public interface ReturnStargateConsumer<T>
	{
		T run(AbstractStargateEntity stargate);
	}
	
	private void stargateRun(MinecraftServer server, StargateConsumer consumer)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate != null)
			consumer.run(stargate);
	}
	
	private <T> T stargateReturn(MinecraftServer server, ReturnStargateConsumer<T> consumer, @Nullable T defaultValue)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate != null)
			return consumer.run(stargate);
		
		return defaultValue;
	}
}
