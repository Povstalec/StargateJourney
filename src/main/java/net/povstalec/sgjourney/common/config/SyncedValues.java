package net.povstalec.sgjourney.common.config;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SyncedValues
{
	protected List<SyncedValue<?>> values = new ArrayList<>();
	
	public SyncedValues copy()
	{
		SyncedValues copy = new SyncedValues();
		copy.values = new ArrayList<>(this.values);
		return copy;
	}
	
	public void write(FriendlyByteBuf buffer)
	{
		for(SyncedValue<?> value : values)
		{
			value.write(buffer);
		}
	}
	
	public void read(FriendlyByteBuf buffer)
	{
		for(SyncedValue<?> value : values)
		{
			value.read(buffer);
		}
	}
	
	public void updateFrom(SyncedValues other)
	{
		if(this.values.size() != other.values.size())
			throw new RuntimeException("Incompatible synced values length!");
		
		for(int i = 0; i < this.values.size(); i++)
		{
			this.values.get(i).updateFrom(other.values.get(i));
		}
	}
	
	
	
	public SyncedValue<Boolean> create(boolean value)
	{
		SyncedValue<Boolean> syncedValue = new SyncedValue<>(value)
		{
			@Override
			protected void write(FriendlyByteBuf buffer)
			{
				buffer.writeBoolean(value);
			}
			
			@Override
			protected void read(FriendlyByteBuf buffer)
			{
				value = buffer.readBoolean();
			}
		};
		values.add(syncedValue);
		
		return syncedValue;
	}
	
	public SyncedValue<Integer> create(int value)
	{
		SyncedValue<Integer> syncedValue = new SyncedValue<>(value)
		{
			@Override
			protected void write(FriendlyByteBuf buffer)
			{
				buffer.writeInt(value);
			}
			
			@Override
			protected void read(FriendlyByteBuf buffer)
			{
				value = buffer.readInt();
			}
		};
		values.add(syncedValue);
		
		return syncedValue;
	}
	
	public SyncedValue<Long> create(long value)
	{
		SyncedValue<Long> syncedValue = new SyncedValue<>(value)
		{
			@Override
			protected void write(FriendlyByteBuf buffer)
			{
				buffer.writeLong(value);
			}
			
			@Override
			protected void read(FriendlyByteBuf buffer)
			{
				value = buffer.readLong();
			}
		};
		values.add(syncedValue);
		
		return syncedValue;
	}
}
