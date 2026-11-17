package net.povstalec.sgjourney.api.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.api.stargate.DHDControlledStargate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BlockEntityDHD<DHDEntity extends BlockEntity> extends DHD
{
	// Basic Info
	
	/**
	 * @return BlockPos this DHD's BlockEntity is located at
	 */
	BlockPos getBlockPos();
	
	/**
	 * @return BlockEntity of this DHD, if it currently has one, otherwise null
	 */
	@Nullable DHDEntity getDHDBlockEntity();
	
	// Updating
	
	/**
	 * Updates the DHD with info from a Block Entity, typically runs as a result of {@link BlockEntityDHD#update()} being run
	 * @param stargateEntity The Block Entity to update the DHD with
	 */
	void updateFromBlockEntity(DHDEntity stargateEntity);
	
	/**
	 * Updates this DHD, typically runs as a result of {@link DHDControlledStargate#update()} being run
	 */
	@Override
	default void update()
	{
		dhdRun(this::updateFromBlockEntity);
	}
	
	// Functional Interface stuff
	
	/**
	 * Performs the provided operation on the DHD Block Entity received from {@link BlockEntityDHD#getDHDBlockEntity()} if it isn't null
	 * @param consumer Operation to perform if the DHD Block Entity isn't null
	 */
	default void dhdRun(Consumer<DHDEntity> consumer)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			consumer.accept(dhd);
	}
	
	/**
	 * Performs the provided operation on the DHD Block Entity received from {@link BlockEntityDHD#getDHDBlockEntity()} if it isn't null,
	 * otherwise it performs the provided default action
	 * @param consumer Operation to perform if the DHD Block Entity isn't null
	 * @param defaultAction Default action to perform if the DHD Block Entity is null
	 */
	default void dhdRun(Consumer<DHDEntity> consumer, Runnable defaultAction)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			consumer.accept(dhd);
		else
			defaultAction.run();
	}
	
	/**
	 * Applies the provided function to the DHD Block Entity received from {@link BlockEntityDHD#getDHDBlockEntity()} if it isn't null
	 * and returns its result, otherwise it returns the provided default value
	 * @param function Function to apply if the DHD Block Entity isn't null
	 * @param defaultValue Default value to return if the DHD Block Entity is null
	 */
	default <T> T dhdReturn(Function<DHDEntity, T> function, @Nullable T defaultValue)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return defaultValue;
	}
	
	/**
	 * Applies the provided function to the DHD Block Entity received from {@link BlockEntityDHD#getDHDBlockEntity()} if it isn't null
	 * and returns its result, otherwise it returns a value received from the provided supplier
	 * @param function Function to apply if the DHD Block Entity isn't null
	 * @param supplier Supplier to get the result from instead of the DHD Block Entity is null
	 */
	default <T> T dhdReturnOrSupply(Function<DHDEntity, T> function, @NotNull Supplier<T> supplier)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return supplier.get();
	}
	
	/**
	 * Helper function that can be provided as a parameter to {@link BlockEntityDHD#dhdReturnOrSupply(Function, Supplier)}
	 * when it's meant to return a {@link StargateInfo.FeedbackMessage}, making it easier to track errors
	 * caused by the DHD Block Entity being unreachable.
	 * @return UNKNOWN_ERROR
	 */
	static StargateInfo.FeedbackMessage noDHDBlockEntity()
	{
		StargateJourney.LOGGER.error("BlockEntityDHD.noDHDBlockEntity: DHD Block Entity could not be found");
		Thread.dumpStack();
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	/**
	 * Method that should typically be run as a result of {@link BlockEntity#onChunkUnloaded()} being run
	 */
	default void onDHDBlockEntityUnloaded() {}
	
	/**
	 * Method that should typically be run as a result of {@link BlockEntity#setRemoved()} being run
	 */
	default void onDHDBlockEntityRemoved() {}
	
	@Override
	default boolean isLoaded()
	{
		Level level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
}
