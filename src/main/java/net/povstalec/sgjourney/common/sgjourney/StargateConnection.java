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
	protected static final int energyBypassMultiplier = CommonStargateConfig.energy_bypass_multiplier.get();
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	protected static final long systemWideConnectionCost = CommonStargateConfig.system_wide_connection_energy_cost.get();
	protected static final long interstellarConnectionCost = CommonStargateConfig.interstellar_connection_energy_cost.get();
	protected static final long intergalacticConnectionCost = CommonStargateConfig.intergalactic_connection_energy_cost.get();

	protected static final long systemWideConnectionDraw = CommonStargateConfig.system_wide_connection_energy_draw.get();
	protected static final long interstellarConnectionDraw = CommonStargateConfig.interstellar_connection_energy_draw.get();
	protected static final long intergalacticConnectionDraw = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	
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
		SYSTEM_WIDE(systemWideConnectionCost, systemWideConnectionDraw),
		INTERSTELLAR(interstellarConnectionCost, interstellarConnectionDraw),
		INTERGALACTIC(intergalacticConnectionCost, intergalacticConnectionDraw);
		
		private long establishingPowerCost;
		private long powerDraw;
		
		Type(long establishingPowerCost, long powerDraw)
		{
			this.establishingPowerCost = establishingPowerCost;
			this.powerDraw = powerDraw;
		}
		
		public long getEstablishingPowerCost()
		{
			return this.establishingPowerCost;
		}
		
		public long getPowerDraw()
		{
			return this.powerDraw;
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
			dialedStargate.getStargateEntity(server).resetStargate(StargateInfo.Feedback.INTERRUPTED_BY_INCOMING_CONNECTION);
			
			dialingStargate.getStargateEntity(server).setKawooshTickCount(0);
			dialingStargate.getStargateEntity(server).updateClient();
			dialedStargate.getStargateEntity(server).setKawooshTickCount(0);
			dialedStargate.getStargateEntity(server).updateClient();

			dialingStargate.getStargateEntity(server).connectStargate(uuid, StargateConnection.State.OUTGOING_CONNECTION);
			dialedStargate.getStargateEntity(server).connectStargate(uuid, StargateConnection.State.INCOMING_CONNECTION);
			
			return new StargateConnection(uuid, connectionType, dialingStargate, dialedStargate, doKawoosh);
		}
		return null;
	}
	
	public final void terminate(MinecraftServer server, StargateInfo.Feedback feedback)
	{
		{
			AbstractStargateEntity entity = this.dialingStargate.getStargateEntity(server);
			if (this.dialingStargate != null && entity != null) {
				entity.updateInterfaceBlocks(EVENT_DISCONNECTED, feedback.getCode(), true); // true: Was dialing out
				this.dialingStargate.resetStargate(server, feedback, true);
			}
		}
		{
			AbstractStargateEntity entity = this.dialedStargate.getStargateEntity(server);
			if (this.dialedStargate != null && entity != null) {
				entity.updateInterfaceBlocks(EVENT_DISCONNECTED, feedback.getCode(), false); // false: Was being dialed
				this.dialedStargate.resetStargate(server, feedback, true);
			}
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
		if(stargate == null || stargate.getStargateEntity(server) == null)
		{
			StargateJourney.LOGGER.error("Stargate does not exist");
			return false;
		}
		
		BlockPos stargatePos = stargate.getBlockPos();
		Level stargateLevel = stargate.getStargateEntity(server).getLevel();
		
		if(stargateLevel.getBlockEntity(stargatePos) instanceof AbstractStargateEntity)
		{
			if(stargate.isConnected(server))
				return true;

			StargateJourney.LOGGER.info("Stargate is not connected");
			return false;
		}
		else
			StargateJourney.LOGGER.info("Stargate not found");
			
		
		return false;
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
			this.dialedStargate.getStargateEntity(server).updateInterfaceBlocks(EVENT_INCOMING_CONNECTION);
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = !doKawoosh() ? StargateInfo.ChevronLockSpeed.FAST : this.dialedStargate.getStargateEntity(server).getChevronLockSpeed();
		int chevronWaitTicks = chevronLockSpeed.getChevronWaitTicks();
		int kawooshStartTicks = chevronLockSpeed.getKawooshStartTicks();
		int maxKawooshTicks = kawooshStartTicks + KAWOOSH_TICKS;
		int maxOpenTicks = maxKawooshTicks + VORTEX_TICKS;
		
		this.increaseTicks(kawooshStartTicks, maxKawooshTicks, maxOpenTicks);
		int realOpenTime = this.openTime - kawooshStartTicks;
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons
		if(this.openTime <= kawooshStartTicks)
		{
			if(doKawoosh())
			{
				playStargateOpenSound(this.dialingStargate.getStargateEntity(server), kawooshStartTicks, this.openTime, false);
				playStargateOpenSound(this.dialedStargate.getStargateEntity(server), kawooshStartTicks, this.openTime, true);
			}
			
			int addressLength = this.dialingStargate.getStargateEntity(server).getAddress().getLength();
			Address dialingAddress = this.dialingStargate.getStargateEntity(server).getConnectionAddress(addressLength);
			
			this.dialedStargate.getStargateEntity(server).setEngagedChevrons(AbstractStargateEntity.getChevronConfiguration(addressLength));
			
			// Used for handling what the Stargate does when it's being dialed
			// For example: Pegasus Stargate's ring booting up
			this.dialedStargate.getStargateEntity(server).doWhileDialed(this.openTime, chevronLockSpeed);
			
			if(this.openTime % chevronWaitTicks == 0)
			{
				int dialedAddressLength = this.dialedStargate.getStargateEntity(server).getAddress().getLength();
				
				if(dialedAddressLength < dialingAddress.getLength())
				{
					if(this.openTime / chevronWaitTicks == 4 && addressLength < 7)
						return;
					else if(this.openTime / chevronWaitTicks == 5 && addressLength < 8)
						return;
					else
						this.dialedStargate.getStargateEntity(server).encodeChevron(dialingAddress.getSymbol(dialedAddressLength), true, false);
				}
				else
				{
					this.dialedStargate.getStargateEntity(server).chevronSound((short) 0, true, false, false);
					this.dialedStargate.getStargateEntity(server).updateInterfaceBlocks(EVENT_CHEVRON_ENGAGED, this.dialedStargate.getStargateEntity(server).getAddress().getLength() + 1,
							AbstractStargateEntity.getChevron(this.dialedStargate.getStargateEntity(server), this.dialedStargate.getStargateEntity(server).getAddress().getLength() + 1), true, 0);
				}
			}
			
			// Updates Interfaces when a wormhole is detected
			if(this.openTime == kawooshStartTicks)
			{
				List<Integer> emptyAddressList = Arrays.stream(new int[] {}).boxed().toList();
				List<Integer> dialedAddressList = Arrays.stream(dialedStargate.getStargateEntity(server).getAddress().toArray()).boxed().toList();
				dialedStargate.getStargateEntity(server).updateBasicInterfaceBlocks(EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.getStargateEntity(server).updateCrystalInterfaceBlocks(EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.getStargateEntity(server).updateAdvancedCrystalInterfaceBlocks(EVENT_INCOMING_WORMHOLE, dialedAddressList);
				List<Integer> dialingAddressList = Arrays.stream(dialingStargate.getStargateEntity(server).getAddress().toArray()).boxed().toList();
				dialingStargate.getStargateEntity(server).updateInterfaceBlocks(EVENT_OUTGOING_WORMHOLE, dialingAddressList);
			}
			
			return;
		}
		
		// Handles kawoosh progress
		if(this.openTime < maxOpenTicks)
		{
			this.dialingStargate.getStargateEntity(server).doKawoosh(realOpenTime);
			this.dialedStargate.getStargateEntity(server).doKawoosh(realOpenTime);
		}
		else
		{
			this.dialingStargate.getStargateEntity(server).setKawooshTickCount(realOpenTime);
			this.dialingStargate.getStargateEntity(server).updateClient();
			this.dialedStargate.getStargateEntity(server).setKawooshTickCount(realOpenTime);
			this.dialedStargate.getStargateEntity(server).updateClient();
		}
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(doKawoosh() && this.openTime < maxKawooshTicks)
			return;

		this.dialingStargate.getStargateEntity(server).idleWormholeSound(false);
		this.dialedStargate.getStargateEntity(server).idleWormholeSound(true);
		
		if(this.connectionTime >= maxOpenTime && !energyBypassEnabled)
		{
			terminate(server, StargateInfo.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		// Depletes energy over time
		if(requireEnergy)
		{
			long energyDraw = this.connectionType.getPowerDraw();
			energyDraw = this.connectionTime >= maxOpenTime ? energyDraw * energyBypassMultiplier : energyDraw;
			
			if(!this.dialingStargate.canExtractEnergy(server, energyDraw) && !this.dialedStargate.canExtractEnergy(server, energyDraw))
			{
				terminate(server, StargateInfo.Feedback.RAN_OUT_OF_POWER);
				return;
			}
			
			if(CommonStargateConfig.can_draw_power_from_both_ends.get() && this.dialedStargate.getStargateEntity(server).getEnergyStored() > this.dialingStargate.getStargateEntity(server).getEnergyStored())
				this.dialedStargate.depleteEnergy(server, energyDraw, false);
			else
				this.dialingStargate.depleteEnergy(server, energyDraw, false);
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		doWormhole(this.dialingStargate.getStargateEntity(server).getWormhole(), this.dialingStargate.getStargateEntity(server), this.dialedStargate.getStargateEntity(server), StargateInfo.WormholeTravel.ENABLED);
		doWormhole(this.dialedStargate.getStargateEntity(server).getWormhole(), this.dialedStargate.getStargateEntity(server), this.dialingStargate.getStargateEntity(server), CommonStargateConfig.two_way_wormholes.get());
		
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		if(this.dialingStargate.getStargateEntity(server).dhdInfo().autoclose() > 0 && this.timeSinceLastTraveler >= this.dialingStargate.getStargateEntity(server).dhdInfo().autoclose() * 20)
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
		
		if(this.dialedStargate.getStargateEntity(server).dhdInfo().autoclose() > 0 && this.timeSinceLastTraveler >= this.dialedStargate.getStargateEntity(server).dhdInfo().autoclose() * 20)
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
	}
	
	private final void playStargateOpenSound(AbstractStargateEntity stargate, int kawooshStartTicks, int ticks, boolean incoming)
	{
		if(ticks == kawooshStartTicks - stargate.getOpenSoundLead())
			stargate.openWormholeSound(incoming);
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
			this.dialedStargate.getStargateEntity(server).receiveStargateMessage(message);
		else
			this.dialingStargate.getStargateEntity(server).receiveStargateMessage(message);
	}
	
	public void sendStargateTransmission(MinecraftServer server, AbstractStargateEntity sendingStargate, int transmissionJumps, int frequency, String transmission)
	{
		if(sendingStargate.get9ChevronAddress().equals(this.dialingStargate.get9ChevronAddress()))
			this.dialedStargate.getStargateEntity(server).forwardTransmission(transmissionJumps, frequency, transmission);
		else
			this.dialingStargate.getStargateEntity(server).forwardTransmission(transmissionJumps, frequency, transmission);
	}
	
	public float checkStargateShieldingState(MinecraftServer server, AbstractStargateEntity sendingStargate)
	{
		if(sendingStargate.get9ChevronAddress().equals(this.dialingStargate.get9ChevronAddress()))
			return this.dialedStargate.getStargateEntity(server) instanceof IrisStargateEntity irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F;
		else
			return this.dialingStargate.getStargateEntity(server) instanceof IrisStargateEntity irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F;
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
