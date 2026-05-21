package net.povstalec.sgjourney.common.sgjourney.transporter;

import javax.annotation.Nullable;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SGJourneyTransporter implements Transporter
{
	public static final Vec3 FORWARD = new Vec3(1, 0, 0);
	public static final Vec3 UP = new Vec3(0, 1, 0);
	public static final Vec3 RIGHT = new Vec3(0, 0, 1);
	
	private final TransporterType<?> type;
	
	protected TransporterID transporterID;
	protected ResourceKey<Level> dimension;
	
	protected Set<Integer> networks = new HashSet<>();
	
	@Nullable
	protected Component name;
	
	public SGJourneyTransporter(TransporterType<?> type)
	{
		this.type = type;
	}
	
	@Override
	public final TransporterType<?> getTransporterType()
	{
		return this.type;
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
	public Set<Integer> getNetworks()
	{
		return this.networks;
	}
	
	@Override
	public Component getName()
	{
		return name != null ? name : Component.empty();
	}
	
	@Override
	public boolean receiveTraveler(MinecraftServer server, TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		Vec3 destinationPosition = fromTransporterCoords(server, relativePosition, true).add(transportPos(server));
		Vec3 destinationMomentum = fromTransporterCoords(server, relativeMomentum, false);
		Vec3 destinationLookAngle = fromTransporterCoords(server, relativeLookAngle, false);
		
		return Transporting.receiveTraveler(connection, getLevel(server), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
	}
	
	@Override
	public TransporterInfo.Feedback tryConnect(MinecraftServer server, Transporter initiatingTransporter)
	{
		// If Transporter is obstructed
		if(isObstructed(server))
			return TransporterInfo.Feedback.TARGET_OBSTRUCTED;
		
		// If Transporter is restricted
		if(isNetworkRestricted(server, initiatingTransporter.getNetworks()))
			return TransporterInfo.Feedback.TARGET_RESTRICTED;
		
		if(transporterIDFilterInfo(server).getFilterType().isBlacklist() && transporterIDFilterInfo(server).isIDBlacklisted(initiatingTransporter.getID()))
			return TransporterInfo.Feedback.BLACKLISTED_BY_TARGET;
		
		// If Transporter has a whitelist
		if(transporterIDFilterInfo(server).getFilterType().isWhitelist() && !transporterIDFilterInfo(server).isIDWhitelisted(initiatingTransporter.getID()))
			return TransporterInfo.Feedback.NOT_WHITELISTED_BY_TARGET;
		
		return Dialing.connectTransporters(server, initiatingTransporter, this);
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	public TransporterInfo.Feedback dialTransporter(MinecraftServer server, TransporterID otherID)
	{
		if(isObstructed(server))
			return resetTransporter(server, TransporterInfo.Feedback.SELF_OBSTRUCTED);
		
		if(transporterIDFilterInfo(server).getFilterType().isBlacklist() && transporterIDFilterInfo(server).isIDBlacklisted(otherID))
			return TransporterInfo.Feedback.TARGET_BLACKLISTED;
		
		if(transporterIDFilterInfo(server).getFilterType().isWhitelist() && !transporterIDFilterInfo(server).isIDWhitelisted(otherID))
			return TransporterInfo.Feedback.TARGET_NOT_WHITELISTED;
		
		return Dialing.dialTransporterID(server, this, otherID, false);
	}
	
	@Override
	public TransporterInfo.Feedback dialTransporter(MinecraftServer server, Vec3i coords)
	{
		if(isObstructed(server))
			return resetTransporter(server, TransporterInfo.Feedback.SELF_OBSTRUCTED);
		
		return Dialing.dialTransporterCoords(server, this, coords, false);
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		tag.putString(DIMENSION, getDimension().location().toString());
		
		if(this.name != null)
			tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name));
		
		tag.putIntArray(NETWORKS, networks.stream().toList());
	}
	
	public void deserializeNBT(MinecraftServer server, TransporterID transporterID, CompoundTag tag)
	{
		this.transporterID = transporterID;
		
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		
		if(tag.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
			this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		if(tag.contains("Network", Tag.TAG_INT)) //TODO Keeping this here for the time being for legacy reasons
			this.networks = new HashSet<>(List.of(tag.getInt("Network")));
		else if(tag.contains(NETWORKS, Tag.TAG_INT_ARRAY))
			this.networks = new HashSet<>(Arrays.stream(tag.getIntArray(NETWORKS)).boxed().toList());
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public String toString()
	{
		return "[ " + getName().toString() + " | ID: " + getID() + " ]";
	}
}
