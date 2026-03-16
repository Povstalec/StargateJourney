package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
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
	default boolean isLoaded(MinecraftServer server)
	{
		ServerLevel level  = server.getLevel(getDimension());
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
	
	@Override
	default @Nullable Vec3 getPosition(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.getCenter(), null);
	}
	
	@Override
	default boolean isRestricted(MinecraftServer server, int network)
	{
		return stargateReturn(server, stargate -> stargate.isRestricted(network), false);
	}
	
	@Override
	default Address.Mutable getAddress(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.getAddress(), new Address.Mutable());
	}
	
	@Override
	default StargateInfo.Feedback resetStargate(MinecraftServer server, StargateInfo.Feedback feedback, boolean updateInterfaces)
	{
		StargateEntity stargateEntity = getStargateEntity(server);
		
		//this.stargate = null; //TODO bring back once Stargates get cached
		
		if(stargateEntity != null)
			return stargateEntity.resetStargate(feedback, updateInterfaces);
		else
			StargateJourney.LOGGER.error("Failed to reset Stargate as it does not exist");
		
		return feedback;
	}
	
	@Override
	default boolean isConnected(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isConnected(), false);
	}
	
	@Override
	default boolean isObstructed(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isObstructed(), false);
	}
	
	@Override
	default boolean isPrimary(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.isPrimary(), false);
	}
	
	@Override
	default boolean checkValidity(MinecraftServer server)
	{
		StargateEntity stargate = getStargateEntity(server);
		
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
	default void setChevronConfiguration(MinecraftServer server, int[] chevronConfiguration)
	{
		stargateRun(server, stargate -> stargate.setEngagedChevrons(chevronConfiguration));
	}
	
	// Updating
	
	@Override
	default void updateClient(MinecraftServer server)
	{
		stargateRun(server, stargate -> stargate.updateClient());
	}
	
	@Override
	default void updateInterfaceBlocks(MinecraftServer server, @Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects)
	{
		stargateRun(server, stargate ->
		{
			if(type == null)
				stargate.updateInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.BASIC)
				stargate.updateBasicInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.CRYSTAL)
				stargate.updateCrystalInterfaceBlocks(eventName, objects);
			else if(type == AbstractInterfaceEntity.InterfaceType.ADVANCED_CRYSTAL)
				stargate.updateAdvancedCrystalInterfaceBlocks(eventName, objects);
		});
	}
	
	// Communication
	
	@Override
	default void receiveStargateMessage(MinecraftServer server, String message)
	{
		stargateRun(server, stargate -> stargate.receiveStargateMessage(message));
	}
	
	@Override
	default void forwardTransmission(MinecraftServer server, int transmissionJumps, int frequency, String transmission)
	{
		stargateRun(server, stargate -> stargate.forwardTransmission(transmissionJumps, frequency, transmission));
	}
	
	@Override
	default float checkStargateShieldingState(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate instanceof IrisStargateEntity<?> irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F, 0F);
	}
	
	// Energy
	
	@Override
	default long getEnergyStored(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.energyStorage.getTrueEnergyStored(), 0L);
	}
	
	@Override
	default long getEnergyCapacity(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.energyStorage.getTrueMaxEnergyStored(), 0L);
	}
	
	@Override
	default long extractEnergy(MinecraftServer server, long energy, boolean simulate)
	{
		return stargateReturn(server, stargate -> stargate.energyStorage.depleteEnergy(energy, simulate), 0L);
	}
	
	// Stargate Connection
	
	@Override
	default void connectionUpdate(MinecraftServer server, StargateConnection connection)
	{
		stargateRun(server, stargate ->
		{
			stargate.setKawooshTickCount(connection.getKawooshTime(server));
			stargate.setOpenTime(connection.getOpenTime());
			stargate.setTimeSinceLastTraveler(connection.getTimeSinceLastTraveler());
		});
	}
	
	@Override
	default boolean callForward(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.dhdInfo().shouldCallForward(), false);
	}
	
	@Override
	default void connectStargate(MinecraftServer server, StargateConnection connection, StargateConnection.State connectionState)
	{
		stargateRun(server, stargate -> stargate.connectStargate(connection.getID(), connectionState));
	}
	
	@Override
	default void doWhileConnecting(MinecraftServer server, StargateConnection connection, boolean incoming, int kawooshStartTicks)
	{
		stargateRun(server, stargate -> stargate.doWhileConnecting(incoming, connection.doKawoosh(), kawooshStartTicks, connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileDialed(MinecraftServer server, StargateConnection connection, Address connectedAddress, int kawooshStartTicks)
	{
		stargateRun(server, stargate -> stargate.doWhileDialed(connectedAddress, kawooshStartTicks, connection.doKawoosh(), connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileConnected(MinecraftServer server, StargateConnection connection, boolean incoming)
	{
		stargateRun(server, stargate -> stargate.doWhileConnected(incoming, connection.getConnectionTime()));
	}
	
	default void wormholeEntities(MinecraftServer server, StargateConnection connection, Stargate destinationStargate, boolean incoming, StargateInfo.WormholeTravel wormholeTravel, List<Entity> wormholeCandidates)
	{
		stargateRun(server, stargate ->
		{
			if(getWormhole().wormholeEntities(server, connection, this, destinationStargate, wormholeTravel, wormholeCandidates))
				connection.setUsed(true);
		});
	}
	
	@Override
	default void doWormhole(MinecraftServer server, StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		stargateRun(server, stargate ->
		{
			List<Entity> wormholeCandidates = stargate.findWormholeCandidates();
			
			// If this Stargate has its iris closed, then there's no point in trying to transport Entities
			if(stargate instanceof IrisStargateEntity irisStargate && irisStargate.irisInfo().isIrisClosed())
				return;
			
			Stargate connectedStargate = incoming ? connection.getDialingStargate() : connection.getDialedStargate();
			
			if(!wormholeCandidates.isEmpty() && connection.used())
				connection.setTimeSinceLastTraveler(0);
			
			wormholeEntities(server, connection, connectedStargate, incoming, wormholeTravel, wormholeCandidates);
		});
	}
	
	@Override
	default @Nullable Entity receiveTraveler(MinecraftServer server, StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		return stargateReturn(server, stargate ->
				{
					// Call Forwarding
					if(stargate.dhdInfo().shouldCallForward() && callForwardDeny(traveler))
					{
						if(connection.getDialedStargates().size() > 1) // There are at least 2 gates currently connected -> traveler gets sent to a random other gate
							return connection.getDialedStargates().get(new Random().nextInt(1, connection.getDialedStargates().size())).receiveTraveler(server, connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
						else // There is only one gate connected -> traveler gets sent back to the gate they initially entered
							return initialStargate.receiveTraveler(server, connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
					}
					
					// TODO Tie this to Advanced Protocols
					Vec3 tempMomentum = stargate.pushTraveler() && relativeMomentum.x() > -SGJourneyStargate.MIN_TRAVELER_SPEED ? new Vec3(-SGJourneyStargate.MIN_TRAVELER_SPEED, relativeMomentum.y(), relativeMomentum.z()) : relativeMomentum;
					
					Vec3 destinationPosition = fromStargateCoords(server, relativePosition, true, true).add(stargate.getCenter());
					Vec3 destinationMomentum = fromStargateCoords(server, tempMomentum, false, true);
					Vec3 destinationLookAngle = fromStargateCoords(server, relativeLookAngle, false, true);
					
					if(stargate instanceof IrisStargateEntity<?> irisStargate && !getWormhole().checkShielding(irisStargate, destinationPosition, destinationMomentum, traveler))
					{
						getWormhole().handleShielding(irisStargate, traveler);
						return traveler;
					}
					
					return getWormhole().receiveTraveler((ServerLevel) stargate.getLevel(), this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
				},
				null);
	}
	
	@Override
	default boolean shouldAutoclose(MinecraftServer server, StargateConnection connection)
	{
		// Ends the connection automatically once at least one traveler has traveled through the Stargate and a certain amount of time has passed
		return stargateReturn(server, stargate ->
		{
			if(stargate.dhdInfo().autoclose() <= 0)
				return false;
			
			return connection.getTimeSinceLastTraveler() > stargate.dhdInfo().autoclose() * 20;
		}, false); //TODO Maybe move the "* 20" into DHD info?
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	default AddressFilterInfo addressFilterInfo(MinecraftServer server)
	{
		return stargateReturn(server, stargate -> stargate.addressFilterInfo(), new AddressFilterInfo());
	}
}
