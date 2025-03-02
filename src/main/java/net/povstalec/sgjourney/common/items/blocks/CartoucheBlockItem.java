package net.povstalec.sgjourney.common.items.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;

public class CartoucheBlockItem extends BlockItem
{
	public CartoucheBlockItem(Block block, Properties properties)
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
		
		Orientation orientation = Orientation.getOrientationFromXRot(context.getPlayer());
		Direction direction = context.getHorizontalDirection().getOpposite();
		int maxBuildHeight = level.getMaxBuildHeight();
		
		if(orientation != Orientation.REGULAR)
			maxBuildHeight -= 1;
		
		if(blockpos.getY() > maxBuildHeight || !level.getBlockState(blockpos.relative(Orientation.getMultiDirection(direction, Direction.UP, orientation))).canBeReplaced(context))
		{
			player.displayClientMessage(Component.translatable("block.sgjourney.cartouche.not_enough_space"), true);
			return false;
		}
		
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
		
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag compoundtag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			BlockEntity blockentity = level.getBlockEntity(pos);
            if(blockentity != null)
            {
            	if(!level.isClientSide && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
            		return false;
            	
            	CompoundTag compoundtag1 = blockentity.saveWithoutMetadata(minecraftserver.registryAccess());
            	CompoundTag compoundtag2 = compoundtag1.copy();
            	
            	compoundtag1.merge(compoundtag);
            	
            	if(!compoundtag1.equals(compoundtag2))
            	{
            		blockentity.loadCustomOnly(compoundtag1, minecraftserver.registryAccess());
            		blockentity.setChanged();
            		
            		return setupBlockEntity(level, blockentity, compoundtag);
            	}
            }
		}
		
		return false;
	}
	
	private static boolean setupBlockEntity(Level level, BlockEntity baseEntity, CompoundTag info)
	{
		if(baseEntity instanceof CartoucheEntity cartouche)
		{
			cartouche.setNew();
			if(info.contains(CartoucheEntity.DIMENSION) && !info.contains(CartoucheEntity.ADDRESS))
			{
				cartouche.setDimension(ResourceLocation.tryParse(info.getString(CartoucheEntity.DIMENSION)));
				cartouche.setAddressFromDimension();
			}
			return true;
		}
		
		return false;
	}
	
}
