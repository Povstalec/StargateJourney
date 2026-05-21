package net.povstalec.sgjourney.common.misc;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AutoCache<T>
{
	protected boolean dirty = true;
	protected T object = null;
	
	protected Supplier<T> fetch = () -> null;
	protected BooleanSupplier revalidate = () -> false;
	
	protected OldNewConsumer<T> onChanged = (oldObject, newObject) -> {};
	
	/**
	 * Caches the provided Object
	 * @param object Object to be cached
	 */
	public void set(@Nullable T object)
	{
		T old = this.object;
		this.object = object;
		dirty = false;
		
		onChanged.accept(old, object);
	}
	
	/**
	 * Clears the cache and marks it dirty
	 */
	public void clear()
	{
		T old = object;
		object = null;
		markDirty();
		
		onChanged.accept(old, null);
	}
	
	/**
	 * Sets the Consumer that fires every time there is a change to the cache (after the changes are applied to the cache)
	 * @param onChanged OldNewConsumer that fires every time there is a change to the cache (first argument is the old Object, second argument is new Object, either argument may be null)
	 */
	public void setOnChanged(OldNewConsumer<T> onChanged)
	{
		this.onChanged = onChanged;
	}
	
	/**
	 * Marks the cache dirty, causing it to try fetching an Object next time it is accessed
	 */
	public void markDirty()
	{
		dirty = true;
	}
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 * Sets the Supplier responsible for fetching an Object
	 * @param fetch Supplier that returns the fetched Object, or null if fetching was unsuccessful
	 */
	public void setFetch(Supplier<T> fetch)
	{
		this.fetch = fetch;
	}
	
	/**
	 * Sets the Supplier responsible for revalidating an Object when the cache is dirty
	 * @param revalidate Supplier that returns true if the Object is still valid, otherwise false
	 */
	public void setRevalidate(BooleanSupplier revalidate)
	{
		this.revalidate = revalidate;
	}
	
	/**
	 * Fetches an Object for the cache
	 */
	protected void fetch()
	{
		set(fetch.get());
	}
	
	protected boolean shouldFetch()
	{
		return dirty && !revalidate.getAsBoolean();
	}
	
	/**
	 * Checks if the there is an Object inside the cache and attempts to fetch one in case it's dirty
	 * @return True if there is an Object inside the cache, otherwise false
	 */
	public boolean isPresent()
	{
		if(shouldFetch())
			fetch();
		
		return object != null;
	}
	
	/**
	 * Like {@link #isPresent()}, but doesn't attempt to fetch when no Object is found
	 * @return True if there is an Object inside the cache, otherwise false
	 */
	public boolean isCached()
	{
		return object != null;
	}
	
	/**
	 * If the cache is dirty, it attempts to fetch an Object
	 * @return Returns the cached Object, or null if there is none
	 */
	@Nullable
	public T get()
	{
		if(shouldFetch())
			fetch();
		
		return object;
	}
	
	/**
	 * Like {@link #get()}, but doesn't attempt to fetch when no Object is found
	 * @return Returns the cached Object, or null if there is none
	 */
	@Nullable
	public T getCached()
	{
		return object;
	}
	
	/**
	 * If there is an Object cached, performs the specified action on it (attempts to fetch an Object if the cache is dirty)
	 * @param action Consumer to run if there is an Object cached
	 */
	public void ifPresent(Consumer<T> action)
	{
		if(isPresent())
			action.accept(object);
	}
	
	/**
	 * Like {@link #ifPresent(Consumer)}, but doesn't attempt to fetch when no Object is found
	 * @param action Consumer to run if there is an Object cached
	 */
	public void ifCached(Consumer<T> action)
	{
		if(isCached())
			action.accept(object);
	}
	
	/**
	 * Performs the specified action on a cached Object, or a default action for empty cache (attempts to fetch an Object if the cache is dirty)
	 * @param action Consumer to run if there is an Object cached
	 * @param defaultAction Runnable to run if there is no Object cached
	 */
	public void ifPresentOrElse(Consumer<T> action, Runnable defaultAction)
	{
		if(isPresent())
			action.accept(object);
		else
			defaultAction.run();
	}
	
	/**
	 * Like {@link #ifPresentOrElse(Consumer, Runnable)}, but doesn't attempt to fetch when no Object is found
	 * @param action Consumer to run if there is an Object cached
	 * @param defaultAction Runnable to run if there is no Object cached
	 */
	public void ifCachedOrElse(Consumer<T> action, Runnable defaultAction)
	{
		if(isCached())
			action.accept(object);
		else
			defaultAction.run();
	}
	
	/**
	 * If there is an Object cached, applies the specified function on it (attempts to fetch an Object if the cache is dirty)
	 * @param function Function to apply on the Object
	 * @param defaultValue Default value to return if there is no cached Object
	 * @return Result of the specified function, or the default value if there is no cached Object
	 * @param <U> Type to return
	 */
	public <U> U returnOrDefault(Function<T, U> function, U defaultValue)
	{
		if(isPresent())
			return function.apply(object);
		
		return defaultValue;
	}
	
	/**
	 * Like {@link #returnOrDefault(Function, U)}, but doesn't attempt to fetch when no Object is found
	 * @param function Function to apply on the Object
	 * @param defaultValue Default value to return if there is no cached Object
	 * @return Result of the specified function, or the default value if there is no cached Object
	 * @param <U> Type to return
	 */
	public <U> U returnCachedOrDefault(Function<T, U> function, U defaultValue)
	{
		if(isCached())
			return function.apply(object);
		
		return defaultValue;
	}
	
	public interface OldNewConsumer<T>
	{
		void accept(@Nullable T oldObject, @Nullable T newObject);
	}
	
	//============================================================================================
	//*******************************************Friend*******************************************
	//============================================================================================
	
	public static abstract class Friend<T, F extends Friend<?, ?>> extends AutoCache<T>
	{
		@Nullable
		protected F friend; // Friendly cache to which this cache is connected to
		
		/**
		 * If this has a friend, clears friend's cache and sets friend to null
		 */
		protected void breakLink()
		{
			if(friend != null)
			{
				friend.clear(); // Clear the friend's cache, breaking the link between this and friend
				friend = null;
			}
		}
		
		protected abstract void onChanging(T newObject);
		
		
		public void setTwoWays(@Nullable T object)
		{
			breakLink();
			
			if(object != null)
				onChanging(object);
			
			set(object);
		}
		
		@Override
		public void clear()
		{
			friend = null;
			super.clear();
		}
		
		public void clearTwoWays()
		{
			breakLink();
			
			clear();
		}
		
		public void markDirtyTwoWays()
		{
			if(friend != null) // Prevents infinite loop of one Friend marking the friend as dirty
				friend.markDirty(); // Mark friend as dirty when marking this as dirty, indicating the link needs to be updated
			
			markDirty();
		}
		
		@Override
		protected void fetch()
		{
			breakLink(); // If we're fetching, we're definitely breaking the link,
			
			setTwoWays(fetch.get());
		}
	}
	
	//============================================================================================
	//******************************************Receiver******************************************
	//============================================================================================
	
	public interface IController<C extends IController<C, R>, R extends IReceiver<C, R>>
	{
		Receiver<C, R> receiverCache();
	}
	
	public static class Controller<C extends IController<C, R>, R extends IReceiver<C, R>> extends Friend<C, Receiver<C, R>>
	{
		public final R receiver; // The receiver this cache belongs to
		
		public Controller(R receiver)
		{
			this.receiver = receiver;
		}
		
		@Override
		protected void onChanging(C newObject)
		{
			newObject.receiverCache().set(receiver);
		}
		
		@Override
		public void set(@Nullable C object)
		{
			super.set(object);
			
			if(object != null)
				this.friend = object.receiverCache(); // Automatically set the receiver whenever setting the Object
		}
		
		@Override
		public String toString()
		{
			return "Controller Cache of " + receiver.toString();
		}
	}
	
	//============================================================================================
	//*****************************************Controller*****************************************
	//============================================================================================
	
	public interface IReceiver<C extends IController<C, R>, R extends IReceiver<C, R>>
	{
		Controller<C, R> controllerCache();
	}
	
	public static class Receiver<C extends IController<C, R>, R extends IReceiver<C, R>> extends Friend<R, Controller<C, R>>
	{
		public final C controller; // The controller this cache belongs to
		
		public Receiver(C controller)
		{
			this.controller = controller;
		}
		
		@Override
		protected void onChanging(R newObject)
		{
			newObject.controllerCache().set(controller);
		}
		
		@Override
		public void set(@Nullable R object)
		{
			super.set(object);
			
			if(object != null)
				this.friend = object.controllerCache(); // Automatically set the controller whenever setting the Object
		}
		
		@Override
		public String toString()
		{
			return "Receiver Cache of " + controller.toString();
		}
	}
}
