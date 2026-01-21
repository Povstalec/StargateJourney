package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface Transporter
{
	String DIMENSION = "Dimension";
	String COORDINATES = "Coordinates";
	
	String CUSTOM_NAME = "CustomName";
	
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
	 * @param frequency Frequency to be tested
	 * @return True if the specified frequency is accepted by the Transporter, otherwise false
	 */
	boolean acceptsFrequency(int frequency);
	
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
	
	@Nullable
	Component getName();
	
	int getTimeOffset(MinecraftServer server);
	
	List<Entity> entitiesToTransport(MinecraftServer server);
	
	void transportTravelers(MinecraftServer server, TransporterConnection connection, Transporter receivingTransporter, List<Entity> travelers);
	
	boolean receiveTraveler(MinecraftServer server, TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	void connect(MinecraftServer server, UUID connectionID);
	
	void disconnect(MinecraftServer server);
	
	void reset(MinecraftServer server);
	
	boolean isConnected(MinecraftServer server);
	
	void updateTicks(MinecraftServer server, int connectionTime);
	
	/**
	 * @return Transporter info serialized into a CompoundTag
	 */
	CompoundTag serializeNBT();
	
	/**
	 * Deserializes the Transporter info
	 * @param server Current Minecraft Server
	 * @param transporterID ID of the Transporter
	 * @param tag CompoundTag containing information to be deserialized
	 */
	void deserializeNBT(MinecraftServer server, TransporterID transporterID, CompoundTag tag);
}
