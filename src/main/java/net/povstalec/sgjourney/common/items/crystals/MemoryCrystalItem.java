package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.init.DataComponentInit;
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
		return /*stack.hasTag();*/ false;
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof AbstractTransporterEntity transporter)
		{
			if(transporter.getID() != null)
			{
				saveUUID(player.getItemInHand(InteractionHand.MAIN_HAND), transporter.getID());
				player.displayClientMessage(Component.translatable("message.sgjourney.memory_crystal.saved_id").withStyle(ChatFormatting.BLUE), true);
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
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
		ListTag list = getMemoryList(stack);
		
		for(int i = 0; i < list.size(); i++)
		{
			tooltipComponents.add(Component.literal("[" + i + "] ")
					.append(memoryTypeAt(list, i)));
		}

		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	private Component memoryTypeAt(ListTag list, int index)
	{
		if(list.getCompound(index).contains(ADDRESS, Tag.TAG_INT_ARRAY))
			return Component.translatable("tooltip.sgjourney.address").withStyle(ChatFormatting.AQUA);
		else if(list.getCompound(index).contains(COORDINATES, Tag.TAG_INT_ARRAY))
			return Component.translatable("tooltip.sgjourney.coordinates").withStyle(ChatFormatting.BLUE);
		else if(list.getCompound(index).contains(ID, Tag.TAG_STRING))
			return Component.translatable("tooltip.sgjourney.id").withStyle(ChatFormatting.DARK_AQUA);
		else
			return Component.translatable("tooltip.sgjourney.unknown").withStyle(ChatFormatting.DARK_RED);
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
	public static Address.Immutable getAddress(ListTag list, ItemStack stack, int index)
	{
		if(list.getCompound(index).contains(ADDRESS, Tag.TAG_INT_ARRAY))
			return new Address.Immutable(list.getCompound(index).getIntArray(ADDRESS));
		
		return null;
	}
	
	@Nullable
	public static Address.Immutable getFirstAddress(ListTag list, ItemStack stack)
	{
		for(int i = 0; i < list.size(); i++)
		{
			Address.Immutable address = getAddress(list, stack, i);
			if(address != null)
				return address;
		}
		
		return null;
	}
	
	@Nullable
	public static Address.Immutable getFirstAddress(ItemStack stack)
	{
		return getFirstAddress(getMemoryList(stack), stack);
	}
	
	@Nullable
	public static Vec3i getCoords(ListTag list, ItemStack stack, int index)
	{
		if(list.getCompound(index).contains(COORDINATES, Tag.TAG_INT_ARRAY))
			return Conversion.intArrayToVec(list.getCompound(index).getIntArray(COORDINATES));
		
		return null;
	}
	
	@Nullable
	public static Vec3i getFirstCoords(ListTag list, ItemStack stack)
	{
		for(int i = 0; i < list.size(); i++)
		{
			Vec3i coords = getCoords(list, stack, i);
			if(coords != null)
				return coords;
		}
		
		return null;
	}
	
	@Nullable
	public static Vec3i getFirstCoords(ItemStack stack)
	{
		return getFirstCoords(getMemoryList(stack), stack);
	}
	
	@Nullable
	public static UUID getUUID(ListTag list, ItemStack stack, int index)
	{
		if(list.getCompound(index).contains(ID, Tag.TAG_STRING))
		{
			try { return UUID.fromString(list.getCompound(index).getString(ID)); }
			catch(IllegalArgumentException e) { return null; }
		}
		
		return null;
	}
	
	@Nullable
	public static UUID getFirstUUID(ListTag list, ItemStack stack)
	{
		for(int i = 0; i < list.size(); i++)
		{
			UUID uuid = getUUID(list, stack, i);
			if(uuid != null)
				return uuid;
		}
		
		return null;
	}
	
	@Nullable
	public static UUID getFirstUUID(ItemStack stack)
	{
		return getFirstUUID(getMemoryList(stack), stack);
	}
	
	public static ListTag getMemoryList(ItemStack stack)
	{
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			CompoundTag tag = stack.get(DataComponentInit.CRYSTAL_MEMORY);
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
			stack.set(DataComponentInit.CRYSTAL_MEMORY, tag);
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
