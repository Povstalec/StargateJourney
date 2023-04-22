package net.povstalec.sgjourney.items;

import java.util.List;
import java.util.Set;

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
import net.povstalec.sgjourney.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.init.ItemInit;
import net.povstalec.sgjourney.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.stargate.Addressing;

public class RingRemoteItem extends Item
{
	private BlockPos center;
	private int[] distance;
	private CompoundTag tag;
	private BlockPos target;

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
		else if(!player.isShiftKeyDown() && canActivate(player.getItemInHand(hand)))
		{
			if(!level.isClientSide())
			{
				
			}
		}
		
		
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
		
		/*if(!level.isClientSide())
		{
			BlockPos pos = this.getNearestRings(BlockEntityList.get(level).getBlockEntities("TransportRingsList"), player.blockPosition(), 16);
			if(pos != null)
			{
				BlockEntity localRings = level.getBlockEntity(pos);
	    		
				if(localRings instanceof TransportRingsEntity rings)
	    		  {
	    			  if(rings.canTransport() && player.getItemInHand(hand).hasTag())
		    		  {
	    				  int[] coords = player.getItemInHand(hand).getTag().getIntArray("Coordinates");
	    				  
	    				  BlockPos targetPos = new BlockPos(coords[0], coords[1], coords[2]);
			    		  
			    		  BlockEntity targetRings = level.getBlockEntity(targetPos);
			    		  
			    		  if(targetRings instanceof TransportRingsEntity target)
			    		  {
			    			  if(target.canTransport() && !target.isActivated())
				    		  {
			    				  rings.activate(targetPos);
				    		  }
			    		  }
	    				  
		    		  } 
	    		  }
			}
			else
			{
				System.out.println("Pos is null");
			}
		}
			
        return super.use(level, player, usedHand);*/
    }
	
	public boolean canActivate(ItemStack stack)
	{
		//TODO
		return true;
	}
	
	private int[] findFirstCoords(ItemStack memoryCrystal)
	{
		int[] address = new int[0];
		for(int i = 0; i < MemoryCrystalItem.getMemoryListSize(memoryCrystal); i++)
    	{
        	address = MemoryCrystalItem.getAddressAt(memoryCrystal, i);
        	
        	if(address.length > 0)
        		return address;
    	}
		return address;
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
        {
        	ItemStack memoryCrystal = itemHandler.getStackInSlot(0);
        	
        	if(memoryCrystal.getItem() instanceof MemoryCrystalItem)
        	{
                tooltipComponents.add(Component.translatable("item.sgjourney.memory_crystal").withStyle(ChatFormatting.DARK_BLUE));

            	int[] firstCoords = findFirstCoords(memoryCrystal);
            	
            	if(firstCoords.length == 3)
            		tooltipComponents.add(Component.literal("X: " + firstCoords[0] + " Y: " + firstCoords[1] + " Z: " + firstCoords[2]).withStyle(ChatFormatting.YELLOW));
                
                super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        	}
        });

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	public BlockPos getNearestRings(CompoundTag nbtTag, BlockPos center, int maxDistance)
	{
		this.center = center;
		int[] distance = {maxDistance, maxDistance, maxDistance};
		this.distance = distance;

		this.tag = nbtTag.copy();
		Set<String> list = tag.getAllKeys();
		
		list.stream().forEach(this::ringsNetworking);
		System.out.println(list);
		System.out.println("Closest rings at X: " + target.getX() + " Y: " + target.getY() + " Z: " + target.getZ());
		
		return this.target;
	}
	
	private void ringsNetworking(String ringsID)
	{
		int targetX = tag.getIntArray(ringsID)[0];
		int targetY = tag.getIntArray(ringsID)[1];
		int targetZ = tag.getIntArray(ringsID)[2];
		
		int distanceX = Math.abs(targetX - center.getX());
		int distanceY = Math.abs(targetY - center.getY());
		int distanceZ = Math.abs(targetZ - center.getZ());
		
		if(distanceX <= distance[0] && distanceY <= distance[1] && distanceZ <= distance[2])
		{
			this.distance[0] = distanceX;
			this.distance[1] = distanceY;
			this.distance[2] = distanceZ;
			
			this.target = new BlockPos(targetX, targetY, targetZ);
		}
	}
}
