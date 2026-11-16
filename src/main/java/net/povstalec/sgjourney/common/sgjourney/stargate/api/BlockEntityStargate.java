package net.povstalec.sgjourney.common.sgjourney.stargate.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BlockEntityStargate<StargateEntity extends BlockEntity> extends Stargate
{
	String COORDINATES = "coordinates";
	
	// Basic Info
	
	/**
	 * @return BlockPos this Stargate's BlockEntity is located at
	 */
	BlockPos getBlockPos();
	
	/**
	 * @return BlockEntity of this Stargate, if it currently has one, otherwise null
	 */
	@Nullable StargateEntity getStargateBlockEntity();
	
	// Updating
	
	/**
	 * Updates the Stargate with info from a Block Entity, typically runs as a result of {@link BlockEntityStargate#update()} being run
	 * @param stargateEntity The Block Entity to update the Stargate with
	 */
	void updateFromBlockEntity(StargateEntity stargateEntity);
	
	/**
	 * Updates this Stargate, typically used whenever the Stargate needs updating, like when a Stargate
	 * is added to/removed from the Stargate Network and the Stargate priority list needs to be recalculated
	 */
	@Override
	default void update()
	{
		stargateRun(this::updateFromBlockEntity);
	}
	
	// Functional Interface stuff
	
	/**
	 * Performs the provided operation on the Stargate Block Entity received from {@link BlockEntityStargate#getStargateBlockEntity()} if it isn't null
	 * @param consumer Operation to perform if the Stargate Block Entity isn't null
	 */
	default void stargateRun(Consumer<StargateEntity> consumer)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			consumer.accept(stargate);
	}
	
	/**
	 * Performs the provided operation on the Stargate Block Entity received from {@link BlockEntityStargate#getStargateBlockEntity()} if it isn't null,
	 * otherwise it performs the provided default action
	 * @param consumer Operation to perform if the Stargate Block Entity isn't null
	 * @param defaultAction Default action to perform if the Stargate Block Entity is null
	 */
	default void stargateRun(Consumer<StargateEntity> consumer, Runnable defaultAction)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			consumer.accept(stargate);
		else
			defaultAction.run();
	}
	
	/**
	 * Applies the provided function to the Stargate Block Entity received from {@link BlockEntityStargate#getStargateBlockEntity()} if it isn't null
	 * and returns its result, otherwise it returns the provided default value
	 * @param function Function to apply if the Stargate Block Entity isn't null
	 * @param defaultValue Default value to return if the Stargate Block Entity is null
	 */
	default <T> T stargateReturn(Function<StargateEntity, T> function, @Nullable T defaultValue)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			return function.apply(stargate);
		
		return defaultValue;
	}
	
	/**
	 * Applies the provided function to the Stargate Block Entity received from {@link BlockEntityStargate#getStargateBlockEntity()} if it isn't null
	 * and returns its result, otherwise it returns a value received from the provided supplier
	 * @param function Function to apply if the Stargate Block Entity isn't null
	 * @param supplier Supplier to get the result from instead of the Stargate Block Entity is null
	 */
	default <T> T stargateReturnOrSupply(Function<StargateEntity, T> function, @NotNull Supplier<T> supplier)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			return function.apply(stargate);
		
		return supplier.get();
	}
	
	/**
	 * Helper function that can be provided as a parameter to {@link BlockEntityStargate#stargateReturnOrSupply(Function, Supplier)}
	 * when it's meant to return a {@link StargateInfo.FeedbackMessage}, making it easier to track errors
	 * caused by the Stargate Block Entity being unreachable.
	 * @return UNKNOWN_ERROR
	 */
	static StargateInfo.FeedbackMessage noStargateBlockEntity()
	{
		StargateJourney.LOGGER.error("BlockEntityStargate.noStargateBlockEntity: Stargate Block Entity could not be found");
		Thread.dumpStack();
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	// Stuff for handling Block Entities
	
	/**
	 * Method that should typically be run as a result of {@link BlockEntity#onChunkUnloaded()} being run
	 */
	default void onStargateBlockEntityUnloaded() {}
	
	/**
	 * Method that should typically be run as a result of {@link BlockEntity#setRemoved()} being run
	 */
	default void onStargateBlockEntityRemoved() {}
	
	@Override
	default boolean isLoaded()
	{
		Level level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
}
