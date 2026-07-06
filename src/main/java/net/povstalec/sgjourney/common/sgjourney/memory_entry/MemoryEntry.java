package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MemoryEntry<T>
{
	public static final String NAME = "name";
	public static final String TIME_STAMP = "time_stamp";
	public static final String ENTRY_TYPE = "entry_type";
	
	protected String name;
	protected long timeStamp; // Ticks since server start, can be negative to represent some random time before that
	protected Type<?> entryType;
	protected T entry;
	
	public MemoryEntry(CompoundTag tag)
	{
		load(tag);
	}
	
	public MemoryEntry(String name, long timeStamp, Type<?> entryType, T entry)
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
	
	public Type<?> entryType()
	{
		return this.entryType;
	}
	
	public T entry()
	{
		return this.entry;
	}
	
	public String entryString()
	{
		return this.entry.toString();
	}
	
	protected abstract void saveEntry(CompoundTag tag);
	
	protected abstract T loadEntry(CompoundTag tag);
	
	public final CompoundTag save()
	{
		CompoundTag tag = new CompoundTag();
		
		if(!name.isEmpty())
			tag.putString(NAME, name);
		tag.putLong(TIME_STAMP, timeStamp);
		tag.putInt(ENTRY_TYPE, entryType.getId());
		saveEntry(tag);
		
		return tag;
	}
	
	public final void load(CompoundTag tag)
	{
		this.name = tag.getString(NAME);
		this.timeStamp = tag.getLong(TIME_STAMP);
		this.entryType = Type.fromId(tag.getInt(ENTRY_TYPE));
		this.entry = loadEntry(tag);
	}
	
	@Override
	public String toString()
	{
		if(name.isEmpty())
			return entry.toString();
		
		return '[' + name + "] " + entry.toString();
	}
	
	public ChatFormatting entryChatFormatting()
	{
		return ChatFormatting.WHITE;
	}
	
	public MutableComponent toComponent()
	{
		MutableComponent component = name.isEmpty() ? Component.empty() : Component.literal('[' + name + "] ").withStyle(ChatFormatting.GREEN);
		
		return component.append(Component.literal(entry.toString()).withStyle(entryChatFormatting()));
	}
	
	//============================================================================================
	//******************************************Entries*******************************************
	//============================================================================================
	
	public static Unknown unknown()
	{
		return new MemoryEntry.Unknown("", 0, (byte) 0);
	}
	
	public static class Unknown extends MemoryEntry<Byte>
	{
		public Unknown(CompoundTag tag)
		{
			super(tag);
		}
		
		public Unknown(String name, long timeStamp, Byte entry)
		{
			super(name, timeStamp, Type.UNKNOWN, entry);
		}
		
		public String entryString()
		{
			return "UNKNOWN";
		}
		
		@Override
		protected void saveEntry(CompoundTag tag) {}
		
		@Override
		protected Byte loadEntry(CompoundTag tag)
		{
			return 0;
		}
		
		@Override
		public String toString()
		{
			if(name.isEmpty())
				return "UNKNOWN";
			
			return '[' + name + "] UNKNOWN";
		}
		
		@Override
		public MutableComponent toComponent()
		{
			MutableComponent component = name.isEmpty() ? Component.empty() : Component.literal('[' + name + "] ").withStyle(ChatFormatting.GREEN);
			
			return component.append(Component.translatable("tooltip.sgjourney.unknown").withStyle(entryChatFormatting()));
		}
	}
	
	//============================================================================================
	//********************************************Type********************************************
	//============================================================================================
	
	public static class Type<T extends MemoryEntry<?>>
	{
		private final int id;
		private final Component component;
		private final Function<CompoundTag, T> constructor;
		
		private static final List<Type<?>> MEMORY_TYPES = new ArrayList<>();
		
		public static final Type<Unknown> UNKNOWN = register(Component.translatable("tooltip.sgjourney.unknown").withStyle(ChatFormatting.DARK_RED), Unknown::new);
		
		public static final Type<TextEntry> TEXT = register(Component.translatable("tooltip.sgjourney.text").withStyle(ChatFormatting.GRAY), TextEntry::new);
		public static final Type<AddressEntry> ADDRESS = register(Component.translatable("tooltip.sgjourney.address").withStyle(ChatFormatting.AQUA), AddressEntry::new);
		public static final Type<TransporterIDEntry> TRANSPORTER_ID = register(Component.translatable("tooltip.sgjourney.transporter_id").withStyle(ChatFormatting.DARK_AQUA), TransporterIDEntry::new);
		public static final Type<CoordinateEntry> COORDINATES = register(Component.translatable("tooltip.sgjourney.coordinates").withStyle(ChatFormatting.YELLOW), CoordinateEntry::new);
		
		public static final Type<TransporterConnectionEntry.ID> TRANSPORTER_ID_CONNECTION_RESULT = register(Component.translatable("tooltip.sgjourney.transporter_id_connection_result").withStyle(ChatFormatting.DARK_BLUE), TransporterConnectionEntry.ID::new);
		public static final Type<TransporterConnectionEntry.Coordinates> TRANSPORTER_COORDS_CONNECTION_RESULT = register(Component.translatable("tooltip.sgjourney.transporter_coords_connection_result").withStyle(ChatFormatting.DARK_BLUE), TransporterConnectionEntry.Coordinates::new);
		public static final Type<StargateConnectionEntry> STARGATE_CONNECTION_RESULT = register(Component.translatable("tooltip.sgjourney.stargate_connection_result").withStyle(ChatFormatting.DARK_BLUE), StargateConnectionEntry::new);
		
		private Type(int id, Component component, Function<CompoundTag, T> constructor)
		{
			this.id = id;
			this.component = component;
			this.constructor = constructor;
		}
		
		public int getId()
		{
			return id;
		}
		
		public boolean is(int id)
		{
			return this.id == id;
		}
		
		public Component getComponent()
		{
			return this.component;
		}
		
		public T loadFromTag(CompoundTag tag)
		{
			return constructor.apply(tag);
		}
		
		private static <T extends MemoryEntry<?>> Type<T> register(Component component, Function<CompoundTag, T> function)
		{
			Type<T> type = new Type<>(MEMORY_TYPES.size(), component, function);
			MEMORY_TYPES.add(type);
			return type;
		}
		
		public static Type<?> fromId(int id)
		{
			if(id < 0 || id >= MEMORY_TYPES.size())
				return UNKNOWN;
			
			return MEMORY_TYPES.get(id);
		}
	}
}
