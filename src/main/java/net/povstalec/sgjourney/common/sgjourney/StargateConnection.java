package net.povstalec.sgjourney.common.sgjourney;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;

public final class StargateConnection
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
	protected Stargate dialedStargate; // Dialed Stargates can be changed mid connection
	protected boolean doKawoosh;
	
	protected boolean used;
	protected int openTime;
	protected int connectionTime;
	protected int timeSinceLastTraveler;
	
	private StargateConnection(UUID uuid, StargateConnection.Type connectionType, Stargate dialingStargate, Stargate dialedStargate,
							   boolean used, int openTime, int connectionTime, int timeSinceLastTraveler, boolean doKawoosh)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.dialingStargate = dialingStargate;
		this.dialedStargate = dialedStargate;
		this.used = used;
		this.openTime = openTime;
		this.connectionTime = connectionTime;
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
		System.out.println(" | Open Time: " + openTime);
		System.out.println(" | Connection Time: " + connectionTime);
	}
	
	public static final StargateConnection.Type getType(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate)
	{
		SolarSystem.Serializable dialingSystem = Universe.get(server).getSolarSystemFromDimension(dialingStargate.getDimension());
		SolarSystem.Serializable dialedSystem = Universe.get(server).getSolarSystemFromDimension(dialedStargate.getDimension());
		
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
			
			dialingStargate.setKawooshTickCount(server, 0);
			dialingStargate.updateClient(server);
			dialedStargate.setKawooshTickCount(server, 0);
			dialedStargate.updateClient(server);

			dialingStargate.connectStargate(server, uuid, StargateConnection.State.OUTGOING_CONNECTION);
			dialedStargate.connectStargate(server, uuid, StargateConnection.State.INCOMING_CONNECTION);
			
			return new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, doKawoosh);
		}
		return null;
	}
	
	public final void terminate(MinecraftServer server, StargateInfo.Feedback feedback)
	{
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
		
		StargateNetwork.get(server).removeConnection(uuid, feedback);
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
		if(this.openTime == 0)
			this.dialedStargate.updateInterfaceBlocks(server, null, EVENT_INCOMING_CONNECTION);
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = !doKawoosh() ? StargateInfo.ChevronLockSpeed.FAST : this.dialedStargate.getChevronLockSpeed(server);
		int chevronWaitTicks = chevronLockSpeed.getChevronWaitTicks();
		int kawooshStartTicks = chevronLockSpeed.getKawooshStartTicks();
		int maxKawooshTicks = kawooshStartTicks + KAWOOSH_TICKS;
		int maxOpenTicks = maxKawooshTicks + VORTEX_TICKS;
		
		this.increaseTicks(kawooshStartTicks, maxKawooshTicks, maxOpenTicks);
		int realOpenTime = this.openTime - kawooshStartTicks;
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons
		if(this.openTime <= kawooshStartTicks)
		{
			int addressLength = this.dialingStargate.getAddress(server).getLength();
			Address dialingAddress = this.dialingStargate.getConnectionAddress(server, addressLength);
			
			this.dialedStargate.setChevronConfiguration(server, AbstractStargateEntity.getChevronConfiguration(addressLength));
			
			// Used for handling what the Stargate does when it's being dialed
			// For example: Pegasus Stargate's ring booting up
			this.dialingStargate.doWhileConnecting(server, false, doKawoosh(), kawooshStartTicks, this.openTime);
			this.dialedStargate.doWhileConnecting(server, true, doKawoosh(), kawooshStartTicks, this.openTime);
			
			this.dialedStargate.doWhileDialed(server, dialingAddress, kawooshStartTicks, chevronLockSpeed, this.openTime);
			
			// Updates Interfaces when a wormhole is detected
			if(this.openTime == kawooshStartTicks)
			{
				List<Integer> emptyAddressList = Arrays.stream(new int[] {}).boxed().toList();
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
		if(this.openTime < maxOpenTicks)
		{
			this.dialingStargate.doKawoosh(server, realOpenTime);
			this.dialedStargate.doKawoosh(server, realOpenTime);
		}
		else
		{
			this.dialingStargate.setKawooshTickCount(server, realOpenTime);
			this.dialingStargate.updateClient(server);
			this.dialedStargate.setKawooshTickCount(server, realOpenTime);
			this.dialedStargate.updateClient(server);
		}
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(doKawoosh() && this.openTime < maxKawooshTicks)
			return;

		this.dialingStargate.doWhileConnected(server, false, this.openTime);
		this.dialedStargate.doWhileConnected(server, true, this.openTime);
		
		if(this.connectionTime >= maxOpenTime && !energyBypassEnabled)
		{
			terminate(server, StargateInfo.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		// Depletes energy over time
		if(requireEnergy)
		{
			long energyDraw = this.connectionType.getPowerDraw(this.connectionTime >= maxOpenTime);
			
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
		
		doWormhole(this.dialingStargate.getStargateEntity(server).getWormhole(), this.dialingStargate.getStargateEntity(server), this.dialedStargate.getStargateEntity(server), StargateInfo.WormholeTravel.ENABLED);
		doWormhole(this.dialedStargate.getStargateEntity(server).getWormhole(), this.dialedStargate.getStargateEntity(server), this.dialingStargate.getStargateEntity(server), CommonStargateConfig.two_way_wormholes.get());
		
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		if(this.dialingStargate.autoclose(server) > 0 && this.timeSinceLastTraveler >= this.dialingStargate.autoclose(server) * 20)
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
		
		if(this.dialedStargate.autoclose(server) > 0 && this.timeSinceLastTraveler >= this.dialedStargate.autoclose(server) * 20)
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
	}
	
	private final void increaseTicks(int kawooshStartTicks, int maxKawooshTicks, int maxOpenTicks)
	{
		if(!doKawoosh() && this.openTime >= kawooshStartTicks && this.openTime < maxKawooshTicks)
			this.openTime += KAWOOSH_TICKS + VORTEX_TICKS;
		else if(this.openTime < maxOpenTicks)
			this.openTime++;
		
		if(this.openTime > maxKawooshTicks)
			this.connectionTime++;
	}
	
	private final void doWormhole(Wormhole wormhole, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, StargateInfo.WormholeTravel wormholeTravel)
	{
		if(initialStargate instanceof IrisStargateEntity irisStargate && irisStargate.irisInfo().isIrisClosed())
			return;
		
		Vec3 stargatePos = initialStargate.getCenter();
		
		if(wormhole.findCandidates(initialStargate.getLevel(), stargatePos, initialStargate.getDirection()) && this.used)
			this.timeSinceLastTraveler = 0;
		if(targetStargate.dhdInfo().shouldCallForward())
		{
			if(wormhole.wormholeEntities(initialStargate, initialStargate, wormholeTravel))
				this.used = true;
		}
		else
		{
			if(wormhole.wormholeEntities(initialStargate, targetStargate, wormholeTravel))
				this.used = true;
		}
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
	//******************************************Getters*******************************************
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
	
	public int getOpenTime()
	{
		return this.openTime;
	}
	
	public int getConnectionTime()
	{
		return this.connectionTime;
	}
	
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
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
		tag.putInt(OPEN_TIME, this.openTime);
		tag.putInt(CONNECTION_TIME, this.connectionTime);
		tag.putInt(TIME_SINCE_LAST_TRAVELER, this.timeSinceLastTraveler);
		tag.putString(CONNECTION_TYPE, this.connectionType.toString().toUpperCase());
		tag.putBoolean(DO_KAWOOSH, this.doKawoosh);
		
		return tag;
	}
	
	protected CompoundTag serializeStargate(Stargate stargate)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putIntArray(ADDRESS, stargate.get9ChevronAddress().toArray());
		
		//TODO Remove
		tag.putString(DIMENSION, stargate.getDimension().toString());
		tag.putIntArray(COORDINATES, new int[] {stargate.getBlockPos().getX(), stargate.getBlockPos().getY(), stargate.getBlockPos().getZ()});
		
		return tag;
	}
	
	@Nullable
	public static StargateConnection deserialize(MinecraftServer server, UUID uuid, CompoundTag tag)
	{
		Type connectionType = Type.valueOf(tag.getString(CONNECTION_TYPE));
		Stargate dialingStargate = deserializeStargate(server, tag.getCompound(DIALING_STARGATE));
		Stargate dialedStargate = deserializeStargate(server, tag.getCompound(DIALED_STARGATE));
		boolean used = tag.getBoolean(USED);
		int openTime = tag.getInt(OPEN_TIME);
		int connectionTime = tag.getInt(CONNECTION_TIME);
		int timeSinceLastTraveler = tag.getInt(TIME_SINCE_LAST_TRAVELER);
		boolean doKawoosh = tag.getBoolean(DO_KAWOOSH);
		
		return new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, used, openTime, connectionTime, timeSinceLastTraveler, doKawoosh);
	}
	
	private static Stargate deserializeStargate(MinecraftServer server, CompoundTag stargateInfo)
	{
		return BlockEntityList.get(server).getStargate(new Address.Immutable(stargateInfo.getIntArray(ADDRESS)));
	}
}
