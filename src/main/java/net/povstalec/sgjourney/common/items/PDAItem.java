package net.povstalec.sgjourney.common.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.misc.AncientTech;
import net.povstalec.sgjourney.common.misc.GoauldTech;

public class PDAItem extends Item implements AncientTech, GoauldTech
{
	public PDAItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos blockpos = context.getClickedPos();
		BlockState state = level.getBlockState(blockpos);
		Block block = state.getBlock();
		
		if(!level.isClientSide())
			player.sendSystemMessage(Component.translatable(block.getDescriptionId()).withStyle(ChatFormatting.GRAY));
		
		if(block instanceof AbstractStargateRingBlock)
			blockpos = state.getValue(AbstractStargateRingBlock.PART).getBaseBlockPos(blockpos, state.getValue(AbstractStargateRingBlock.FACING), state.getValue(AbstractStargateRingBlock.ORIENTATION));
		
		if(level.getBlockEntity(blockpos) instanceof EnergyBlockEntity blockEntity)
			blockEntity.getStatus(player);
		
		return super.useOn(context);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(!level.isClientSide())
		{
			if(player.isShiftKeyDown())
				this.scanEntity(player, player);
		}
		
		return super.use(level, player, hand);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
	{
		if(!player.getLevel().isClientSide())
			this.scanEntity(player, target);
		
		return super.interactLivingEntity(stack, player, target, hand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.pda.info").withStyle(ChatFormatting.GRAY));
	}
	
	private void scanEntity(Player user, Entity target)
	{
		user.sendSystemMessage(target.getName().copy().withStyle(ChatFormatting.YELLOW));
		
		if(canUseGoauldTech(target))
			user.sendSystemMessage(Component.translatable("message.sgjourney.pda.has_naquadah_in_bloodstream").withStyle(ChatFormatting.DARK_GREEN));
		
		if(canUseAncientTech(target))
			user.sendSystemMessage(Component.translatable("message.sgjourney.pda.has_ancient_gene").withStyle(ChatFormatting.AQUA));
	}
}
