package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

public class TransporterConnectionEntry
{
	public static final String TRANSPORTER_CONNECTION_RESULT = "transporter_connection_result";
	
	public static class ID extends MemoryEntry<TransporterConnection.IDResult> implements ITransporterIDEntry<TransporterID.Immutable>
	{
		public ID(CompoundTag tag)
		{
			super(tag);
		}
		
		public ID(String name, long timeStamp, TransporterConnection.IDResult entry)
		{
			super(name, timeStamp, Type.TRANSPORTER_ID_CONNECTION_RESULT, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			tag.put(TRANSPORTER_CONNECTION_RESULT, entry.save());
		}
		
		@Override
		protected TransporterConnection.IDResult loadEntry(CompoundTag tag)
		{
			TransporterConnection.IDResult result = new TransporterConnection.IDResult();
			result.load(tag.getCompound(TRANSPORTER_CONNECTION_RESULT));
			return result;
		}
		
		@Override
		public ChatFormatting entryChatFormatting()
		{
			return ChatFormatting.DARK_BLUE;
		}
		
		@Override
		public TransporterID.Immutable getTransporterIDEntry()
		{
			return entry.transporterID();
		}
	}
	
	public static class Coordinates extends MemoryEntry<TransporterConnection.CoordsResult> implements ICoordinateEntry
	{
		public Coordinates(CompoundTag tag)
		{
			super(tag);
		}
		
		public Coordinates(String name, long timeStamp, TransporterConnection.CoordsResult entry)
		{
			super(name, timeStamp, Type.TRANSPORTER_COORDS_CONNECTION_RESULT, entry);
		}
		
		@Override
		protected void saveEntry(CompoundTag tag)
		{
			tag.put(TRANSPORTER_CONNECTION_RESULT, entry.save());
		}
		
		@Override
		protected TransporterConnection.CoordsResult loadEntry(CompoundTag tag)
		{
			TransporterConnection.CoordsResult result = new TransporterConnection.CoordsResult();
			result.load(tag.getCompound(TRANSPORTER_CONNECTION_RESULT));
			return result;
		}
		
		@Override
		public ChatFormatting entryChatFormatting()
		{
			return ChatFormatting.DARK_BLUE;
		}
		
		@Override
		public Vec3i getCoordinateEntry()
		{
			return entry.coords();
		}
	}
}
