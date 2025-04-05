package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;

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
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag);
}
