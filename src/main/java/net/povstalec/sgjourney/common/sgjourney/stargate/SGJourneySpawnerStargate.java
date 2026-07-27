package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.SpawnerTimer;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SGJourneySpawnerStargate implements SpawnerStargate
{
	protected final StargateType<?> type;
	protected final MinecraftServer server;
	
	protected Address.Immutable id9ChevronAddress;
	
	protected Address.Mutable address = new Address.Mutable();
	@Nullable
	protected UUID connectionID = null;
	
	protected SpawnerTimer spawnerTimer = new SpawnerTimer();
	
	protected Function<RandomSource, EntityType<?>> entityTypeRandomizer;
	protected BiConsumer<Entity, RandomSource> onEntitySpawn;
	
	public SGJourneySpawnerStargate(StargateType<?> type, MinecraftServer server)
	{
		this.type = type;
		this.server = server;
	}
	
	@Override
	public final StargateType<?> getStargateType()
	{
		return this.type;
	}
	
	@Override
	public MinecraftServer getServer()
	{
		return this.server;
	}
	
	@Override
	public Address.Immutable get9ChevronAddress()
	{
		return this.id9ChevronAddress;
	}
	
	@Override
	public @Nullable ResourceKey<Level> getDimension()
	{
		return Conversion.stringToDimension("sgjourney:abydos"); // TODO Don't have it in Abydos
	}
	
	@Override
	public boolean hasDHD()
	{
		return true; //TODO
	}
	
	@Override
	public int getTimesOpened()
	{
		return 0; //TODO Actually count the number of times opened
	}
	
	@Override
	public Address.Mutable getAddress()
	{
		return this.address;
	}
	
	@Override
	public StargateInfo.FeedbackMessage resetStargate(StargateInfo.FeedbackMessage feedback)
	{
		this.connectionID = null;
		this.address.reset();
		
		this.spawnerTimer.reset();
		return feedback;
	}
	
	@Override
	public boolean isConnected()
	{
		return this.connectionID != null;
	}
	
	@Override
	public float checkStargateShieldingState()
	{
		return 0;
	}
	
	@Override
	public long getEnergyStored()
	{
		return CommonStargateConfig.intergalactic_connection_energy_cost.get(); // CommonStargateConfig.interstellar_connection_energy_cost.get(); //TODO Add some actual energy handling
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return CommonStargateConfig.stargate_energy_capacity.get();
	}
	
	@Override
	public long extractEnergy(long energy, boolean simulate)
	{
		return Math.min(energy, getEnergyStored());
	}
	
	// Stargate Connection
	
	public abstract StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh);
	
	@Override
	public int dialedEngageTime(boolean doKawoosh)
	{
		return getChevronLockSpeed(doKawoosh).getKawooshStartTicks();
	}
	
	@Override
	public int wormholeEstablishTime(boolean doKawoosh)
	{
		return 0;
	}
	
	public void encodeAddress(Address address)
	{
		this.address = new Address.Mutable(address);
	}
	
	@Override
	public void connectStargate(StargateConnection connection, StargateConnection.State connectionState)
	{
		this.connectionID = connection.getID();
	}
	
	@Override
	public void serializeNBT(CompoundTag tag, HolderLookup.Provider registries)
	{
		//TODO
	}
	
	@Override
	public void deserializeNBT(Address.Immutable id9ChevronAddress, CompoundTag tag, HolderLookup.Provider registries)
	{
		this.id9ChevronAddress = id9ChevronAddress;
		
		//TODO
	}
	
	//============================================================================================
	//**************************************Entity Spawning***************************************
	//============================================================================================
	
	public SGJourneySpawnerStargate setEntityTypeRandomizer(Function<RandomSource, EntityType<?>> entityTypeRandomizer)
	{
		this.entityTypeRandomizer = entityTypeRandomizer;
		
		return this;
	}
	
	public SGJourneySpawnerStargate setOnEntitySpawn(BiConsumer<Entity, RandomSource> onEntitySpawn)
	{
		this.onEntitySpawn = onEntitySpawn;
		
		return this;
	}
	
	@Override
	public SpawnerTimer getSpawnerTimer()
	{
		return spawnerTimer;
	}
	
	@Override
	public @Nullable Entity createEntity(ServerLevel level)
	{
		if(entityTypeRandomizer == null)
			return null;
		
		EntityType<?> entityType = entityTypeRandomizer.apply(level.getRandom());
		
		if(entityType == null)
			return null;
		
		return entityType.create(level);
	}
	
	@Override
	public void spawnEntity(ServerLevel level, Entity entity)
	{
		if(entity instanceof Mob mob)
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData) null);
		
		onEntitySpawn.accept(entity, level.getRandom());
	}
}
