package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
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
	Capability<Transporter> TRANSPORTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	String DIMENSION = "Dimension"; //TODO Change this to "dimension"
	
	String CUSTOM_NAME = "CustomName"; //TODO Change this to "custom_name"
	
	String NETWORK_RESTRICTIONS = "network_restrictions";
	String NETWORKS = "networks";
	
	TransporterType<?> getTransporterType();
	
	/**
	 * @return Current Minecraft Server
	 */
	MinecraftServer getServer();
	
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
	 * @return Level the Transporter is currently located in, null if it's not located in any Level
	 */
	@Nullable
	default ServerLevel getLevel()
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return getServer().getLevel(dimension);
	}
	
	/**
	 * @return Address Region the Transporter is located in or null if it's not located in any Address Region
	 */
	@Nullable
	default AddressRegion getAddressRegion()
	{
		return Universe.get(getServer()).getAddressRegionFromDimension(getDimension());
	}
	
	/**
	 * @return Resource Key of the Address Region the Transporter is located in or null if it's not located in any Address Region
	 */
	@Nullable
	default ResourceKey<AddressRegion> getAddressRegionKey()
	{
		AddressRegion addressRegion = getAddressRegion();
		if(addressRegion == null)
			return null;
		
		return addressRegion.getResourceKey();
	}
	
	/**
	 * @return Position vector of the Transporter's center or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition();
	
	/**
	 * @return Unit Vector with the direction the Transporter is facing or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getForward();
	
	/**
	 * @return Unit Vector with the direction the Transporter considers up or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getUp();
	
	/**
	 * @return Unit Vector with the direction the Transporter considers right or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getRight();
	
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
	int getTransferEfficiency();
	
	//TODO Javadoc
	double maxTransportDistance();
	
	//TODO Javadoc
	default double distanceFrom(Transporter other)
	{
		if(getLevel() != null && other.getLevel() != null && getPosition() != null && other.getPosition() != null)
			return CoordinateHelper.distanceAcrossDimensions(getLevel().dimensionType(), getPosition(), other.getLevel().dimensionType(), other.getPosition());
		
		return Double.NaN; // Distance not applicable
	}
	
	//TODO Javadoc
	default boolean isInRange(Transporter other)
	{
		double distance = distanceFrom(other);
		
		if(Double.isNaN(distance))
			return true; // TODO Come up with a way to handle Transporters that aren't actually in any Dimension
		
		return distance <= maxTransportDistance();
	}
	
	//TODO Javadoc
	boolean allowInterdimensionalTransport();
	
	/**
	 * Transforms the vector from an absolute coordinate system to a coordinate system relative to Transporter, where X is the direction which the Transporter is facing, Y is Transporter's up direction and Z is Transporter's right direction
	 * @param vector Vector to be transformed
	 * @param scaleWithTransporter Whether the coordinates should scale with the Transporter (for example, relative position within the Transporter should be scaled with it, but momentum should not)
	 * @return A new vector with the coordinates of the original vector, but transformed to Transporter's relative coordinate system
	 */
	default Vec3 toTransporterCoords(Vec3 vector, boolean scaleWithTransporter)
	{
		Vec3 result = CoordinateHelper.Relative.fromOrthogonalBasis(vector, getForward(), getUp(), getRight());
		
		if(scaleWithTransporter)
			return new Vec3(result.x() / getInnerRadius(), result.y(), result.z() / getInnerRadius());
		
		return result;
	}
	
	/**
	 * Transforms the vector from a Transporter's relative coordinate system, where X is the direction which the Transporter is facing, Y is Transporter's up direction and Z is Transporter's right direction,
	 * with the X and Z vectors being a percentage of the Transporter's radius, to a vector in the absolute coordinate system
	 * @param vector Vector to be transformed
	 * @param scaleWithTransporter Whether the coordinates should scale with the Transporter (for example, relative position within the Transporter should be scaled with it, but momentum should not)
	 * @return A new vector with the coordinates of the original vector, but transformed to absolute coordinate system
	 */
	default Vec3 fromTransporterCoords(Vec3 vector, boolean scaleWithTransporter)
	{
		if(scaleWithTransporter)
			vector = new Vec3(vector.x() * getInnerRadius(), vector.y(), vector.z() * getInnerRadius());
		
		return CoordinateHelper.Relative.toOrthogonalBasis(vector, getForward(), getUp(), getRight());
	}
	
	@Nullable
	Vec3 transportPos();
	
	/**
	 * @return Returns true if this Transporter is valid (for example, in the case of BlockEntity-based Transporters, if the Block Entity can still be found in the world and if its ID is the same as the Transporter object's)
	 */
	boolean checkValidity();
	
	/**
	 * @return Returns true if this Transporter is loaded (for example, in the case of Transporters placed in the world, if the Chunk the Transporter is located in is loaded)
	 */
	boolean isLoaded();
	
	// Updating
	
	/**
	 * Updates this Transporter
	 */
	default void update() {}
	
	/**
	 * Update all Tech Interfaces that are currently connected to the Transporter
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	default void updateInterfaceBlocks(@Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects) {}
	
	//TODO Javadoc
	@Nullable
	Component getName();
	
	//TODO Javadoc
	TransporterInfo.FeedbackMessage resetTransporter(TransporterInfo.FeedbackMessage feedback);
	
	//TODO Javadoc
	default TransporterInfo.FeedbackMessage resetTransporter(TransporterInfo.Feedback feedback, Object... additionalInfo)
	{
		return resetTransporter(feedback.withInfo(additionalInfo));
	}
	
	// Energy
	
	/**
	 * @return Energy currently stored in the Transporter's energy buffer
	 */
	long getEnergyStored();
	
	/**
	 * @return Max amount of energy that can be stored in the Transporter's energy buffer
	 */
	long getEnergyCapacity();
	
	/**
	 * Extracts energy from the Transporter's energy buffer (used mainly for drawing energy to establish a Transporter Connection)
	 * @param energy Amount of energy to be depleted
	 * @param simulate True if the depletion will only be simulated and the amount of energy in the Transporter's energy buffer will stay the same, if false, the energy is extracted from the energy buffer
	 * @return Amount of energy that was actually depleted
	 */
	long extractEnergy(long energy, boolean simulate);
	
	// Transporter Connection
	
	//TODO Javadoc
	int getTimeUntilTransport();
	
	//TODO Javadoc
	List<Entity> entitiesToTransport();
	
	//TODO Javadoc
	boolean transportTravelers(TransporterConnection connection, Transporter receivingTransporter, List<Entity> travelers);
	
	//TODO Javadoc
	boolean receiveTraveler(TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	//TODO Javadoc
	void connect(UUID connectionID);
	
	//TODO Javadoc
	void disconnect();
	
	//TODO Javadoc
	boolean isConnected();
	
	/**
	 * @return True if this Stargate is currently obstructed, otherwise false
	 */
	boolean isObstructed();
	
	//TODO Javadoc
	void updateTicks(int transportTicks, int connectionTime);
	
	//TODO Javadoc
	TransporterInfo.FeedbackMessage tryConnect(Transporter initiatingTransporter);
	
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
	TransporterIDFilterInfo transporterIDFilterInfo();
	
	//TODO Javadoc
	TransporterInfo.FeedbackMessage dialTransporter(TransporterID otherID);
	
	//TODO Javadoc
	TransporterInfo.FeedbackMessage dialTransporter(Vec3i coords);
	
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
	 * @param transporterID ID of the Transporter
	 * @param tag CompoundTag containing information to be deserialized
	 */
	void deserializeNBT(TransporterID transporterID, CompoundTag tag);
}
