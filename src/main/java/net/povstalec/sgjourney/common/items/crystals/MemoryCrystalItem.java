package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.*;
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
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.MEMORY;
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

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		ListTag list = getMemoryList(stack);
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.memory_capacity").append(Component.literal(": " + list.size() + '/' + getMemoryCapacity())).withStyle(ChatFormatting.BLUE));
		
		for(int i = 0; i < list.size() && i < 10; i++)
		{
			tooltipComponents.add(Component.literal("[" + i + "] ").withStyle(ChatFormatting.BLUE).append(memoryTypeComponentAt(list, i)));
		}
		if(list.size() >= 10)
			tooltipComponents.add(Component.literal("...").withStyle(ChatFormatting.BLUE));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.memory_crystal.description"));
	}
	
	public static MemoryEntry.Type<?> memoryTypeAt(ListTag list, int index)
	{
		CompoundTag tag = list.getCompound(index);
		int id = tag.contains(MemoryEntry.ENTRY_TYPE, Tag.TAG_INT) ? tag.getInt(MemoryEntry.ENTRY_TYPE) : 0;
		return MemoryEntry.Type.fromId(id);
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
	
	public static boolean isMemoryEntryType(ListTag list, MemoryEntry.Type<?> entryType, int index)
	{
		CompoundTag tag = list.getCompound(index);
		return entryType.is(tag.getInt(MemoryEntry.ENTRY_TYPE));
	}
	
	public static boolean isMemoryEntryType(ItemStack stack, MemoryEntry.Type<?> entryType, int index)
	{
		return isMemoryEntryType(getMemoryList(stack), entryType, index);
	}
	
	public static int countMemoryEntriesOfType(ItemStack stack, MemoryEntry.Type<?>... entryTypes)
	{
		if(entryTypes.length == 0)
			return getMemoryListSize(stack);
		
		ListTag list = getMemoryList(stack);
		
		int count = 0;
		for(int i = 0; i < list.size(); i++)
		{
			for(MemoryEntry.Type<?> entryType : entryTypes)
			{
				if(isMemoryEntryType(list, entryType, i))
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
	
	/**
	 * Saves a CompoundTag to the Memory Crystal
	 * @param stack Memory Crystal ItemStack
	 * @param savedTag CompoundTag to be saved
	 * @param overrideOldMemory Whether old entries get pushed out when the capacity reaches its limit
	 * @return Tag containing oldest saved Memory Entry in the Crystal that got pushed out, or null if the Memory Crystal still has space
	 */
	public CompoundTag saveCompound(ItemStack stack, CompoundTag savedTag, boolean overrideOldMemory)
	{
		ListTag list = getMemoryList(stack);
		
		if(list.size() < getMemoryCapacity())
		{
			ListTag newList = new ListTag();
			newList.add(savedTag);
			newList.addAll(list);
			setMemoryList(stack, newList);
			return null;
		}
		
		if(!overrideOldMemory)
			return null;
		
		ListTag newList = new ListTag();
		newList.add(savedTag);
		newList.addAll(list);
		Tag tag = newList.remove(newList.size() - 1);
		setMemoryList(stack, newList);
		
		if(tag.getId() == Tag.TAG_COMPOUND)
			return (CompoundTag) tag;
		
		return null;
	}
	
	/**
	 * Saves a Memory Entry to the Memory Crystal
	 * @param stack Memory Crystal ItemStack
	 * @param memoryEntry Memory Entry to be saved
	 * @param overrideOldMemory Whether old entries get pushed out when the capacity reaches its limit
	 * @return Tag containing oldest saved Memory Entry in the Crystal that got pushed out, or null if the Memory Crystal still has space
	 */
	public CompoundTag saveMemoryEntry(ItemStack stack, MemoryEntry<?> memoryEntry, boolean overrideOldMemory)
	{
		return saveCompound(stack, memoryEntry.save(), overrideOldMemory);
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadMemoryEntry(ListTag list, MemoryEntry.Type<T> entryType, int index)
	{
		CompoundTag tag = list.getCompound(index);
		if(tag.contains(MemoryEntry.ENTRY_TYPE, Tag.TAG_INT) && entryType.is(tag.getInt(MemoryEntry.ENTRY_TYPE)))
			return entryType.loadFromTag(tag);
		
		return null;
	}
	
	@Nullable
	public static <T extends MemoryEntry<?>> T loadMemoryEntry(ItemStack stack, MemoryEntry.Type<T> entryType, int index)
	{
		return loadMemoryEntry(getMemoryList(stack), entryType, index);
	}
	
	/*@Nullable
	public static <T extends MemoryEntry<?>> T loadFirstMemoryEntry(ListTag list, MemoryEntry.Type<T> entryType)
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
	public static <T extends MemoryEntry<?>> T loadFirstMemoryEntry(ItemStack stack, MemoryEntry.Type<T> entryType)
	{
		return loadFirstMemoryEntry(getMemoryList(stack), entryType);
	}*/
	
	public static <T extends MemoryEntry<?>> void memoryEntryRun(ListTag list, MemoryEntry.Type<T> entryType, int index, Consumer<T> consumer)
	{
		T entry = loadMemoryEntry(list, entryType, index);
		if(entry != null)
			consumer.accept(entry);
	}
	
	public static <T extends MemoryEntry<?>> void memoryEntryRun(ItemStack stack, MemoryEntry.Type<T> entryType, int index, Consumer<T> consumer)
	{
		memoryEntryRun(getMemoryList(stack), entryType, index, consumer);
	}
	
	public static <T extends MemoryEntry<?>, R> R memoryEntryReturn(ListTag list, MemoryEntry.Type<T> entryType, int index, Function<T, R> function, R defaultValue)
	{
		T entry = loadMemoryEntry(list, entryType, index);
		if(entry != null)
			return function.apply(entry);
		
		return defaultValue;
	}
	
	public static <T extends MemoryEntry<?>, R> R memoryEntryReturn(ItemStack stack, MemoryEntry.Type<T> entryType, int index, Function<T, R> function, R defaultValue)
	{
		return memoryEntryReturn(getMemoryList(stack), entryType, index, function, defaultValue);
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

		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
