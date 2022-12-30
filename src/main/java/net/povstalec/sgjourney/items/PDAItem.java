package net.povstalec.sgjourney.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;

public class PDAItem extends Item
{
	public PDAItem(Properties properties)
	{
		super(properties);
	}
	
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos blockpos = context.getClickedPos();
		Block block = level.getBlockState(blockpos).getBlock();
		
		if(!level.isClientSide())
			player.sendSystemMessage(Component.translatable(block.getDescriptionId()).withStyle(ChatFormatting.GRAY));
		
		if(level.getBlockEntity(blockpos) instanceof SGJourneyBlockEntity blockentity)
			blockentity.getStatus(player);
		
		return super.useOn(context);
	}
	
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltips.sgjourney.pda_info").withStyle(ChatFormatting.GRAY));
	}
}
