package net.povstalec.sgjourney.api.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BlockEntityTransporter<TransporterEntity extends BlockEntity> extends Transporter
{
	String COORDINATES = "coordinates";
	
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
	
	default <T> T transporterReturnOrSupply(MinecraftServer server, Function<TransporterEntity, T> consumer, @NotNull Supplier<T> defaultValueSupplier)
	{
		TransporterEntity transporter = getTransporterEntity(server);
		
		if(transporter != null)
			return consumer.apply(transporter);
		
		return defaultValueSupplier.get();
	}
	
	static TransporterInfo.Feedback noTransporterEntity()
	{
		StargateJourney.LOGGER.error("SGJourneyTransporter.noTransporterEntity: Transporter Entity could not be found");
		return TransporterInfo.Feedback.UNKNOWN_ERROR;
	}
	
	@Override
	default boolean isLoaded()
	{
		ServerLevel level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
}
