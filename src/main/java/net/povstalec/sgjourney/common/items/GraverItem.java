package net.povstalec.sgjourney.common.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.init.*;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraverItem extends TieredItem
{
	public static final ItemAbility GRAVER_ENGRAVE = ItemAbility.get("graver_engrave");
	public static final Set<ItemAbility> DEFAULT_GRAVER_ACTIONS = ItemInit.ofItemAbilities(GRAVER_ENGRAVE);
	
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
		
		if(state.getBlock() instanceof SpecialEngravableBlock engravable)
		{
			if(isCorrectForEngraving(state))
				return engravable.onGraverUsed(context);
			else if(player != null)
				player.displayClientMessage(Component.translatable("message.sgjourney.graver.low_tier").withStyle(ChatFormatting.RED), true);
			
			return InteractionResult.FAIL;
		}
		
		BlockState newState = state.getToolModifiedState(context, GRAVER_ENGRAVE, false);
		if(newState != null)
		{
			if(isCorrectForEngraving(state))
			{
				level.playSound(player, pos, SoundInit.GRAVER_ENGRAVE.get(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
				level.setBlock(pos, newState, Block.UPDATE_ALL_IMMEDIATE);
				level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
				
				ItemStack itemstack = context.getItemInHand();
				if(player instanceof ServerPlayer serverPlayer)
				{
					CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemstack);
					itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
				}
				
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else if(player != null)
				player.displayClientMessage(Component.translatable("message.sgjourney.graver.low_tier").withStyle(ChatFormatting.RED), true);
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility)
	{
		return DEFAULT_GRAVER_ACTIONS.contains(itemAbility);
	}
	
	public boolean isCorrectForEngraving(BlockState state)
	{
		if(getTier() instanceof ToolMaterialInit tier)
		{
			return switch(tier)
			{
				case NAQUADAH, TRINIUM -> true;
				default -> false;
			};
		}
		
		if(state.is(TagInit.Blocks.NEEDS_DIAMOND_GRAVER))
		{
			if(getTier() instanceof Tiers tier)
			{
				return switch(tier)
				{
					case NETHERITE, DIAMOND -> true;
					default -> false;
				};
			}
		}
		else if(state.is(TagInit.Blocks.NEEDS_IRON_GRAVER))
		{
			if(getTier() instanceof Tiers tier)
			{
				return switch(tier)
				{
					case NETHERITE, DIAMOND, IRON -> true;
					default -> false;
				};
			}
		}
		else if(state.is(TagInit.Blocks.NEEDS_STONE_GRAVER))
		{
			if(getTier() instanceof Tiers tier)
			{
				return switch(tier)
				{
					case NETHERITE, DIAMOND, IRON, STONE -> true;
					default -> false;
				};
			}
		}
		
		return false;
	}
	
	public static TagKey<Block> getTagFromVanillaTier(Tiers tier)
	{
		return switch(tier)
		{
			case WOOD -> Tags.Blocks.NEEDS_WOOD_TOOL;
			case GOLD -> Tags.Blocks.NEEDS_GOLD_TOOL;
			case STONE -> BlockTags.NEEDS_STONE_TOOL;
			case IRON -> BlockTags.NEEDS_IRON_TOOL;
			case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
			case NETHERITE -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
		};
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.graver.description"));
	}
}
