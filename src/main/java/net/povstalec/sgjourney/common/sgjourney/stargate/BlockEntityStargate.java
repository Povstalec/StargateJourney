package net.povstalec.sgjourney.common.sgjourney.stargate;

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
	// Basic Info
	
	/**
	 * @return BlockPos this Stargate's BlockEntity is located at
	 */
	BlockPos getBlockPos();
	
	/**
	 * @return BlockEntity of this Stargate, if it currently has one, otherwise null
	 */
	@Nullable StargateEntity getStargateBlockEntity();
	
	void updateFromBlockEntity(StargateEntity stargateEntity);
	
	// Updating
	
	@Override
	default void update()
	{
		stargateRun(this::updateFromBlockEntity);
	}
	
	// Functional Interface stuff
	
	default void stargateRun(Consumer<StargateEntity> consumer)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			consumer.accept(stargate);
	}
	
	default void stargateRun(Consumer<StargateEntity> consumer, Runnable defaultAction)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			consumer.accept(stargate);
		else
			defaultAction.run();
	}
	
	default <T> T stargateReturn(Function<StargateEntity, T> function, @Nullable T defaultValue)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			return function.apply(stargate);
		
		return defaultValue;
	}
	
	default <T> T stargateReturnOrSupply(Function<StargateEntity, T> function, @NotNull Supplier<T> supplier)
	{
		StargateEntity stargate = getStargateBlockEntity();
		
		if(stargate != null)
			return function.apply(stargate);
		
		return supplier.get();
	}
	
	static StargateInfo.FeedbackMessage noStargateBlockEntity()
	{
		StargateJourney.LOGGER.error("BlockEntityStargate.noStargateBlockEntity: Stargate Block Entity could not be found");
		Thread.dumpStack();
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	// Stuff for handling Block Entities
	
	@Override
	default boolean isLoaded()
	{
		Level level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
	
	void onStargateBlockEntityUnloaded();
	
	void onStargateBlockEntityRemoved();
}
