package net.povstalec.sgjourney.common.sgjourney.transporter;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.Transporting;

public class SGJourneyTransporter implements Transporter
{
	public static final Vec3 FORWARD = new Vec3(1, 0, 0);
	public static final Vec3 UP = new Vec3(0, 1, 0);
	public static final Vec3 RIGHT = new Vec3(0, 0, 1);
	public static final double INNER_RADIUS = 2;
	
	private TransporterID transporterID;
	private ResourceKey<Level> dimension;
	private BlockPos blockPos;
	
	@Nullable
	private Component name;
	
	public SGJourneyTransporter() {}
	
	public SGJourneyTransporter(TransporterID transporterID, ResourceKey<Level> dimension, BlockPos blockPos, Component name)
	{
		this.transporterID = transporterID;
		this.dimension = dimension;
		this.blockPos = blockPos;
		
		this.name = name;
	}
	
	public SGJourneyTransporter(AbstractTransporterEntity transporterEntity)
	{
		this(transporterEntity.getID(), transporterEntity.getLevel().dimension(), transporterEntity.getBlockPos(), transporterEntity.getCustomName());
	}
	
	@Override
	public TransporterID getID()
	{
		return transporterID;
	}
	
	@Override
	public ResourceKey<Level> getDimension()
	{
		return dimension;
	}
	
	public BlockPos getBlockPos()
	{
		return blockPos;
	}
	
	@Override
	public @Nullable Vec3 getPosition(MinecraftServer server)
	{
		return getBlockPos().getCenter();
	}
	
	@Override
	public @Nullable Vec3 getForward(MinecraftServer server)
	{
		return FORWARD;
	}
	
	@Override
	public @Nullable Vec3 getUp(MinecraftServer server)
	{
		return UP;
	}
	
	@Override
	public @Nullable Vec3 getRight(MinecraftServer server)
	{
		return RIGHT;
	}
	
	@Override
	public double getInnerRadius()
	{
		return INNER_RADIUS;
	}
	
	public boolean acceptsFrequency(int frequency)
	{
		return true; // TODO Add frequency logic
	}
	
	@Override
	@Nullable
	public Vec3 transportPos(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.transportPos().getCenter();
		
		return null;
	}
	
	@Override
	public Component getName()
	{
		return name != null ? name : Component.empty();
	}
	
	@Nullable
	public AbstractTransporterEntity getTransporterEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			return transporter;
		
		return null;
	}
	
	@Override
	public int getTimeOffset(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.getTimeOffset();
		
		return 0;
	}
	
	@Override
	public List<Entity> entitiesToTransport(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.entitiesToTransport();
		
		return ImmutableList.of();
	}
	
	@Override
	public void transportTravelers(MinecraftServer server, TransporterConnection connection, Transporter receivingTransporter, List<Entity> travelers)
	{
		transporterRun(server, transporter ->
		{
			Transporting.transportTravelers(server, connection, this, receivingTransporter, travelers);
		});
		
	}
	
	@Override
	public boolean receiveTraveler(MinecraftServer server, TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		Vec3 destinationPosition = fromTransporterCoords(server, relativePosition, true).add(transportPos(server));
		Vec3 destinationMomentum = fromTransporterCoords(server, relativeMomentum, false);
		Vec3 destinationLookAngle = fromTransporterCoords(server, relativeLookAngle, false);
		
		return Transporting.receiveTraveler(getLevel(server), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
	}
	
	@Override
	public void connect(MinecraftServer server, UUID connectionID)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.connectTransporter(connectionID);
	}
	
	@Override
	public void disconnect(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.disconnectTransporter();
	}
	
	@Override
	public void reset(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.resetTransporter();
	}
	
	@Override
	public boolean isConnected(MinecraftServer server)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return transporter.isConnected();
		
		return false;
	}
	
	@Override
	public void updateTicks(MinecraftServer server, int connectionTime)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			transporter.updateTicks(connectionTime);
	}
	
	
	
	@Override
	public String toString()
	{
		String nameString = name != null ? name.getString() : transporterID.toString();
		
		return "[ " + nameString + " | Pos: " + blockPos.toString() + " ]";
	}
	
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag transporterTag = new CompoundTag();
		ResourceKey<Level> level = this.getDimension();
		BlockPos pos = this.getBlockPos();
		
		transporterTag.putString(DIMENSION, level.location().toString());
		transporterTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		
		if(this.name != null)
			transporterTag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name));
		
		return transporterTag;
	}
	
	public void deserializeNBT(MinecraftServer server, TransporterID transporterID, CompoundTag tag)
	{
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		this.blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		if(tag.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
			this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		this.transporterID = transporterID;
	}
	
	
	
	public interface TransporterConsumer
	{
		void run(AbstractTransporterEntity transporter);
	}
	
	public interface ReturnTransporterConsumer<T>
	{
		T run(AbstractTransporterEntity transporter);
	}
	
	private void transporterRun(MinecraftServer server, TransporterConsumer consumer)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			consumer.run(transporter);
	}
	
	private <T> T transporterReturn(MinecraftServer server, ReturnTransporterConsumer<T> consumer, @Nullable T defaultValue)
	{
		AbstractTransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return consumer.run(transporter);
		
		return defaultValue;
	}
}
