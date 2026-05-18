package net.povstalec.sgjourney.common.misc;

import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockEntityCache<T extends BlockEntity>
{
	public enum Dirty
	{
		DIRTY,
		FETCHING,
		NOT_DIRTY
	}
	
	private Dirty dirty = Dirty.DIRTY;
	private T blockEntity = null;
	
	private Supplier<T> fetch = () -> null;
	
	/**
	 * Caches the provided Block Entity
	 * @param blockEntity Block Entity to be cached
	 */
	public void set(T blockEntity)
	{
		this.blockEntity = blockEntity;
		dirty = Dirty.NOT_DIRTY;
	}
	
	/**
	 * Marks the cache dirty, causing it to try fetching a Block Entity next time it is accessed
	 */
	public void markDirty()
	{
		dirty = Dirty.DIRTY;
	}
	
	/**
	 * Used for checking if this cache is currently fetching its Block Entity, in order to prevent cyclical fetching
	 * @return True if the cache is fetching its Block Entity, otherwise false
	 */
	public boolean isFetching()
	{
		return dirty == Dirty.FETCHING;
	}
	
	/**
	 * Sets the Supplier responsible for fetching a Block Entity
	 * @param fetch Supplier that returns the fetched Block Entity, or null if fetching was unsuccessful
	 */
	public void setFetch(Supplier<T> fetch)
	{
		this.fetch = fetch;
	}
	
	/**
	 * Fetches a Block Entity for the cache
	 * @return Fetched Block Entity or null if none was found
	 */
	@Nullable
	public T fetch()
	{
		dirty = Dirty.FETCHING;
		set(fetch.get());
		
		return blockEntity;
	}
	
	/**
	 * Checks if the there is a Block Entity inside the cache and attempts to fetch one in case it's dirty
	 * @return True if there is a Block Entity inside the cache, otherwise false
	 */
	public boolean hasBlockEntity()
	{
		if(dirty == Dirty.DIRTY)
			blockEntity = fetch();
		
		return blockEntity != null;
	}
	
	/**
	 * If the cache is dirty, it attempts to fetch a Block Entity
	 * @return Returns the cached Block Entity, or null if there is none
	 */
	@Nullable
	public T getBlockEntity()
	{
		if(dirty == Dirty.DIRTY)
			blockEntity = fetch();
		
		return blockEntity;
	}
	
	/**
	 * If there is a BlockEntity cached, performs the specified action on it (attempts to fetch a Block Entity if the cache is dirty)
	 * @param action Consumer to run if there is a Block Entity cached
	 */
	public void ifPresent(Consumer<T> action)
	{
		if(hasBlockEntity())
			action.accept(blockEntity);
	}
	
	/**
	 * Performs the specified action on a cached Block Entity, or a default action for empty cache (attempts to fetch a Block Entity if the cache is dirty)
	 * @param action Consumer to run if there is a Block Entity cached
	 * @param defaultAction Runnable to run if there is no Block Entity cached
	 */
	public void ifPresentOrElse(Consumer<T> action, Runnable defaultAction)
	{
		if(hasBlockEntity())
			action.accept(blockEntity);
		else
			defaultAction.run();
	}
	
	/**
	 * If there is a BlockEntity cached, applies the specified function on it (attempts to fetch a Block Entity if the cache is dirty)
	 * @param function Function to apply on the Block Entity
	 * @param defaultValue Default value to return if there is no cached Block Entity
	 * @return Result of the specified function, or the default value if there is no cached Block Entity
	 * @param <U> Type to return
	 */
	public <U> U returnOrDefault(Function<T, U> function, U defaultValue)
	{
		if(hasBlockEntity())
			return function.apply(blockEntity);
		
		return defaultValue;
	}
}
