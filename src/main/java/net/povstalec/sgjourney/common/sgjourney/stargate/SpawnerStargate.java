package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

public class SpawnerStargate implements Stargate
{
	public static final double INNER_RADIUS = Wormhole.INNER_RADIUS;
	
	public static final int KAWOOSH_TICKS = 40;
	
	protected Address.Immutable id9ChevronAddress;
	
	protected final int attackerMinCount;
	protected final int attackerMaxCount;
	protected final int attackerMinInterval;
	protected final int attackerMaxInverval;
	
	protected Address.Mutable address = new Address.Mutable();
	@Nullable
	protected UUID connectionID = null;
	
	protected Random random;
	protected int counter;
	protected int timer;
	
	public SpawnerStargate(Address.Immutable address, int attackerMinCount, int attackerMaxCount, int attackerMinInterval, int attackerMaxInverval)
	{
		this.id9ChevronAddress = address;
		
		this.random = new Random();
		this.attackerMinCount = attackerMinCount;
		this.attackerMaxCount = attackerMaxCount;
		this.attackerMinInterval = attackerMinInterval;
		this.attackerMaxInverval = attackerMaxInverval;
		
		this.timer = nextAttackerInterval();
		this.counter = nextAttackerCount();
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
	public Address.Immutable get9ChevronAddress()
	{
		return this.id9ChevronAddress;
	}
	
	@Override
	public @Nullable ResourceKey<Level> getDimension()
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getPosition(MinecraftServer server)
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getForward(MinecraftServer server)
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getUp(MinecraftServer server)
	{
		return null;
	}
	
	@Override
	public @Nullable Vec3 getRight(MinecraftServer server)
	{
		return null;
	}
	
	@Override
	public double getInnerRadius()
	{
		return 0;
	}
	
	@Override
	public @Nullable SolarSystem.Serializable getSolarSystem(MinecraftServer server)
	{
		return Universe.get(server).getSolarSystemFromDimension(Conversion.stringToDimension("sgjourney:abydos")); // TODO
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
	public Address.Mutable getAddress(MinecraftServer server)
	{
		return this.address;
	}
	
	@Override
	public StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces)
	{
		this.connectionID = null;
		this.address.reset();
		
		this.timer = nextAttackerInterval();
		this.counter = nextAttackerCount();
		return feedback;
	}
	
	@Override
	public boolean isConnected(MinecraftServer server)
	{
		return this.connectionID != null;
	}
	
	@Override
	public boolean isObstructed(MinecraftServer server)
	{
		return false;
	}
	
	@Override
	public boolean isValid(MinecraftServer server)
	{
		return true;
	}
	
	@Override
	public boolean isLoaded(MinecraftServer server)
	{
		return true;
	}
	
	@Override
	public float checkStargateShieldingState(MinecraftServer server)
	{
		return 0;
	}
	
	@Override
	public long getEnergyStored(MinecraftServer server)
	{
		return CommonStargateConfig.intergalactic_connection_energy_cost.get(); // CommonStargateConfig.interstellar_connection_energy_cost.get(); //TODO Add some actual energy handling
	}
	
	@Override
	public long getEnergyCapacity(MinecraftServer server)
	{
		return CommonStargateConfig.stargate_energy_capacity.get();
	}
	
	@Override
	public boolean canPowerFromOtherSide(MinecraftServer server)
	{
		return false;
	}
	
	@Override
	public long extractEnergy(MinecraftServer server, long energy, boolean simulate)
	{
		return Math.min(energy, getEnergyStored(server));
	}
	
	@Override
	public int dialedEngageTime(MinecraftServer server, boolean doKawoosh)
	{
		return StargateInfo.ChevronLockSpeed.SLOW.getKawooshStartTicks();
	}
	
	@Override
	public int wormholeEstablishTime(MinecraftServer server, boolean doKawoosh)
	{
		return KAWOOSH_TICKS;
	}
	
	public void encodeAddress(Address address)
	{
		this.address = new Address.Mutable(address);
	}
	
	public StargateInfo.Feedback dial(MinecraftServer server)
	{
		return Dialing.dialStargate(server, this, getAddress(server), true, true);
	}
	
	@Override
	public StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateJourney.LOGGER.error("Stargate does not permit connections");
		return StargateInfo.Feedback.UNKNOWN_ERROR;
	}
	
	@Override
	public void connectStargate(MinecraftServer server, StargateConnection connection, StargateConnection.State connectionState)
	{
		this.connectionID = connection.getID();
	}
	
	protected Entity createEntity(ServerLevel level, EntityType<?> entityType)
	{
		return entityType.create(level);
	}
	
	protected void spawnEntity(ServerLevel level, Entity entity)
	{
		if(entity instanceof Mob mob)
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData) null, (CompoundTag) null);
	}
	
	@Override
	public void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
		
		if(timer > 0)
			timer--;
		else if(0 < counter)
		{
			ServerLevel level = connectedStargate.getLevel(server);
			
			if(level != null)
			{
				timer = nextAttackerInterval();
				counter--;
				
				Entity entity = createEntity(level, EntityInit.JAFFA.get());
				if(entity != null)
				{
					Entity traveler = connectedStargate.receiveTraveler(server, connection, this, entity, new Vec3(0, -2.0/INNER_RADIUS, random.nextDouble(-1.5/INNER_RADIUS, 1.5/INNER_RADIUS)), new Vec3(-0.4, 0, 0), new Vec3(-1, 0, 0));
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
	public @Nullable Entity receiveTraveler(MinecraftServer server, StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		if(traveler instanceof Player player)
			player.displayClientMessage(Component.translatable("no"), true); // TODO add an actual message
		
		return null;
	}
	
	@Override
	public boolean shouldAutoclose(MinecraftServer server, StargateConnection connection)
	{
		return connection.getOpenTime() > 200;
	}
	
	@Override
	public boolean requiresEnergyBypass(MinecraftServer server, int openTime)
	{
		return openTime > SGJourneyStargate.MAX_OPEN_TIME;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		return null; //TODO
	}
	
	@Override
	public void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag)
	{
		//TODO
	}
	
	public interface SpawnerConsumer
	{
		Entity onEntitySpawn(Entity entity); //TODO Prepare some way to control the specifics of how entities are spawned
	}
}
