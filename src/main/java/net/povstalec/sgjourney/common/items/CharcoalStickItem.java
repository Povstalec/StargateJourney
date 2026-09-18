package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.blocks.SpecialSymbolBlock;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CharcoalStickItem extends Item
{
	public CharcoalStickItem(Properties properties)
	{
		super(properties.defaultDurability(16));
	}
	
	@Nullable
	public ItemStack tryCreateSymbolPaperItem(@Nullable ResourceKey<PointOfOrigin> pointOfOrigin, @Nullable ResourceKey<Symbols> symbols)
	{
		if(pointOfOrigin == null && symbols == null)
			return null;
		
		ItemStack stack = new ItemStack(ItemInit.SYMBOL_PAPER.get());
		
		if(pointOfOrigin != null)
			SymbolPaperItem.setPointOfOrigin(stack, pointOfOrigin);
		if(symbols != null)
			SymbolPaperItem.setSymbols(stack, symbols);
		
		return stack;
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		
		Player player = context.getPlayer();
		if(player != null && state.getBlock() instanceof SpecialSymbolBlock specialSymbolBlock)
		{
			ResourceKey<PointOfOrigin> pointOfOrigin = specialSymbolBlock.getPointOfOrigin(level, pos, state);
			ResourceKey<Symbols> symbols = specialSymbolBlock.getSymbols(level, pos, state);
			
			InteractionHand otherHand = context.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
			ItemStack otherStack = player.getItemInHand(otherHand);
			if(!otherStack.is(Items.PAPER))
			{
				player.displayClientMessage(Component.translatable("message.sgjourney.charcoal_stick.no_paper").withStyle(ChatFormatting.RED), true);
				return InteractionResult.FAIL;
			}
			
			ItemStack symbolPaperStack = tryCreateSymbolPaperItem(pointOfOrigin, symbols);
			if(symbolPaperStack == null)
				return InteractionResult.FAIL;
			
			ItemStack heldStack = context.getItemInHand();
			heldStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
			
			if(!player.getAbilities().instabuild)
				otherStack.shrink(1);
			
			player.awardStat(Stats.ITEM_USED.get(this));
			player.level.playSound(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundSource(), 1.0F, 1.0F);
			
			if(otherStack.isEmpty())
				player.setItemInHand(otherHand, symbolPaperStack);
			else if(!player.getInventory().add(symbolPaperStack.copy()))
				player.drop(symbolPaperStack, false);
			
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
	{
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.charcoal_stick.description"));
	}
}
