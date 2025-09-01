package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.sgjourney.*;

import javax.annotation.Nullable;

public interface Stargate
{
	String DIMENSION = "Dimension";
	String COORDINATES = "Coordinates";
	
	String HAS_DHD = "HasDHD";
	String GENERATION = "Generation";
	String TIMES_OPENED = "TimesOpened";
	
	String NETWORK = "Network";
	
	// Basic Info
	
	/**
	 * @return 9-Chevron Address of the Stargate
	 */
	Address.Immutable get9ChevronAddress();
	
	/**
	 * @return Dimension the Stargate is located in or null if it's not located in any Dimension
	 */
	@Nullable
	ResourceKey<Level> getDimension();
	
	/**
	 * @param server Current Minecraft Server
	 * @return Level the Stargate is currently located in, null if it's not located in any Level
	 */
	@Nullable
	default ServerLevel getLevel(MinecraftServer server)
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return server.getLevel(dimension);
	}
	
	/**
	 * @return Position vector the Stargate is located at or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition();
	
	/**
	 * @return Unit Vector with the direction the Stargate is facing or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getForward(MinecraftServer server);
	
	/**
	 * @return Unit Vector with the direction the Stargate considers up or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getUp(MinecraftServer server);
	
	/**
	 * @return Inner Radius of the Stargate or 0 if the Stargate doesn't have a real form
	 */
	double getInnerRadius();
	
	/**
	 * @return Solar System the Stargate is located in or null if it's not located in any Solar System
	 */
	@Nullable
	default SolarSystem.Serializable getSolarSystem(MinecraftServer server)
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return Universe.get(server).getSolarSystemFromDimension(dimension);
	}
	
	/**
	 * @return True if the Stargate is connected to a DHD, otherwise false
	 */
	boolean hasDHD();
	
	/**
	 * @return Generation of the Stargate
	 */
	StargateInfo.Gen getGeneration();
	
	/**
	 * @return Number of times the Stargate was opened
	 */
	int getTimesOpened();
	
	/**
	 * @return The network this Stargate is a part of
	 */
	default int getNetwork()
	{
		return 0;
	}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Address currently encoded in this Stargate
	 */
	Address getAddress(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @param solarSystem Solar System requesting this Stargate's connection Address, can be null
	 * @param addressLength Length of the requested Address type
	 * @return The Address which this Stargate will provide to the Stargate Network during connections
	 * (For example, during an interstellar connection, the Stargate will provide the 7-Chevron Address of its Solar System instead of its 9-Chevron Address)
	 */
	default Address getConnectionAddress(MinecraftServer server, @Nullable SolarSystem.Serializable solarSystem, int addressLength)
	{
		SolarSystem.Serializable localSolarSystem = getSolarSystem(server);
		if(localSolarSystem != null)
		{
			if(addressLength == 6)
			{
				Galaxy.Serializable galaxy = localSolarSystem.findCommonGalaxy(solarSystem);
				if(galaxy != null)
				{
					Address.Immutable address = localSolarSystem.getAddressFromGalaxy(galaxy);
					if(address != null)
						return address.mutable();
				}
			}
			else if(addressLength == 7)
				return localSolarSystem.getExtragalacticAddress().mutable();
		}
		
		// This setup basically means that a 9-chevron Address is returned for a Connection when a Stargate isn't in any Solar System
		return get9ChevronAddress().mutable();
	}
	
	/**
	 * Resets this Stargate (Disconnects it, wipes the currently encoded Address, revalidates, ...)
	 * @param server Current Minecraft Server
	 * @param feedback Feedback with information regarding why this Stargate was reset
	 * @param updateInterfaces Whether or not to update any interfaces connected to this Stargate
	 * @return Feedback with information regarding how this Stargate's reset attempt went
	 */
	StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces);
	
	/**
	 * @param server Current Minecraft Server
	 * @return True if this Stargate is currently connected, otherwise false
	 */
	boolean isConnected(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return True if this Stargate is currently obstructed, otherwise false
	 */
	boolean isObstructed(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return True if this Stargate is the Primary Stargate of the Solar System it's located in, otherwise false
	 */
	default boolean isPrimary(MinecraftServer server)
	{
		return false;
	}
	
	/**
	 * Updates this Stargate
	 * @param server Current Minecraft Server
	 */
	default void update(MinecraftServer server) {}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Returns true if this Stargate is valid (for example, in the case of BlockEntity-based Stargates, if the Block Entity can still be found in the world)
	 */
	boolean isValid(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Returns true if this Stargate is loaded (for example, in the case of Stargates placed in the world, if the Chunk the Stargate is located in is loaded)
	 */
	boolean isLoaded(MinecraftServer server);
	
	/**
	 * Sets the order in which the Stargate's chevrons should engage
	 * @param server Current Minecraft Server
	 * @param chevronConfiguration Integer array representing the order in which this Stargate's chevrons engage
	 */
	default void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration) {}
	
	// Client Connection
	
	/**
	 * Updates this Stargate's information on the client-side
	 * @param server Current Minecraft Server
	 */
	default void updateClient(MinecraftServer server) {}
	
	// Communication
	
	/**
	 * Receives a Stargate message in the form of a String
	 * @param server Current Minecraft Server
	 * @param message Message that was received
	 */
	default void receiveStargateMessage(MinecraftServer server, String message) {}
	
	/**
	 * Receives a radio transmission from a GDO or a Transceiver and forwards it further
	 * @param server Current Minecraft Server
	 * @param transmissionJumps Current count of transmission jumps
	 * @param frequency Radio frequency at which the transmission was sent
	 * @param transmission Transmission contents in the form of a String
	 */
	default void forwardTransmission(MinecraftServer server, int transmissionJumps, int frequency, String transmission) {}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Percentage of how much the Stargate's iris/shield is closed, with 0 being open and 1 being fully closed
	 */
	float checkStargateShieldingState(MinecraftServer server);
	
	/**
	 * Update all Tech Interfaces that are currently connected to the Stargate
	 * @param server The Server this is happening on
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	default void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects) {}
	
	// Energy
	
	/**
	 * @param server Current Minecraft Server
	 * @return Energy currently stored in the Stargate's energy buffer
	 */
	long getEnergyStored(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Max amount of energy tuat can be stored in the Stargate's energy buffer
	 */
	long getEnergyCapacity(MinecraftServer server);
	
	/**
	 * Extracts energy from the Stargate's energy buffer
	 * @param server Current Minecraft Server
	 * @param energy Amount of energy to be depleted
	 * @param simulate True if the depletion will only be simulated and the amount of energy in the Stargate's energy buffer will stay the same, if false, the energy is extracted from the energy buffer
	 * @return Amount of energy that was actually depleted
	 */
	long extractEnergy(MinecraftServer server, long energy, boolean simulate);
	
	// Stargate Connection
	
	/**
	 * @param server Current Minecraft Server
	 * @param doKawoosh Whether kawoosh should form when the connection is established (for instance, when Nox open the Stargate)
	 * @return Time (in ticks) it takes the Stargate to engage its Chevrons and start establishing a wormhole (kawoosh is not included in this)
	 */
	int dialedEngageTime(MinecraftServer server, boolean doKawoosh);
	
	/**
	 * @param server Current Minecraft Server
	 * @param doKawoosh Whether kawoosh should form when the connection is established (for instance, when Nox open the Stargate)
	 * @return Time (in ticks) it takes the Stargate to establish wormhole (basically, how long before kawoosh is over and the Stargate can be safely used)
	 */
	int wormholeEstablishTime(MinecraftServer server, boolean doKawoosh);
	
	/**
	 * Checks if this Stargate can connect to the dialing Stargate and creates a Stargate Connection
	 * @param server Current Minecraft Server
	 * @param dialingStargate Stargate that dialed this Stargate
	 * @param addressType Address type that was used to dial this Stargate
	 * @param doKawoosh Whether kawoosh should form when the connection is established
	 * @return Stargate Feedback describing how successful the formation of the connection was (for example, throw an error when this Stargate is already connected)
	 */
	StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh);
	
	/**
	 * Sets the Stargate to a connected state and updates it accordingly
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param connectionState State of the connection in relation to this Stargate (incoming or outgoing connection)
	 */
	void connectStargate(MinecraftServer server, StargateConnection connection, StargateConnection.State connectionState);
	
	/**
	 * Performs whatever the Stargate needs to do while connecting (for example, playing the kawoosh sound and handling the kawoosh itself) - happens on both sides of the connection
	 * @param server Current Minecraft Server
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 * @param doKawoosh Whether kawoosh should form when the connection is established
	 * @param kawooshStartTicks Time of connection (in ticks) at which the kawoosh is scheduled to start
	 * @param openTime Amount of time (in ticks) that has passed since the connection was established
	 */
	default void doWhileConnecting(MinecraftServer server, boolean incoming, boolean doKawoosh, int kawooshStartTicks, int openTime) {}
	
	/**
	 * Performs whatever the Stargate needs to do while being dialed (for example, engage chevrons, display symbols or start rotating) - happens only on the receiving side of the connection
	 * @param server Current Minecraft Server
	 * @param dialingAddress The connection Address of the dialing Stargate in relation to this connection
	 * @param kawooshStartTicks Time of connection (in ticks) at which the kawoosh is scheduled to start
	 * @param doKawoosh Whether kawoosh should form when the connection is established
	 * @param openTime Amount of time (in ticks) that has passed since the connection was established
	 */
	default void doWhileDialed(MinecraftServer server, Address dialingAddress, int kawooshStartTicks, boolean doKawoosh, int openTime) {}
	
	/**
	 * Updates Stargate's timers with new time information
	 * @param server Current Minecraft Server
	 * @param connectionTime Number of ticks that have passed since the connection was established (Right after dialing Stargate finished dialing)
	 * @param kawooshTime Number of ticks that have passed since the kawoosh started
	 * @param openTime Number of ticks that have passed since the wormhole formed (after kawoosh ended)
	 * @param timeSinceLastTraveler Number of ticks that have passed since the last time a traveler has appeared near any of the connected Stargates
	 */
	default void updateTimers(MinecraftServer server, int connectionTime, int kawooshTime, int openTime, int timeSinceLastTraveler) {}
	
	/**
	 * Performs whatever the Stargate needs to do while it's connected after the kawoosh (for example play idle wormhole sounds)
	 * @param server Current Minecraft Server
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 * @param connectionTime Amount of time (in ticks) that has passed since the connection was established
	 */
	default void doWhileConnected(MinecraftServer server, boolean incoming, int connectionTime) {}
	
	/**
	 * Performs whatever the Stargate needs for its wormhole to try sending travelers to the connected Stargate
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 * @param wormholeTravel Specifies if outgoing travel from this Stargate is allowed
	 */
	void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel);
	
	/**
	 * Receives information about incoming traveler and teleports the traveler to the Stargate's position
	 * @param server Current Minecraft Server
	 * @param initialStargate Stargate from which the traveler is being sent
	 * @param traveler The traveler Entity which is being received
	 * @param relativePosition Traveler's position vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction. Y and Z are scaled to be percentages (from 0 to 1) of the initial Stargate's inner radius
	 * @param relativeMomentum Traveler's momentum vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction.
	 * @param relativeLookAngle Traveler's look angle turned into a vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction.
	 * @return True if traveler was accepted and transported to this Stargate, otherwise false
	 */
	boolean receiveTraveler(MinecraftServer server, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	/**
	 * @param server Current Minecraft Server
	 * @return The amount of time (in ticks) the Stargate Connection should wait for new travelers before automatically closing the wormhole (this counts from both ends of the connection),
	 * leave as 0 if the connection shouldn't automatically close from this Stargate's side
	 */
	int autoclose(MinecraftServer server);
	
	// Saving and loading
	
	/**
	 * @return Stargate info serialized into a CompoundTag
	 */
	CompoundTag serializeNBT();
	
	/**
	 * Deserializes the Stargate info
	 * @param server Current Minecraft Server
	 * @param address Address of the Stargate
	 * @param tag CompoundTag containing information to be deserialized
	 */
	void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag);
	
	
	
	static int getMaxGateOpenTime()
	{
		return CommonStargateConfig.max_wormhole_open_time.get() * 20;
	}
}
