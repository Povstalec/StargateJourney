package net.povstalec.sgjourney.common.items;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
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
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.block_entities.transporter.TransportRingsEntity;
import net.povstalec.sgjourney.common.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

public class RingRemoteItem extends Item
{
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
	
	protected List<TransportRingsEntity> getNearbyTransportRings(Level level, BlockPos blockPos, int maxDistance)
	{
		List<TransportRingsEntity> transporters = new ArrayList<TransportRingsEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = level.getChunk(blockPos.east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof TransportRingsEntity transportRings)
						transporters.add(transportRings);
				});
			}
		}
		
		return transporters;
	}
	
	public Optional<TransportRingsEntity> findNearestTransportRings(Level level, BlockPos blockPos, int maxDistance)
	{
		List<TransportRingsEntity> transporters = getNearbyTransportRings(level, blockPos, maxDistance);
		transporters.sort(Comparator.comparing(transporter -> Double.valueOf(blockPos.distSqr(transporter.getBlockPos()))));
		
		if(!transporters.isEmpty())
			return Optional.of(transporters.get(0));
		
		return Optional.empty();
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		
		if(player.isShiftKeyDown() && !level.isClientSide())
		{
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			
			if(offHandStack.is(ItemInit.RING_REMOTE.get()))
			{
				offHandStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
				{
					ItemStack returnStack;
					if(!mainHandStack.isEmpty())
						returnStack = itemHandler.insertItem(0, mainHandStack, false);
					else
						returnStack = itemHandler.extractItem(0, 1, false);
					
					player.setItemInHand(InteractionHand.MAIN_HAND, returnStack);
				});
				
			}
		}
		else if(!player.isShiftKeyDown())
		{
			ItemStack stack = player.getItemInHand(hand);
			if(!canActivate(stack))
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_memory_crystal").withStyle(ChatFormatting.BLUE), true);
			else
			{
				if(!level.isClientSide())
				{
					stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
					{
						ItemStack crystalStack = itemHandler.getStackInSlot(0);
						
						//TODO Transport based on coords, let players choose from the list of transport locations
						if(crystalStack.getItem() instanceof MemoryCrystalItem)
							tryStartTransport(level, player, transporterFromIDEntry(level, MemoryCrystalItem.loadFirstMemoryEntry(crystalStack, MemoryEntry.Type.TRANSPORTER_ID)));
						else
							player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_coordinates").withStyle(ChatFormatting.BLUE), true);
					});
				}
			}
		}
		
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
	
	@Nullable
	private static Transporter transporterFromIDEntry(Level level, MemoryEntry.TransporterID transporterID)
	{
		if(transporterID == null)
			return null;
		
		return TransporterNetwork.get(level).getTransporter(transporterID.entry());
	}
	
	@Nullable
	private static Transporter transporterFromCoords(Level level, Vec3i coords)
	{
		BlockEntity blockEntity = level.getBlockEntity(new BlockPos(coords.getX(), coords.getY(), coords.getZ()));
		if(blockEntity instanceof AbstractTransporterEntity transporter)
			return transporter.getTransporter();
		
		return null;
	}
	
	private void tryStartTransport(Level level, Player player, @Nullable Transporter target)
	{
		Optional<TransportRingsEntity> transportRings = findNearestTransportRings(level, player.blockPosition(), 16);
		if(transportRings.isPresent())
		{
			if(target != null && transportRings.get().canTransport() && transportRings.get().canTransport())
					transportRings.get().startTransport(target);
			else
				player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.BLUE), true);
		}
		else
			player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_transport_rings_nearby").withStyle(ChatFormatting.BLUE), true);
	}
	
	public static boolean canActivate(ItemStack stack)
	{
		if(stack.is(ItemInit.RING_REMOTE.get()))
		{
			Optional<Boolean> canActivate = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> !itemHandler.getStackInSlot(0).isEmpty());
			
			return canActivate.orElse(false);
		}
		
		return false;
	}
	
	public ItemStack getHeldItem(ItemStack holderStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null)
			return ItemStack.EMPTY;
		
		return itemHandler.getStackInSlot(0);
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		ItemStack heldItem = getHeldItem(stack);
		
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
					tooltipComponents.add(Component.literal("[" + i + "] ").withStyle(ChatFormatting.BLUE).append(memoryTypeAt(list, type, i)));
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
