package net.povstalec.sgjourney.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.HitResult;
import net.povstalec.sgjourney.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.misc.AncientTech;
import net.povstalec.sgjourney.misc.GoauldTech;
import net.povstalec.sgjourney.stargate.StargatePart;

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
		Block block = level.getBlockState(blockpos).getBlock();
		
		if(!level.isClientSide())
			player.sendSystemMessage(Component.translatable(block.getDescriptionId()).withStyle(ChatFormatting.GRAY));
		
		if(level.getBlockState(blockpos).getBlock() instanceof AbstractStargateRingBlock)
			blockpos = StargatePart.getMainBlockPos(blockpos, level.getBlockState(blockpos).getValue(AbstractStargateRingBlock.FACING), level.getBlockState(blockpos).getValue(AbstractStargateRingBlock.PART));
		
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
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.pda_info").withStyle(ChatFormatting.GRAY));
	}
	
	private void scanEntity(Player user, Entity target)
	{
		user.sendSystemMessage(target.getName().copy().withStyle(ChatFormatting.YELLOW));
		
		if(canUseGoauldTech(target))
			user.sendSystemMessage(Component.translatable("message.sgjourney.pda_has_naquadah_in_bloodstream").withStyle(ChatFormatting.DARK_GREEN));
		
		if(canUseAncientTech(target))
			user.sendSystemMessage(Component.translatable("message.sgjourney.pda_has_ancient_gene").withStyle(ChatFormatting.AQUA));
	}
	
	// Blah blah blah, whatever
	/*public Block getLookedAtBlock()
	{
		Minecraft minecraft = Minecraft.getInstance();
		
	    HitResult result = minecraft.hitResult;

	    double x = result.getLocation().x;
	    double y = result.getLocation().y;
	    double z = result.getLocation().z;

	    double xAngle = minecraft.player.getLookAngle().x;
	    double yAngle = minecraft.player.getLookAngle().y;
	    double zAngle = minecraft.player.getLookAngle().z;

	    if (x % 1 == 0 && xAngle < 0)
	    	x -= 0.01;
	    if (y % 1 == 0 && yAngle < 0)
	    	y -= 0.01;
	    if (z % 1 == 0 && zAngle < 0)
	    	z -= 0.01;

	    BlockPos pos = new BlockPos(x, y, z);
	    BlockState state = minecraft.level.getBlockState(pos);

	    return state.getBlock();
	}
	
	public Block playerLookingAt(Player player)
	{
		HitResult result = player.pick(MAX_BAR_WIDTH, EAT_DURATION, canRepair);
	}*/
}
