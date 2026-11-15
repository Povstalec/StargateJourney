package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;
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
	
	protected SGJourneyEnergy energyStorage = createEnergyStorage();
	
	// Connection stuff
	protected int connectionTime = 0;
	protected int kawooshTime = 0;
	protected int openTime = 0;
	protected int timeSinceLastTraveler = 0;
	
	// Spawning
	
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
	public boolean hasNetworkRestrictions()
	{
		return false;
	}
	
	@Override
	public Address getAddress()
	{
		return this.address;
	}
	
	public void encodeAddress(Address address)
	{
		this.address = new Address.Mutable(address);
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
	public StargateConnection.Direction getConnectionDirection()
	{
		// It shouldn't be possible to dial this Stargate, so doing it like this should be fine
		return isConnected() ? StargateConnection.Direction.OUTGOING : StargateConnection.Direction.NONE;
	}
	
	@Override
	public boolean isWormholeOpen()
	{
		if(isConnected())
		{
			StargateConnection connection = StargateNetwork.get(getServer()).getConnection(connectionID);
			if(connection != null)
				return connection.getOpenTime() > 0;
		}
		
		return false;
	}
	
	@Override
	public float checkStargateShieldingState()
	{
		return 0;
	}
	
	public SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(CommonStargateConfig.stargate_energy_capacity.get(), CommonStargateConfig.intergalactic_connection_energy_cost.get(), 0)
		{
			@Override
			public void onEnergyChanged(long difference, boolean simulate) {}
			
			@Override
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				return maxReceive;
			}
			
			@Override
			public long depleteEnergy(long maxExtract, boolean simulate)
			{
				return maxExtract;
			}
		};
	}
	
	@Override
	public SGJourneyEnergy getEnergyStorage()
	{
		return energyStorage;
	}
	
	@Override
	public long getEnergyStored()
	{
		return energyStorage.getTrueEnergyStored(); // CommonStargateConfig.interstellar_connection_energy_cost.get();
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return energyStorage.getTrueMaxEnergyStored();
	}
	
	@Override
	public long extractEnergy(long energy, boolean simulate)
	{
		return Math.min(energy, getEnergyStored());
	}
	
	@Override
	public long receiveEnergy(long energy, boolean simulate)
	{
		return energy;
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	@Override
	public StargateInfo.FeedbackMessage directEngageSymbol(int symbol, boolean canEngageStargate, StargateInfo.ChevronSound chevronSound)
	{
		if(getSymbolMap().isSymbolOutOfBounds(symbol))
			return StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS.withInfo(symbol);
		
		return encodeSymbol(getSymbolMap().getMappedSymbol(symbol), canEngageStargate);
	}
	
	/**
	 * Method to encode symbols and handle edge cases involving Point of Origin
	 * @param symbol Symbol to be encoded
	 * @param canEngageStargate If true, encoding the Point of Origin will automatically engage the Stargate
	 * @return Feedback from encoding the symbol
	 */
	protected StargateInfo.FeedbackMessage encodeSymbol(int symbol, boolean canEngageStargate)
	{
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
			else
				return StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.withInfo();
		}
		
		StargateInfo.FeedbackMessage feedback = encodeChevron(symbol);
		
		if(canEngageStargate && getAddress().hasPointOfOriginOrMaxLength() && !feedback.feedback().isError())
			return engageStargate(true, Dialing.Action.EXECUTE);
		
		return feedback;
	}
	
	protected StargateInfo.FeedbackMessage encodeChevron(int symbol)
	{
		if(address.containsSymbol(symbol)) // Address already contains the encoded symbol
			return StargateInfo.Feedback.SYMBOL_IN_ADDRESS.withInfo(symbol);
		
		if(!address.addSymbol(symbol)) // Trying to encode 10th symbol (impossible)
			return resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		
		return StargateInfo.Feedback.SYMBOL_ENCODED.withInfo(symbol);
	}
	
	@Override
	public StargateInfo.FeedbackMessage engageStargate(boolean doKawoosh, Dialing.Action action)
	{
		return Dialing.dialStargate(getServer(), this, getAddress(), true, true/*Only search for loaded Stargates*/, action);
	}
	
	@Override
	public StargateInfo.FeedbackMessage instaDial(Address address, boolean doKawoosh, Dialing.Action action)
	{
		encodeAddress(address);
		return engageStargate(doKawoosh, action);
	}
	
	@Override
	public StargateInfo.FeedbackMessage disconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		//TODO Maybe check the side of the connection?
		
		return bypassDisconnectStargate(feedback);
	}
	
	@Override
	public StargateInfo.FeedbackMessage bypassDisconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		if(connectionID != null)
			StargateNetwork.get(server).terminateConnection(connectionID, feedback);
		return resetStargate(feedback);
	}
	
	//============================================================================================
	//************************************Stargate Connection*************************************
	//============================================================================================
	
	public abstract StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh);
	
	@Override
	public int getConnectionTime()
	{
		return connectionTime;
	}
	
	@Override
	public int getKawooshTime()
	{
		return kawooshTime;
	}
	
	@Override
	public int getOpenTime()
	{
		return openTime;
	}
	
	@Override
	public int getTimeSinceLastTraveler()
	{
		return timeSinceLastTraveler;
	}
	
	@Override
	public void updateFromConnection(StargateConnection connection)
	{
		connectionTime = connection.getConnectionTime();
		kawooshTime = connection.getKawooshTime();
		openTime = connection.getOpenTime();
		timeSinceLastTraveler = connection.getTimeSinceLastTraveler();
	}
	
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
	
	@Override
	public void connectStargate(StargateConnection connection, StargateConnection.State connectionState)
	{
		this.connectionID = connection.getID();
	}
	
	@Override
	public void tick() {}
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		//TODO
	}
	
	@Override
	public void deserializeNBT(Address.Immutable id9ChevronAddress, CompoundTag tag)
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
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData) null, (CompoundTag) null);
		
		onEntitySpawn.accept(entity, level.getRandom());
	}
}
