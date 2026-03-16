package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public interface Stargate extends Comparable<Stargate>
{
	Capability<Stargate> STARGATE_CAPABILITY = CapabilityManager.get(new CapabilityToken<Stargate>() {});
	
	String DIMENSION = "Dimension";
	
	String HAS_DHD = "HasDHD";
	String GENERATION = "Generation";
	String TIMES_OPENED = "TimesOpened";
	
	String NETWORK = "Network";
	
	// Basic Info
	
	StargateType<?> getStargateType();
	
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
	 * @return Address Region the Stargate is located in or null if it's not located in any Address Region
	 */
	@Nullable
	default AddressRegion.Serializable getAddressRegion(MinecraftServer server)
	{
		return Universe.get(server).getAddressRegionFromDimension(getDimension());
	}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Position vector of the Stargate's center or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Stargate is facing or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getForward(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Stargate considers up or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getUp(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Stargate considers right or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getRight(MinecraftServer server);
	
	/**
	 * @return Inner Radius of the Stargate or {@literal <= 0} if the Stargate doesn't have a real form
	 */
	double getInnerRadius();
	
	/**
	 * Transforms the vector from an absolute coordinate system to a coordinate system relative to Stargate, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction
	 * @param server Current Minecraft Server
	 * @param vector Vector to be transformed
	 * @param scaleWithStargate Whether the coordinates should scale with the Stargate (for example, relative position within the Stargate should be scaled with it, but momentum should not)
	 * @return A new vector with the coordinates of the original vector, but transformed to Stargate's relative coordinate system
	 */
	default Vec3 toStargateCoords(MinecraftServer server, Vec3 vector, boolean scaleWithStargate)
	{
		Vec3 result = CoordinateHelper.Relative.fromOrthogonalBasis(vector, getForward(server), getUp(server), getRight(server));
		
		if(scaleWithStargate)
			return new Vec3(result.x(), result.y() / getInnerRadius(), result.z() / getInnerRadius());
		
		return result;
	}
	
	/**
	 * Transforms the vector from a Stargate's relative coordinate system, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction,
	 * with the Y and Z vectors being a percentage of the Stargate's radius, to a vector in the absolute coordinate system
	 * @param server Current Minecraft Server
	 * @param vector Vector to be transformed
	 * @param scaleWithStargate Whether the coordinates should scale with the Stargate (for example, relative position within the Stargate should be scaled with it, but momentum should not)
	 * @param mirror Whether the coordinates should be mirrored, for example when a traveler is exiting the Stargate
	 * @return A new vector with the coordinates of the original vector, but transformed to absolute coordinate system
	 */
	default Vec3 fromStargateCoords(MinecraftServer server, Vec3 vector, boolean scaleWithStargate, boolean mirror)
	{
		if(scaleWithStargate)
			vector = new Vec3(vector.x(), vector.y() * getInnerRadius(), vector.z() * getInnerRadius());
		
		return mirror ? CoordinateHelper.Relative.toOrthogonalBasis(vector, CoordinateHelper.Relative.mirrorVector(getForward(server)), getUp(server), CoordinateHelper.Relative.mirrorVector(getRight(server))) :
				CoordinateHelper.Relative.toOrthogonalBasis(vector, getForward(server), getUp(server), getRight(server));
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
	 * @param network Network ID to test
	 * @return False if the provided network passes the restriction check successfully, otherwise true
	 */
	default boolean isRestricted(MinecraftServer server, int network)
	{
		return false;
	}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Address currently encoded in this Stargate
	 */
	Address.Mutable getAddress(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @param addressRegion Address Region requesting this Stargate's connection Address, can be null
	 * @param addressType Type of the requested Address
	 * @return The Address which this Stargate will provide to the Stargate Network during connections
	 * (For example, during an interstellar connection, the Stargate will provide the 7-Chevron Address of its Solar System instead of its 9-Chevron Address)
	 */
	default Address.Immutable getConnectionAddress(MinecraftServer server, @Nullable AddressRegion.Serializable addressRegion, Address.Type addressType)
	{
		AddressRegion.Serializable localAddressRegion = getAddressRegion(server);
		if(localAddressRegion != null)
		{
			if(addressType == Address.Type.ADDRESS_7_CHEVRON)
			{
				Galaxy.Serializable galaxy = localAddressRegion.findCommonGalaxy(addressRegion);
				if(galaxy != null)
				{
					Address.Immutable address = localAddressRegion.getAddressInGalaxy(galaxy);
					if(address != null)
						return address;
				}
			}
			else if(addressType == Address.Type.ADDRESS_8_CHEVRON)
				return localAddressRegion.getExtragalacticAddress();
		}
		
		// This setup basically means that a 9-chevron Address is returned for a Connection when a Stargate isn't in any Solar System
		return get9ChevronAddress();
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
	 * @param server Current Minecraft Server
	 * @return Returns true if this Stargate is valid (for example, in the case of BlockEntity-based Stargates, if the Block Entity can still be found in the world and if its address is the same as the Stargate object's)
	 */
	boolean checkValidity(MinecraftServer server);
	
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
	
	// Updating
	
	/**
	 * Updates this Stargate
	 * @param server Current Minecraft Server
	 */
	default void update(MinecraftServer server) {}
	
	/**
	 * Updates this Stargate's information on the client-side
	 * @param server Current Minecraft Server
	 */
	default void updateClient(MinecraftServer server) {}
	
	/**
	 * Update all Tech Interfaces that are currently connected to the Stargate
	 * @param server The Server this is happening on
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	default void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects) {}
	
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
	
	// Energy
	
	/**
	 * @param server Current Minecraft Server
	 * @return Energy currently stored in the Stargate's energy buffer
	 */
	long getEnergyStored(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Max amount of energy that can be stored in the Stargate's energy buffer
	 */
	long getEnergyCapacity(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return True if this Stargate can supply energy to the connection even if it did not initiate the connection
	 */
	boolean canPowerFromOtherSide(MinecraftServer server);
	
	/**
	 * Extracts energy from the Stargate's energy buffer (used mainly for drawing energy to establish and then feed a Stargate Connection)
	 * @param server Current Minecraft Server
	 * @param energy Amount of energy to be depleted
	 * @param simulate True if the depletion will only be simulated and the amount of energy in the Stargate's energy buffer will stay the same, if false, the energy is extracted from the energy buffer
	 * @return Amount of energy that was actually depleted
	 */
	long extractEnergy(MinecraftServer server, long energy, boolean simulate);
	
	// Stargate Connection
	
	/**
	 * Updates Stargate with current information about the Stargate Connection
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 */
	default void connectionUpdate(MinecraftServer server, StargateConnection connection) {}
	
	/**
	 * @param server Current Minecraft Server
	 * @param doKawoosh Whether kawoosh should form when the connection is established (for instance, when Nox open the Stargate)
	 * @return Time (in ticks) it takes the Stargate to engage its Chevrons and start establishing a wormhole (kawoosh is not included in this)
	 */
	int dialedEngageTime(MinecraftServer server, boolean doKawoosh); //TODO Make the network wait
	
	/**
	 * @param server Current Minecraft Server
	 * @param doKawoosh Whether kawoosh should form when the connection is established (for instance, when Nox open the Stargate)
	 * @return Time (in ticks) it takes the Stargate to establish wormhole (basically, how long before kawoosh is over and the Stargate can be safely used)
	 */
	int wormholeEstablishTime(MinecraftServer server, boolean doKawoosh); //TODO Make the network wait
	
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
	 * @param server Current Minecraft Server
	 * @return True if the Stargate can call forward, otherwise false
	 */
	default boolean callForward(MinecraftServer server)
	{
		return false;
	}
	
	/**
	 * Checks which Stargates actually get dialed when this Stargate is dialed (mainly matters in case of Call Forwarding)
	 * @param server Current Minecraft Server
	 * @param dialingStargate The Stargate that is attempting to dial this Stargate
	 * @param connectionType The type of the connection to be created between the dialingStargate and this Stargate
	 * @return List of Stargates the dialingStargate will be connected to, first Stargate on this list will be considered the "main" Stargate of the connection (because Stargate connections are still primarily 1:1)
	 */
	default List<Stargate> getDialedStargates(MinecraftServer server, Stargate dialingStargate, StargateConnection.Type connectionType)
	{
		return List.of(this);
	}
	
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
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 * @param kawooshStartTicks Time of connection (in ticks) at which the kawoosh is scheduled to start
	 */
	default void doWhileConnecting(MinecraftServer server, StargateConnection connection, boolean incoming, int kawooshStartTicks) {}
	
	/**
	 * Performs whatever the Stargate needs to do while being dialed (for example, engage chevrons, display symbols or start rotating) - happens only on the receiving side of the connection
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param dialingAddress The connection Address of the dialing Stargate in relation to this connection
	 * @param kawooshStartTicks Time of connection (in ticks) at which the kawoosh is scheduled to start
	 */
	default void doWhileDialed(MinecraftServer server, StargateConnection connection, Address dialingAddress, int kawooshStartTicks) {}
	
	/**
	 * Performs whatever the Stargate needs to do while it's connected after the kawoosh (for example play idle wormhole sounds)
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 */
	default void doWhileConnected(MinecraftServer server, StargateConnection connection, boolean incoming) {}
	
	/**
	 * Performs whatever the Stargate needs for its wormhole to try sending travelers to the connected Stargate
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param incoming Whether the Stargate is on the incoming side or outgoing side of the connection
	 * @param wormholeTravel Specifies if outgoing travel from this Stargate is allowed
	 */
	void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel);
	
	/**
	 * Redirects incoming travelers using Call Forwarding
	 * @param traveler Incoming traveler
	 * @return True if the incoming traveler should be denied entry and sent elsewhere through Call Forwarding, otherwise false
	 */
	default boolean callForwardDeny(Entity traveler)
	{
		if(traveler instanceof Player player && player.isSpectator())
			return false; // Spectators can pass through Call Forwarding just fine
		
		return traveler instanceof LivingEntity; //TODO Let players specify what can pass through
	}
	
	/**
	 * Receives information about incoming traveler and teleports the traveler to the Stargate's position
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection that connects the two Stargates
	 * @param initialStargate Stargate from which the traveler is being sent
	 * @param traveler The traveler Entity which is being received
	 * @param relativePosition Traveler's position vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction. Y and Z are scaled to be percentages (from 0 to 1) of the initial Stargate's inner radius
	 * @param relativeMomentum Traveler's momentum vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction.
	 * @param relativeLookAngle Traveler's look angle turned into a vector relative to the initial Stargate, with X direction being the direction the initial Stargate was facing,
	 * Y being the initial Stargate's up and Z being the initial Stargate's right direction.
	 * @return Traveler entity (that may have been created on the other side) if the traveler was accepted and transported to this Stargate, otherwise null
	 */
	@Nullable Entity receiveTraveler(MinecraftServer server, StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	/**
	 * Checks if the current Stargate Connection should be automatically closed (for example, if the open time exceeds the maximum time allowed for the Stargate to be open)
	 * @param server Current Minecraft Server
	 * @param connection Stargate Connection in question
	 * @return True if the Stargate connection should be closed, otherwise false
	 */
	boolean shouldAutoclose(MinecraftServer server, StargateConnection connection);
	
	/**
	 * Checks if the current Stargate Connection has reached the point of energy bypass (like going past the canonical 38 minute mark)
	 * @param server Current Minecraft Server
	 * @param openTime Time since the wormhole formed (after kawoosh ended)
	 * @return True if the Stargate has reached a point where it requires extra energy to bypass the max wormhole open time, otherwise false
	 */
	boolean requiresEnergyBypass(MinecraftServer server, int openTime);
	
	@Override
	default int compareTo(@NotNull Stargate other)
	{
		// Stargates with a DHD take precedence
		if(!CommonStargateNetworkConfig.disable_dhd_preference.get())
		{
			int dhdRes = Boolean.compare(other.hasDHD(), this.hasDHD());
			if(dhdRes != 0)
				return dhdRes;
		}
		
		// Stargates with higher generation take precedence
		int genRes = other.getGeneration().compareTo(this.getGeneration());
		if(genRes != 0)
			return genRes;
		
		// Stargates with more uses take precedence
		return Integer.compare(other.getTimesOpened(), this.getTimesOpened());
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	AddressFilterInfo addressFilterInfo(MinecraftServer server);
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	/**
	 * Serializes Stargate info into a tag
	 * @param tag CompoundTag that will store the serialized information
	 */
	void serializeNBT(CompoundTag tag);
	
	/**
	 * Deserializes the Stargate info
	 * @param server Current Minecraft Server
	 * @param id9ChevronAddress 9-Chevron Address of the Stargate
	 * @param tag CompoundTag containing information to be deserialized
	 */
	void deserializeNBT(MinecraftServer server, Address.Immutable id9ChevronAddress, CompoundTag tag);
}
