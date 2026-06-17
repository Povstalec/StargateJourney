package net.povstalec.sgjourney.common.sgjourney.factions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.entities.FactionMember;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SpawnerStargate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoauldFaction extends AbstractFaction
{
	public static final int UPDATE_INTERVAL = 400;//24000;
	
	public static final int ATTACKER_MIN_COUNT = 3;
	public static final int ATTACKER_MAX_COUNT = 7;
	public static final int ATTACKER_MIN_INTERVAL = 20;
	public static final int ATTACKER_MAX_INTERVAL = 20 * 3;
	
	private static final Address.Immutable TERRA = new Address.Immutable(27, 25, 4, 35, 10, 28);
	private static final Address.Immutable ABYDOS = new Address.Immutable(26, 6, 14, 31, 11, 29);
	private static final Address.Immutable RIMA = new Address.Immutable(33, 20, 10, 22, 3, 17);
	private static final Address.Immutable UNITAS = new Address.Immutable(2, 27, 8, 34, 24, 15);
	
	protected List<Address.Immutable> addresses = new ArrayList<>(); // Addresses this faction knows about and can attack
	protected final SpawnerStargate spawnerStargate;
	
	protected final Random random;
	@Nullable
	protected Address.Immutable incursionTarget;
	protected int incursionTime = 0;
	
	public GoauldFaction(MinecraftServer server)
	{
		this.addresses.add(TERRA);
		//this.addresses.add(ABYDOS);
		//this.addresses.add(RIMA);
		//this.addresses.add(UNITAS);
		
		this.spawnerStargate = StargateInit.MILKY_WAY_SPAWNER.get().constructStargate(server);
		this.spawnerStargate.loadStargate(Address.Immutable.randomAddress(8, 36, 0),
				ATTACKER_MIN_COUNT, ATTACKER_MAX_COUNT, ATTACKER_MIN_INTERVAL, ATTACKER_MAX_INTERVAL,
				randomSource -> EntityInit.JAFFA.get(), (entity, randomSource) ->
		{
			if(entity instanceof FactionMember factionMember)
				factionMember.setFaction(this);
		});
		
		this.random = new Random(0);
	}
	
	public void prepareNextIncursions(int intervalTicks)
	{
		if(addresses.isEmpty())
			return;
		
		incursionTime = random.nextInt(intervalTicks, UPDATE_INTERVAL);
		incursionTarget = addresses.get(this.random.nextInt(addresses.size()));
		System.out.println("Next incursion at: " + incursionTime + " " +  incursionTarget);
	}
	
	public boolean launchIncursion(MinecraftServer server)
	{
		if(incursionTarget == null)
			return false;
		
		if(spawnerStargate.isConnected())
			return false;
		
		spawnerStargate.encodeAddress(incursionTarget);
		StargateInfo.Feedback feedback = spawnerStargate.dial();
		
		System.out.println("Dial attempt: " + feedback.getMessage());
		
		return !feedback.isError();
	}
	
	@Override
	public void tickFaction(MinecraftServer server, int ticks)
	{
		int intervalTicks = ticks % UPDATE_INTERVAL;
		
		if(intervalTicks == 0)
			prepareNextIncursions(intervalTicks);
		else if(intervalTicks == incursionTime)
		{
			if(!launchIncursion(server))
				prepareNextIncursions(intervalTicks);
			else
				incursionTarget = null;
		}
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = new CompoundTag();
		
		//TODO
		
		return tag;
	}
	
	public void deserializeNBT(CompoundTag tag)
	{
		//TODO
	}
}
