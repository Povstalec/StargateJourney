package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpawnerStargate implements Stargate
{
	public static final double INNER_RADIUS = Wormhole.INNER_RADIUS;
	
	public static final int KAWOOSH_TICKS = 40;
	
	private final StargateType<?> type;
	private final MinecraftServer server;
	
	protected Address.Immutable id9ChevronAddress;
	
	protected int attackerMinCount;
	protected int attackerMaxCount;
	protected int attackerMinInterval;
	protected int attackerMaxInverval;
	
	protected Address.Mutable address = new Address.Mutable();
	@Nullable
	protected UUID connectionID = null;
	
	protected Random random;
	protected int counter;
	protected int timer;
	
	protected Function<RandomSource, EntityType<?>> randomizedEntityType;
	protected BiConsumer<Entity, RandomSource> onEntitySpawn;
	
	public SpawnerStargate(StargateType<?> type, MinecraftServer server)
	{
		this.type = type;
		this.server = server;
	}
	
	public void loadStargate(Address.Immutable address, int attackerMinCount, int attackerMaxCount, int attackerMinInterval, int attackerMaxInverval, Function<RandomSource, EntityType<?>> randomizedEntityType, BiConsumer<Entity, RandomSource> onEntitySpawn)
	{
		this.id9ChevronAddress = address;
		
		this.random = new Random();
		this.attackerMinCount = attackerMinCount;
		this.attackerMaxCount = attackerMaxCount;
		this.attackerMinInterval = attackerMinInterval;
		this.attackerMaxInverval = attackerMaxInverval;
		
		this.timer = nextAttackerInterval();
		this.counter = nextAttackerCount();
		
		this.randomizedEntityType = randomizedEntityType;
		this.onEntitySpawn = onEntitySpawn;
	}
	
	protected int nextAttackerInterval()
	{
		return random.nextInt(attackerMinInterval, attackerMaxInverval + 1);
	}
	
	protected int nextAttackerCount()
	{
		return random.nextInt(attackerMinCount, attackerMaxCount + 1);
	}
	
	@Override
	public StargateType<?> getStargateType()
	{
		return type;
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
	public @Nullable Vec3 getPosition()
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getForward()
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getUp()
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getRight()
	{
		return null;
	}
	
	@Override
	public double getInnerRadius()
	{
		return 0;
	}
	
	@Override
	public boolean hasDHD()
	{
		return true;
	}
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return StargateInfo.Gen.GEN_2;
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
		
		this.timer = nextAttackerInterval();
		this.counter = nextAttackerCount();
		return feedback;
	}
	
	@Override
	public boolean isConnected()
	{
		return this.connectionID != null;
	}
	
	@Override
	public boolean isObstructed()
	{
		return false;
	}
	
	@Override
	public boolean checkValidity()
	{
		return true;
	}
	
	@Override
	public boolean isLoaded()
	{
		return true;
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
	public boolean canPowerFromOtherSide()
	{
		return false;
	}
	
	@Override
	public long extractEnergy(long energy, boolean simulate)
	{
		return Math.min(energy, getEnergyStored());
	}
	
	@Override
	public int dialedEngageTime(boolean doKawoosh)
	{
		return StargateInfo.ChevronLockSpeed.SLOW.getKawooshStartTicks();
	}
	
	@Override
	public int wormholeEstablishTime(boolean doKawoosh)
	{
		return KAWOOSH_TICKS;
	}
	
	public void encodeAddress(Address address)
	{
		this.address = new Address.Mutable(address);
	}
	
	public StargateInfo.FeedbackMessage dial()
	{
		return Dialing.dialStargate(server, this, getAddress(), true, true);
	}
	
	@Override
	public StargateInfo.FeedbackMessage tryConnect(Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateJourney.LOGGER.error("Stargate does not permit connections");
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	@Override
	public void connectStargate(StargateConnection connection, StargateConnection.State connectionState)
	{
		this.connectionID = connection.getID();
	}
	
	@Nullable
	protected Entity createEntity(ServerLevel level)
	{
		if(randomizedEntityType == null)
			return null;
		
		EntityType<?> entityType = randomizedEntityType.apply(level.getRandom());
		
		if(entityType == null)
			return null;
		
		return entityType.create(level);
	}
	
	protected void spawnEntity(ServerLevel level, Entity entity)
	{
		if(entity instanceof Mob mob)
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData) null, (CompoundTag) null);
		
		onEntitySpawn.accept(entity, level.getRandom());
	}
	
	@Override
	public void doWormhole(StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
		
		if(timer > 0)
			timer--;
		else if(0 < counter && connectedStargate != null)
		{
			ServerLevel level = connectedStargate.getLevel();
			
			if(level != null)
			{
				timer = nextAttackerInterval();
				counter--;
				
				Entity entity = createEntity(level);
				if(entity != null)
				{
					Entity traveler = connectedStargate.receiveTraveler(connection, this, entity, new Vec3(0, -2.0/INNER_RADIUS, random.nextDouble(-1.5/INNER_RADIUS, 1.5/INNER_RADIUS)), new Vec3(-0.4, 0, 0), new Vec3(-1, 0, 0));
					if(traveler != null)
					{
						connection.setTimeSinceLastTraveler(0);
						connection.setUsed(true);
						
						spawnEntity(level, traveler);
					}
					else
						entity.discard();
				}
			}
		}
	}
	
	@Override
	public @Nullable Entity receiveTraveler(StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		if(traveler instanceof Player player)
			player.displayClientMessage(Component.translatable("no"), true); // TODO add an actual message
		
		return null;
	}
	
	@Override
	public boolean shouldAutoclose(StargateConnection connection)
	{
		return connection.getOpenTime() > 200;
	}
	
	@Override
	public boolean requiresEnergyBypass(int openTime)
	{
		return openTime > SGJourneyStargate.MAX_OPEN_TIME;
	}
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		//TODO
	}
	
	@Override
	public void deserializeNBT(Address.Immutable id9ChevronAddress, CompoundTag tag)
	{
		//TODO
	}
	
	@Override
	public AddressFilterInfo addressFilterInfo()
	{
		return new AddressFilterInfo();
	}
}
