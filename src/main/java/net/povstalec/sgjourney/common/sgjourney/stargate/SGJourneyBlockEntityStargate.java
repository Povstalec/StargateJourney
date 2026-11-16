package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.misc.Trinary;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.dhd.api.DHD;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.api.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.api.DHDControlledStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.api.Stargate;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public interface SGJourneyBlockEntityStargate<StargateEntity extends AbstractStargateEntity<?>> extends BlockEntityStargate<StargateEntity>, DHDControlledStargate
{
	// Basic Info
	
	Wormhole getWormhole();
	
	// Stuff for handling Block Entities
	
	default void loadFromBlockEntity(AbstractStargateEntity<?> stargate)
	{
		stargate.setupAutoCache(controllerCache());
	}
	
	// Updating
	
	@Override
	default void updateDHD(DHD dhd)
	{
		stargateRun(stargate -> stargate.updateDHD(dhd));
	}
	
	@Override
	default void update()
	{
		BlockEntityStargate.super.update();
		DHDControlledStargate.super.update();
	}
	
	
	
	@Override
	default @Nullable Vec3 getPosition()
	{
		return stargateReturn(stargate -> stargate.getCenter(), null);
	}
	
	@Override
	default Address getAddress()
	{
		return stargateReturnOrSupply(stargate -> stargate.getAddress(), Address.Mutable::new);
	}
	
	@Override
	default SymbolMap getSymbolMap()
	{
		return stargateReturn(stargate -> stargate.symbolMap, SymbolMap.Immutable.SYMBOL_MAP);
	}
	
	@Override
	default StargateInfo.FeedbackMessage instaDial(Address address, boolean doKawoosh, Dialing.Action action)
	{
		return stargateReturnOrSupply(stargate -> stargate.instaDial(address, doKawoosh, action), BlockEntityStargate::noStargateBlockEntity);
	}
	
	@Override
	default StargateInfo.FeedbackMessage disconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		return stargateReturnOrSupply(stargate -> stargate.disconnectStargate(feedback), BlockEntityStargate::noStargateBlockEntity);
	}
	
	@Override
	default StargateInfo.FeedbackMessage bypassDisconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		return stargateReturnOrSupply(stargate -> stargate.bypassDisconnectStargate(feedback), BlockEntityStargate::noStargateBlockEntity);
	}
	
	@Override
	default StargateInfo.FeedbackMessage resetStargate(StargateInfo.FeedbackMessage feedback)
	{
		StargateEntity stargateEntity = getStargateBlockEntity();
		
		if(stargateEntity != null)
			return stargateEntity.resetStargate(feedback);
		else
			StargateJourney.LOGGER.error("Failed to reset Stargate as it does not exist");
		
		return feedback;
	}
	
	@Override
	default boolean isConnected()
	{
		return stargateReturn(stargate -> stargate.isConnected(), false);
	}
	
	@Override
	default StargateConnection.Direction getConnectionDirection()
	{
		return stargateReturn(stargate -> stargate.getConnectionState().getDirection(), StargateConnection.Direction.NONE);
	}
	
	@Override
	default boolean isWormholeOpen()
	{
		return stargateReturn(stargate -> stargate.isWormholeOpen(), false);
	}
	
	@Override
	default boolean isObstructed()
	{
		return stargateReturn(stargate -> stargate.isObstructed(), false);
	}
	
	@Override
	default boolean isPrimary()
	{
		return stargateReturn(stargate -> stargate.isPrimary(), false);
	}
	
	@Override
	default boolean checkValidity()
	{
		StargateEntity stargate = getStargateBlockEntity();
		
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
		stargateRun(stargate -> stargate.setEngagedChevrons(chevronConfiguration));
	}
	
	// Updating
	
	default void updateBlockEntityList()
	{
		BlockEntityList.get(getServer()).setDirty();
	}
	
	@Override
	default void updateClient()
	{
		stargateRun(stargate ->
		{
			stargate.setKawooshTime(getKawooshTime());
			stargate.setOpenTime(getOpenTime());
			stargate.setTimeSinceLastTraveler(getTimeSinceLastTraveler());
			stargate.updateClient();
		});
	}
	
	@Override
	default void updateInterfaceBlocks(@Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects)
	{
		stargateRun(stargate ->
		{
			if(type == null)
				stargate.updateInterfaceBlocks(eventName, objects);
			else
				switch(type)
				{
					case BASIC:
						stargate.updateBasicInterfaceBlocks(eventName, objects);
						break;
					case CRYSTAL:
						stargate.updateCrystalInterfaceBlocks(eventName, objects);
						break;
					case ADVANCED_CRYSTAL:
						stargate.updateAdvancedCrystalInterfaceBlocks(eventName, objects);
						break;
				}
		});
	}
	
	// Communication
	
	@Override
	default void receiveStargateMessage(String message)
	{
		stargateRun(stargate -> stargate.receiveStargateMessage(message));
	}
	
	@Override
	default void forwardTransmission(int transmissionJumps, int frequency, String transmission)
	{
		stargateRun(stargate -> stargate.forwardTransmission(transmissionJumps, frequency, transmission));
	}
	
	@Override
	default float checkStargateShieldingState()
	{
		return stargateReturn(stargate -> stargate instanceof IrisStargateEntity<?> irisStargate ? irisStargate.irisInfo().checkIrisState() : 0F, 0F);
	}
	
	// Energy
	
	@Override
	default SGJourneyEnergy getEnergyStorage()
	{
		return stargateReturn(stargate -> stargate.energyStorage, null);
	}
	
	@Override
	default long getEnergyStored()
	{
		return stargateReturn(stargate -> stargate.energyStorage.getTrueEnergyStored(), 0L);
	}
	
	@Override
	default long getEnergyCapacity()
	{
		return stargateReturn(stargate -> stargate.energyStorage.getTrueMaxEnergyStored(), 0L);
	}
	
	@Override
	default long extractEnergy(long energy, boolean simulate)
	{
		return stargateReturn(stargate -> stargate.energyStorage.depleteEnergy(energy, simulate), 0L);
	}
	
	@Override
	default long receiveEnergy(long energy, boolean simulate)
	{
		return stargateReturn(stargate -> stargate.energyStorage.receiveLongEnergy(energy, simulate), 0L);
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	@Override
	default StargateInfo.FeedbackMessage indirectEngageSymbol(int symbol, boolean canEngageStargate, StargateInfo.ChevronSound chevronSound)
	{
		return stargateReturnOrSupply(stargate -> stargate.indirectEngageSymbol(symbol, canEngageStargate, chevronSound), BlockEntityStargate::noStargateBlockEntity);
	}
	
	@Override
	default StargateInfo.FeedbackMessage directEngageSymbol(int symbol, boolean canEngageStargate, StargateInfo.ChevronSound chevronSound)
	{
		return stargateReturnOrSupply(stargate -> stargate.directEngageSymbol(symbol, canEngageStargate, chevronSound), BlockEntityStargate::noStargateBlockEntity);
	}
	
	@Override
	default StargateInfo.FeedbackMessage engageStargate(boolean doKawoosh, Dialing.Action action)
	{
		return stargateReturnOrSupply(stargate -> stargate.engageStargate(), BlockEntityStargate::noStargateBlockEntity);
	}
	
	//============================================================================================
	//************************************Stargate Connection*************************************
	//============================================================================================
	
	@Override
	default void connectStargate(StargateConnection connection, StargateConnection.State connectionState)
	{
		stargateRun(stargate -> stargate.connectStargate(connection.getID(), connectionState));
	}
	
	@Override
	default void doWhileConnecting(StargateConnection connection, boolean incoming, int kawooshStartTicks)
	{
		stargateRun(stargate -> stargate.doWhileConnecting(incoming, connection.doKawoosh(), kawooshStartTicks, connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileDialed(StargateConnection connection, Address connectedAddress, int kawooshStartTicks)
	{
		stargateRun(stargate -> stargate.doWhileDialed(connectedAddress, kawooshStartTicks, connection.doKawoosh(), connection.getConnectionTime()));
	}
	
	@Override
	default void doWhileConnected(StargateConnection connection, boolean incoming)
	{
		stargateRun(stargate -> stargate.doWhileConnected(incoming, connection.getConnectionTime()));
	}
	
	default void wormholeEntities(StargateConnection connection, Stargate destinationStargate, boolean incoming, StargateInfo.WormholeTravel wormholeTravel, List<Entity> wormholeCandidates)
	{
		stargateRun(stargate ->
		{
			if(getWormhole().wormholeEntities(getServer(), connection, this, destinationStargate, wormholeTravel, wormholeCandidates))
				connection.setUsed(true);
		});
	}
	
	@Override
	default void doWormhole(StargateConnection connection, boolean incoming, StargateInfo.WormholeTravel wormholeTravel)
	{
		stargateRun(stargate ->
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
	
	default Vec3 adjustDestinationHeight(StargateEntity stargate, Entity traveler, Vec3 destinationPosition, boolean shouldTravelerStickToGround)
	{
		if(stargate.getOrientation() != Orientation.REGULAR)
			return destinationPosition;
		
		ServerLevel level = (ServerLevel) stargate.getLevel();
		
		double bottomToCenterDist = traveler.getBoundingBox().getCenter().y() - traveler.getY();
		AABB aabb = AABB.ofSize(destinationPosition.add(0, bottomToCenterDist, 0), traveler.getBoundingBox().getXsize(), traveler.getBoundingBox().getYsize(), traveler.getBoundingBox().getZsize())
			.setMinY(stargate.getBlockPos().getY()); // Get all blocks under the traveler, but not those under the Stargate
		
		Optional<Double> maxY = BlockPos.betweenClosedStream(aabb).filter(pos ->
		{
			// Ignore blocks that on the same plane as the Stargate
			if(stargate.getDirection().getAxis() == Direction.Axis.X)
				return pos.getX() == stargate.getBlockPos().getX();
			else
				return pos.getZ() == stargate.getBlockPos().getZ();
		}).map(pos ->
		{
			double y = level.getBlockState(pos).getCollisionShape(level, pos).max(Direction.Axis.Y);
			return y + pos.getY();
		}).filter(height -> !Double.isInfinite(height)).max(Comparator.naturalOrder());
		
		if(maxY.isPresent())
		{
			// Don't move the traveler, they've run into a wall
			if(maxY.get() >= aabb.maxY)
				return destinationPosition;
			
			// Move the traveler up or stick them to the ground
			if(maxY.get() > destinationPosition.y() || shouldTravelerStickToGround)
				return new Vec3(destinationPosition.x, maxY.get(), destinationPosition.z);
		}
		
		return destinationPosition;
	}
	
	@Override
	default @Nullable Entity receiveTraveler(StargateConnection connection, Stargate initialStargate, Entity traveler, Vec3 relativePosition, Vec3 relativeMomentum, Vec3 relativeLookAngle)
	{
		return stargateReturn(stargate ->
				{
					// Call Forwarding
					if(controllerCache().returnOrDefault(DHD::doCallForwarding, false) && callForwardDeny(traveler))
					{
						if(connection.getDialedStargates().size() > 1) // There are at least 2 gates currently connected -> traveler gets sent to a random other gate
							return connection.getDialedStargates().get(new Random().nextInt(1, connection.getDialedStargates().size())).receiveTraveler(connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
						else // There is only one gate connected -> traveler gets sent back to the gate they initially entered
							return initialStargate.receiveTraveler(connection, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle);
					}
					
					Trinary shouldAdjustExitSpeed = shouldAdjustExitSpeed(traveler, true).or(initialStargate.shouldAdjustExitSpeed(traveler, false));
					
					// Push the traveler in some situations to help them
					Vec3 tempMomentum = shouldAdjustExitSpeed.isTrue() && stargate.pushTraveler() && relativeMomentum.x() > -SGJourneyStargate.MIN_TRAVELER_SPEED ?
						new Vec3(-SGJourneyStargate.MIN_TRAVELER_SPEED, relativeMomentum.y(), relativeMomentum.z()) : relativeMomentum;
					
					Vec3 destinationPosition = fromStargateCoords(relativePosition, true, true).add(stargate.getCenter());
					// Adjust traveler height to try and prevent them from ending up inside the floor on the other side
					Trinary shouldAdjustExitPos = shouldAdjustExitPos(traveler, true).or(initialStargate.shouldAdjustExitPos(traveler, false));
					if(shouldAdjustExitPos.isTrue())
					{
						// Adjust traveler height to make them stick to the ground if they entered the gate while sticking to the ground
						Trinary shouldTravelerStickToGround = shouldTravelerStickToGround(traveler, true).or(initialStargate.shouldTravelerStickToGround(traveler, false));
						destinationPosition = adjustDestinationHeight(stargate, traveler, destinationPosition, shouldTravelerStickToGround.isTrue());
					}
					Vec3 destinationMomentum = fromStargateCoords(tempMomentum, false, true);
					Vec3 destinationLookAngle = fromStargateCoords(relativeLookAngle, false, true);
					
					if(stargate instanceof IrisStargateEntity<?> irisStargate && !getWormhole().checkShielding(irisStargate, destinationPosition, destinationMomentum, traveler))
					{
						getWormhole().handleShielding(irisStargate, traveler);
						return traveler;
					}
					
					return getWormhole().receiveTraveler(connection, (ServerLevel) stargate.getLevel(), initialStargate, this, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
				},
				null);
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	default AddressFilterInfo addressFilterInfo()
	{
		return stargateReturn(stargate -> stargate.addressFilterInfo(), new AddressFilterInfo());
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	/**
	 * Actions the Stargate will perform while its respective Block Entity is ticking
	 */
	default void tick(StargateEntity stargateEntity)
	{
		tick();
	}
}
