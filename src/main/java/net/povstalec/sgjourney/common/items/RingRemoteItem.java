package net.povstalec.sgjourney.common.items;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.CoordinateEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterIDEntry;
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
				return stack.isEmpty() || stack.getItem() instanceof MemoryCrystalItem;
			}
		};
	}
	
	@Override
	public void onSwapped(ItemStack ringRemoteStack, ItemStack insertedStack, ItemStack removedStack)
	{
		int index = getIndex(ringRemoteStack);
		int memoryListSize = MemoryCrystalItem.getMemoryListSize(getHeldItem(ringRemoteStack));
		
		if(memoryListSize >= index)
			setIndex(ringRemoteStack, 0);
	}
	
	protected int getIndex(ItemStack ringRemoteStack)
	{
		if(!ringRemoteStack.hasTag())
			return 0;
		
		int index;
		
		CompoundTag tag = ringRemoteStack.getTag();
		
		index = tag.getInt(INDEX);
		
		return index;
	}
	
	protected void setIndex(ItemStack ringRemoteStack, int index)
	{
		CompoundTag tag = ringRemoteStack.getOrCreateTag();
		tag.putInt(INDEX, index);
	}
	
	protected int incrementIndex(ItemStack ringRemoteStack)
	{
		int index = getIndex(ringRemoteStack);
		index++;
		setIndex(ringRemoteStack, index);
		
		return index;
	}
	
	protected int getTotalIndexCount(ItemStack ringRemoteStack)
	{
		ItemStack heldItem = getHeldItem(ringRemoteStack);
		CrystalCache.Type type = getCrystalType(heldItem);
		
		if(type != null)
		{
			return switch(type)
			{
				case MEMORY -> MemoryCrystalItem.getMemoryListSize(getHeldItem(ringRemoteStack));
				case COMMUNICATION, MATERIALIZATION -> 5;
				default -> 6;
			};
		}
		
		return 6;
	}
	
	protected void handleDestinationSelect(ItemStack ringRemoteStack, Player player)
	{
		if(MemoryCrystalItem.getMemoryListSize(getHeldItem(ringRemoteStack)) == 0)
			return;
		
		int index = incrementIndex(ringRemoteStack);
		
		if(index >= getTotalIndexCount(ringRemoteStack))
		{
			index = 0;
			setIndex(ringRemoteStack, index);
		}
		
		ListTag list = MemoryCrystalItem.getMemoryList(getHeldItem(ringRemoteStack));
		MemoryEntry.Type<?> type = MemoryCrystalItem.memoryTypeAt(list, index);
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			TransporterIDEntry entry = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			if(entry.name().isEmpty())
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.entryString()).withStyle(ChatFormatting.AQUA)), true);
			else
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.name()).withStyle(ChatFormatting.GREEN)), true);
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			CoordinateEntry entry = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			
			if(entry.name().isEmpty())
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.entryString()).withStyle(ChatFormatting.YELLOW)), true);
			else
				player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.literal(entry.name()).withStyle(ChatFormatting.GREEN)), true);
		}
		else
			player.displayClientMessage(Component.literal("[" + index + "] ").withStyle(ChatFormatting.BLUE).append(Component.translatable("message.sgjourney.ring_remote.error.invalid_entry")).withStyle(ChatFormatting.DARK_RED), true);
	}
	
	// Memory Crystal
	
	protected void handleMemoryTransport(Player player, ItemStack ringRemoteStack, ItemStack crystalStack, AbstractTransporterEntity<?> connectedTransporter)
	{
		int index = getIndex(ringRemoteStack);
		
		ListTag list = MemoryCrystalItem.getMemoryList(crystalStack);
		MemoryEntry.Type<?> type = MemoryCrystalItem.memoryTypeAt(list, index);
		
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			TransporterIDEntry transporterID = MemoryCrystalItem.loadMemoryEntry(crystalStack, MemoryEntry.Type.TRANSPORTER_ID, index);
			if(transporterID != null)
			{
				idTransport(player, transporterID.entry(), connectedTransporter);
				return;
			}
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			CoordinateEntry coords = MemoryCrystalItem.loadMemoryEntry(crystalStack, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
			{
				coordTransport(player, coords.entry(), connectedTransporter);
				return;
			}
		}
		
		player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.invalid_entry").withStyle(ChatFormatting.DARK_RED), true);
	}
	
	private void idTransport(Player player, TransporterID transporterID, AbstractTransporterEntity<?> connectedTransporter)
	{
		TransporterInfo.FeedbackMessage feedback = connectedTransporter.dialTransporter(transporterID);
		if(feedback.feedback().isError())
			player.displayClientMessage(feedback.getMessageComponent(), true);
	}
	
	private void coordTransport(Player player, Vec3i coords, AbstractTransporterEntity<?> connectedTransporter)
	{
		TransporterInfo.FeedbackMessage feedback = connectedTransporter.dialTransporter(coords);
		if(feedback.feedback().isError())
			player.displayClientMessage(feedback.getMessageComponent(), true);
	}
	
	// Communication Crystal
	
	protected void handleNetworkTransport(ServerLevel level, Player player, ItemStack ringRemoteStack, ItemStack crystalStack, AbstractTransporterEntity<?> connectedTransporter)
	{
		if(!CommunicationCrystalItem.hasFrequency(crystalStack))
		{
			//TODO Message that there is no frequency
			return;
		}
		
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInDimension(level, connectedTransporter.getBlockPos(), connectedTransporter.maxTransportRange(), transporter ->
				!connectedTransporter.getID().equals(transporter.getID()) &&
						transporter.getNetworks().contains(CommunicationCrystalItem.getFrequency(crystalStack))
		).iterator();
		
		if(transporterIterator.hasNext()) // Found Transporter to dial
		{
			TransporterInfo.FeedbackMessage feedback = connectedTransporter.dialTransporter(transporterIterator.next().getID());
			if(feedback.feedback().isError())
				player.displayClientMessage(feedback.getMessageComponent(), true);
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.DARK_RED), true);
	}
	
	// Materialization Crystal
	
	protected void handleInterdimensionalTransport(ServerLevel level, Player player, ItemStack ringRemoteStack, ItemStack crystalStack, AbstractTransporterEntity<?> connectedTransporter)
	{
		//TODO
	}
	
	// Transport handling
	
	@Nullable
	public CrystalCache.Type getCrystalType(ItemStack stack)
	{
		if(stack.getItem() instanceof AbstractCrystalItem crystal)
			return crystal.getType();
		
		return null;
	}
	
	protected void handleTransport(Level level, Player player, InteractionHand hand)
	{
		if(level.isClientSide())
			return;
		
		AbstractTransporterEntity<?> transporter = LocatorHelper.getNearestTransporter(level, player.blockPosition(), 16);
		if(transporter == null) // No Transporter found
		{
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.DARK_RED), true);
			return;
		}
		else if(!transporter.canTransport()) // Transporter is busy
		{
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.DARK_RED), true);
			return;
		}
		
		// Connected Transporter is ready, proceed further and check for any Crystals
		ItemStack ringRemoteStack = player.getItemInHand(hand);
		ringRemoteStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			ItemStack crystalStack = itemHandler.getStackInSlot(0);
			CrystalCache.Type type = getCrystalType(crystalStack);
			
			if(type != null)
			{
				switch(type)
				{
					case MEMORY -> handleMemoryTransport(player, ringRemoteStack, crystalStack, transporter);
					case COMMUNICATION -> handleNetworkTransport((ServerLevel) level, player, ringRemoteStack, crystalStack, transporter);
					case MATERIALIZATION -> handleInterdimensionalTransport((ServerLevel) level, player, ringRemoteStack, crystalStack, transporter);
					default -> nearestTransport((ServerLevel) level, player, transporter);
				}
			}
			else
				nearestTransport((ServerLevel) level, player, transporter);
		});
	}
	
	private void nearestTransport(ServerLevel level, Player player, AbstractTransporterEntity<?> connectedTransporter)
	{
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInDimension(level, connectedTransporter.getBlockPos(), connectedTransporter.maxTransportRange(), transporter ->
				!connectedTransporter.getID().equals(transporter.getID()) && // Ignore the Transporter the Ring Remote is connected to
				!transporter.isNetworkRestricted(connectedTransporter.getNetworks()) && // Don't show restricted Transporters
				!connectedTransporter.isNetworkRestricted(transporter.getNetworks()) // Don't show Transporters in other networks if this one is restricted
		).iterator();
		
		if(transporterIterator.hasNext()) // Found Transporter to dial
		{
			TransporterInfo.FeedbackMessage feedback = connectedTransporter.dialTransporter(transporterIterator.next().getID());
			if(feedback.feedback().isError())
				player.displayClientMessage(feedback.getMessageComponent(), true);
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
		
		/*CrystalCache.Type crystalType = getCrystalType(heldItem);
		
		ChatFormatting chatFormatting = switch(crystalType)
		{
			case MEMORY -> ChatFormatting.BLUE;
			case COMMUNICATION -> ChatFormatting.GRAY;
			case MATERIALIZATION -> ChatFormatting.DARK_AQUA;
			default -> ChatFormatting.DARK_GRAY;
		};*/
		
		if(!heldItem.isEmpty())
		{
			ListTag list = MemoryCrystalItem.getMemoryList(heldItem);
			for(int i = 0; i < list.size(); i++)
			{
				tooltipComponents.add(Component.literal(indexPrefix(i, i == indexAt)).withStyle(ChatFormatting.BLUE).append(memoryComponentAt(list, i)));
			}
		}

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	private Component memoryComponentAt(ListTag list, int index)
	{
		MemoryEntry<?> memoryEntry = MemoryCrystalItem.loadMemoryEntry(list, index);
		
		if(memoryEntry.entryType() == MemoryEntry.Type.TRANSPORTER_ID || memoryEntry.entryType() == MemoryEntry.Type.COORDINATES)
			return memoryEntry.toComponent();
		
		return Component.translatable("tooltip.sgjourney.invalid_entry").withStyle(ChatFormatting.DARK_RED);
	}
}
