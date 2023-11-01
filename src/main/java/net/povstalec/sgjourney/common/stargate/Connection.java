package net.povstalec.sgjourney.common.stargate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Connection
{
	private static final String EVENT_CHEVRON_ENGAGED = "stargate_chevron_engaged";
	private static final String EVENT_INCOMING_WORMHOLE = "stargate_incoming_wormhole";
	private static final String EVENT_OUTGOING_WORMHOLE = "stargate_outgoing_wormhole";
	private static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";

	private static final String USED = "Used";
	private static final String TIME_SINCE_LAST_TRAVELER = "TimeSinceLastTraveler";
	private static final String OPEN_TIME = "OpenTime";
	private static final String CONNECTION_TIME = "ConnectionTime";
	private static final String CONNECTION_TYPE = "ConnectionType";
	
	public static final int KAWOOSH_TICKS = 40;
	public static final int VORTEX_TICKS = 20;
	
	protected static final int maxOpenTime = CommonStargateConfig.max_wormhole_open_time.get() * 20;
	protected static final boolean energyBypassEnabled = CommonStargateConfig.enable_energy_bypass.get();
	protected static final int energyBypassMultiplier = CommonStargateConfig.energy_bypass_multiplier.get();
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	protected static final long systemWideConnectionCost = CommonStargateConfig.system_wide_connection_energy_cost.get();
	protected static final long interstellarConnectionCost = CommonStargateConfig.interstellar_connection_energy_cost.get();
	protected static final long intergalacticConnectionCost = CommonStargateConfig.intergalactic_connection_energy_cost.get();

	protected static final long systemWideConnectionDraw = CommonStargateConfig.system_wide_connection_energy_draw.get();
	protected static final long interstellarConnectionDraw = CommonStargateConfig.interstellar_connection_energy_draw.get();
	protected static final long intergalacticConnectionDraw = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	
	protected final String uuid;
	protected final ConnectionType connectionType;
	protected final AbstractStargateEntity dialingStargate;
	protected AbstractStargateEntity dialedStargate; // Dialed Stargates can be changed mid connection
	
	protected boolean used = false;
	protected int openTime = 0;
	protected int connectionTime = 0;
	protected int timeSinceLastTraveler = 0;
	
	private Connection(String uuid, ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, boolean used, int openTime, int connectionTime, int timeSinceLastTraveler)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.dialingStargate = dialingStargate;
		this.dialedStargate = dialedStargate;
		this.used = used;
		this.openTime = openTime;
		this.connectionTime = connectionTime;
		this.timeSinceLastTraveler = timeSinceLastTraveler;
	}
	
	public enum ConnectionType
	{
		SYSTEM_WIDE(systemWideConnectionCost, systemWideConnectionDraw),
		INTERSTELLAR(interstellarConnectionCost, interstellarConnectionDraw),
		INTERGALACTIC(intergalacticConnectionCost, intergalacticConnectionDraw);
		
		private long establishingPowerCost;
		private long powerDraw;
		
		ConnectionType(long estabilishingPowerCost, long powerDraw)
		{
			this.establishingPowerCost = estabilishingPowerCost;
			this.powerDraw = powerDraw;
		}
		
		public long getEstabilishingPowerCost()
		{
			return this.establishingPowerCost;
		}
		
		public long getPowerDraw()
		{
			return this.powerDraw;
		}
	}
	
	private Connection(String uuid, ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		this(uuid, connectionType, dialingStargate, dialedStargate, false, 0, 0, 0);
	}
	
	public static Connection create(ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		String uuid = UUID.randomUUID().toString();
		
		if(dialingStargate != null && dialedStargate != null)
		{
			dialedStargate.resetStargate(Stargate.Feedback.INTERRUPTED_BY_INCOMING_CONNECTION);
			
			dialingStargate.setKawooshTickCount(0);
			dialingStargate.updateClient();
			dialedStargate.setKawooshTickCount(0);
			dialedStargate.updateClient();

			dialingStargate.connectStargate(uuid, ConnectionState.OUTGOING_CONNECTION);
			dialedStargate.connectStargate(uuid, ConnectionState.INCOMING_CONNECTION);
			
			return new Connection(uuid, connectionType, dialingStargate, dialedStargate);
		}
		return null;
	}
	
	public void terminate(MinecraftServer server, Stargate.Feedback feedback)
	{
		if(this.dialingStargate != null)
		{
			this.dialingStargate.updateInterfaceBlocks(EVENT_DISCONNECTED, feedback.getCode());
			this.dialingStargate.resetStargate(feedback);
		}
		if(this.dialedStargate != null)
		{
			this.dialedStargate.updateInterfaceBlocks(EVENT_DISCONNECTED, feedback.getCode());
			this.dialedStargate.resetStargate(feedback);
		}
		
		StargateNetwork.get(server).removeConnection(server, uuid, feedback);
	}
	
	//TODO make this work
	/*public void reroute(AbstractStargateEntity newDialedStargate)
	{
		if(this.dialedStargate != null)
			this.dialedStargate.resetStargate(Stargate.Feedback.CONNECTION_REROUTED);
		newDialedStargate.connectStargate(this.uuid, false);
	}*/
	
	public boolean isStargateValid(AbstractStargateEntity stargate)
	{
		if(stargate == null)
		{
			StargateJourney.LOGGER.info("Stargate does not exist");
			return false;
		}
		
		BlockPos stargatePos = stargate.getBlockPos();
		Level stargateLevel = stargate.getLevel();
		
		if(stargateLevel.getBlockEntity(stargatePos) instanceof AbstractStargateEntity targetStargate)
		{
			if(stargate.isConnected())
				return true;

			StargateJourney.LOGGER.info("Stargate is not connected");
			return false;
		}
		
		return false;
	}
	
	public void tick(MinecraftServer server)
	{
		Stargate.ChevronLockSpeed chevronLockSpeed = this.dialedStargate.getChevronLockSpeed();
		int waitMultiplier = chevronLockSpeed.getMultiplier();
		int chevronWaitTicks = chevronLockSpeed.getChevronWaitTicks();
		int kawooshStartTicks = chevronLockSpeed.getKawooshStartTicks();
		int maxKawooshTicks = kawooshStartTicks + KAWOOSH_TICKS;
		int maxOpenTicks = maxKawooshTicks + VORTEX_TICKS;
		
		if(!isStargateValid(this.dialingStargate) || !isStargateValid(this.dialedStargate))
		{
			terminate(server, Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
			return;
		}
		
		this.increaseTicks(maxKawooshTicks, maxOpenTicks);
		int openTime = this.openTime - kawooshStartTicks;
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons
		if(this.openTime < kawooshStartTicks)
		{
			playStargateOpenSound(this.dialingStargate, kawooshStartTicks, this.openTime);
			playStargateOpenSound(this.dialedStargate, kawooshStartTicks, this.openTime);
			
			int addressLenght = this.dialingStargate.getAddress().getLength();
			Address dialingAddress = new Address().fromString(this.dialingStargate.getConnectionAddress(addressLenght));
			
			this.dialedStargate.setEngagedChevrons(AbstractStargateEntity.getChevronConfiguration(addressLenght));
			
			if(this.dialedStargate instanceof PegasusStargateEntity pegasusStargate && pegasusStargate.currentSymbol < 36)
			{
				pegasusStargate.currentSymbol = this.openTime / waitMultiplier;
				pegasusStargate.updatePegasusStargate();
			}
			
			if(this.openTime % chevronWaitTicks == 0)
			{
				int dialedAddressLength = this.dialedStargate.getAddress().getLength();
				
				if(dialedAddressLength < dialingAddress.getLength())
				{
					if(this.openTime / chevronWaitTicks == 4 && addressLenght < 7)
						return;
					else if(this.openTime / chevronWaitTicks == 5 && addressLenght < 8)
						return;
					else
						this.dialedStargate.encodeChevron(dialingAddress.getSymbol(dialedAddressLength), true);
				}
				else
				{
					this.dialedStargate.chevronSound(true);
					this.dialedStargate.updateInterfaceBlocks(EVENT_CHEVRON_ENGAGED, this.dialedStargate.getAddress().getLength() + 1, 0, true);
				}
			}
			
			return;
		}
		
		// Updates Interfaces when wormhole starts forming
		if(this.openTime == kawooshStartTicks)
		{
			List<Integer> dialedAddress = Arrays.stream(dialedStargate.getAddress().toArray()).boxed().toList();
			dialedStargate.updateInterfaceBlocks(EVENT_INCOMING_WORMHOLE, dialedAddress);
			List<Integer> dialingAddress = Arrays.stream(dialingStargate.getAddress().toArray()).boxed().toList();
			dialingStargate.updateInterfaceBlocks(EVENT_OUTGOING_WORMHOLE, dialingAddress);
		}
		
		// Handles kawoosh progress
		if(this.openTime < maxOpenTicks)
		{
			this.dialingStargate.doKawoosh(openTime);
			this.dialedStargate.doKawoosh(openTime);
		}
		else
		{
			this.dialingStargate.setKawooshTickCount(openTime);
			this.dialingStargate.updateClient();
			this.dialedStargate.setKawooshTickCount(openTime);
			this.dialedStargate.updateClient();
		}
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(this.openTime < maxKawooshTicks)
			return;
		
		this.dialingStargate.idleWormholeSound();
		this.dialedStargate.idleWormholeSound();
		
		if(this.connectionTime >= maxOpenTime && !energyBypassEnabled)
		{
			terminate(server, Stargate.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		// Depletes energy over time
		if(requireEnergy)
		{
			long energyDraw = this.connectionType.getPowerDraw();
			energyDraw = this.connectionTime >= maxOpenTime ? energyDraw * energyBypassMultiplier : energyDraw;
			
			if(this.dialingStargate.getEnergyStored() < energyDraw && this.dialedStargate.getEnergyStored() < energyDraw)
			{
				terminate(server, Stargate.Feedback.RAN_OUT_OF_POWER);
				return;
			}
			
			if(this.dialedStargate.getEnergyStored() > this.dialingStargate.getEnergyStored())
				this.dialedStargate.depleteEnergy(energyDraw, false);
			else
				this.dialingStargate.depleteEnergy(energyDraw, false);
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		doWormhole(this.dialingStargate.getWormhole(), this.dialingStargate, this.dialedStargate, Stargate.WormholeTravel.ENABLED);
		doWormhole(this.dialedStargate.getWormhole(), this.dialedStargate, this.dialingStargate, CommonStargateConfig.two_way_wormholes.get());
		
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		if((this.dialingStargate.advancedProtocolsEnabled() || this.dialedStargate.advancedProtocolsEnabled()) && this.timeSinceLastTraveler >= 200)
			terminate(server, Stargate.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
	}
	
	protected void playStargateOpenSound(AbstractStargateEntity stargate, int kawooshStartTicks, int ticks)
	{
		if(ticks == kawooshStartTicks - stargate.getOpenSoundLead())
			stargate.openWormholeSound();
	}
	
	protected void increaseTicks(int maxKawooshTicks, int maxOpenTicks)
	{
		if(this.openTime < maxOpenTicks)
			this.openTime++;
		
		if(this.openTime > maxKawooshTicks)
			this.connectionTime++;
	}
	
	protected void doWormhole(Wormhole wormhole, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, Stargate.WormholeTravel wormholeTravel)
	{
		BlockPos pos = initialStargate.getCenterPos();
		double yCenter = initialStargate instanceof TollanStargateEntity ? 0.0 : 0.5;
		Vec3 stargatePos = new Vec3(pos.getX() + 0.5, pos.getY() + yCenter, pos.getZ() + 0.5);
		
		if(wormhole.findCandidates(initialStargate.getLevel(), stargatePos, initialStargate.getDirection()) && this.used)
			this.timeSinceLastTraveler = 0;
		if(wormhole.wormholeEntities(initialStargate, targetStargate, wormholeTravel))
			this.used = true;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public String getID()
	{
		return this.uuid;
	}
	
	public int getKawooshTime()
	{
		return this.openTime;
	}
	
	public int getOpenTime()
	{
		return this.connectionTime;
	}
	
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(DIALING_STARGATE, serializeStargate(this.dialingStargate));
		tag.put(DIALED_STARGATE, serializeStargate(this.dialedStargate));
		tag.putInt(OPEN_TIME, this.openTime);
		tag.putInt(CONNECTION_TIME, this.connectionTime);
		tag.putInt(TIME_SINCE_LAST_TRAVELER, this.timeSinceLastTraveler);
		tag.putString(CONNECTION_TYPE, this.connectionType.toString().toUpperCase());
		
		return tag;
	}
	
	protected CompoundTag serializeStargate(AbstractStargateEntity stargate)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString(DIMENSION, stargate.getLevel().dimension().location().toString());
		tag.putIntArray(COORDINATES, new int[] {stargate.getBlockPos().getX(), stargate.getBlockPos().getY(), stargate.getBlockPos().getZ()});
		
		return tag;
	}
	
	public static Connection deserialize(MinecraftServer server, String uuid, CompoundTag tag)
	{
		ConnectionType connectionType = ConnectionType.valueOf(tag.getString(CONNECTION_TYPE));
		AbstractStargateEntity dialingStargate = deserializeStargate(server, tag.getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = deserializeStargate(server, tag.getCompound(DIALED_STARGATE));
		boolean used = tag.getBoolean(USED);
		int openTime = tag.getInt(OPEN_TIME);
		int connectionTime = tag.getInt(CONNECTION_TIME);
		int timeSinceLastTraveler = tag.getInt(TIME_SINCE_LAST_TRAVELER);
		
		return new Connection(uuid, connectionType, dialingStargate, dialedStargate, used, openTime, connectionTime, timeSinceLastTraveler);
	}
	
	protected static AbstractStargateEntity deserializeStargate(MinecraftServer server, CompoundTag stargateInfo)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(stargateInfo.getString(DIMENSION));
		BlockPos pos = Conversion.intArrayToBlockPos(stargateInfo.getIntArray(COORDINATES));
		
		BlockEntity blockEntity = server.getLevel(dimension).getBlockEntity(pos);
		
		if(blockEntity instanceof AbstractStargateEntity stargate)
			return stargate;
		
		return null;
	}
}
