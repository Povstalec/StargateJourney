package net.povstalec.sgjourney.common.items;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.misc.Conversion;
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
						if(crystalStack.getItem() instanceof MemoryCrystalItem crystal)
							tryStartTransport(level, player, transporterFromUUID(level, crystal.getFirstUUID(crystalStack)));
						else
							player.displayClientMessage(Component.translatable("message.sgjourney.ring_remote.error.no_coordinates").withStyle(ChatFormatting.BLUE), true);
					});
				}
			}
		}
		
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
	
	@Nullable
	private static Transporter transporterFromUUID(Level level, UUID uuid)
	{
		return TransporterNetwork.get(level).getTransporter(uuid);
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
			
			return canActivate.isPresent() ? canActivate.get() : false;
		}
		
		return false;
	}
	
	private int[] findFirstCoords(ItemStack memoryCrystal)
	{
		int[] address = new int[0];
		/*for(int i = 0; i < MemoryCrystalItem.getMemoryListSize(memoryCrystal); i++)
    	{
        	address = MemoryCrystalItem.getAddressAt(memoryCrystal, i);
        	
        	if(address.length > 0)
        		return address;
    	}*/
		return address;
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
        {
        	ItemStack crystalStack = itemHandler.getStackInSlot(0);
        	if(crystalStack.getItem() instanceof MemoryCrystalItem crystal)
        	{
                tooltipComponents.add(Component.translatable("item.sgjourney.memory_crystal").withStyle(ChatFormatting.BLUE));
				
				ListTag list = MemoryCrystalItem.getMemoryList(crystalStack);
				for(int i = 0; i < list.size(); i++)
				{
					tooltipComponents.add(Component.literal("[" + i + "] ")
							.append(memoryTypeAt(level, stack, list, i)));
				}
        	}
        });

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	private Component memoryTypeAt(Level level, ItemStack stack, ListTag list, int index)
	{
		if(list.getCompound(index).contains(MemoryCrystalItem.ADDRESS, Tag.TAG_INT_ARRAY))
			return Component.translatable("tooltip.sgjourney.address").withStyle(ChatFormatting.AQUA);
		
		Vec3i coords = MemoryCrystalItem.getCoords(list, index);
		if(coords != null)
		{
			return Component.translatable("tooltip.sgjourney.coordinates")
					.append(Component.literal(" " + coords.toShortString())).withStyle(ChatFormatting.BLUE);
		}
		
		UUID id = MemoryCrystalItem.getUUID(list, index);
		if(id != null)
			return Component.literal(id.toString()).withStyle(ChatFormatting.DARK_AQUA);
		else
			return Component.translatable("tooltip.sgjourney.corrupt_data").withStyle(ChatFormatting.DARK_RED);
	}
}
