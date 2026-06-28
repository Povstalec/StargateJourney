package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

public class StargateConnectionEntry extends MemoryEntry<StargateConnection.Result> implements IAddressEntry<Address.Immutable>
{
	public static final String STARGATE_CONNECTION_RESULT = "stargate_connection_result";
	
	public StargateConnectionEntry(CompoundTag tag)
	{
		super(tag);
	}
	
	public StargateConnectionEntry(String name, long timeStamp, Type<?> entryType, StargateConnection.Result entry)
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
	
	@Override
	public ChatFormatting entryChatFormatting()
	{
		return ChatFormatting.DARK_BLUE;
	}
	
	@Override
	public Address.Immutable getAddressEntry()
	{
		return entry.address();
	}
}
