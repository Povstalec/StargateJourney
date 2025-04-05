package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class MemoryCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_MEMORY_CAPACITY = 4;
	public static final int ADVANCED_MEMORY_CAPACITY = 6;
	
	public static final Codec CRYSTAL_MODE_CODEC = StringRepresentable.fromValues(() -> new MemoryType[]{MemoryType.ID, MemoryType.COORDINATES, MemoryType.ADDRESS});

	//private static final String MEMORY_TYPE = "MemoryType";
	private static final String MEMORY_LIST = "MemoryList";

	private static final String ID = "ID";
	private static final String COORDINATES = "Coordinates";
	private static final String ADDRESS = "Address";

	public MemoryCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum MemoryType implements StringRepresentable
	{
		ID("id"),
		COORDINATES("coordinates"),
		ADDRESS("address");
		
		private final String name;
		
		private MemoryType(String name)
		{
			this.name = name;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
		}
	}

	public int getMemoryCapacity()
	{
		return DEFAULT_MEMORY_CAPACITY;
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
	}

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
			if(getMemoryListSize(stack) >= memoryCrystal.getMemoryCapacity())
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

		StargateJourney.LOGGER.error("Failed to save Memory");
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

		Address address = new Address();
		if(tag.contains(MEMORY_TYPE, Tag.TAG_STRING))
		{
			String memoryType = tag.getString(MEMORY_TYPE);

			if(MemoryType.valueOf(memoryType) == MemoryType.ADDRESS)
			{
				int[] coordinates = tag.getIntArray(ADDRESS);
				StargateJourney.LOGGER.info("Found Address at Memory Slot " + memory);
				address.fromArray(coordinates);
			}
		}

		return address.toArray();
	}

	public static boolean saveAddress(ItemStack stack, int[] address)
	{
		CompoundTag memory = new CompoundTag();

		memory.putString(MEMORY_TYPE, MemoryType.ADDRESS.toString().toUpperCase());
		memory.putIntArray(ADDRESS, address);

		return saveMemory(stack, memory);
	}*/

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return /*stack.hasTag();*/ false;
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
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		/*for(int i = 0; i < getMemoryListSize(stack); i++)
		{
			Address address = new Address(getAddressAt(stack, i));

			switch(address.getLength())
			{
				case 6:
					tooltipComponents.add(Component.literal(address.toString()).withStyle(ChatFormatting.GOLD));
					break;
				case 7:
					tooltipComponents.add(Component.literal(address.toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
					break;
				case 8:
					tooltipComponents.add(Component.literal(address.toString()).withStyle(ChatFormatting.AQUA));
					break;
				default:
					break;
			}

		}*/

		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
			return DEFAULT_MEMORY_CAPACITY;
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
