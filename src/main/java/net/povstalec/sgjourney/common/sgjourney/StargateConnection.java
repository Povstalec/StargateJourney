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
import javax.xml.transform.Result;

public class StargateConnection
{
	public static final String EVENT_INCOMING_CONNECTION = "stargate_incoming_connection";
	public static final String EVENT_INCOMING_WORMHOLE = "stargate_incoming_wormhole";
	public static final String EVENT_OUTGOING_WORMHOLE = "stargate_outgoing_wormhole";
	public static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	public static final String ADDRESS = Address.ADDRESS;
	public static final String DIMENSION = "Dimension";
	public static final String COORDINATES = "Coordinates";
	
	public static final String DIALING_STARGATE = "DialingStargate";
	public static final String DIALED_STARGATE = "DialedStargate";
	
	public static final String USED = "Used";
	public static final String TIME_SINCE_LAST_TRAVELER = "TimeSinceLastTraveler";
	public static final String OPEN_TIME = "OpenTime";
	public static final String CONNECTION_TIME = "ConnectionTime";
	public static final String CONNECTION_TYPE = "ConnectionType";
	public static final String DO_KAWOOSH = "DoKawoosh";
	public static final String KAWOOSH_TICKS = "kawoosh_ticks";
	
	public static final int KAWOOSH_DURATION = 40;
	
	protected static final boolean ENERGY_BYPASS_ENABLED = CommonStargateConfig.enable_energy_bypass.get();
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	protected static final long SYSTEM_WIDE_CONNECTION_COST = CommonStargateConfig.system_wide_connection_energy_cost.get();
	protected static final long SYSTEM_WIDE_CONNECTION_DRAW = CommonStargateConfig.system_wide_connection_energy_draw.get();
	protected static final long SYSTEM_WIDE_CONNECTION_BYPASS_DRAW = CommonStargateConfig.system_wide_connection_bypass_energy_draw.get();
	
	protected static final long INTERSTELLAR_CONNECTION_COST = CommonStargateConfig.interstellar_connection_energy_cost.get();
	protected static final long INTERSTELLAR_CONNECTION_DRAW = CommonStargateConfig.interstellar_connection_energy_draw.get();
	protected static final long INTERSTELLAR_CONNECTION_BYPASS_DRAW = CommonStargateConfig.interstellar_connection_bypass_energy_draw.get();
	
	protected static final long INTERGALACTIC_CONNECTION_COST = CommonStargateConfig.intergalactic_connection_energy_cost.get();
	protected static final long INTERGALACTIC_CONNECTION_DRAW = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	protected static final long INTERGALACTIC_CONNECTION_BYPASS_DRAW = CommonStargateConfig.intergalactic_connection_bypass_energy_draw.get();
	
	protected final UUID uuid;
	protected final StargateConnection.Type connectionType;
	protected Stargate dialingStargate;
	protected Stargate dialedStargate; // Dialed Stargates can be changed mid-connection
	protected boolean doKawoosh;
	
	protected boolean used;
	protected int connectionTime; // Time since the connection was established (Right after dialing Stargate finished dialing)
	protected int openTime; // Time since the wormhole formed (after kawoosh ended)
	protected int timeSinceLastTraveler; // Time since a traveler has last appeared near any of the connected Stargates
	
	@Nullable
	private Address.Immutable dialingAddress = null;
	
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
		SYSTEM_WIDE(SYSTEM_WIDE_CONNECTION_COST, SYSTEM_WIDE_CONNECTION_DRAW, SYSTEM_WIDE_CONNECTION_BYPASS_DRAW),
		INTERSTELLAR(INTERSTELLAR_CONNECTION_COST, INTERSTELLAR_CONNECTION_DRAW, INTERSTELLAR_CONNECTION_BYPASS_DRAW),
		INTERGALACTIC(INTERGALACTIC_CONNECTION_COST, INTERGALACTIC_CONNECTION_DRAW, INTERGALACTIC_CONNECTION_BYPASS_DRAW);
		
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
			
