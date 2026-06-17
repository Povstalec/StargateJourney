package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Wormhole;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BlockEntityStargate<StargateEntity extends AbstractStargateEntity<?>> extends Stargate
{
	String COORDINATES = "Coordinates";
	
	BlockPos getBlockPos();
	
	@Nullable StargateEntity getStargateEntity(MinecraftServer server);
	
	default void stargateRun(MinecraftServer server, Consumer<StargateEntity> consumer)
	{
		StargateEntity stargate = getStargateEntity(server);
		
		if(stargate != null)
			consumer.accept(stargate);
	}
	
	default <T> T stargateReturn(MinecraftServer server, Function<StargateEntity, T> function, @Nullable T defaultValue)
	{
		StargateEntity stargate = getStargateEntity(server);
		
		if(stargate != null)
			return function.apply(stargate);
		
		return defaultValue;
	}
	
	static StargateInfo.Feedback noStargateEntity()
	{
		StargateJourney.LOGGER.error("IBlockStargate.noStargateEntity: Stargate Entity could not be found");
		return StargateInfo.Feedback.UNKNOWN_ERROR;
	}
	
	void loadFromBlockEntity(AbstractStargateEntity<?> stargate);
	
	Wormhole getWormhole();
	
	
	
	@Override
	default boolean isLoaded()
	{
		ServerLevel level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
	
	@Override
	default @Nullable Vec3 getPosition()
	{
		return stargateReturn(getServer(), stargate -> stargate.getCenter(), null);
	}
	
	@Override
	default Address.Mutable getAddress()
	{
		return stargateReturn(getServer(), stargate -> stargate.getAddress(), new Address.Mutable());
	}
	
	@Override
	default StargateInfo.Feedback resetStargate(StargateInfo.Feedback feedback)
	{
		StargateEntity stargateEntity = getStargateEntity(getServer());
		
		//this.stargate = null; //TODO bring back once Stargates get cached
		
		if(stargateEntity != null)
			return stargateEntity.resetStargate(feedback);
		else
			StargateJourney.LOGGER.error("Failed to reset Stargate as it does not exist");
		
		return feedback;
	}
	
	@Override
	default boolean isConnected()
	{
		return stargateReturn(getServer(), stargate -> stargate.isConnected(), false);
	}
	
	@Override
	default boolean isObstructed()
	{
		return stargateReturn(getServer(), stargate -> stargate.isObstructed(), false);
	}
	
	@Override
	default boolean isPrimary()
	{
		return stargateReturn(getServer(), stargate -> stargate.isPrimary(), false);
	}
	
	@Override
	default boolean checkValidity()
	{
		StargateEntity stargate = getStargateEntity(getServer());
		
		if(stargate == null)
		{
			StargateJourney.LOGGER.error("Stargate not found");
			return false;
			
		}
		else if(!get9ChevronAddress().equals(stargate.get9ChevronAddress()))
		{
			StargateJourney.LOGGER.error("Block Entity Address wasn't equal to Stargate Address");
			if(stargate.get9ChevronAddress() == null) // In case Address becomes null for some reason during updating, it should get updated from this Stargate's Address
				stargate.set9ChevronAddress(new Address.Immutable(get9ChevronAddress()));
			else
				return false;
		}
		
		stargate.checkStargate();
		return true;
	}
	
	@Override
	default void setChevronConfiguration(int[] chevronConfiguration)
	{
		stargateRun(getServer(), stargate -> stargate.setEngagedChevrons(chevronConfiguration));
	}
	
	// Updating
	
	@Override
	default void updateClient()
	{
		stargateRun(getServer(), stargate -> stargate.updateClient());
	}
	
	@Override
	default void updateInterfaceBlocks(@Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects)
	{
		stargateRun(getServer(), starghateEntity ->
		{
			if(type == null)
				starghateEntity.updateInterfaceBlocks(eventName, objects);
			else
				switch(type)
				{
					case BASIC:
						starghateEntity.updateBasicInterfaceBlocks(eventName, objects);
						break;
					case CRYSTAL:
						starghateEntity.updateCrystalInterfaceBlocks(eventName, objects);
						break;
					case ADVANCED_CRYSTAL:
						starghateEntity.updateAdvancedCrystalInterfaceBlocks(eventName, objects);
						break;
				}
		});
	}
	
	// Communication
	
	@Override
	default void receiveStargateMessage(String message)
	{
		stargateRun(getServer(), stargate -> stargate.receiveStargateMessage(message));
	}
	
	@Override
	default void forwardTransmission(int transmissionJumps, int frequency, String transmission)
	{
		stargateRun(getServer(), stargate -> stargate.forwardTransmission(transmissionJumps, frequency, transmission));
	}
	
	@Override
	default float checkStargateShieldingState()
	{
		return stargateReturn(getServer(), stargate -> stargate instanceof IrisStargateEntity<?> irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F, 0F);
	}
	
	// Energy
	
	@Override
	default long getEnergyStored()
	{
		return stargateReturn(getServer(), stargate -> stargate.energyStorage.getTrueEnergyStored(), 0L);
	}
	
	@Override
	default long getEnergyCapacity()
	{
		return stargateReturn(getServer(), stargate -> stargate.energyStorage.getTrueMaxEnergyStored(), 0L);
	}
	
	@Override
	default long extractEnergy(long energy, boolean simulate)
	{
		return stargateReturn(getServer(), stargate -> stargate.energyStorage.depleteEnergy(energy, simulate), 0L);
	}
	
	// Stargate Connection
	
	@Override
	default void connectionUpdate(StargateConnection connection)
	{
		stargateRun(getServer(), stargate ->
		{
			stargate.setKawooshTickCount(connection.getKawooshTime());
			stargate.setOpenTime(connection.getOpenTime());
			stargate.setTimeSinceLastTraveler(connection.getTimeSinceLastTraveler());
		});
	}
	
	@Override
	default boolean callForward()
	{
		return stargateReturn(getServer(), stargate -> stargate.dhdCache.returnOrDefault(AbstractDHDEntity::callForwardingEnabled, false), false);
	}
	
	@Override
	default void connectStargate(StargateConnection connection, StargateConnection.State connectionState)
	{
		stargateRun(getServer(), stargate -> stargate.connectStargate(connection.getID(), connectionState));
	}
	
	@Override
	default void doWhileConnecting(StargateConnection connection, boolean incoming, int kawooshStartTicks)
	{
		stargateRun(getServer(), stargate -> stargate.doWhileConnecting(incoming, connection.doKawoosh(), kawooshStartTicks, connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileDialed(StargateConnection connection, Address connectedAddress, int kawooshStartTicks)
	{
		stargateRun(getServer(), stargate -> stargate.doWhileDialed(connectedAddress, kawooshStartTicks, connection.doKawoosh(), connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileConnected(StargateConnection connection, boolean incoming)
	{
		stargateRun(getServer(), stargate -> stargate.doWhileConnected(incoming, connection.getConnectionTime()));
	}
	
	default void wormholeEntities(StargateConnection connection, Stargate destinationStargate, boolean incoming, StargateInfo.WormholeTravel wormholeTravel, List<Entity> wormholeCandidates)
	{
		stargateRun(getServer(), stargate ->
		{
			if(getWormhole().wormholeEntities(getServer(), connection, this, destinationStargate, wormholeTravel, wormholeCandidates))
				connection.setUsed(true);
		});
	}
	
	@Override
	default void doWormhole(StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		stargateRun(getServer(), stargate ->
		{
			List<Entity> wormholeCandidates = stargate.findWormholeCandidates();
			
			// If this Stargate has its iris closed, then there's no point in trying to transport Entities
			if(stargate instanceof IrisStargateEntity<?> irisStargate && irisStargate.irisInfo().isIrisClosed())
				return;
			
			Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
			
			if(!wormholeCandidates.isEmpty() && connection.used())
				connection.setTimeSinceLastTraveler(0);
			
			wormholeEntities(connection, connectedStargate, incoming, wormholeTravel, wormholeCandidates);
		});
	}
	
	@Override
	default @Nullable Entity receiveTraveler(StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		return stargateReturn(getServer(), stargate ->
				{
					// Call Forwarding
					if(stargate.dhdCache.returnOrDefault(AbstractDHDEntity::callForwardingEnabled, false) && callForwardDeny(traveler))
					{
						if(connection.getDialedStargates().size() > 1) // There are at least 2 gates currently connected -> traveler gets sent to a random other gate
							return connection.getDialedStargates().get(new Random().nextInt(1, connection.getDialedStargates().size())).receiveTraveler(connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
						else // There is only one gate connected -> traveler gets sent back to the gate they initially entered
							return initialStargate.receiveTraveler(connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
					}
					
					// TODO Tie this to Advanced Protocols
					Vec3 tempMomentum = stargate.pushTraveler() && relativeMomentum.x() > -SGJourneyStargate.MIN_TRAVELER_SPEED ? new Vec3(-SGJourneyStargate.MIN_TRAVELER_SPEED, relativeMomentum.y(), relativeMomentum.z()) : relativeMomentum;
					
					Vec3 destinationPosition = fromStargateCoords(relativePosition, true, true).add(stargate.getCenter());
					Vec3 destinationMomentum = fromStargateCoords(tempMomentum, false, true);
					Vec3 destinationLookAngle = fromStargateCoords(relativeLookAngle, false, true);
					
					if(stargate instanceof IrisStargateEntity<?> irisStargate && !getWormhole().checkShielding(irisStargate, destinationPosition, destinationMomentum, traveler))
					{
						getWormhole().handleShielding(irisStargate, traveler);
						return traveler;
					}
					
					return getWormhole().receiveTraveler(connection, (ServerLevel) stargate.getLevel(), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
				},
				null);
	}
	
	@Override
	default boolean shouldAutoclose(StargateConnection connection)
	{
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		return stargateReturn(getServer(), stargate ->
		{
			int autoclose = stargate.dhdCache.returnOrDefault(AbstractDHDEntity::autoclose, 0);
			if(autoclose <= 0)
				return false;
			
			return connection.getTimeSinceLastTraveler() > autoclose * 20;
		}, false); //TODO Maybe move the "* 20" into DHD info?
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	default AddressFilterInfo addressFilterInfo()
	{
		return stargateReturn(getServer(), stargate -> stargate.addressFilterInfo(), new AddressFilterInfo());
	}
}
