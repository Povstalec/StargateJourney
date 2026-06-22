package net.povstalec.sgjourney.common.sgjourney.transporter;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.Transporting;
import net.povstalec.sgjourney.common.sgjourney.info.TransporterIDFilterInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BlockEntityTransporter<TransporterEntity extends AbstractTransporterEntity<?>> extends Transporter
{
	String COORDINATES = "Coordinates"; //TODO Change this to "coordinates"
	
	BlockPos getBlockPos();
	
	@Nullable
	TransporterEntity getTransporterEntity(MinecraftServer server);
	
	default void transporterRun(MinecraftServer server, Consumer<TransporterEntity> consumer)
	{
		TransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			consumer.accept(transporter);
	}
	
	default <T> T transporterReturn(MinecraftServer server, Function<TransporterEntity, T> consumer, @Nullable T defaultValue)
	{
		TransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return consumer.apply(transporter);
		
		return defaultValue;
	}
	
	static TransporterInfo.Feedback noTransporterEntity()
	{
		StargateJourney.LOGGER.error("SGJourneyTransporter.noTransporterEntity: Transporter Entity could not be found");
		return TransporterInfo.Feedback.UNKNOWN_ERROR;
	}
	
	void loadFromBlockEntity(AbstractTransporterEntity<?> transporterEntity);
	
	
	
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
		return getBlockPos().getCenter();
	}
	
	
	
	@Override
	@Nullable
	default Vec3 transportPos()
	{
		return transporterReturn(getServer(), transporter -> transporter.transportPos().getCenter(), null);
	}
	
	@Override
	default void updateInterfaceBlocks(@Nullable AbstractInterfaceEntity.InterfaceType type, @Nullable String eventName, Object... objects)
	{
		transporterRun(getServer(), transporterEntity ->
		{
			if(type == null)
				transporterEntity.updateInterfaceBlocks(eventName, objects);
			else
				switch(type)
				{
					case BASIC:
						transporterEntity.updateBasicInterfaceBlocks(eventName, objects);
						break;
					case CRYSTAL:
						transporterEntity.updateCrystalInterfaceBlocks(eventName, objects);
						break;
					case ADVANCED_CRYSTAL:
						transporterEntity.updateAdvancedCrystalInterfaceBlocks(eventName, objects);
						break;
				}
		});
	}
	
	@Override
	default TransporterInfo.FeedbackMessage resetTransporter(TransporterInfo.FeedbackMessage feedback)
	{
		TransporterEntity transporterEntity = getTransporterEntity(getServer());
		
		if(transporterEntity != null)
			return transporterEntity.resetTransporter(feedback);
		else
			StargateJourney.LOGGER.error("Failed to reset Transporter as it does not exist");
		
		return feedback;
	}
	
	@Override
	default long getEnergyStored()
	{
		return transporterReturn(getServer(), transporter -> transporter.energyStorage.getTrueEnergyStored(), 0L);
	}
	
	@Override
	default long getEnergyCapacity()
	{
		return transporterReturn(getServer(), transporter -> transporter.energyStorage.getTrueMaxEnergyStored(), 0L);
	}
	
	@Override
	default long extractEnergy(long energy, boolean simulate)
	{
		return transporterReturn(getServer(), transporter -> transporter.energyStorage.depleteEnergy(energy, simulate), 0L);
	}
	
	@Override
	default int getTimeUntilTransport()
	{
		return transporterReturn(getServer(), transporter -> transporter.getTimeUntilTransport(), 0);
	}
	
	@Override
	default List<Entity> entitiesToTransport()
	{
		return transporterReturn(getServer(), transporter -> transporter.entitiesToTransport(), ImmutableList.of());
	}
	
	@Override
	default boolean checkValidity()
	{
		TransporterEntity transporter = getTransporterEntity(getServer());
		
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Transporter not found");
			return false;
			
		}
		else if(!getID().equals(transporter.getID()))
		{
			StargateJourney.LOGGER.error("Block Entity ID wasn't equal to Transporter ID");
			if(transporter.getID() == null) // In case Transporter ID becomes null for some reason during updating, it should get updated from this Transporter's ID
				transporter.setID(new TransporterID.Immutable(getID()));
			else
				return false;
		}
		
		return true;
	}
	
	@Override
	default boolean transportTravelers(TransporterConnection connection, Transporter receivingTransporter, List<Entity> travelers)
	{
		return transporterReturn(getServer(), transporter -> Transporting.transportTravelers(getServer(), connection, this, receivingTransporter, travelers), false);
		
	}
	
	@Override
	default void connect(UUID connectionID)
	{
		transporterRun(getServer(), transporter -> transporter.connectTransporter(connectionID));
	}
	
	@Override
	default void disconnect()
	{
		transporterRun(getServer(), transporter -> transporter.disconnectTransporter(TransporterInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT));
	}
	
	@Override
	default boolean isConnected()
	{
		return transporterReturn(getServer(), transporter -> transporter.isConnected(), false);
	}
	
	@Override
	default boolean isObstructed()
	{
		return transporterReturn(getServer(), transporter -> transporter.isObstructed(), false);
	}
	
	@Override
	default void updateTicks(int transportTicks, int connectionTime)
	{
		transporterRun(getServer(), transporter -> transporter.updateTicks(transportTicks, connectionTime));
	}
	
	//============================================================================================
	//**********************************Additional functionality**********************************
	//============================================================================================
	
	@Override
	default TransporterIDFilterInfo transporterIDFilterInfo()
	{
		return transporterReturn(getServer(), transporter -> transporter.transporterIDFilterInfo(), new TransporterIDFilterInfo());
	}
}
