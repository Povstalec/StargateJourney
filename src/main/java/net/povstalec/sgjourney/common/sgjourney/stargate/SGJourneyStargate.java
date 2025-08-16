package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class SGJourneyStargate implements Stargate
{
	private Address.Immutable address;
	
	@Nullable
	private WeakReference<AbstractStargateEntity> stargate;
	private ResourceKey<Level> dimension;
	private BlockPos blockPos;
	
	// Preferred Stargate decision
	private boolean hasDHD;
	private StargateInfo.Gen generation;
	private int timesOpened;
	private int network;
	
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
	
	
	
	public ResourceKey<Level> getDimension()
	{
		return this.dimension;
	}
	
	public BlockPos getBlockPos()
	{
		return this.blockPos;
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
	
	@Override
	public Address getConnectionAddress(MinecraftServer server, int addressLength)
	{
		return stargateReturn(server, stargate -> stargate.getConnectionAddress(addressLength), get9ChevronAddress().mutable());
	}
	
	private AbstractStargateEntity cacheStargateEntity(AbstractStargateEntity stargate)
	{
		//this.stargate = new WeakReference(stargate); //TODO Bring caching back once Stargates are more flexible
		
		return stargate;
	}
	
	@Nullable
	private AbstractStargateEntity tryCacheStargateEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractStargateEntity stargate)
			return cacheStargateEntity(stargate);
		
		return null;
	}
	
	@Nullable
	public AbstractStargateEntity getStargateEntity(MinecraftServer server)
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
	
	
	
	public void update(AbstractStargateEntity stargate)
	{
		this.hasDHD = stargate.dhdInfo().hasDHD();
		this.generation = stargate.getGeneration();
		this.timesOpened = stargate.getTimesOpened();
		this.network = stargate.getNetwork();
	}
	
	public boolean checkStargateEntity(MinecraftServer server)
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
	
	@Override
	public void chevronSound(MinecraftServer server, short chevron, boolean incoming, boolean open, boolean encode)
	{
		stargateRun(server, stargate -> stargate.chevronSound(chevron, incoming, open, encode));
	}
	
	@Override
	public void idleWormholeSound(MinecraftServer server, boolean incoming)
	{
		stargateRun(server, stargate -> stargate.idleWormholeSound(incoming));
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
	public void doKawoosh(MinecraftServer server, int kawooshTime)
	{
		stargateRun(server, stargate -> stargate.doKawoosh(kawooshTime));
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
	public StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		AbstractStargateEntity targetStargateEntity = getStargateEntity(server);
		
		if(targetStargateEntity == null)
			return StargateInfo.Feedback.UNKNOWN_ERROR;
		
		// If last Stargate is obstructed
		if(targetStargateEntity.isObstructed())
			return StargateInfo.Feedback.TARGET_OBSTRUCTED;
		
		// If last Stargate is restricted
		if(targetStargateEntity.isRestricted(dialingStargate.getNetwork()))
			return StargateInfo.Feedback.TARGET_RESTRICTED;
		
		// If last Stargate has a blacklist
		if(targetStargateEntity.addressFilterInfo().getFilterType().isBlacklist() && targetStargateEntity.addressFilterInfo().isAddressBlacklisted(dialingAddress))
			return StargateInfo.Feedback.BLACKLISTED_BY_TARGET;
		
		// If last Stargate has a whitelist
		if(targetStargateEntity.addressFilterInfo().getFilterType().isWhitelist() && !targetStargateEntity.addressFilterInfo().isAddressWhitelisted(dialingAddress))
			return StargateInfo.Feedback.NOT_WHITELISTED_BY_TARGET;
		
		return Dialing.connectStargates(server, dialingStargate, this, addressType, doKawoosh);
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
	public void doWhileDialed(MinecraftServer server, Address connectedAddress, boolean doKawoosh, int kawooshStartTicks, StargateInfo.ChevronLockSpeed chevronLockSpeed, int openTime)
	{
		stargateRun(server, stargate -> stargate.doWhileDialed(connectedAddress, doKawoosh, kawooshStartTicks, chevronLockSpeed, openTime));
	}
	
	@Override
	public void setKawooshTickCount(MinecraftServer server, int kawooshTick)
	{
		stargateRun(server, stargate -> stargate.setKawooshTickCount(kawooshTick));
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