			StargateConnection stargateConnection = new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, doKawoosh);

			dialingStargate.connectStargate(server, stargateConnection, StargateConnection.State.OUTGOING_CONNECTION);
			dialedStargate.connectStargate(server, stargateConnection, StargateConnection.State.INCOMING_CONNECTION);
			
			dialingStargate.connectionUpdate(server, stargateConnection);
			dialingStargate.updateClient(server);
			dialedStargate.connectionUpdate(server, stargateConnection);
			dialedStargate.updateClient(server);
			
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
	
	private Address.Immutable getDialingAddress(MinecraftServer server)
	{
		// Get dialing address and cache it for later use
		if(this.dialingAddress == null)
			this.dialingAddress = Address.Immutable.extendWithPointOfOrigin(this.dialingStargate.getConnectionAddress(server, dialedStargate.getSolarSystem(server), this.dialingStargate.getAddress(server).getType()));
		
		return this.dialingAddress;
	}
	
	private void tickEstablishConnection(MinecraftServer server, int kawooshStartTicks)
	{
		Address.Immutable dialingAddress = getDialingAddress(server);
		
		this.dialedStargate.setChevronConfiguration(server, Dialing.getChevronConfiguration(dialingAddress.getType()));
		
		this.dialingStargate.doWhileConnecting(server, this, false, kawooshStartTicks);
		this.dialedStargate.doWhileConnecting(server, this, true, kawooshStartTicks);
		
		// Used for handling what the Stargate does when it's being dialed
		// For example: Pegasus Stargate's ring booting up
		this.dialedStargate.doWhileDialed(server, this, dialingAddress, kawooshStartTicks);
		
		// Updates Interfaces when a wormhole is detected
		if(this.connectionTime == kawooshStartTicks)
		{
			List<Integer> emptyAddressList = new ArrayList<>();
			List<Integer> dialedAddressList = Arrays.stream(dialedStargate.getAddress(server).getArray()).boxed().toList();
			dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.BASIC, EVENT_INCOMING_WORMHOLE, emptyAddressList);
			dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.CRYSTAL, EVENT_INCOMING_WORMHOLE, emptyAddressList);
			dialedStargate.updateInterfaceBlocks(server, AbstractInterfaceEntity.InterfaceType.ADVANCED_CRYSTAL, EVENT_INCOMING_WORMHOLE, dialedAddressList);
			List<Integer> dialingAddressList = Arrays.stream(dialingStargate.getAddress(server).getArray()).boxed().toList();
			dialingStargate.updateInterfaceBlocks(server, null, EVENT_OUTGOING_WORMHOLE, dialingAddressList);
		}
	}
	
	public static boolean canExtract(MinecraftServer server, Stargate stargate, long energyExtracted)
	{
		return stargate.extractEnergy(server, energyExtracted, true) >= energyExtracted;
	}
	
	private boolean depleteEnergy(MinecraftServer server, long energyDraw)
	{
		if(canExtract(server, this.dialingStargate, energyDraw))
		{
			this.dialingStargate.extractEnergy(server, energyDraw, false);
			return true;
		}
		//TODO Tie this to Advanced Protocols
		else if(this.dialedStargate.canPowerFromOtherSide(server) && canExtract(server, this.dialedStargate, energyDraw))
		{
			this.dialedStargate.extractEnergy(server, energyDraw, false);
			return true;
		}
		
		return false;
	}
	
	private boolean requiresEnergyBypass(MinecraftServer server, int openTime)
	{
		return this.dialingStargate.requiresEnergyBypass(server, openTime) || this.dialedStargate.requiresEnergyBypass(server, openTime);
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
		
		int kawooshStartTicks = this.dialedStargate.dialedEngageTime(server, doKawoosh());
		// Time after which the dangerous part of the kawoosh is finished and it's safe to go through the wormhole
		int maxKawooshTicks = kawooshStartTicks + Math.max(this.dialedStargate.wormholeEstablishTime(server, doKawoosh()), this.dialingStargate.wormholeEstablishTime(server, doKawoosh()));
		
		this.increaseTicks(maxKawooshTicks);
		
		this.dialingStargate.connectionUpdate(server, this);
		this.dialingStargate.updateClient(server);
		this.dialedStargate.connectionUpdate(server, this);
		this.dialedStargate.updateClient(server);
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons, then both Stargates do kawoosh
		if(this.connectionTime < maxKawooshTicks)
			tickEstablishConnection(server, kawooshStartTicks);
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(doKawoosh() && this.connectionTime < maxKawooshTicks)
			return;

		this.dialingStargate.doWhileConnected(server, this, false);
		this.dialedStargate.doWhileConnected(server, this, true);
		
		if(requiresEnergyBypass(server, this.openTime) && !ENERGY_BYPASS_ENABLED)
		{
			terminate(server, StargateInfo.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		// Depletes energy over time
		if(REQUIRE_ENERGY && !depleteEnergy(server, getPowerDraw(server)))
		{
			terminate(server, StargateInfo.Feedback.RAN_OUT_OF_POWER);
			return;
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		// Closes the connection if either Stargate requires it
		if(this.dialingStargate.shouldAutoclose(server, this) || this.dialedStargate.shouldAutoclose(server, this))
		{
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
			return;
		}
		
		this.dialingStargate.doWormhole(server, this, false, StargateInfo.WormholeTravel.ENABLED);
		this.dialedStargate.doWormhole(server, this, true, CommonStargateConfig.two_way_wormholes.get());
	}
	
	private void increaseTicks(int maxKawooshTicks)
	{
		this.connectionTime++;
		
		if(this.connectionTime > maxKawooshTicks)
			this.openTime = this.connectionTime - maxKawooshTicks;
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
	
	/**
	 * @return Time (in ticks) since the connection was established (Right after dialing Stargate finished dialing)
	 */
	public int getConnectionTime()
	{
		return this.connectionTime;
	}
	
	/**
	 * @return Time (in ticks) since the kawoosh started
	 */
	public int getKawooshTime(MinecraftServer server)
	{
		int kawooshStartTicks = this.dialedStargate.dialedEngageTime(server, doKawoosh());
		int kawooshTime = this.connectionTime - kawooshStartTicks;
		
		return kawooshTime < 0 ? 0 : kawooshTime;
	}
	
	/**
	 * @return Time (in ticks) since wormhole formed (after kawoosh ended)
	 */
	public int getOpenTime()
	{
		return this.openTime;
	}
	
	public void setTimeSinceLastTraveler(int timeSinceLastTraveler)
	{
		this.timeSinceLastTraveler = timeSinceLastTraveler;
	}
	
	/**
	 * @return Time (in ticks) since a traveler has last appeared near any of the connected Stargates
	 */
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
	}
	
	public void setUsed(boolean used)
	{
		this.used = used;
	}
	
	/**
	 * @return True if a traveler has used this connection to travel through a Stargate
	 */
	public boolean used()
	{
		return this.used;
	}
	
	/**
	 * @return True if the connection involves a kawoosh forming, otherwise false
	 */
	public boolean doKawoosh()
	{
		return this.doKawoosh;
	}
	
	public long getPowerDraw(MinecraftServer server)
	{
		return this.connectionType.getPowerDraw(requiresEnergyBypass(server, this.openTime));
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
	
	
	
	public static class Result
	{
		public static final String FEEDBACK = "feedback";
		
		protected Address address;
		protected StargateInfo.Feedback feedback;
		
		public Result() {}
		
		public Result(Address address, StargateInfo.Feedback feedback)
		{
			this.address = address;
			this.feedback = feedback;
		}
		
		public Address address()
		{
			return address;
		}
		
		public StargateInfo.Feedback feedback()
		{
			return feedback;
		}
		
		public CompoundTag save()
		{
			CompoundTag tag = new CompoundTag();
			
			address.saveToCompoundTag(tag, ADDRESS);
			tag.putInt(FEEDBACK, feedback.ordinal());
			
			return tag;
		}
		
		public void load(CompoundTag tag)
		{
			address = new Address.Immutable(tag.getIntArray(ADDRESS));
			feedback = StargateInfo.Feedback.fromOrdinal(tag.getInt(FEEDBACK));
		}
	}
}
