package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
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
	
	Address.Immutable get9ChevronAddress();
	
	ResourceKey<Level> getDimension(); // TODO Remove
	
	BlockPos getBlockPos(); // TODO Remove
	
	@Nullable
	public AbstractStargateEntity getStargateEntity(MinecraftServer server); //TODO Remove
	
	boolean hasDHD();
	
	StargateInfo.Gen getGeneration();
	
	int getTimesOpened();
	
	int getNetwork();
	
	StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces);
	
	boolean isConnected(MinecraftServer server);
	
	boolean isObstructed(MinecraftServer server);
	
	boolean canExtractEnergy(MinecraftServer server, long energy);
	
	void depleteEnergy(MinecraftServer server, long energy, boolean simulate);
	
	StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh);
	
	boolean isPrimary(MinecraftServer server);
	
	void update(AbstractStargateEntity stargate); //TODO Remove
	
	boolean checkStargateEntity(MinecraftServer server); //TODO Remove
	
	void doWhileDialed(MinecraftServer server, int openTime, StargateInfo.ChevronLockSpeed chevronLockSpeed);
	
	void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration);
	
	void updateInterfaceBlocks(MinecraftServer server, @Nullable String eventName, Object... objects);
	
	void setKawooshTickCount(MinecraftServer server, int kawooshTick);
	
	void updateClient(MinecraftServer server);
	
	void connectStargate(MinecraftServer server, UUID connectionID, StargateConnection.State connectionState);
	
	void receiveStargateMessage(MinecraftServer server, String message);
	
	// Saving and loading
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag);
}
