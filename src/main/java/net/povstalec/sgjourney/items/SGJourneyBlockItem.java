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
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.PegasusStargateEntity;

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
		else
		{
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
            			SGJourneyBlockEntity baseBlockEntity = (SGJourneyBlockEntity)blockentity;
            			if(compoundtag.contains("AddToNetwork") && !compoundtag.getBoolean("AddToNetwork"))
            				return true;
            			else
            			{
            				if(compoundtag.contains("ID"))
	            				baseBlockEntity.addToBlockEntityList();
            				else
            					baseBlockEntity.addNewToBlockEntityList();
            				
            				if(baseBlockEntity instanceof MilkyWayStargateEntity milkyWayStargate)
        					{
            					if(!compoundtag.contains("PointOfOrigin"))
            						milkyWayStargate.setPointOfOrigin(level);
        						if(!compoundtag.contains("Symbols"))
        							milkyWayStargate.setSymbols(level);
        					}
        					else if(baseBlockEntity instanceof PegasusStargateEntity pegasusStargate)
        					{
        						//TODO Get this back here: pegasusStargate.setSymbols(StargateNetwork.get(level).getGalaxy(level).getSymbols().getSymbols());
        						pegasusStargate.setPointOfOrigin(level);
        					}
            			}
	            		return true;
	            	}
	            }
			}
			else
			{
    			SGJourneyBlockEntity baseBlockEntity = (SGJourneyBlockEntity)level.getBlockEntity(pos);
				baseBlockEntity.addNewToBlockEntityList();
				
				if(baseBlockEntity instanceof MilkyWayStargateEntity milkyWayStargate)
				{
					milkyWayStargate.setPointOfOrigin(level);
					milkyWayStargate.setSymbols(level);
				}
			}
			
			return false;
		}
	}
	
}
