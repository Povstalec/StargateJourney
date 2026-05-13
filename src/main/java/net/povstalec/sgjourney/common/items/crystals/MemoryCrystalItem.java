package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class MemoryCrystalItem extends AbstractCrystalItem
{
	public static final int BAR_COLOR_RGB = 0x0095ff;
	
	public static final String MEMORY_LIST = "memory_list";

	public MemoryCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getMemoryListSize(stack) > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.floor(13.0F * (float) getMemoryListSize(stack) / getMemoryCapacity());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return BAR_COLOR_RGB;
	}

	public int getMemoryCapacity()
	{
		return CommonCrystalConfig.memory_crystal_capacity.get();
	}

	/*@Override
	public Optional<Component> descriptionInDHD()
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.memory.basic").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public Optional<Component> descriptionInRing()
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_ring.memory.basic").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}*/

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		ListTag list = getMemoryList(stack);
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.memory_capacity").append(Component.literal(": " + list.size() + '/' + getMemoryCapacity())).withStyle(ChatFormatting.BLUE));
		
		for(int i = 0; i < list.size(); i++)
		{
			tooltipComponents.add(Component.literal("[" + i + "] ").withStyle(ChatFormatting.BLUE).append(memoryTypeComponentAt(list, i)));
		}

		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static MemoryEntry.Type memoryTypeAt(ListTag list, int index)
	{
		CompoundTag tag = list.getCompound(index);
		int ordinal = tag.contains(MemoryEntry.ENTRY_TYPE, Tag.TAG_INT) ? tag.getInt(MemoryEntry.ENTRY_TYPE) : 0;
		return MemoryEntry.Type.fromOrdinal(ordinal);
	}
	
	public static Component memoryTypeComponentAt(ListTag list, int index)
	{
		return memoryTypeAt(list, index).getComponent();
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public static int getMemoryListSize(ItemStack stack)
	{
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			CompoundTag tag = stack.getTag();
			if(tag != null && tag.contains(MEMORY_LIST, Tag.TAG_LIST))
				return tag.getList(MEMORY_LIST, Tag.TAG_COMPOUND).size();
		}
		
		return 0;
	}
	
	public static int countMemoryEntriesOfType(ItemStack stack, MemoryEntry.Type... entryTypes)
	{
		if(entryTypes.length == 0)
			return getMemoryListSize(stack);
		
		ListTag list = getMemoryList(stack);
		
		int count = 0;
		for(int i = 0; i < list.size(); i++)
		{
			for(MemoryEntry.Type entryType : entryTypes)
			{
				if(loadMemoryEntry(list, entryType, i) != null)
				{
					count++;
					break;
				}
			}
		}
		
		return count;
	}
	
	public static ListTag getMemoryList(ItemStack stack)
	{
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			CompoundTag tag = stack.getTag();
			if(tag != null && tag.contains(MEMORY_LIST, Tag.TAG_LIST))
				return tag.getList(MEMORY_LIST, Tag.TAG_COMPOUND);
		}
		
		return new ListTag();
	}
	
	private static void setMemoryList(ItemStack stack, ListTag list)
	{
		if(stack.getItem() instanceof MemoryCrystalItem && list != null)
		{
			CompoundTag tag = new CompoundTag();
			tag.put(MEMORY_LIST, list);
			stack.setTag(tag);
		}
	}
	
	public boolean saveMemoryEntry(ItemStack stack, MemoryEntry<?> memoryEntry, boolean overrideOldMemory)
	{
		ListTag list = getMemoryList(stack);
		
		if(list.size() < getMemoryCapacity())
		{
			ListTag newList = new ListTag();
			newList.add(memoryEntry.save());
			newList.addAll(list);
			setMemoryList(stack, newList);
			return true;
		}
		
		if(!overrideOldMemory)
			return false;
		
		ListTag newList = new ListTag();
		newList.add(memoryEntry.save());
		newList.addAll(list);
		newList.remove(newList.size() - 1);
		setMemoryList(stack, newList);
		
		return true;
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadMemoryEntry(ListTag list, MemoryEntry.Type entryType, int index)
	{
		CompoundTag tag = list.getCompound(index);
		if(tag.contains(MemoryEntry.ENTRY_TYPE, Tag.TAG_INT) && tag.getInt(MemoryEntry.ENTRY_TYPE) == entryType.ordinal())
			return (T) entryType.loadFromTag(tag);
		
		return null;
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadMemoryEntry(ItemStack stack, MemoryEntry.Type entryType, int index)
	{
		return loadMemoryEntry(getMemoryList(stack), entryType, index);
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadFirstMemoryEntry(ListTag list, MemoryEntry.Type entryType)
	{
		for(int i = 0; i < list.size(); i++)
		{
			T memoryEntry = loadMemoryEntry(list, entryType, i);
			if(memoryEntry != null)
				return memoryEntry;
		}
		
		return null;
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadFirstMemoryEntry(ItemStack stack, MemoryEntry.Type entryType)
	{
		return loadFirstMemoryEntry(getMemoryList(stack), entryType);
	}
	
	//============================================================================================
	//************************************Specific Entry Types************************************
	//============================================================================================
	
	@Nullable
	public static String loadText(ListTag list, int index)
	{
		MemoryEntry.Text text = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TEXT, index);
		return text != null ? text.entry() : null;
	}
	
	@Nullable
	public String loadText(ItemStack stack, int index)
	{
		MemoryEntry.Text text = MemoryCrystalItem.loadMemoryEntry(stack, MemoryEntry.Type.TEXT, index);
		return text != null ? text.entry() : null;
	}
	
	@Nullable
	public static Address loadAddress(ListTag list, int index)
	{
		MemoryEntry.Address address = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.ADDRESS, index);
		return address != null ? address.entry() : null;
	}
	
	@Nullable
	public Address loadAddress(ItemStack stack, int index)
	{
		MemoryEntry.Address address = MemoryCrystalItem.loadMemoryEntry(stack, MemoryEntry.Type.ADDRESS, index);
		return address != null ? address.entry() : null;
	}
	
	@Nullable
	public static TransporterID loadTransporterID(ListTag list, int index)
	{
		MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
		return transporterID != null ? transporterID.entry() : null;
	}
	
	@Nullable
	public TransporterID loadTransporterID(ItemStack stack, int index)
	{
		MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(stack, MemoryEntry.Type.TRANSPORTER_ID, index);
		return transporterID != null ? transporterID.entry() : null;
	}
	
	@Nullable
	public static Vec3i loadCoordinates(ListTag list, int index)
	{
		MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
		return coords != null ? coords.entry() : null;
	}
	
	@Nullable
	public Vec3i loadCoordinates(ItemStack stack, int index)
	{
		MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(stack, MemoryEntry.Type.COORDINATES, index);
		return coords != null ? coords.entry() : null;
	}
	
	

	public static class Advanced extends MemoryCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}

		@Override
		public int getMemoryCapacity()
		{
			return CommonCrystalConfig.advanced_memory_crystal_capacity.get();
		}

		/*@Override
		public Optional<Component> descriptionInDHD()
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.memory.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}

		@Override
		public Optional<Component> descriptionInRing()
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_ring.memory.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}*/

		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
