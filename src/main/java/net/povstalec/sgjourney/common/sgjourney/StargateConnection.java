package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringRepresentable;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class StargateConnection
{
	public static final String EVENT_INCOMING_CONNECTION = "stargate_incoming_connection";
	public static final String EVENT_INCOMING_WORMHOLE = "stargate_incoming_wormhole";
	public static final String EVENT_OUTGOING_WORMHOLE = "stargate_outgoing_wormhole";
	public static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	public static final String ADDRESS = Address.ADDRESS;
	
	public static final String DIALING_STARGATE = "dialing_stargate";
	public static final String DIALED_STARGATES = "dialed_stargates";
	
	public static final String USED = "used";
	public static final String TIME_SINCE_LAST_TRAVELER = "time_since_last_traveler";
	public static final String OPEN_TIME = "open_time";
	public static final String CONNECTION_TIME = "connection_time";
	public static final String CONNECTION_TYPE = "connection_type";
	public static final String DO_KAWOOSH = "do_kawoosh";
	public static final String KAWOOSH_TICKS = "kawoosh_ticks";
	
	public static final int KAWOOSH_DURATION = 40;
	
	public static final boolean ENERGY_BYPASS_ENABLED = CommonStargateConfig.enable_energy_bypass.get();
	public static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final long SYSTEM_WIDE_CONNECTION_COST = CommonStargateConfig.system_wide_connection_energy_cost.get();
	public static final long SYSTEM_WIDE_CONNECTION_DRAW = CommonStargateConfig.system_wide_connection_energy_draw.get();
	public static final long SYSTEM_WIDE_CONNECTION_BYPASS_DRAW = CommonStargateConfig.system_wide_connection_bypass_energy_draw.get();
	
	public static final long INTERSTELLAR_CONNECTION_COST = CommonStargateConfig.interstellar_connection_energy_cost.get();
	public static final long INTERSTELLAR_CONNECTION_DRAW = CommonStargateConfig.interstellar_connection_energy_draw.get();
	public static final long INTERSTELLAR_CONNECTION_BYPASS_DRAW = CommonStargateConfig.interstellar_connection_bypass_energy_draw.get();
	
	public static final long INTERGALACTIC_CONNECTION_COST = CommonStargateConfig.intergalactic_connection_energy_cost.get();
	public static final long INTERGALACTIC_CONNECTION_DRAW = CommonStargateConfig.intergalactic_connection_energy_draw.get();
	public static final long INTERGALACTIC_CONNECTION_BYPASS_DRAW = CommonStargateConfig.intergalactic_connection_bypass_energy_draw.get();
	
	protected final UUID uuid;
	protected final StargateConnection.Type connectionType;
	protected Stargate dialingStargate;
	protected List<Stargate> dialedStargates; //TODO Dialed Stargates can be changed mid-connection
	protected boolean doKawoosh;
	
	protected boolean used;
	protected int connectionTime; // Time since the connection was established (Right after dialing Stargate finished dialing)
	protected int openTime; // Time since the wormhole formed (after kawoosh ended)
	protected int timeSinceLastTraveler; // Time since a traveler has last appeared near any of the connected Stargates
	
	@Nullable
	private Address.Immutable dialingAddress = null;
	
	private StargateConnection(UUID uuid, StargateConnection.Type connectionType, Stargate dialingStargate, List<Stargate> dialedStargates,
							   boolean used, int connectionTime, int openTime, int timeSinceLastTraveler, boolean doKawoosh)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.dialingStargate = dialingStargate;
		this.dialedStargates = dialedStargates;
		this.used = used;
		this.connectionTime = connectionTime;
		this.openTime = openTime;
		this.timeSinceLastTraveler = timeSinceLastTraveler;
		this.doKawoosh = doKawoosh;
	}
	
	public enum Type implements StringRepresentable
	{
		SYSTEM_WIDE("system_wide", SYSTEM_WIDE_CONNECTION_COST, SYSTEM_WIDE_CONNECTION_DRAW, SYSTEM_WIDE_CONNECTION_BYPASS_DRAW),
		INTERSTELLAR("interstellar", INTERSTELLAR_CONNECTION_COST, INTERSTELLAR_CONNECTION_DRAW, INTERSTELLAR_CONNECTION_BYPASS_DRAW),
		INTERGALACTIC("intergalactic", INTERGALACTIC_CONNECTION_COST, INTERGALACTIC_CONNECTION_DRAW, INTERGALACTIC_CONNECTION_BYPASS_DRAW);
		
		private final String name;
		private final long establishingPowerCost;
		private final long powerDraw;
		private final long bypassPowerDraw;
		
		Type(String name, long establishingPowerCost, long powerDraw, long bypassPowerDraw)
		{
			this.name = name;
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
		
		@Override
		public @NotNull String getSerializedName()
		{
			return this.name;
		}
		
		public static Type fromString(String name)
		{
			return switch(name)
			{
				case "system_wide" -> SYSTEM_WIDE;
				case "interstellar" -> INTERSTELLAR;
				case "intergalactic" -> INTERGALACTIC;
				default -> null;
			};
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
		System.out.println(" | To:");
		for(Stargate dialedStargate : dialedStargates)
		{
			System.out.print(" " + dialedStargate.get9ChevronAddress().toString());
		}
		System.out.println(" | Connection Time: " + connectionTime);
		System.out.println(" | Open Time: " + openTime);
	}
	
	public static StargateConnection.Type getType(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate)
	{
		AddressRegion dialingRegion = dialingStargate.getAddressRegion();
		AddressRegion dialedRegion = dialedStargate.getAddressRegion();
		
		if(dialingRegion != null && dialedRegion != null)
		{
			if(dialingRegion.equals(dialedRegion))
				return StargateConnection.Type.SYSTEM_WIDE;
			
			if(dialingRegion.findCommonGalaxy(server, dialedRegion) != null)
				return Type.INTERSTELLAR;
		}
		
		return StargateConnection.Type.INTERGALACTIC;
	}
	
	private StargateConnection(UUID uuid, StargateConnection.Type connectionType, Stargate dialingStargate, List<Stargate> dialedStargates, boolean doKawoosh)
	{
		this(uuid, connectionType, dialingStargate, dialedStargates, false, 0, 0, 0, doKawoosh);
	}
	
	public static StargateConnection create(StargateConnection.Type connectionType, Stargate dialingStargate, List<Stargate> dialedStargates, boolean doKawoosh)
	{
		UUID uuid = UUID.randomUUID();
		
		if(dialingStargate != null && dialedStargates != null)
		{
			for(Stargate dialedStargate : dialedStargates)
			{
				dialedStargate.resetStargate(StargateInfo.Feedback.INTERRUPTED_BY_INCOMING_CONNECTION);
			}
			
			StargateConnection stargateConnection = new StargateConnection(uuid, connectionType, dialingStargate, dialedStargates, doKawoosh);

			dialingStargate.connectStargate(stargateConnection, StargateConnection.State.OUTGOING_CONNECTION);
			for(Stargate dialedStargate : dialedStargates)
			{
				dialedStargate.connectStargate(stargateConnection, StargateConnection.State.INCOMING_CONNECTION);
			}
			
			dialingStargate.connectionUpdate(stargateConnection);
			dialingStargate.updateClient();
			for(Stargate dialedStargate : dialedStargates)
			{
				dialedStargate.connectionUpdate(stargateConnection);
				dialedStargate.updateClient();
			}
			
			return stargateConnection;
		}
		return null;
	}
	
	public void terminate(MinecraftServer server, StargateInfo.FeedbackMessage feedback)
	{
		SGJourneyEvents.onStargateConnectionTerminated(server, this, feedback.feedback());
		
		if(this.dialingStargate != null)
		{
			this.dialingStargate.updateInterfaceBlocks(null, EVENT_DISCONNECTED, feedback.feedback().getCode(), true); // true: Was dialing out
			this.dialingStargate.resetStargate(feedback);
		}
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.updateInterfaceBlocks(null, EVENT_DISCONNECTED, feedback.feedback().getCode(), false); // false: Was being dialed
			dialedStargate.resetStargate(feedback);
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
	
	private void removeDialedStargate(MinecraftServer server, Stargate removedStargate, StargateInfo.FeedbackMessage feedback)
	{
		if(this.dialedStargates.size() == 1)
			terminate(server, feedback);
		else
			this.dialedStargates = this.dialedStargates.stream().filter(dialedStargate -> dialedStargate != removedStargate).toList();
	}
	
	public void removeStargate(MinecraftServer server, Address address, StargateInfo.FeedbackMessage feedback)
	{
		if(address.equals(this.dialingStargate.get9ChevronAddress()))
			terminate(server, feedback);
		else
		{
			for(Stargate dialedStargate : this.dialedStargates)
			{
				if(address.equals(dialedStargate.get9ChevronAddress()))
				{
					removeDialedStargate(server, dialedStargate, feedback);
					return;
				}
			}
		}
	}
	
	public boolean isStargateValid(Stargate stargate) //TODO Remove
	{
		if(stargate == null)
		{
			StargateJourney.LOGGER.error("Stargate does not exist");
			return false;
		}
		
		if(!stargate.isConnected())
		{
			StargateJourney.LOGGER.info("Stargate {} is not connected", stargate.get9ChevronAddress());
			return false;
		}
		
		return true;
	}
	
	private Address.Immutable getDialingAddress(MinecraftServer server)
	{
		// Get dialing address and cache it for later use
		if(this.dialingAddress == null)
			this.dialingAddress = Address.Immutable.extendWithPointOfOrigin(this.dialingStargate.getConnectionAddress(getDialedStargate().getAddressRegion(), this.dialingStargate.getAddress().getType()));
		
		return this.dialingAddress;
	}
	
	private void tickEstablishConnection(MinecraftServer server, int kawooshStartTicks)
	{
		Address.Immutable dialingAddress = getDialingAddress(server);
		
		Address.Type addressType = dialingAddress.getType();
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.setChevronConfiguration(Dialing.getChevronConfiguration(addressType));
		}
		
		this.dialingStargate.doWhileConnecting(this, false, kawooshStartTicks);
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.doWhileConnecting(this, true, kawooshStartTicks);
		}
		
		// Used for handling what the Stargate does when it's being dialed
		// For example: Pegasus Stargate's ring booting up
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.doWhileDialed(this, dialingAddress, kawooshStartTicks);
		}
		
		// Updates Interfaces when a wormhole is detected
		if(this.connectionTime == kawooshStartTicks)
		{
			List<Integer> emptyAddressList = new ArrayList<>();
			for(Stargate dialedStargate : this.dialedStargates)
			{
				List<Integer> dialedAddressList = dialedStargate.getAddress().toList();
				dialedStargate.updateInterfaceBlocks(AbstractInterfaceEntity.InterfaceType.BASIC, EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.updateInterfaceBlocks(AbstractInterfaceEntity.InterfaceType.CRYSTAL, EVENT_INCOMING_WORMHOLE, emptyAddressList);
				dialedStargate.updateInterfaceBlocks(AbstractInterfaceEntity.InterfaceType.ADVANCED_CRYSTAL, EVENT_INCOMING_WORMHOLE, dialedAddressList);
			}
			
			List<Integer> dialingAddressList = dialingStargate.getAddress().toList();
			dialingStargate.updateInterfaceBlocks(null, EVENT_OUTGOING_WORMHOLE, dialingAddressList);
		}
	}
	
	public static boolean canExtract(Stargate stargate, long energyExtracted)
	{
		return stargate.extractEnergy(energyExtracted, true) >= energyExtracted;
	}
	
	private boolean depleteEnergy(long energyDraw)
	{
		if(canExtract(this.dialingStargate, energyDraw))
		{
			this.dialingStargate.extractEnergy(energyDraw, false);
			return true;
		}
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			//TODO Tie this to Advanced Protocols
			if(dialedStargate.canPowerFromOtherSide() && canExtract(dialedStargate, energyDraw))
			{
				dialedStargate.extractEnergy(energyDraw, false);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean requiresEnergyBypass(int openTime)
	{
		if(this.dialingStargate.requiresEnergyBypass(openTime))
			return true;
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			if(dialedStargate.requiresEnergyBypass(openTime))
				return true;
		}
		
		return false;
	}
	
	private boolean shouldAutoclose()
	{
		if(this.dialingStargate.shouldAutoclose(this))
			return true;
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			if(dialedStargate.shouldAutoclose(this))
				return true;
		}
		
		return false;
	}
	
	private boolean hasInvalidStargate()
	{
		if(!isStargateValid(this.dialingStargate))
			return true;
		
		for(Stargate dialedStargate : this.dialedStargates)
		{
			if(!isStargateValid(dialedStargate)) //TODO Handle singular Stargates being destroyed during the connection, only end the connection once there are no dialed Stargates
				return true;
		}
		
		return false;
	}
	
	public void tick(MinecraftServer server)
	{
		if(hasInvalidStargate())
		{
			terminate(server, StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE.withInfo());
			return;
		}
		
		// Updates Interfaces when incoming connection is detected
		if(this.connectionTime == 0)
		{
			for(Stargate dialedStargate : this.dialedStargates)
			{
				dialedStargate.updateInterfaceBlocks(null, EVENT_INCOMING_CONNECTION);
			}
		}
		
		int kawooshStartTicks = getDialedStargate().dialedEngageTime(doKawoosh());
		// Time after which the dangerous part of the kawoosh is finished, and it's safe to go through the wormhole
		int maxKawooshTicks = kawooshStartTicks + Math.max(getDialedStargate().wormholeEstablishTime(doKawoosh()), this.dialingStargate.wormholeEstablishTime(doKawoosh()));
		
		this.increaseTicks(maxKawooshTicks);
		
		this.dialingStargate.connectionUpdate(this);
		this.dialingStargate.updateClient();
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.connectionUpdate(this);
			dialedStargate.updateClient();
		}
		
		// Dialing Stargate waits here while dialed Stargate is locking Chevrons, then both Stargates do kawoosh
		if(this.connectionTime < maxKawooshTicks)
			tickEstablishConnection(server, kawooshStartTicks);
		
		// Prevents anything after this point from happening while the kawoosh has not yet finished
		if(doKawoosh() && this.connectionTime < maxKawooshTicks)
			return;

		this.dialingStargate.doWhileConnected(this, false);
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.doWhileConnected(this, true);
		}
		
		if(requiresEnergyBypass(this.openTime) && !ENERGY_BYPASS_ENABLED)
		{
			terminate(server, StargateInfo.Feedback.EXCEEDED_CONNECTION_TIME.withInfo());
			return;
		}
		
		// Depletes energy over time
		if(REQUIRE_ENERGY && !depleteEnergy(getPowerDraw()))
		{
			terminate(server, StargateInfo.Feedback.RAN_OUT_OF_POWER.withInfo());
			return;
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		// Closes the connection if any Stargate requires it
		if(shouldAutoclose())
		{
			terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE.withInfo());
			return;
		}
		
		this.dialingStargate.doWormhole(this, false, StargateInfo.WormholeTravel.ENABLED);
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargate.doWormhole(this, true, CommonStargateConfig.two_way_wormholes.get());
		}
	}
	
	private void increaseTicks(int maxKawooshTicks)
	{
		this.connectionTime++;
		
		if(this.connectionTime > maxKawooshTicks)
			this.openTime = this.connectionTime - maxKawooshTicks;
	}
	
	public void sendStargateMessage(MinecraftServer server, Address address, String message)
	{
		if(address.equals(this.dialingStargate.get9ChevronAddress()))
		{
			for(Stargate dialedStargate : this.dialedStargates)
			{
				dialedStargate.receiveStargateMessage(message);
			}
		}
		else
			this.dialingStargate.receiveStargateMessage(message);
	}
	
	public void sendStargateTransmission(Address address, int transmissionJumps, int frequency, String transmission)
	{
		if(address.equals(this.dialingStargate.get9ChevronAddress()))
		{
			for(Stargate dialedStargate : this.dialedStargates)
			{
				dialedStargate.forwardTransmission(transmissionJumps, frequency, transmission);
			}
		}
		else
			this.dialingStargate.forwardTransmission(transmissionJumps, frequency, transmission);
	}
	
	public float checkStargateShieldingState(Address address)
	{
		if(address.equals(this.dialingStargate.get9ChevronAddress()))
			return getDialedStargate().checkStargateShieldingState();
		else
			return this.dialingStargate.checkStargateShieldingState();
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
	
	public List<Stargate> getDialedStargates()
	{
		return dialedStargates;
	}
	
	@Nullable
	public Stargate getDialedStargate()
	{
		if(dialedStargates.isEmpty())
			return null;
		
		return dialedStargates.get(0); // The Stargate Network is still primarily made for 1:1 connections, this is an easy way to get the "main" dialed Stargate
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
	public int getKawooshTime()
	{
		int kawooshStartTicks = getDialedStargate().dialedEngageTime(doKawoosh());
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
	
	public long getPowerDraw()
	{
		return this.connectionType.getPowerDraw(requiresEnergyBypass(this.openTime));
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(DIALING_STARGATE, serializeStargate(this.dialingStargate));
		ListTag dialedStargatesList = new ListTag();
		for(Stargate dialedStargate : this.dialedStargates)
		{
			dialedStargatesList.add(serializeStargate(dialedStargate));
		}
		tag.put(DIALED_STARGATES, dialedStargatesList);
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
		stargate.get9ChevronAddress().saveToCompoundTag(tag, ADDRESS);
		return tag;
	}
	
	@Nullable
	public static StargateConnection deserialize(MinecraftServer server, UUID uuid, CompoundTag tag)
	{
		Type connectionType = Type.valueOf(tag.getString(CONNECTION_TYPE));
		Stargate dialingStargate = deserializeStargate(server, tag.getCompound(DIALING_STARGATE));
		List<Stargate> dialedStargates = new ArrayList<>();
		ListTag listTag = tag.getList(DIALED_STARGATES, Tag.TAG_COMPOUND);
		for(int i = 0; i < listTag.size(); i++)
		{
			dialedStargates.add(deserializeStargate(server, listTag.getCompound(i)));
		}
		boolean used = tag.getBoolean(USED);
		int openTime = tag.getInt(CONNECTION_TIME);
		int connectionTime = tag.getInt(OPEN_TIME);
		int timeSinceLastTraveler = tag.getInt(TIME_SINCE_LAST_TRAVELER);
		boolean doKawoosh = tag.getBoolean(DO_KAWOOSH);
		
		return new StargateConnection(uuid, connectionType, dialingStargate, dialedStargates, used, openTime, connectionTime, timeSinceLastTraveler, doKawoosh);
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
