package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
	
	protected Address.Immutable id9ChevronAddress;
	
	protected final int attackerMinCount;
	protected final int attackerMaxCount;
	protected final int attackerMinInterval;
	protected final int attackerMaxInverval;
	
	protected Address address = new Address();
	@Nullable
	protected UUID connectionID = null;
	
	protected Random random;
	protected int counter;
	protected int timer;
	
	public SpawnerStargate(Address address, int attackerMinCount, int attackerMaxCount, int attackerMinInterval, int attackerMaxInverval)
	{
		this.id9ChevronAddress = address.immutable();
		
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
	public @Nullable Vec3 getPosition()
	{
		return null;
	}
	
	@Override
	public @Nullable SolarSystem.Serializable getSolarSystem(MinecraftServer server)
	{
		return Universe.get(server).getSolarSystemFromDimension(Conversion.stringToDimension("sgjourney:chulak")); // TODO
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
		return 0;
	}
	
	@Override
	public int getNetwork()
	{
		return 2;
	}
	
	@Override
	public Address getAddress(MinecraftServer server)
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
	public boolean canExtractEnergy(MinecraftServer server, long energy)
	{
		return true;
	}
	
	@Override
	public void depleteEnergy(MinecraftServer server, long energy, boolean simulate)
	{
	
	}
	
	public void encodeAddress(Address address)
	{
		this.address = address;
	}
	
	public StargateInfo.Feedback dial(MinecraftServer server)
	{
		return Dialing.dialStargate(server, this, getAddress(server).immutable(), true, true);
	}
	
	@Override
	public StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		return StargateInfo.Feedback.UNKNOWN_ERROR;
	}
	
	@Override
	public void connectStargate(MinecraftServer server, UUID connectionID, StargateConnection.State connectionState)
	{
		this.connectionID = connectionID;
	}
	
	protected Entity spawnEntity(ServerLevel level, EntityType<?> entityType)
	{
		return entityType.spawn(level, (CompoundTag) null, null, new BlockPos(0, 1024, 0), MobSpawnType.PATROL, true, false);
	}
	
	@Override
	public void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
		
		if(timer > 0)
			timer--;
		else if(0 < counter)
		{
			ResourceKey<Level> dimension = Conversion.stringToDimension("sgjourney:chulak");
			if(dimension != null)
			{
				timer = nextAttackerInterval();
				counter--;
				
				ServerLevel level = server.getLevel(dimension);
				if(level != null)
				{
					Entity entity = spawnEntity(level, EntityInit.JAFFA.get());
					if(entity != null && connectedStargate.receiveTraveler(server, this, entity, new Vec3(0, -2.0/INNER_RADIUS, random.nextDouble(-1.5/INNER_RADIUS, 1.5/INNER_RADIUS)), new Vec3(-0.4, 0, 0), new Vec3(-1, 0, 0)))
					{
						connection.setTimeSinceLastTraveler(0);
						connection.setUsed(true);
					}
				}
			}
		}
	}
	
	@Override
	public boolean receiveTraveler(MinecraftServer server, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		if(traveler instanceof Player player)
			player.displayClientMessage(Component.translatable("no"), true); // TODO add an actual message
		
		return false;
	}
	
	@Override
	public int autoclose(MinecraftServer server)
	{
		return 10;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		return null;
	}
	
	@Override
	public void deserializeNBT(MinecraftServer server, Address.Immutable address, CompoundTag tag)
	{
	
	}
}
