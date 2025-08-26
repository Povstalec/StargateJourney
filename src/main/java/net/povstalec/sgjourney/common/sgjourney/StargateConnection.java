package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;

public class StargateConnection
{
	private static final String EVENT_CHEVRON_ENGAGED = AbstractStargateEntity.EVENT_CHEVRON_ENGAGED;
	private static final String EVENT_INCOMING_CONNECTION = "stargate_incoming_connection";
	private static final String EVENT_INCOMING_WORMHOLE = "stargate_incoming_wormhole";
	private static final String EVENT_OUTGOING_WORMHOLE = "stargate_outgoing_wormhole";
	private static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	//TODO Use snake_case
	private static final String ADDRESS = "address";
	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";

	private static final String USED = "Used";
	private static final String TIME_SINCE_LAST_TRAVELER = "TimeSinceLastTraveler";
	private static final String OPEN_TIME = "OpenTime";
	private static final String CONNECTION_TIME = "ConnectionTime";
	private static final String CONNECTION_TYPE = "ConnectionType";
	private static final String DO_KAWOOSH = "DoKawoosh";
	
	public static final int KAWOOSH_TICKS = 40;
	public static final int VORTEX_TICKS = 20;
	
	protected static final int maxOpenTime = CommonStargateConfig.max_wormhole_open_time.get() * 20;
	protected static final boolean energyBypassEnabled = CommonStargateConfig.enable_energy_bypass.get();
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	protected static final long systemWideConnectionCost = CommonStargateConfig.system_wide_connection_energy_cost.get();
	protected static final long systemWideConnectionDraw = CommonStargateConfig.system_wide_connection_energy_draw.get();
	protected static final long systemWideConnectionBypassDraw = CommonStargateConfig.system_wide_connection_bypass_energy_draw.get();
	
	protected static final long interstellarConnectionCost = CommonStargateConfig.interstellar_connection_energy_cost.get();
	protected static final long interstellarConnectionDraw = CommonStargateConfig.interstellar_connection_energy_draw.get();
	protected static final long interstellarConnectionBypassDraw = CommonStargateConfig.interstellar_connection_bypass_energy_draw.get();
	
	protected static final long intergalacticConnectionCost = CommonStargateConfig.intergalactic_connection_energy_cost.get();
	protected static final long intergalacticConnectionDraw = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	protected static final long intergalacticConnectionBypassDraw = CommonStargateConfig.intergalactic_connection_bypass_energy_draw.get();
	
	protected final UUID uuid;
	protected final StargateConnection.Type connectionType;
	protected Stargate dialingStargate;
	protected Stargate dialedStargate; // Dialed Stargates can be changed mid-connection
	protected boolean doKawoosh;
	
	protected boolean used;
	protected int connectionTime; // Time since the connection was established (Right after dialing Stargate finished dialing)
	protected int openTime; // Time since wormhole formed (after kawoosh ended)
	protected int timeSinceLastTraveler; // Time since a traveler has last appeared near any of the connected Stargates
	
