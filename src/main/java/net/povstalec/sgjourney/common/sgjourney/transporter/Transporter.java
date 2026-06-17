package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.info.TransporterIDFilterInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Transporter extends Comparable<Transporter>
{
	String DIMENSION = "Dimension"; //TODO Change this to "dimension"
	
	String CUSTOM_NAME = "CustomName"; //TODO Change this to "custom_name"
	
	String NETWORK_RESTRICTIONS = "network_restrictions";
	String NETWORKS = "networks";
	
	TransporterType<?> getTransporterType();
	
	/**
	 * @return ID of the Transporter
	 */
	TransporterID getID();
	
	/**
	 * @return Dimension the Transporter is located in or null if it's not located in any Dimension
	 */
	@Nullable
	ResourceKey<Level> getDimension();
	
	/**
	 * @return Address Region the Transporter is located in or null if it's not located in any Address Region
	 */
	@Nullable
	default AddressRegion getAddressRegion(MinecraftServer server)
	{
		return Universe.get(server).getAddressRegionFromDimension(getDimension());
	}
	
	/**
	 * @return Resource Key of the Address Region the Transporter is located in or null if it's not located in any Address Region
	 */
	@Nullable
	default ResourceKey<AddressRegion> getAddressRegionKey(MinecraftServer server)
	{
		AddressRegion addressRegion = getAddressRegion(server);
		if(addressRegion == null)
			return null;
		
		return addressRegion.getResourceKey();
	}
	
	/**
	 * @param server Current Minecraft Server
	 * @return Level the Transporter is currently located in, null if it's not located in any Level
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
	 * @param server Current Minecraft Server
	 * @return Position vector of the Transporter's center or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Transporter is facing or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getForward(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Transporter considers up or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getUp(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Unit Vector with the direction the Transporter considers right or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getRight(MinecraftServer server);
	
	/**
	 * @return Inner Radius of the Transporter or {@literal <= 0} if the Transporter doesn't have a real form
	 */
	double getInnerRadius();
	
	/**
	 * @return Set of networks this Transporter is a part of
	 */
	Set<Integer> getNetworks();
	
	/**
	 * @param networks Network IDs to test
	 * @return False if the provided networks pass the restriction check successfully, otherwise true
	 */
	default boolean isNetworkRestricted(Collection<Integer> networks)
	{
		return false;
	}
	
	/**
	 * @param network Network ID to test
	 * @return False if the provided network passes the restriction check successfully, otherwise true
	 */
	default boolean isNetworkRestricted(int network)
	{
		return isNetworkRestricted(List.of(network));
	}
	
	//TODO Javadoc
	double maxTransportDistance(MinecraftServer server);
	
	//TODO Javadoc
	default double distanceFrom(MinecraftServer server, Transporter other)
	{
		if(getLevel(server) != null && other.getLevel(server) != null && getPosition(server) != null && other.getPosition(server) != null)
			return DimensionType.getTeleportationScale(getLevel(server).dimensionType(), other.getLevel(server).dimensionType()) * Math.sqrt(getPosition(server).distanceTo(other.getPosition(server)));
		
		return Double.NaN; // Distance not applicable
	}
	
	//TODO Javadoc
	default boolean isInRange(MinecraftServer server, Transporter other)
	{
		double distance = distanceFrom(server, other);
		
		if(Double.isNaN(distance))
			return true; // TODO Come up with a way to handle Transporters that aren't actually in any Dimension
		
		return distance <= maxTransportDistance(server);
	}
	
	//TODO Javadoc
	boolean allowInterdimensionalTransport(MinecraftServer server);
	
	/**
	 * Transforms the vector from an absolute coordinate system to a coordinate system relative to Transporter, where X is the direction which the Transporter is facing, Y is Transporter's up direction and Z is Transporter's right direction
	 * @param server Current Minecraft Server
	 * @param vector Vector to be transformed
	 * @param scaleWithTransporter Whether the coordinates should scale with the Transporter (for example, relative position within the Transporter should be scaled with it, but momentum should not)
	 * @return A new vector with the coordinates of the original vector, but transformed to Transporter's relative coordinate system
	 */
	default Vec3 toTransporterCoords(MinecraftServer server, Vec3 vector, boolean scaleWithTransporter)
	{
		Vec3 result = CoordinateHelper.Relative.fromOrthogonalBasis(vector, getForward(server), getUp(server), getRight(server));
		
		if(scaleWithTransporter)
			return new Vec3(result.x() / getInnerRadius(), result.y(), result.z() / getInnerRadius());
		
		return result;
	}
	
	/**
	 * Transforms the vector from a Transporter's relative coordinate system, where X is the direction which the Transporter is facing, Y is Transporter's up direction and Z is Transporter's right direction,
	 * with the X and Z vectors being a percentage of the Transporter's radius, to a vector in the absolute coordinate system
	 * @param server Current Minecraft Server
	 * @param vector Vector to be transformed
	 * @param scaleWithTransporter Whether the coordinates should scale with the Transporter (for example, relative position within the Transporter should be scaled with it, but momentum should not)
	 * @return A new vector with the coordinates of the original vector, but transformed to absolute coordinate system
	 */
	default Vec3 fromTransporterCoords(MinecraftServer server, Vec3 vector, boolean scaleWithTransporter)
	{
		if(scaleWithTransporter)
			vector = new Vec3(vector.x() * getInnerRadius(), vector.y(), vector.z() * getInnerRadius());
		
		return CoordinateHelper.Relative.toOrthogonalBasis(vector, getForward(server), getUp(server), getRight(server));
	}
	
	@Nullable
	Vec3 transportPos(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Returns true if this Transporter is valid (for example, in the case of BlockEntity-based Transporters, if the Block Entity can still be found in the world and if its ID is the same as the Transporter object's)
	 */
	boolean checkValidity(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Returns true if this Transporter is loaded (for example, in the case of Transporters placed in the world, if the Chunk the Transporter is located in is loaded)
	 */
	boolean isLoaded(MinecraftServer server);
	
	// Updating
	
	/**
	 * Updates this Transporter
	 * @param server Current Minecraft Server
	 */
	default void update(MinecraftServer server) {}
	
	/**
	 * Update all Tech Interfaces that are currently connected to the Transporter
	 * @param server The Server this is happening on
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	default void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects) {}
	
	//TODO Javadoc
	@Nullable
	Component getName();
	
	//TODO Javadoc
	TransporterInfo.Feedback resetTransporter(MinecraftServer server, TransporterInfo.Feedback feedback);
	
	// Energy
	
	/**
	 * @param server Current Minecraft Server
	 * @return Energy currently stored in the Transporter's energy buffer
	 */
	long getEnergyStored(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return Max amount of energy that can be stored in the Transporter's energy buffer
	 */
	long getEnergyCapacity(MinecraftServer server);
	
	/**
	 * Extracts energy from the Transporter's energy buffer (used mainly for drawing energy to establish a Transporter Connection)
	 * @param server Current Minecraft Server
	 * @param energy Amount of energy to be depleted
	 * @param simulate True if the depletion will only be simulated and the amount of energy in the Transporter's energy buffer will stay the same, if false, the energy is extracted from the energy buffer
	 * @return Amount of energy that was actually depleted
	 */
	long extractEnergy(MinecraftServer server, long energy, boolean simulate);
	
	// Transporter Connection
	
	//TODO Javadoc
	int getTimeUntilTransport(MinecraftServer server);
	
	//TODO Javadoc
	List<Entity> entitiesToTransport(MinecraftServer server);
	
	//TODO Javadoc
	boolean transportTravelers(MinecraftServer server, TransporterConnection connection, Transporter receivingTransporter, List<Entity> travelers);
	
	//TODO Javadoc
	boolean receiveTraveler(MinecraftServer server, TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	//TODO Javadoc
	void connect(MinecraftServer server, UUID connectionID);
	
	//TODO Javadoc
	void disconnect(MinecraftServer server);
	
	//TODO Javadoc
	boolean isConnected(MinecraftServer server);
	
	/**
	 * @param server Current Minecraft Server
	 * @return True if this Stargate is currently obstructed, otherwise false
	 */
	boolean isObstructed(MinecraftServer server);
	
	//TODO Javadoc
	void updateTicks(MinecraftServer server, int transportTicks, int connectionTime);
	
	//TODO Javadoc
	TransporterInfo.Feedback tryConnect(MinecraftServer server, Transporter initiatingTransporter);
	
	//TODO Javadoc
	@Override
	default int compareTo(@NotNull Transporter other)
	{
		return other.getID().compareTo(this.getID());
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	//TODO Javadoc
	TransporterIDFilterInfo transporterIDFilterInfo(MinecraftServer server);
	
	//TODO Javadoc
	TransporterInfo.Feedback dialTransporter(MinecraftServer server, TransporterID otherID);
	
	//TODO Javadoc
	TransporterInfo.Feedback dialTransporter(MinecraftServer server, Vec3i coords);
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	/**
	 * Serializes Transporter info into a tag
	 * @param tag CompoundTag that will store the serialized information
	 */
	void serializeNBT(CompoundTag tag);
	
	/**
	 * Deserializes the Transporter info
	 * @param server Current Minecraft Server
	 * @param transporterID ID of the Transporter
	 * @param tag CompoundTag containing information to be deserialized
	 */
	void deserializeNBT(MinecraftServer server, TransporterID transporterID, CompoundTag tag);
}
