package net.povstalec.sgjourney.common.sgjourney;

import java.util.Random;

/**
 * Class to help with randomizing the time and amount of entities that get spawned (by Spawner Stargates or Transporters)
 */
public class SpawnerTimer
{
	protected int minSpawns;
	protected int maxSpawns;
	protected int minInterval;
	protected int maxInterval;
	
	protected Random random = new Random();
	
	protected int spawnCounter = 0; // Entities left to spawn
	protected int timer = 0; // Time left until next Entity should spawn
	
	protected boolean shouldSpawn = false;
	
	protected boolean isTicking = true;
	
	public SpawnerTimer setSpawnCount(int minSpawnCount, int maxSpawnCount)
	{
		this.minSpawns = minSpawnCount;
		this.maxSpawns = maxSpawnCount;
		this.spawnCounter = randomSpawnCounter();
		
		return this;
	}
	
	public SpawnerTimer setSpawnCount(int spawnCount)
	{
		return setSpawnCount(spawnCount, spawnCount);
	}
	
	public SpawnerTimer setInterval(int minInterval, int maxInterval)
	{
		this.minInterval = minInterval;
		this.maxInterval = maxInterval;
		this.timer = randomInterval();
		
		return this;
	}
	
	public SpawnerTimer setInterval(int interval)
	{
		return setInterval(interval, interval);
	}
	
	public int randomSpawnCounter()
	{
		if(minSpawns == maxSpawns)
			return minSpawns;
		
		return random.nextInt(minSpawns, maxSpawns + 1);
	}
	
	public int randomInterval()
	{
		if(minInterval == maxInterval)
			return minInterval;
		
		return random.nextInt(minInterval, maxInterval + 1);
	}
	
	public void reset()
	{
		timer = randomInterval();
		spawnCounter = randomSpawnCounter();
		isTicking = true;
	}
	
	public boolean shouldSpawn()
	{
		return shouldSpawn;
	}
	
	public Random getRandom()
	{
		return random;
	}
	
	public void tick()
	{
		shouldSpawn = false;
		
		if(isTicking)
		{
			if(timer > 0)
				timer--;
			else if(spawnCounter > 0)
			{
				timer = randomInterval();
				spawnCounter--;
				
				shouldSpawn = true;
			}
			
			if(spawnCounter == 0)
				isTicking = false;
		}
	}
}
