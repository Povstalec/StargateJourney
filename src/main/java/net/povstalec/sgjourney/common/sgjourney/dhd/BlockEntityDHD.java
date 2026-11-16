package net.povstalec.sgjourney.common.sgjourney.dhd;

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
	
	// Functional Interface stuff
	
	default void dhdRun(Consumer<DHDEntity> consumer)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			consumer.accept(dhd);
	}
	
	default void dhdRun(Consumer<DHDEntity> consumer, Runnable defaultAction)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			consumer.accept(dhd);
		else
			defaultAction.run();
	}
	
	default <T> T dhdReturn(Function<DHDEntity, T> function, @Nullable T defaultValue)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return defaultValue;
	}
	
	default <T> T dhdReturnOrSupply(Function<DHDEntity, T> function, @NotNull Supplier<T> supplier)
	{
		DHDEntity dhd = getDHDBlockEntity();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return supplier.get();
	}
	
	static StargateInfo.FeedbackMessage noDHDBlockEntity()
	{
		StargateJourney.LOGGER.error("BlockEntityDHD.noDHDBlockEntity: DHD Block Entity could not be found");
		return StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
	}
	
	@Override
	default boolean isLoaded()
	{
		Level level = getLevel();
		if(level == null)
			return false;
		
		return level.isLoaded(getBlockPos());
	}
	
	void onDHDBlockEntityUnloaded();
	
	void onDHDBlockEntityRemoved();
}
