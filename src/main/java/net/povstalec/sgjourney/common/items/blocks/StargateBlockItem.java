package net.povstalec.sgjourney.common.items.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.data.StargateNetwork;

public class StargateBlockItem extends BlockItem
{
	private static final String ADD_TO_NETWORK = AbstractStargateEntity.ADD_TO_NETWORK;
	private static final String POINT_OF_ORIGIN = AbstractStargateEntity.POINT_OF_ORIGIN;
	private static final String SYMBOLS = AbstractStargateEntity.SYMBOLS;
	private static final String IRIS_PROGRESS = AbstractStargateEntity.IRIS_PROGRESS;
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public StargateBlockItem(Block block, Properties properties)
	{
		super(block, properties);
	}
	
	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state)
	{
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		
		Player player = context.getPlayer();
		CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		
		Orientation orientation = Orientation.getOrientationFromXRot(player);
		
		if(orientation == Orientation.REGULAR && blockpos.getY() > level.getMaxBuildHeight() - 6)
			return false;
		
		if(state.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				if(!part.equals(StargatePart.BASE) && !level.getBlockState(part.getRingPos(blockpos, context.getHorizontalDirection().getOpposite(), orientation)).canBeReplaced(context))
				{
					if(player != null)
						player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
					return false;
				}
			}
			
			ItemStack stack = context.getItemInHand();
			
			if(stack.getTag() != null)
			{
				CompoundTag itemTag = stack.getTag();
				
				if(itemTag.contains(BLOCK_ENTITY_TAG))
				{
					CompoundTag blockEntityTag = itemTag.getCompound(BLOCK_ENTITY_TAG);
					
					if(blockEntityTag.contains(IRIS_PROGRESS))
					{
						short irisProgress = blockEntityTag.getShort(IRIS_PROGRESS);
						
						for(ShieldingPart part : stargateBlock.getShieldingParts())
						{
							if(part.canExist(ShieldingState.fromProgress(irisProgress)) && !level.getBlockState(part.getShieldingPos(blockpos, context.getHorizontalDirection().getOpposite(), orientation)).canBeReplaced(context))
							{
								if(player != null)
									player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
								return false;
							}
						}
					}
				}
			}
		}
		else
			return false;
		
		return (!this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext);
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
			
			// Sets up symbols on the Milky Way Stargate
			else if(stargate instanceof PegasusStargateEntity)
			{
				if(!info.contains(POINT_OF_ORIGIN))
					stargate.setPointOfOriginFromDimension(level.dimension());
				if(!info.contains(SYMBOLS))
					stargate.setSymbolsFromDimension(level.dimension());
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
