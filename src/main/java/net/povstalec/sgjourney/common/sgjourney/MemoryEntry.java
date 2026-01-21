package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.misc.Conversion;

import java.util.function.Function;

public abstract class MemoryEntry<T>
{
	public static final String NAME = "name";
	public static final String TIME_STAMP = "time_stamp";
	public static final String ENTRY_TYPE = "entry_type";
	
	public static final String TEXT = "text";
	public static final String ADDRESS = net.povstalec.sgjourney.common.sgjourney.Address.ADDRESS;
	public static final String TRANSPORTER_ID = net.povstalec.sgjourney.common.sgjourney.TransporterID.TRANSPORTER_ID;
	public static final String COORDINATES = "coords";
	
	public enum Type
	{
		UNKNOWN(Component.translatable("tooltip.sgjourney.unknown").withStyle(ChatFormatting.DARK_RED), Unknown.class, Unknown::new),
		TEXT(Component.translatable("tooltip.sgjourney.text").withStyle(ChatFormatting.GRAY), Text.class, Text::new),
		ADDRESS(Component.translatable("tooltip.sgjourney.address").withStyle(ChatFormatting.AQUA), Address.class, Address::new),
		TRANSPORTER_ID(Component.translatable("tooltip.sgjourney.transporter_id").withStyle(ChatFormatting.DARK_AQUA), TransporterID.class, TransporterID::new),
		COORDINATES(Component.translatable("tooltip.sgjourney.coordinates").withStyle(ChatFormatting.BLUE), Coordinates.class, Coordinates::new);
		
		private final Component component;
		private final Class clazz;
		private final Function<CompoundTag, MemoryEntry<?>> function;
		
		Type(Component component, Class clazz, Function<CompoundTag, MemoryEntry<?>> function)
		{
			this.component = component;
			this.clazz = clazz;
			this.function = function;
		}
		
		public Component getComponent()
		{
			return this.component;
		}
		
		public static Type fromOrdinal(int ordinal)
		{
			if(ordinal < 0 || ordinal >= values().length)
				return UNKNOWN;
			
			return Type.values()[ordinal];
		}
		
		public boolean isSameClass(Class clazz)
		{
			return this.clazz == clazz;
		}
		
		public MemoryEntry<?> loadFromTag(CompoundTag tag)
		{
			return function.apply(tag);
		}
	}
	
	protected String name;
	protected long timeStamp; // Ticks since server start, can be negative to represent some random time before that
	protected Type entryType;
	protected T entry;
	
	public MemoryEntry(CompoundTag tag)
	{
		load(tag);
	}
	
	public MemoryEntry(String name, long timeStamp, Type entryType, T entry)
	{
		this.name = name;
		this.timeStamp = timeStamp;
		this.entryType = entryType;
		this.entry = entry;
	}
	
	public String name()
	{
		return this.name;
	}
	
	public long timeStamp()
	{
		return this.timeStamp;
	}
	
	public Type entryType()
	{
		return this.entryType;
	}
	
	public T entry()
	{
		return this.entry;
	}
	
	protected abstract void saveEntry(CompoundTag tag);
	
	protected abstract T loadEntry(CompoundTag tag);
	
	public final CompoundTag save()
	{
		CompoundTag tag = new CompoundTag();
		
		if(!name.isEmpty())
			tag.putString(NAME, name);
		tag.putLong(TIME_STAMP, timeStamp);
		tag.putInt(ENTRY_TYPE, entryType.ordinal());
		saveEntry(tag);
		
		return tag;
	}
	
	public final void load(CompoundTag tag)
	{
		this.name = tag.getString(NAME);
		this.timeStamp = tag.getLong(TIME_STAMP);
		this.entryType = Type.fromOrdinal(tag.getInt(ENTRY_TYPE));
		this.entry = loadEntry(tag);
	}
	
	@Override
	public String toString()
	{
		if(name.isEmpty())
			return entry.toString();
		
		return '[' + name + "] " + entry.toString();
	}
	
	//============================================================================================
	//******************************************Entries*******************************************
	//============================================================================================
	
	public static class Unknown extends MemoryEntry<Byte>
	{
		public Unknown(CompoundTag tag)
		{
			super(tag);
		}
		
		public Unknown(String name, long timeStamp, Type entryType, Byte entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag) {}
		
		@Override
		protected Byte loadEntry(CompoundTag tag)
		{
			return 0;
		}
	}
	
	public static class Text extends MemoryEntry<String>
	{
		public Text(CompoundTag tag)
		{
			super(tag);
		}
		
		public Text(String name, long timeStamp, Type entryType, String entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			if(!entry.isEmpty())
				tag.putString(TEXT, entry);
		}
		
		@Override
		protected String loadEntry(CompoundTag tag)
		{
			return tag.getString(TEXT);
		}
	}
	
	public static class Address extends MemoryEntry<net.povstalec.sgjourney.common.sgjourney.Address>
	{
		public Address(CompoundTag tag)
		{
			super(tag);
		}
		
		public Address(String name, long timeStamp, Type entryType, net.povstalec.sgjourney.common.sgjourney.Address entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			entry.saveToCompoundTag(tag, ADDRESS);
		}
		
		@Override
		protected net.povstalec.sgjourney.common.sgjourney.Address loadEntry(CompoundTag tag)
		{
			return new net.povstalec.sgjourney.common.sgjourney.Address.Immutable(tag.getIntArray(ADDRESS));
		}
	}
	
	public static class TransporterID extends MemoryEntry<net.povstalec.sgjourney.common.sgjourney.TransporterID>
	{
		public TransporterID(CompoundTag tag)
		{
			super(tag);
		}
		
		public TransporterID(String name, long timeStamp, Type entryType, net.povstalec.sgjourney.common.sgjourney.TransporterID entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			entry.saveToCompoundTag(tag, TRANSPORTER_ID);
		}
		
		@Override
		protected net.povstalec.sgjourney.common.sgjourney.TransporterID loadEntry(CompoundTag tag)
		{
			return new net.povstalec.sgjourney.common.sgjourney.TransporterID.Immutable(tag.getIntArray(TRANSPORTER_ID));
		}
	}
	
	public static class Coordinates extends MemoryEntry<Vec3i>
	{
		public Coordinates(CompoundTag tag)
		{
			super(tag);
		}
		
		public Coordinates(String name, long timeStamp, Type entryType, Vec3i entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		public Vec3 asVec3()
		{
			return new Vec3(entry.getX(), entry.getY(), entry.getZ());
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			tag.putIntArray(COORDINATES, Conversion.vecToIntArray(entry));
		}
		
		@Override
		protected Vec3i loadEntry(CompoundTag tag)
		{
			return Conversion.intArrayToVec(tag.getIntArray(COORDINATES));
		}
	}
	
	public static class StargateConnectionResult extends MemoryEntry<StargateConnection.Result>
	{
		public static final String STARGATE_CONNECTION_RESULT = "stargate_connection_result";
		
		public StargateConnectionResult(CompoundTag tag)
		{
			super(tag);
		}
		
		public StargateConnectionResult(String name, long timeStamp, Type entryType, StargateConnection.Result entry)
		{
			super(name, timeStamp, entryType, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			tag.put(STARGATE_CONNECTION_RESULT, entry.save());
		}
		
		@Override
		protected StargateConnection.Result loadEntry(CompoundTag tag)
		{
			StargateConnection.Result result = new StargateConnection.Result();
			result.load(tag.getCompound(STARGATE_CONNECTION_RESULT));
			return result;
		}
	}
}
