package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.Addressing;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryCrystalItem extends Item
{
	private static final String MEMORY_TYPE = "MemoryType";
	private static final String MEMORY_LIST = "MemoryList";

	private static final String ID = "ID";
	private static final String COORDINATES = "Coordinates";
	private static final String ADDRESS = "Address";
	
	private int memoryCapacity;
	
	public MemoryCrystalItem(Properties properties, int memoryCapacity)
	{
		super(properties);
		this.memoryCapacity = memoryCapacity;
	}
	
	public enum MemoryType
	{
		ID,
		COORDINATES,
		ADDRESS
	}
	
	/*public static ItemStack atlantisAddress()
	{
		ItemStack stack = new ItemStack(ItemInit.MEMORY_CRYSTAL.get());

		saveAddress(stack, new int[] {18, 20, 1, 15, 14, 7, 19});
		
		return stack;
	}
	
	public static ItemStack abydosAddress()
	{
		ItemStack stack = new ItemStack(ItemInit.MEMORY_CRYSTAL.get());

		saveAddress(stack, new int[] {26, 6, 14, 31, 11, 29});
		
		return stack;
	}*/
	
	public static ListTag getMemoryList(ItemStack stack)
	{
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			CompoundTag tag = stack.getOrCreateTag();
			
			if(tag.contains(MEMORY_LIST, Tag.TAG_LIST))
				return tag.getList(MEMORY_LIST, Tag.TAG_COMPOUND);
		}
		
		return new ListTag();
	}
	
	public static int getMemoryListSize(ItemStack stack)
	{
		return getMemoryList(stack).size();
	}
	
	public static CompoundTag getMemory(ItemStack stack, int memory)
	{
		ListTag memoryList = getMemoryList(stack);
		return memoryList.getCompound(memory);
	}
	
	public static boolean saveMemory(ItemStack stack, CompoundTag memory)
	{
		if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
		{
			if(getMemoryListSize(stack) >= memoryCrystal.memoryCapacity)
			{
				StargateJourney.LOGGER.info("Memory at maximum capacity");
				return false;
			}
			
			CompoundTag tag = stack.getOrCreateTag();
			ListTag memoryList = getMemoryList(stack);
			
			memoryList.add(memory);
			tag.put(MEMORY_LIST, memoryList);
			stack.setTag(tag);

			StargateJourney.LOGGER.info("Saved Memory");
			return true;
		}
		
		StargateJourney.LOGGER.info("Failed to save Memory");
		return false;
	}
	
	public static int[] getCoordinatesAt(ItemStack stack, int memory)
	{
		CompoundTag tag = getMemory(stack, memory);
		
		if(tag.contains(MEMORY_TYPE, Tag.TAG_STRING))
		{
			String memoryType = tag.getString(MEMORY_TYPE);
			
			if(MemoryType.valueOf(memoryType) == MemoryType.COORDINATES)
			{
				int[] coordinates = tag.getIntArray(COORDINATES);
				StargateJourney.LOGGER.info("Found Coordinates at Memory Slot " + memory);
				return coordinates;
			}
		}
		
		return new int[0];
	}
	
	public static boolean saveCoordinates(ItemStack stack, int[] coordinates)
	{
		CompoundTag memory = new CompoundTag();
		
		memory.putString(MEMORY_TYPE, MemoryType.COORDINATES.toString().toUpperCase());
		memory.putIntArray(COORDINATES, coordinates);
		
		return saveMemory(stack, memory);
	}
	
	public static int[] getAddressAt(ItemStack stack, int memory)
	{
		CompoundTag tag = getMemory(stack, memory);
		
		if(tag.contains(MEMORY_TYPE, Tag.TAG_STRING))
		{
			String memoryType = tag.getString(MEMORY_TYPE);
			
			if(MemoryType.valueOf(memoryType) == MemoryType.ADDRESS)
			{
				int[] coordinates = tag.getIntArray(ADDRESS);
				StargateJourney.LOGGER.info("Found Address at Memory Slot " + memory);
				return coordinates;
			}
		}
		
		return new int[0];
	}
	
	public static boolean saveAddress(ItemStack stack, int[] address)
	{
		CompoundTag memory = new CompoundTag();
		
		memory.putString(MEMORY_TYPE, MemoryType.ADDRESS.toString().toUpperCase());
		memory.putIntArray(ADDRESS, address);
		
		return saveMemory(stack, memory);
	}

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return stack.hasTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        for(int i = 0; i < getMemoryListSize(stack); i++)
        {
        	int[] address = getAddressAt(stack, i);
        	
        	switch(address.length)
        	{
        	case 6:
        		tooltipComponents.add(Component.literal(Addressing.addressIntArrayToString(address)).withStyle(ChatFormatting.GOLD));
        		break;
        	case 7:
        		tooltipComponents.add(Component.literal(Addressing.addressIntArrayToString(address)).withStyle(ChatFormatting.LIGHT_PURPLE));
        		break;
        	case 8:
        		tooltipComponents.add(Component.literal(Addressing.addressIntArrayToString(address)).withStyle(ChatFormatting.AQUA));
        		break;
        	default:
        		break;
        	}
        		
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
