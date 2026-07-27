package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Stargate that doesn't physically exist anywhere in the world,
 * but is used as the outgoing side of a connection that spawns Entities
 */
public interface SpawnerStargate extends Stargate
{
	@Override
	default @Nullable Vec3 getPosition()
	{
		return null;
	}
	
	@Override
	default @Nullable Vec3 getForward()
	{
		return null;
	}
	
	@Override
	default @Nullable Vec3 getUp()
	{
		return null;
	}
	
	@Override
	default @Nullable Vec3 getRight()
	{
		return null;
	}
	
	@Override
	default double getInnerRadius()
	{
		return Wormhole.INNER_RADIUS;
	}
	
	@Override
	default boolean isObstructed()
	{
		return false; // No point in having the Spawner Stargate be obstructed
	}
	
	@Override
	default boolean checkValidity()
	{
		return true; // Doesn't exist anywhere in the world, so let's treat it as always valid
	}
	
	@Override
	default boolean isLoaded()
	{
		return true; // Doesn't exist anywhere in the world, so let's treat it as always loaded
	}
	
	@Override
	default float checkStargateShieldingState()
	{
		return 0; // No point in having the Spawner Stargate shielded
	}
	
	@Override
	default boolean canPowerFromOtherSide()
	{
		return false;
	}
	
	@Override
	default StargateInfo.FeedbackMessage tryConnect(Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateJourney.LOGGER.error("Stargate does not permit connections");
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	@Override
	default void doWormhole(StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		getSpawnerTimer().tick();
		
		if(getSpawnerTimer().shouldSpawn())
		{
			Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
			if(connectedStargate != null)
			{
				ServerLevel level = connectedStargate.getLevel();
				
				if(level != null)
				{
					Entity entity = createEntity(level);
					if(entity != null)
					{
						Entity traveler = connectedStargate.receiveTraveler(connection, this, entity,
								new Vec3(0, -2.0/getInnerRadius(), getSpawnerTimer().getRandom().nextDouble(-1.5/getInnerRadius(), 1.5/getInnerRadius())),
								new Vec3(-0.4, 0, 0), new Vec3(-1, 0, 0));
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
	}
	
	@Override
	default boolean shouldAutoclose(StargateConnection connection)
	{
		return connection.getOpenTime() > 200;
	}
	
	@Override
	default boolean requiresEnergyBypass(int openTime)
	{
		return openTime > SGJourneyStargate.MAX_OPEN_TIME;
	}
	
	@Override
	default AddressFilterInfo addressFilterInfo()
	{
		return new AddressFilterInfo();
	}
	
	@Override
	default void tick() {}
	
	//TODO Make an actual dialing method instead of the one below
	
	default StargateInfo.FeedbackMessage dial()
	{
		return Dialing.dialStargate(getServer(), this, getAddress(), true, true/*Only search for loaded Stargates*/);
	}
	
	@Override
	default @Nullable Entity receiveTraveler(StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		if(traveler instanceof Player player)
			player.displayClientMessage(Component.translatable("no"), true); // TODO add an actual message
		
		return null;
	}
	
	//============================================================================================
	//**************************************Entity Spawning***************************************
	//============================================================================================
	
	/**
	 * @return Spawner timer that randomizes the amount of Entities that spawn and the intervals between each individual spawn
	 */
	SpawnerTimer getSpawnerTimer();
	
	@Nullable
	Entity createEntity(ServerLevel level);
	
	void spawnEntity(ServerLevel level, Entity entity);
}
