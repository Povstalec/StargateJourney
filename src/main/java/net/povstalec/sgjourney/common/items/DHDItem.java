package net.povstalec.sgjourney.common.items;

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
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;

public class DHDItem extends BlockItem
{
	public DHDItem(Block block, Properties properties)
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
            		
            		return setupBlockEntity(level, blockentity, compoundtag);
            	}
            }
		}
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof MilkyWayDHDEntity dhd)
		{
			dhd.recalculateCrystals();
			return true;
		}
		else if(baseEntity instanceof PegasusDHDEntity dhd)
		{
			dhd.recalculateCrystals();
			return true;
		}
		
		return false;
	}
	
}
