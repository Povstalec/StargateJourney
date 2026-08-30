package net.povstalec.sgjourney.common.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import net.povstalec.sgjourney.common.blocks.SpecialGravableBlock;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class GraverItem extends TieredItem implements Vanishable
{
	public static final ToolAction GRAVER_ENGRAVE = ToolAction.get("graver_engrave");
	public static final Set<ToolAction> DEFAULT_GRAVER_ACTIONS = ItemInit.ofToolActions(GRAVER_ENGRAVE);
	
	public static final Map<Block, BlockState> DEFAULT_ENGRAVABLE = Maps.newHashMap((new ImmutableMap.Builder<Block, BlockState>())
		.put(Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE.defaultBlockState())
		.put(Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE.defaultBlockState())
		
		.put(Blocks.SMOOTH_SANDSTONE, BlockInit.SANDSTONE_HIEROGLYPHS.get().defaultBlockState())
		.put(Blocks.SMOOTH_RED_SANDSTONE, BlockInit.RED_SANDSTONE_GLYPHS.get().defaultBlockState())
		.build());
	
	public GraverItem(Tier tier, Properties properties)
	{
		super(tier, properties);
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof SpecialGravableBlock gravable)
			return gravable.onGraverUsed(context);
		
		BlockState newState = state.getToolModifiedState(context, GRAVER_ENGRAVE, false);
		if(newState != null)
		{
			level.playSound(player, pos, SoundInit.GRAVER_ENGRAVE.get(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
			level.setBlock(pos, newState, Block.UPDATE_ALL_IMMEDIATE);
			level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
			
			ItemStack itemstack = context.getItemInHand();
			if(player instanceof ServerPlayer serverPlayer)
			{
				CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemstack);
				itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
			}
			
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction)
	{
		return DEFAULT_GRAVER_ACTIONS.contains(toolAction);
	}
}
