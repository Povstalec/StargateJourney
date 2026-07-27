package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.misc.Conversion;

public class CoordinateEntry extends MemoryEntry<Vec3i> implements ICoordinateEntry
{
	public static final String COORDINATES = "coords";
	
	public CoordinateEntry(CompoundTag tag)
	{
		super(tag);
	}
	
	public CoordinateEntry(String name, long timeStamp, Vec3i entry)
	{
		super(name, timeStamp, Type.COORDINATES, entry);
	}
	
	public Vec3 asVec3()
	{
		return new Vec3(entry.getX(), entry.getY(), entry.getZ());
	}
	
	@Override
	public String entryString()
	{
		return Conversion.vec3iToString(entry);
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
	
	@Override
	public ChatFormatting getChatFormatting()
	{
		return ChatFormatting.YELLOW;
	}
	
	@Override
	public MutableComponent toComponent()
	{
		MutableComponent component = name.isEmpty() ? Component.empty() : Component.literal('[' + name + "] ").withStyle(ChatFormatting.GREEN);
		
		return component.append(Component.literal(Conversion.vec3iToString(entry)).withStyle(getChatFormatting()));
	}
	
	@Override
	public Vec3i getCoordinateEntry()
	{
		return entry;
	}
}
