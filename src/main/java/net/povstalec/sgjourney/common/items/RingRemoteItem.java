package net.povstalec.sgjourney.common.items;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
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
			public boolean isValid(int slot, @NotNull ItemStack stack)
			{
				return stack.isEmpty() || stack.is(ItemInit.MEMORY_CRYSTAL.get());
			}
		};
	}
	
	@Override
	protected void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack)
	{
		int index = getIndex(holderStack);
		int memoryListSize = MemoryCrystalItem.getMemoryListSize(getHeldItem(holderStack));
		
		if(memoryListSize >= index)
			setIndex(holderStack, 0);
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
		if(MemoryCrystalItem.getMemoryListSize(getHeldItem(holderStack)) == 0)
			return;
		
		int index = incrementIndex(holderStack);
		
		if(index >= getCrystalMemoryAmount(holderStack))
		{
			index = 0;
			setIndex(holderStack, index);
		}
		
		ListTag list = MemoryCrystalItem.getMemoryList(getHeldItem(holderStack));
		MemoryEntry.Type type = MemoryCrystalItem.memoryTypeAt(list, index);
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			MemoryEntry.TransporterID entry = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			if(entry.name().isEmpty())
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.entry().toString()).withStyle(ChatFormatting.AQUA)), true);
			else
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.name()).withStyle(ChatFormatting.GREEN)), true);
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			MemoryEntry.Coordinates entry = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			
			if(entry.name().isEmpty())
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.entry().toString()).withStyle(ChatFormatting.YELLOW)), true);
			else
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.name()).withStyle(ChatFormatting.GREEN)), true);
		}
		else
			player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.translatable("message.sgjourney.ring_remote.error.invalid_entry")).withStyle(ChatFormatting.DARK_RED), true);
	}
	
	public boolean hasMemoryCrystal(ItemStack stack)
	{
		return !getHeldItem(stack).isEmpty();
	}
	
	protected void handleTransport(Level level, Player player, InteractionHand hand)
	{
		if(level.isClientSide())
			return;
		
		ItemStack stack = player.getItemInHand(hand);
		
		if(!hasMemoryCrystal(stack))
			nearestTransport((ServerLevel) level, player);
		else
		{
			stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
			{
				int index = getIndex(stack);
				
				ItemStack crystalStack = itemHandler.getStackInSlot(0);
				ListTag list = MemoryCrystalItem.getMemoryList(crystalStack);
				MemoryEntry.Type type = MemoryCrystalItem.memoryTypeAt(list, index);
				
				if(type == MemoryEntry.Type.TRANSPORTER_ID)
				{
					MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(crystalStack, MemoryEntry.Type.TRANSPORTER_ID, index);
					if(transporterID != null)
					{
						memoryTransport(level, player, transporterID.entry());
						return;
					}
				}
				else if(type == MemoryEntry.Type.COORDINATES)
				{
					MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(crystalStack, MemoryEntry.Type.COORDINATES, index);
					if(coords != null)
					{
						coordTransport(level, player, coords.entry());
						return;
					}
				}
				
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.invalid_entry").withStyle(ChatFormatting.DARK_RED), true);
		
		
			});
		}
	}
	
	private void nearestTransport(ServerLevel level, Player player)
	{
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInDimension(level, player.blockPosition(), 1024, transporter -> true).iterator(); // TODO Filtering, distance?
		
		if(transporterIterator.hasNext()) // Found Transpoter to start from
		{
			Transporter connectionCandidate = transporterIterator.next();
			
			if(transporterIterator.hasNext()) // Found Transporter to connect to
			{
				if(connectionCandidate.isConnected()) // Other transporter is still connected
					player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.DARK_RED), true);
				else
				{
					TransporterInfo.FeedbackMessage feedback = connectionCandidate.dialTransporter(transporterIterator.next().getID());
					if(feedback.feedback().isError())
						player.displayClientMessage(feedback.getMessageComponent(), true);
				}
			}
			else
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transporters_nearby").withStyle(ChatFormatting.DARK_RED), true);
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.DARK_RED), true);
	}
	
	private void memoryTransport(Level level, Player player, TransporterID transporterID)
	{
		AbstractTransporterEntity<?> transporter = LocatorHelper.getNearestTransporter(level, player.blockPosition(), 16);
		if(transporter != null)
		{
			if(!transporter.canTransport())
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.DARK_RED), true);
			else
			{
				TransporterInfo.FeedbackMessage feedback = transporter.dialTransporter(transporterID);
				if(feedback.feedback().isError())
					player.displayClientMessage(feedback.getMessageComponent(), true);
			}
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.DARK_RED), true);
	}
	
	private void coordTransport(Level level, Player player, Vec3i coords)
	{
		AbstractTransporterEntity<?> transporter = LocatorHelper.getNearestTransporter(level, player.blockPosition(), 16);
		if(transporter != null)
		{
			if(!transporter.canTransport())
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.DARK_RED), true);
			else
			{
				TransporterInfo.FeedbackMessage feedback = transporter.dialTransporter(coords);
				if(feedback.feedback().isError())
					player.displayClientMessage(feedback.getMessageComponent(), true);
			}
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.DARK_RED), true);
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
				tooltipComponents.add(Component.literal(indexPrefix(i, i == indexAt)).withStyle(ChatFormatting.BLUE).append(memoryComponentAt(list, type, i)));
			}
		}

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	private Component memoryComponentAt(ListTag list, MemoryEntry.Type type, int index)
	{
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			if(transporterID != null)
				return transporterID.toComponent();
		}
		else
		{
			MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
				return coords.toComponent();
		}
		
		return Component.translatable("tooltip.sgjourney.invalid_entry").withStyle(ChatFormatting.DARK_RED);
	}
}
