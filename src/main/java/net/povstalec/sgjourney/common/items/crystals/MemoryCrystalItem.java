package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.Vec3i;
import net.povstalec.sgjourney.common.misc.Conversion;
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
	public static final int DEFAULT_MEMORY_CAPACITY = 5;
	public static final int ADVANCED_MEMORY_CAPACITY = 2 * DEFAULT_MEMORY_CAPACITY;
	
	public static final String MEMORY_LIST = "memory_list";

	public static final String ID = "id";
	public static final String COORDINATES = "coords";
	public static final String ADDRESS = "address";
	
	enum MemoryType
	{
		UNKNOWN(Component.translatable("tooltip.sgjourney.unknown").withStyle(ChatFormatting.DARK_RED)),
		ADDRESS(Component.translatable("tooltip.sgjourney.address").withStyle(ChatFormatting.AQUA)),
		ID(Component.translatable("tooltip.sgjourney.id").withStyle(ChatFormatting.DARK_AQUA)),
		COORDINATES(Component.translatable("tooltip.sgjourney.coordinates").withStyle(ChatFormatting.BLUE));
		
		private final Component component;
		
		MemoryType(Component component)
		{
			this.component = component;
		}
		
		public Component getComponent()
		{
			return this.component;
		}
	}

	public MemoryCrystalItem(Properties properties)
	{
		super(properties);
	}

	public int getMemoryCapacity()
	{
		return DEFAULT_MEMORY_CAPACITY;
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return stack.hasTag();
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
		
		for(int i = 0; i < list.size(); i++)
		{
			tooltipComponents.add(Component.literal("[" + i + "] ")
					.append(memoryTypeComponentAt(list, i)));
		}

		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public MemoryType memoryTypeAt(ListTag list, int index)
	{
		if(list.getCompound(index).contains(ADDRESS, Tag.TAG_INT_ARRAY))
			return MemoryType.ADDRESS;
		else if(list.getCompound(index).contains(COORDINATES, Tag.TAG_INT_ARRAY))
			return MemoryType.COORDINATES;
		else if(list.getCompound(index).contains(ID, Tag.TAG_STRING))
			return MemoryType.ID;
		else
			return MemoryType.UNKNOWN;
	}
	
	public Component memoryTypeComponentAt(ListTag list, int index)
	{
		return memoryTypeAt(list, index).getComponent();
	}
	
	public String memoryStringAt(ListTag list, int index)
	{
		MemoryType memoryType = memoryTypeAt(list, index);
		
		return switch(memoryType)
		{
			case ADDRESS -> getAddress(list, index).toString();
			case COORDINATES -> getCoords(list, index).toString();
			case ID -> getUUID(list, index).toString();
			default -> "-";
		};
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private boolean saveMemory(ItemStack stack, CompoundTag memory)
	{
		ListTag list = getMemoryList(stack);
		
		if(list.size() >= getMemoryCapacity()) //TODO Move old entries forward if needed
			return false;
		
		list.add(memory);
		setMemoryList(stack, list);
		
		return true;
	}
	
	public boolean saveAddress(ItemStack stack, Address.Immutable address)
	{
		CompoundTag addressTag = new CompoundTag();
		addressTag.putIntArray(ADDRESS, address.toArray());
		
		return saveMemory(stack, addressTag);
	}
	
	public boolean saveCoords(ItemStack stack, Vec3i coords)
	{
		CompoundTag coordsTag = new CompoundTag();
		coordsTag.putIntArray(COORDINATES, Conversion.vecToIntArray(coords));
		
		return saveMemory(stack, coordsTag);
	}
	
	public boolean saveUUID(ItemStack stack, UUID uuid)
	{
		CompoundTag uuidTag = new CompoundTag();
		uuidTag.putString(ID, uuid.toString());
		
		return saveMemory(stack, uuidTag);
	}
	
	@Nullable
	public static Address.Immutable getAddress(ListTag list, int index)
	{
		if(list.getCompound(index).contains(ADDRESS, Tag.TAG_INT_ARRAY))
			return new Address.Immutable(list.getCompound(index).getIntArray(ADDRESS));
		
		return null;
	}
	
	@Nullable
	public static Address.Immutable getFirstAddress(ListTag list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			Address.Immutable address = getAddress(list, i);
			if(address != null)
				return address;
		}
		
		return null;
	}
	
	@Nullable
	public static Address.Immutable getFirstAddress(ItemStack stack)
	{
		return getFirstAddress(getMemoryList(stack));
	}
	
	@Nullable
	public static Vec3i getCoords(ListTag list, int index)
	{
		if(list.getCompound(index).contains(COORDINATES, Tag.TAG_INT_ARRAY))
			return Conversion.intArrayToVec(list.getCompound(index).getIntArray(COORDINATES));
		
		return null;
	}
	
	@Nullable
	public static Vec3i getFirstCoords(ListTag list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			Vec3i coords = getCoords(list, i);
			if(coords != null)
				return coords;
		}
		
		return null;
	}
	
	@Nullable
	public static Vec3i getFirstCoords(ItemStack stack)
	{
		return getFirstCoords(getMemoryList(stack));
	}
	
	@Nullable
	public static UUID getUUID(ListTag list, int index)
	{
		if(list.getCompound(index).contains(ID, Tag.TAG_STRING))
		{
			try { return UUID.fromString(list.getCompound(index).getString(ID)); }
			catch(IllegalArgumentException e) { return null; }
		}
		
		return null;
	}
	
	@Nullable
	public static UUID getFirstUUID(ListTag list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			UUID uuid = getUUID(list, i);
			if(uuid != null)
				return uuid;
		}
		
		return null;
	}
	
	@Nullable
	public static UUID getFirstUUID(ItemStack stack)
	{
		return getFirstUUID(getMemoryList(stack));
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
	
	

	public static class Advanced extends MemoryCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}

		@Override
		public int getMemoryCapacity()
		{
			return ADVANCED_MEMORY_CAPACITY;
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
