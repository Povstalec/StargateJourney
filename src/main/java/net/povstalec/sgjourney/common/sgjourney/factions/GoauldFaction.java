package net.povstalec.sgjourney.common.sgjourney.factions;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SpawnerStargate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoauldFaction
{
	public static final int UPDATE_INTERVAL = 24000;
	
	public static final int ATTACKER_MIN_COUNT = 3;
	public static final int ATTACKER_MAX_COUNT = 7;
	public static final int ATTACKER_MIN_INTERVAL = 20;
	public static final int ATTACKER_MAX_INTERVAL = 20 * 3;
	
	private static final Address.Immutable TERRA = new Address.Immutable(27, 25, 4, 35, 10, 28);
	private static final Address.Immutable ABYDOS = new Address.Immutable(26, 6, 14, 31, 11, 29);
	private static final Address.Immutable CHULAK = new Address.Immutable(8, 1, 22, 14, 36, 19);
	
	protected List<Address.Immutable> addresses = new ArrayList<>(); // Addresses this faction knows about and can attack
	protected final SpawnerStargate stargate;
	
	protected final Random random;
	@Nullable
	protected Address.Immutable incursionTarget;
	protected int incursionTime = 0;
	
	public GoauldFaction()
	{
		this.addresses.add(TERRA);
		//this.addresses.add(ABYDOS);
		//this.addresses.add(CHULAK);
		
		this.stargate = new SpawnerStargate(new Address().randomAddress(8, 36, 0), ATTACKER_MIN_COUNT, ATTACKER_MAX_COUNT, ATTACKER_MIN_INTERVAL, ATTACKER_MAX_INTERVAL);
		
		this.random = new Random(0);
	}
	
	public void prepareNextIncursions(int intervalTicks)
	{
		if(addresses.isEmpty())
			return;
		
		incursionTime = random.nextInt(intervalTicks, UPDATE_INTERVAL);
		incursionTarget = addresses.get(this.random.nextInt(addresses.size()));
		//System.out.println("Next incursion at: " + incursionTime + " " +  incursionTarget);
	}
	
	public boolean launchIncursion(MinecraftServer server)
	{
		if(stargate.isConnected(server))
			return false;
		
		if(incursionTarget == null)
			return false;
		
		stargate.encodeAddress(incursionTarget.mutable());
		StargateInfo.Feedback feedback = stargate.dial(server);
		
		//System.out.println("Dial attempt: " + feedback.getMessage());
		
		return !feedback.isError();
	}
	
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
}
