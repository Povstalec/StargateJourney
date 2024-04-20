package net.povstalec.sgjourney.common.items.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.data.StargateNetwork;

public class StargateBlockItem extends BlockItem
{
	private static final String ADD_TO_NETWORK = "AddToNetwork";
	private static final String POINT_OF_ORIGIN = "PointOfOrigin";
	private static final String SYMBOLS = "Symbols";
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public StargateBlockItem(Block block, Properties properties)
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
            	if(!level.isClientSide() && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
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
		else
		{
			BlockEntity baseEntity = level.getBlockEntity(pos);
			
			if(baseEntity instanceof AbstractStargateEntity stargate)
			{
				stargate.addStargateToNetwork();
				
				// Sets up symbols on the Milky Way Stargate
				if(stargate instanceof MilkyWayStargateEntity)
					stargate.setRandomPointOfOrigin(level.dimension());
				
				// Sets up symbols on the Classic Stargate
				else if(stargate instanceof ClassicStargateEntity)
					stargate.setRandomPointOfOrigin(level.dimension());
			}
		}
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof AbstractStargateEntity stargate)
		{
			boolean addToNetwork = true;
			
			if(info.contains(ADD_TO_NETWORK))
				addToNetwork = info.getBoolean(ADD_TO_NETWORK);
			
			if(addToNetwork)
			{
				// Registers it as one of the Block Entities in the list
				stargate.addStargateToNetwork();
				
				if(!level.isClientSide())
					StargateNetwork.get(level).updateStargate((ServerLevel) level, stargate);
			}
			
			// Sets up symbols on the Milky Way Stargate
			if(stargate instanceof MilkyWayStargateEntity)
			{
				if(!addToNetwork)
				{
					if(!info.contains(POINT_OF_ORIGIN))
						stargate.setPointOfOrigin(EMPTY);
					if(!info.contains(SYMBOLS))
						stargate.setSymbols(EMPTY);
				}
			}
			
			// Sets up symbols on the Classic Stargate
			else if(stargate instanceof ClassicStargateEntity)
			{
				if(!addToNetwork)
				{
					if(!info.contains(POINT_OF_ORIGIN))
						stargate.setPointOfOrigin(EMPTY);
					if(!info.contains(SYMBOLS))
						stargate.setSymbols(EMPTY);
				}
			}
		}
		
		return false;
	}
	
}
