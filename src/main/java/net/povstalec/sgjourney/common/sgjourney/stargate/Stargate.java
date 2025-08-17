package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

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
	
	ResourceKey<Level> getDimension(); // TODO Remove
	
	BlockPos getBlockPos(); // TODO Remove
	
	@Nullable
	AbstractStargateEntity getStargateEntity(MinecraftServer server); //TODO Remove
	
	boolean hasDHD();
	
	StargateInfo.Gen getGeneration();
	
	int getTimesOpened();
	
	int getNetwork();
	
	Address getAddress(MinecraftServer server);
	
	Address getConnectionAddress(MinecraftServer server, int addressLength);
	
	StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces);
	
	boolean isConnected(MinecraftServer server);
	
	boolean isObstructed(MinecraftServer server);
	
	boolean isPrimary(MinecraftServer server);
	
	void update(AbstractStargateEntity stargate); //TODO Remove
	
	boolean checkStargateEntity(MinecraftServer server); //TODO Remove
	
	void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration);
	
	// Client Connection
	
	void updateClient(MinecraftServer server);
	
	// Communication
	
	void receiveStargateMessage(MinecraftServer server, String message);
	
	void forwardTransmission(MinecraftServer server, int transmissionJumps, int frequency, String transmission);
	
	float checkStargateShieldingState(MinecraftServer server);
	
	/**
	 * A to update all Tech Interfaces that are connected to the Stargate
	 * @param server The Server this is happening on
	 * @param type Type of Interfaces that should be updated, null will update all types
	 * @param eventName Name of the event with which to update the Interfaces, leave as null if there is none
	 * @param objects Objects that can be sent along with the event to update Interfaces
	 */
	void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects);
	
	// Energy
	
	long getEnergyStored(MinecraftServer server);
	
	boolean canExtractEnergy(MinecraftServer server, long energy);
	
	void depleteEnergy(MinecraftServer server, long energy, boolean simulate);
	
	// Stargate Connection
	
	StargateInfo.ChevronLockSpeed getChevronLockSpeed(MinecraftServer server);
	
	StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh);
	
	void connectStargate(MinecraftServer server, UUID connectionID, StargateConnection.State connectionState);
	
	void doWhileConnecting(MinecraftServer server, boolean incoming, boolean doKawoosh, int kawooshStartTicks, int openTime);
	
	void doWhileDialed(MinecraftServer server, Address dialingAddress, int kawooshStartTicks, StargateInfo.ChevronLockSpeed chevronLockSpeed, int openTime);
	
	void setKawooshTickCount(MinecraftServer server, int kawooshTick);
	
	void doKawoosh(MinecraftServer server, int kawooshTime);
	
	void doWhileConnected(MinecraftServer server, boolean incoming, int openTime);
	
	void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel);
	
	boolean tryWormholeEntity(MinecraftServer server, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle);
	
	
	
	int autoclose(MinecraftServer server);
	
	// Saving and loading
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag);
}
