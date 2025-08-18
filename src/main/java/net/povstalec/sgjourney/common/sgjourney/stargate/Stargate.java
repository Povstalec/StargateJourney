package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.sgjourney.*;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Stargate
{
	String DIMENSION = "Dimension";
	String COORDINATES = "Coordinates";
	
	String HAS_DHD = "HasDHD";
	String GENERATION = "Generation";
	String TIMES_OPENED = "TimesOpened";
	
	String NETWORK = "Network";
	
	// Basic Info
	
	Address.Immutable get9ChevronAddress();
	
	@Nullable
	ResourceKey<Level> getDimension();
	
	@Nullable
	Vec3 getPosition();
	
	@Nullable
	SolarSystem.Serializable getSolarSystem(MinecraftServer server);
	
	boolean hasDHD();
	
	StargateInfo.Gen getGeneration();
	
	int getTimesOpened();
	
	int getNetwork();
	
	Address getAddress(MinecraftServer server);
	
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
	
	StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces);
	
	boolean isConnected(MinecraftServer server);
	
	boolean isObstructed(MinecraftServer server);
	
	default boolean isPrimary(MinecraftServer server)
	{
		return false;
	}
	
	default void update(MinecraftServer server) {}
	
	boolean isValid(MinecraftServer server);
	
	boolean isLoaded(MinecraftServer server);
	
	default void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration) {}
	
	// Client Connection
	
	default void updateClient(MinecraftServer server) {}
	
	// Communication
	
	default void receiveStargateMessage(MinecraftServer server, String message) {}
	
	default void forwardTransmission(MinecraftServer server, int transmissionJumps, int frequency, String transmission) {}
	
	float checkStargateShieldingState(MinecraftServer server);
	
	/**
	 * A to update all Tech Interfaces that are connected to the Stargate
	 * @param server The Server this is happening on
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	default void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects) {}
	
	// Energy
	
	long getEnergyStored(MinecraftServer server);
	
	boolean canExtractEnergy(MinecraftServer server, long energy);
	
	void depleteEnergy(MinecraftServer server, long energy, boolean simulate);
	
	// Stargate Connection
	
	default StargateInfo.ChevronLockSpeed getChevronLockSpeed(MinecraftServer server)
	{
		return StargateInfo.ChevronLockSpeed.SLOW;
	}
	
	StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh);
	
	void connectStargate(MinecraftServer server, UUID connectionID, StargateConnection.State connectionState);
	
	default void doWhileConnecting(MinecraftServer server, boolean incoming, boolean doKawoosh, int kawooshStartTicks, int openTime) {}
	
	default void doWhileDialed(MinecraftServer server, Address dialingAddress, int kawooshStartTicks, StargateInfo.ChevronLockSpeed chevronLockSpeed, int openTime) {}
	
	default void setKawooshTickCount(MinecraftServer server, int kawooshTick) {}
	
	default void doKawoosh(MinecraftServer server, int kawooshTime) {}
	
	default void doWhileConnected(MinecraftServer server, boolean incoming, int openTime) {}
	
	void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel);
	
	boolean receiveTraveler(MinecraftServer server, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	
	
	int autoclose(MinecraftServer server);
	
	// Saving and loading
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag);
}
