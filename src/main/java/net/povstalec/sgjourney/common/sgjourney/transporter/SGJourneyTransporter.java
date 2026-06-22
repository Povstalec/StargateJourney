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

import java.util.*;

public abstract class SGJourneyTransporter implements Transporter
{
	public static final String ALLOW_INTERDIMENSIONAL_TRANSPORT = "interdimensional_transport";
	public static final String MAX_TRANSPORT_DISTANCE = "max_transport_distance";
	public static final String TRANSFER_EFFICIENCY = "transfer_efficiency";
	
	public static final Vec3 FORWARD = new Vec3(1, 0, 0);
	public static final Vec3 UP = new Vec3(0, 1, 0);
	public static final Vec3 RIGHT = new Vec3(0, 0, 1);
	
	protected final TransporterType<?> type;
	protected final MinecraftServer server;
	
	protected TransporterID transporterID;
	protected ResourceKey<Level> dimension;
	
	protected boolean hasNetworkRestrictions = false;
	protected Set<Integer> networks = new HashSet<>();
	
	protected int transferEfficiency = 1;
	
	protected boolean allowInterdimensionalTransport = false;
	protected double maxTransportDistance = 0;
	
	@Nullable
	protected Component name;
	
	public SGJourneyTransporter(TransporterType<?> type, MinecraftServer server)
	{
		this.type = type;
		this.server = server;
	}
	
	@Override
	public final TransporterType<?> getTransporterType()
	{
		return this.type;
	}
	
	@Override
	public MinecraftServer getServer()
	{
		return this.server;
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
	public Set<Integer> getNetworks()
	{
		return this.networks;
	}
	
	@Override
	public boolean isNetworkRestricted(Collection<Integer> testedNetworks)
	{
		// If Transporter has network restrictions turned on, check if the tested network matches any of the networks Transporter is in
		if(hasNetworkRestrictions)
			return Collections.disjoint(getNetworks(), testedNetworks);
		
		return false;
	}
	
	@Override
	public Component getName()
	{
		return name != null ? name : Component.empty();
	}
	
	@Override
	public boolean receiveTraveler(TransporterConnection connection, Transporter sendingTransporter, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		Vec3 destinationPosition = fromTransporterCoords(relativePosition, true).add(transportPos());
		Vec3 destinationMomentum = fromTransporterCoords(relativeMomentum, false);
		Vec3 destinationLookAngle = fromTransporterCoords(relativeLookAngle, false);
		
		return Transporting.receiveTraveler(connection, getLevel(), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
	}
	
	@Override
	public TransporterInfo.FeedbackMessage tryConnect(Transporter initiatingTransporter)
	{
		// If Transporter is obstructed
		if(isObstructed())
			return TransporterInfo.Feedback.TARGET_OBSTRUCTED.withInfo();
		
		// If Transporter is restricted
		if(isNetworkRestricted(initiatingTransporter.getNetworks()))
			return TransporterInfo.Feedback.TARGET_RESTRICTED.withInfo();
		
		if(transporterIDFilterInfo().getFilterType().isBlacklist() && transporterIDFilterInfo().isIDBlacklisted(initiatingTransporter.getID()))
			return TransporterInfo.Feedback.BLACKLISTED_BY_TARGET.withInfo();
		
		// If Transporter has a whitelist
		if(transporterIDFilterInfo().getFilterType().isWhitelist() && !transporterIDFilterInfo().isIDWhitelisted(initiatingTransporter.getID()))
			return TransporterInfo.Feedback.NOT_WHITELISTED_BY_TARGET.withInfo();
		
		return Dialing.connectTransporters(server, initiatingTransporter, this);
	}
	
	@Override
	public int getTransferEfficiency()
	{
		return transferEfficiency;
	}
	
	@Override
	public double maxTransportDistance()
	{
		return maxTransportDistance;
	}
	
	@Override
	public boolean allowInterdimensionalTransport()
	{
		return allowInterdimensionalTransport;
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	public TransporterInfo.FeedbackMessage dialTransporter(TransporterID otherID)
	{
		if(isObstructed())
			return resetTransporter(TransporterInfo.Feedback.SELF_OBSTRUCTED);
		
		if(transporterIDFilterInfo().getFilterType().isBlacklist() && transporterIDFilterInfo().isIDBlacklisted(otherID))
			return TransporterInfo.Feedback.TARGET_BLACKLISTED.withInfo();
		
		if(transporterIDFilterInfo().getFilterType().isWhitelist() && !transporterIDFilterInfo().isIDWhitelisted(otherID))
			return TransporterInfo.Feedback.TARGET_NOT_WHITELISTED.withInfo();
		
		return Dialing.dialTransporterID(server, this, otherID, false);
	}
	
	@Override
	public TransporterInfo.FeedbackMessage dialTransporter(Vec3i coords)
	{
		if(isObstructed())
			return resetTransporter(TransporterInfo.Feedback.SELF_OBSTRUCTED);
		
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
		
		tag.putBoolean(NETWORK_RESTRICTIONS, hasNetworkRestrictions);
		tag.putIntArray(NETWORKS, networks.stream().toList());
		
		tag.putInt(TRANSFER_EFFICIENCY, transferEfficiency);
		
		tag.putBoolean(ALLOW_INTERDIMENSIONAL_TRANSPORT, allowInterdimensionalTransport);
		tag.putDouble(MAX_TRANSPORT_DISTANCE, maxTransportDistance);
	}
	
	public void deserializeNBT(TransporterID transporterID, CompoundTag tag)
	{
		this.transporterID = transporterID;
		
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		
		if(tag.contains(CUSTOM_NAME, CompoundTag.OBJECT_HEADER))
			this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		this.hasNetworkRestrictions = tag.getBoolean(NETWORK_RESTRICTIONS);
		if(tag.contains("Network", Tag.TAG_INT)) //TODO Keeping this here for the time being for legacy reasons
			this.networks = new HashSet<>(List.of(tag.getInt("Network")));
		else if(tag.contains(NETWORKS, Tag.TAG_INT_ARRAY))
			this.networks = new HashSet<>(Arrays.stream(tag.getIntArray(NETWORKS)).boxed().toList());
		
		this.transferEfficiency = tag.getInt(TRANSFER_EFFICIENCY);
		
		this.allowInterdimensionalTransport = tag.getBoolean(ALLOW_INTERDIMENSIONAL_TRANSPORT);
		this.maxTransportDistance = tag.getDouble(MAX_TRANSPORT_DISTANCE);
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