	private StargateConnection(UUID uuid, StargateConnection.Type connectionType, Stargate dialingStargate, Stargate dialedStargate,
							   boolean used, int connectionTime, int openTime, int timeSinceLastTraveler, boolean doKawoosh)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.dialingStargate = dialingStargate;
		this.dialedStargate = dialedStargate;
		this.used = used;
		this.connectionTime = connectionTime;
		this.openTime = openTime;
		this.timeSinceLastTraveler = timeSinceLastTraveler;
		this.doKawoosh = doKawoosh;
	}
	
	public enum Type
	{
		SYSTEM_WIDE(systemWideConnectionCost, systemWideConnectionDraw, systemWideConnectionBypassDraw),
		INTERSTELLAR(interstellarConnectionCost, interstellarConnectionDraw, interstellarConnectionBypassDraw),
		INTERGALACTIC(intergalacticConnectionCost, intergalacticConnectionDraw, intergalacticConnectionBypassDraw);
		
		private long establishingPowerCost;
		private long powerDraw;
		private long bypassPowerDraw;
		
		Type(long establishingPowerCost, long powerDraw, long bypassPowerDraw)
		{
			this.establishingPowerCost = establishingPowerCost;
			this.powerDraw = powerDraw;
			this.bypassPowerDraw = bypassPowerDraw;
		}
		
		public long getEstablishingPowerCost()
		{
			return this.establishingPowerCost;
		}
		
		public long getPowerDraw(boolean energyBypass)
		{
			return energyBypass ? this.bypassPowerDraw : this.powerDraw;
		}
	}
	
	public enum State
	{
		IDLE((byte) 0, false, false),
		
		OUTGOING_CONNECTION((byte) 1, true, true),
		INCOMING_CONNECTION((byte) -1, true, false);
		
		private final byte value;
		private final boolean isConnected;
		private final boolean isDialingOut;
		
		State(byte value, boolean isConnected, boolean isDialingOut)
		{
			this.value = value;
			this.isConnected = isConnected;
			this.isDialingOut = isDialingOut;
		}

		public byte byteValue()
		{
			return this.value;
		}
		
		public boolean isConnected()
		{
			return this.isConnected;
		}
		
		public boolean isDialingOut()
		{
			return this.isDialingOut;
		}
		
		public static State fromByte(byte value)
		{
			return switch (value)
			{
				case 1 -> OUTGOING_CONNECTION;
				case -1 -> INCOMING_CONNECTION;
				default -> IDLE;
			};
		}

	}
	
	//============================================================================================
	//******************************************Utility*******************************************
	//============================================================================================
	
	public final void printConnection()
	{
		System.out.println("-[" + uuid + "]");
		System.out.println(" | From: " + dialingStargate.get9ChevronAddress().toString());
		System.out.println(" | To: " + dialedStargate.get9ChevronAddress().toString());
		System.out.println(" | Connection Time: " + connectionTime);
		System.out.println(" | Open Time: " + openTime);
	}
	
	public static final StargateConnection.Type getType(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate)
	{
		SolarSystem.Serializable dialingSystem = dialingStargate.getSolarSystem(server);
		SolarSystem.Serializable dialedSystem = dialedStargate.getSolarSystem(server);
		
		if(dialingSystem != null && dialedSystem != null)
		{
			if(dialingSystem.equals(dialedSystem))
				return StargateConnection.Type.SYSTEM_WIDE;
			
			List<Entry<Galaxy.Serializable, Address.Immutable>> dialingGalaxies = dialingSystem.getGalacticAddresses().entrySet().stream().toList();
			List<Entry<Galaxy.Serializable, Address.Immutable>> dialedGalaxies = dialedSystem.getGalacticAddresses().entrySet().stream().toList();
			
			if(!dialingGalaxies.isEmpty() && !dialedGalaxies.isEmpty())
			{
				for(int i = 0; i < dialingGalaxies.size(); i++)
				{
					for(int j = 0; j < dialedGalaxies.size(); j++)
					{
						Galaxy.Serializable dialingGalaxy = dialingGalaxies.get(i).getKey();
						Galaxy.Serializable dialedGalaxy = dialedGalaxies.get(j).getKey();
						
						if(dialingGalaxy.equals(dialedGalaxy))
							return StargateConnection.Type.INTERSTELLAR;
					}
				}
			}
		}
		
		return StargateConnection.Type.INTERGALACTIC;
	}
	
	private StargateConnection(UUID uuid, StargateConnection.Type connectionType, Stargate dialingStargate, Stargate dialedStargate, boolean doKawoosh)
	{
		this(uuid, connectionType, dialingStargate, dialedStargate, false, 0, 0, 0, doKawoosh);
	}
	
	public static final StargateConnection create(MinecraftServer server, StargateConnection.Type connectionType, Stargate dialingStargate, Stargate dialedStargate, boolean doKawoosh)
	{
		UUID uuid = UUID.randomUUID();
		
		if(dialingStargate != null && dialedStargate != null)
		{
			dialedStargate.resetStargate(server, StargateInfo.Feedback.INTERRUPTED_BY_INCOMING_CONNECTION, true);
			
			dialingStargate.updateTimers(server, 0, 0, 0, 0);
			dialingStargate.updateClient(server);
			dialedStargate.updateTimers(server, 0, 0, 0, 0);
			dialedStargate.updateClient(server);
			
			StargateConnection stargateConnection = new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, doKawoosh);

			dialingStargate.connectStargate(server, stargateConnection, StargateConnection.State.OUTGOING_CONNECTION);
			dialedStargate.connectStargate(server, stargateConnection, StargateConnection.State.INCOMING_CONNECTION);
			
			return stargateConnection;
		}
		return null;
	}
	
	public final void terminate(MinecraftServer server, StargateInfo.Feedback feedback)
	{
		SGJourneyEvents.onConnectionTerminated(server, this, feedback);
		
		if(this.dialingStargate != null)
		{
			this.dialingStargate.updateInterfaceBlocks(server, null, EVENT_DISCONNECTED, feedback.getCode(), true); // true: Was dialing out
			this.dialingStargate.resetStargate(server, feedback, true);
		}
		
		if (this.dialedStargate != null)
		{
			this.dialedStargate.updateInterfaceBlocks(server, null, EVENT_DISCONNECTED, feedback.getCode(), false); // false: Was being dialed
			this.dialedStargate.resetStargate(server, feedback, true);
		}
		
		StargateNetwork.get(server).removeConnection(uuid);
	}
	
	//TODO make this work
	/*public void reroute(AbstractStargateEntity newDialedStargate)
	{
		if(this.dialedStargate != null)
			this.dialedStargate.resetStargate(Stargate.Feedback.CONNECTION_REROUTED);
		newDialedStargate.connectStargate(this.uuid, false);
	}*/
	
	public final boolean isStargateValid(MinecraftServer server, Stargate stargate) //TODO Remove
	{
		if(stargate == null)
		{
			StargateJourney.LOGGER.error("Stargate does not exist");
			return false;
		}
		
		if(!stargate.isConnected(server))
		{
			StargateJourney.LOGGER.info("Stargate is not connected");
			return false;
		}
		
		return true;
	}
	
	public final void tick(MinecraftServer server)
	{
		if(!isStargateValid(server, this.dialingStargate) || !isStargateValid(server, this.dialedStargate))
		{
			terminate(server, StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
			return;
		}
		
		// Updates Interfaces when incoming connection is detected
		if(this.connectionTime == 0)
			this.dialedStargate.updateInterfaceBlocks(server, null, EVENT_INCOMING_CONNECTION);
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = !doKawoosh() ? StargateInfo.ChevronLockSpeed.FAST : this.dialedStargate.getChevronLockSpeed(server);
		int kawooshStartTicks = chevronLockSpeed.getKawooshStartTicks();
		int maxKawooshTicks = kawooshStartTicks + KAWOOSH_TICKS;
		int maxOpeningTicks = maxKawooshTicks + VORTEX_TICKS;
		
		this.increaseTicks(kawooshStartTicks, maxKawooshTicks, maxOpeningTicks);
		int kawooshTime = this.connectionTime - kawooshStartTicks;
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons
		if(this.connectionTime <= kawooshStartTicks)
		{
			int addressLength = this.dialingStargate.getAddress(server).getLength();
			Address dialingAddress = this.dialingStargate.getConnectionAddress(server, dialedStargate.getSolarSystem(server), addressLength);
			
			this.dialedStargate.setChevronConfiguration(server, Dialing.getChevronConfiguration(dialingAddress.getLength()));
			
			// Used for handling what the Stargate does when it's being dialed
			// For example: Pegasus Stargate's ring booting up
			this.dialingStargate.doWhileConnecting(server, false, doKawoosh(), kawooshStartTicks, this.connectionTime);
			this.dialedStargate.doWhileConnecting(server, true, doKawoosh(), kawooshStartTicks, this.connectionTime);
			
			this.dialedStargate.doWhileDialed(server, dialingAddress, kawooshStartTicks, chevronLockSpeed, this.connectionTime);
			
			// Updates Interfaces when a wormhole is detected
			if(this.connectionTime == kawooshStartTicks)
			{
				List<Integer> emptyAddressList = new ArrayList<>();
				List<Integer> dialedAddressList = Arrays.stream(dialedStargate.getAddress(server).toArray()).boxed().toList();
				dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.BASIC, EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.CRYSTAL, EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.ADVANCED_CRYSTAL, EVENT_INCOMING_WORMHOLE, dialedAddressList);
				List<Integer> dialingAddressList = Arrays.stream(dialingStargate.getAddress(server).toArray()).boxed().toList();
				dialingStargate.updateInterfaceBlocks(server, null, EVENT_OUTGOING_WORMHOLE, dialingAddressList);
			}
			
			return;
		}
		
		// Handles kawoosh progress
		if(this.connectionTime < maxOpeningTicks)
		{
			this.dialingStargate.doKawoosh(server, kawooshTime);
			this.dialedStargate.doKawoosh(server, kawooshTime);
		}
		else
		{
			this.dialingStargate.updateTimers(server, this.connectionTime, kawooshTime, this.openTime, this.timeSinceLastTraveler);
			this.dialingStargate.updateClient(server);
			this.dialedStargate.updateTimers(server, this.connectionTime, kawooshTime, this.openTime, this.timeSinceLastTraveler);
			this.dialedStargate.updateClient(server);
		}
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(doKawoosh() && this.connectionTime < maxKawooshTicks)
			return;

		this.dialingStargate.doWhileConnected(server, false, this.connectionTime);
		this.dialedStargate.doWhileConnected(server, true, this.connectionTime);
		
		if(this.openTime >= maxOpenTime && !energyBypassEnabled)
		{
			terminate(server, StargateInfo.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		// Depletes energy over time
		if(requireEnergy)
		{
			long energyDraw = this.connectionType.getPowerDraw(this.openTime >= maxOpenTime);
			
			if(!this.dialingStargate.canExtractEnergy(server, energyDraw) && !this.dialedStargate.canExtractEnergy(server, energyDraw))
			{
				terminate(server, StargateInfo.Feedback.RAN_OUT_OF_POWER);
				return;
			}
			
			if(CommonStargateConfig.can_draw_power_from_both_ends.get() && this.dialedStargate.getEnergyStored(server) > this.dialingStargate.getEnergyStored(server))
				this.dialedStargate.depleteEnergy(server, energyDraw, false);
			else
				this.dialingStargate.depleteEnergy(server, energyDraw, false);
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		this.dialingStargate.doWormhole(server, this, false, StargateInfo.WormholeTravel.ENABLED);
		this.dialedStargate.doWormhole(server, this, true, CommonStargateConfig.two_way_wormholes.get());
		
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		if(this.dialingStargate.autoclose(server) > 0 && this.timeSinceLastTraveler >= this.dialingStargate.autoclose(server))
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
		
		if(this.dialedStargate.autoclose(server) > 0 && this.timeSinceLastTraveler >= this.dialedStargate.autoclose(server))
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
	}
	
	private final void increaseTicks(int kawooshStartTicks, int maxKawooshTicks, int maxOpenTicks)
	{
		if(!doKawoosh() && this.connectionTime >= kawooshStartTicks && this.connectionTime < maxKawooshTicks)
			this.connectionTime += KAWOOSH_TICKS + VORTEX_TICKS;
		else if(this.connectionTime < maxOpenTicks)
			this.connectionTime++;
		
		if(this.connectionTime > maxKawooshTicks)
			this.openTime++;
	}
	
	public void sendStargateMessage(MinecraftServer server, AbstractStargateEntity sendingStargate, String message)
	{
		if(sendingStargate.get9ChevronAddress().equals(this.dialingStargate.get9ChevronAddress()))
			this.dialedStargate.receiveStargateMessage(server, message);
		else
			this.dialingStargate.receiveStargateMessage(server, message);
	}
	
	public void sendStargateTransmission(MinecraftServer server, AbstractStargateEntity sendingStargate, int transmissionJumps, int frequency, String transmission)
	{
		if(sendingStargate.get9ChevronAddress().equals(this.dialingStargate.get9ChevronAddress()))
			this.dialedStargate.forwardTransmission(server, transmissionJumps, frequency, transmission);
		else
			this.dialingStargate.forwardTransmission(server, transmissionJumps, frequency, transmission);
	}
	
	public float checkStargateShieldingState(MinecraftServer server, AbstractStargateEntity sendingStargate)
	{
		if(sendingStargate.get9ChevronAddress().equals(this.dialingStargate.get9ChevronAddress()))
			return this.dialedStargate.checkStargateShieldingState(server);
		else
			return this.dialingStargate.checkStargateShieldingState(server);
	}
	
	//============================================================================================
	//************************************Getters and Setters*************************************
	//============================================================================================
	
	public UUID getID()
	{
		return this.uuid;
	}
	
	public StargateConnection.Type getConnectionType()
	{
		return this.connectionType;
	}
	
	@Nullable
	public Stargate getDialingStargate()
	{
		return dialingStargate;
	}
	
	@Nullable
	public Stargate getDialedStargate()
	{
		return dialedStargate;
	}
	
	public int getConnectionTime()
	{
		return this.connectionTime;
	}
	
	public int getOpenTime()
	{
		return this.openTime;
	}
	
	public void setTimeSinceLastTraveler(int timeSinceLastTraveler)
	{
		this.timeSinceLastTraveler = timeSinceLastTraveler;
	}
	
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
	}
	
	public void setUsed(boolean used)
	{
		this.used = used;
	}
	
	public boolean used()
	{
		return this.used;
	}
	
	public boolean doKawoosh()
	{
		return this.doKawoosh;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(DIALING_STARGATE, serializeStargate(this.dialingStargate));
		tag.put(DIALED_STARGATE, serializeStargate(this.dialedStargate));
		tag.putBoolean(USED, this.used);
		tag.putInt(CONNECTION_TIME, this.connectionTime);
		tag.putInt(OPEN_TIME, this.openTime);
		tag.putInt(TIME_SINCE_LAST_TRAVELER, this.timeSinceLastTraveler);
		tag.putString(CONNECTION_TYPE, this.connectionType.toString().toUpperCase());
		tag.putBoolean(DO_KAWOOSH, this.doKawoosh);
		
		return tag;
	}
	
	protected CompoundTag serializeStargate(Stargate stargate)
	{
		CompoundTag tag = new CompoundTag();
		tag.putIntArray(ADDRESS, stargate.get9ChevronAddress().toArray());
		return tag;
	}
	
	@Nullable
	public static StargateConnection deserialize(MinecraftServer server, UUID uuid, CompoundTag tag)
	{
		Type connectionType = Type.valueOf(tag.getString(CONNECTION_TYPE));
		Stargate dialingStargate = deserializeStargate(server, tag.getCompound(DIALING_STARGATE));
		Stargate dialedStargate = deserializeStargate(server, tag.getCompound(DIALED_STARGATE));
		boolean used = tag.getBoolean(USED);
		int openTime = tag.getInt(CONNECTION_TIME);
		int connectionTime = tag.getInt(OPEN_TIME);
		int timeSinceLastTraveler = tag.getInt(TIME_SINCE_LAST_TRAVELER);
		boolean doKawoosh = tag.getBoolean(DO_KAWOOSH);
		
		return new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, used, openTime, connectionTime, timeSinceLastTraveler, doKawoosh);
	}
	
	private static Stargate deserializeStargate(MinecraftServer server, CompoundTag stargateInfo)
	{
		return BlockEntityList.get(server).getStargate(new Address.Immutable(stargateInfo.getIntArray(ADDRESS)));
	}
}
