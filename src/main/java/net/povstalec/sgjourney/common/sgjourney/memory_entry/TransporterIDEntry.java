package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

public class TransporterIDEntry extends MemoryEntry<TransporterID.Immutable> implements ITransporterIDEntry<TransporterID.Immutable>
{
	public static final String TRANSPORTER_ID = TransporterID.TRANSPORTER_ID;
	
	public TransporterIDEntry(CompoundTag tag)
	{
		super(tag);
	}
	
	public TransporterIDEntry(String name, long timeStamp, TransporterID.Immutable entry)
	{
		super(name, timeStamp, Type.TRANSPORTER_ID, entry);
	}
	
	@Override
	protected void saveEntry(CompoundTag tag)
	{
		entry.saveToCompoundTag(tag, TRANSPORTER_ID);
	}
	
	@Override
	protected TransporterID.Immutable loadEntry(CompoundTag tag)
	{
		return new TransporterID.Immutable(tag.getIntArray(TRANSPORTER_ID));
	}
	
	@Override
	public ChatFormatting getChatFormatting()
	{
		return entry.getChatFormatting();
	}
	
	@Override
	public TransporterID.Immutable getTransporterIDEntry()
	{
		return entry;
	}
}
