package net.povstalec.sgjourney.items;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.data.BlockEntityList;

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(!level.isClientSide)
		{
			BlockPos pos = this.getNearestRings(BlockEntityList.get(level).getBlockEntities("TransportRingsList"), player.blockPosition(), 16);
			if(pos != null)
			{
				BlockEntity localRings = level.getBlockEntity(pos);
	    		
				if(localRings instanceof TransportRingsEntity rings)
	    		  {
	    			  if(rings.canTransport() && player.getItemInHand(usedHand).hasTag())
		    		  {
	    				  int[] coords = player.getItemInHand(usedHand).getTag().getIntArray("coordinates");
	    				  
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
        return super.use(level, player, usedHand);
    }
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        if(stack.hasTag())
        {
            String location = stack.getTag().getString("location");
            tooltipComponents.add(Component.literal(location));
        }

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
