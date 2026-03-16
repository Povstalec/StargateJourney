package net.povstalec.sgjourney.common.items;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.Transporting;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

public class RingRemoteItem extends HolderItem
{
	public static final String INDEX = "index";
	
	public RingRemoteItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemInventoryProvider(stack)
		{
			@Override
			public int getNumberOfSlots()
			{
				return 1;
			}

			@Override
			public boolean isValid(int slot, ItemStack stack)
			{
				return stack.is(ItemInit.MEMORY_CRYSTAL.get());
			}
		};
	}
	
	protected int getIndex(ItemStack holderStack)
	{
		if(!holderStack.hasTag())
			return 0;
		
		int index;
		
		CompoundTag tag = holderStack.getTag();
		
		index = tag.getInt(INDEX);
		
		return index;
	}
	
	protected void setIndex(ItemStack holderStack, int index)
	{
		CompoundTag tag = holderStack.getOrCreateTag();
		tag.putInt(INDEX, index);
	}
	
	protected int incrementIndex(ItemStack holderStack)
	{
		int index = getIndex(holderStack);
		index++;
		setIndex(holderStack, index);
		
		return index;
	}
	
	protected int getCrystalMemoryAmount(ItemStack holderStack)
	{
		return MemoryCrystalItem.getMemoryListSize(getHeldItem(holderStack));
	}
	
	protected void handleDestinationSelect(ItemStack holderStack, Player player)
	{
		int index = incrementIndex(holderStack);
		
		if(index >= getCrystalMemoryAmount(holderStack))
		{
			index = 0;
			setIndex(holderStack, index);
		}
		
		player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.index").append(Component.literal(" " + index)).withStyle(ChatFormatting.BLUE), true);
	}
	
	public boolean hasMemoryCrystal(ItemStack stack)
	{
		return !getHeldItem(stack).isEmpty();
	}
	
	protected void handleTransport(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(!hasMemoryCrystal(stack))
		{
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_memory_crystal").withStyle(ChatFormatting.BLUE), true); //TODO Transport to nearest Transport Rings
			return;
		}
		
		if(level.isClientSide())
			return;
		
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			ItemStack crystalStack = itemHandler.getStackInSlot(0);
			MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(crystalStack, MemoryEntry.Type.TRANSPORTER_ID, getIndex(stack));
			if(transporterID == null)
			{
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_coordinates").withStyle(ChatFormatting.BLUE), true);
				return;
			}
			
			memoryTransport(level, player, transporterID.entry());
		});
	}
	
	private void memoryTransport(Level level, Player player, TransporterID transporterID)
	{
		AbstractTransporterEntity transporter = LocatorHelper.getNearestTransporter(level, player.blockPosition(), 16);
		if(transporter != null)
		{
			if(!transporter.canTransport())
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.BLUE), true);
			else
			{
				TransporterInfo.Feedback feedback = transporter.dialTransporter(transporterID);
				if(feedback.isError())
					player.displayClientMessage(feedback.getFeedbackMessage(), true);
			}
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.BLUE), true);
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		if(!level.isClientSide())
		{
			if(player.isShiftKeyDown())
				handleDestinationSelect(stack, player);
			else if(!player.isShiftKeyDown())
				handleTransport(level, player, hand);
		}
		
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
	
	@Nullable
	private static Transporter transporterFromCoords(Level level, Vec3i coords)
	{
		BlockEntity blockEntity = level.getBlockEntity(new BlockPos(coords.getX(), coords.getY(), coords.getZ()));
		if(blockEntity instanceof AbstractTransporterEntity transporter)
			return transporter.getTransporter();
		
		return null;
	}
	
	protected String indexPrefix(int index, boolean isSelected)
	{
		if(isSelected)
			return "-> [" + index + "] ";
		
		return "[" + index + "] ";
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		ItemStack heldItem = getHeldItem(stack);
		int indexAt = getIndex(stack);
		
		MutableComponent itemComponent = Component.translatable("tooltip.sgjourney.holding").append(Component.literal(": "));
		if(heldItem.isEmpty())
			itemComponent.append("[-]");
		else
			itemComponent.append(heldItem.getDisplayName());
		tooltipComponents.add(itemComponent);
		
		if(!heldItem.isEmpty())
		{
			ListTag list = MemoryCrystalItem.getMemoryList(heldItem);
			for(int i = 0; i < list.size(); i++)
			{
				MemoryEntry.Type type = MemoryCrystalItem.memoryTypeAt(list, i);
				if(type == MemoryEntry.Type.TRANSPORTER_ID || type == MemoryEntry.Type.COORDINATES)
					tooltipComponents.add(Component.literal(indexPrefix(i, i == indexAt)).withStyle(ChatFormatting.BLUE).append(memoryTypeAt(list, type, i)));
			}
		}

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	private Component memoryTypeAt(ListTag list, MemoryEntry.Type type, int index)
	{
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			if(transporterID != null)
				return Component.literal(transporterID.toString()).withStyle(ChatFormatting.DARK_AQUA);
		}
		else
		{
			MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
				return Component.literal(" " + coords.entry().toShortString()).withStyle(ChatFormatting.BLUE);
		}
		
		return Component.empty();
	}
}
