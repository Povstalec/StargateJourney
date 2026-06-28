package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.nbt.CompoundTag;

public class TextEntry extends MemoryEntry<String> //TODO Maybe make a Component Entry
{
	public static final String TEXT = "text";
	
	public TextEntry(CompoundTag tag)
	{
		super(tag);
	}
	
	public TextEntry(String name, long timeStamp, Type<?> entryType, String entry)
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
