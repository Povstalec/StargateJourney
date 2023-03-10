package net.povstalec.sgjourney.items;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;

public class SGJourneyBlockItem extends BlockItem
{
	public SGJourneyBlockItem(Block block, Properties properties)
	{
		super(block, properties);
	}
	
	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
	{
		return updateCustomBlockEntityTag(level, player, pos, stack);
	}
	
	public static boolean updateCustomBlockEntityTag(Level level, @Nullable Player player, BlockPos pos, ItemStack stack)
	{
		MinecraftServer minecraftserver = level.getServer();
		if(minecraftserver == null)
			return false;
		
		CompoundTag compoundtag = getBlockEntityData(stack);
		if(compoundtag != null)
		{
			BlockEntity blockentity = level.getBlockEntity(pos);
            if(blockentity != null)
            {
            	if(!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
            		return false;
            	
            	CompoundTag compoundtag1 = blockentity.saveWithoutMetadata();
            	CompoundTag compoundtag2 = compoundtag1.copy();
            	
            	compoundtag1.merge(compoundtag);
            	
            	if(!compoundtag1.equals(compoundtag2))
            	{
            		blockentity.load(compoundtag1);
            		blockentity.setChanged();
            		
            		return setupBlockEntity(blockentity, compoundtag);
            	}
            }
		}
		else
		{
			BlockEntity baseEntity = level.getBlockEntity(pos);
			
			if(baseEntity instanceof SGJourneyBlockEntity blockEntity)
				blockEntity.addNewToBlockEntityList();
		}
		
		return false;
	}
	
	private static boolean setupBlockEntity(BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof SGJourneyBlockEntity blockEntity)
		{
			boolean addToNetwork = true;
			
			if(info.contains("AddToNetwork"))
				addToNetwork = info.getBoolean("AddToNetwork");
			
			// Registers it as one of the Block Entities in the list
			if(info.contains("ID") && !info.getString("ID").equals(StargateJourney.EMPTY))
				blockEntity.addToBlockEntityList();
			else
				blockEntity.addNewToBlockEntityList();
			
			// Sets up symbols on the Milky Way Stargate
			if(blockEntity instanceof MilkyWayStargateEntity milkyWayStargate)
			{
				if(!addToNetwork)
				{
					if(!info.contains("PointOfOrigin"))
						milkyWayStargate.setPointOfOrigin("sgjourney:empty");
					if(!info.contains("Symbols"))
						milkyWayStargate.setSymbols("sgjourney:empty");
				}
			}
		}
		
		return false;
	}
	
}
