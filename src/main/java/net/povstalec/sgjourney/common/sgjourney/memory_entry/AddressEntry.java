package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class AddressEntry extends MemoryEntry<Address.Immutable> implements IAddressEntry<Address.Immutable>
{
	public static final String ADDRESS = Address.ADDRESS;
	
	public AddressEntry(CompoundTag tag)
	{
		super(tag);
	}
	
	public AddressEntry(String name, long timeStamp, Address.Immutable entry)
	{
		super(name, timeStamp, Type.ADDRESS, entry);
	}
	
	@Override
	protected void saveEntry(CompoundTag tag)
	{
		entry.saveToCompoundTag(tag, ADDRESS);
	}
	
	@Override
	protected Address.Immutable loadEntry(CompoundTag tag)
	{
		return new Address.Immutable(tag.getIntArray(ADDRESS));
	}
	
	@Override
	public ChatFormatting getChatFormatting()
	{
		return entry.getChatFormatting();
	}
	
	@Override
	public Address.Immutable getAddressEntry()
	{
		return entry;
	}
}
