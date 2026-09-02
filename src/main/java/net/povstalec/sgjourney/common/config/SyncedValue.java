package net.povstalec.sgjourney.common.config;

import net.minecraft.network.FriendlyByteBuf;

public abstract class SyncedValue<T>
{
	protected T value;
	
	public SyncedValue(T value)
	{
		this.value = value;
	}
	
	public void update(T value)
	{
		this.value = value;
	}
	
	@SuppressWarnings("unchecked")
	public void updateFrom(SyncedValue<?> other)
	{
		if(this.value.getClass().isInstance(other.value))
			update((T) other.value);
		else
			throw new RuntimeException("Value " + other + " is not an instance of " + this.value.getClass());
	}
	
	protected abstract void write(FriendlyByteBuf buffer);
	
	protected abstract void read(FriendlyByteBuf buffer);
	
	public T get()
	{
		return value;
	}
}
