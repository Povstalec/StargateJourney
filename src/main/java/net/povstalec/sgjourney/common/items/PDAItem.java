package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.PDAStatus;
import net.povstalec.sgjourney.common.tech.AncientTech;
import net.povstalec.sgjourney.common.tech.GoauldTech;

import javax.annotation.Nullable;
import java.util.List;

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
		BlockPos blockPos = context.getClickedPos();
		BlockState state = level.getBlockState(blockPos);
		Block block = state.getBlock();
		
		if(level.isClientSide() || player == null)
			return super.useOn(context);
		
		player.sendMessage(new TranslatableComponent(block.getDescriptionId()).withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
		
		BlockEntity blockEntity;
		if(block instanceof AbstractStargateBlock stargate)
			blockEntity = stargate.getStargate(level, blockPos, state);
		else
			blockEntity = level.getBlockEntity(blockPos);
		
		if(blockEntity == null)
			return super.useOn(context);
		
		if(blockEntity instanceof PDAStatus pdaStatus)
		{
			for(Component component : pdaStatus.getStatus())
			{
				player.sendMessage(component, Util.NIL_UUID);
			}
		}
		
		tryFindEnergySignature(blockEntity, player);
		
		return super.useOn(context);
	}
	
	private static void showEnergySignature(LazyOptional<IEnergyStorage> capability, Player player)
	{
		capability.ifPresent(energyStorage ->
		{
			if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				player.sendMessage(ComponentHelper.energy("info.sgjourney.energy", sgjourneyEnergy.getTrueEnergyStored()), Util.NIL_UUID);
			else
				player.sendMessage(ComponentHelper.energy("info.sgjourney.energy", energyStorage.getEnergyStored()), Util.NIL_UUID);
		});
	}
	
	private static void tryFindEnergySignature(BlockEntity blockEntity, Player player)
	{
		if(blockEntity.getCapability(CapabilityEnergy.ENERGY).isPresent())
			showEnergySignature(blockEntity.getCapability(CapabilityEnergy.ENERGY), player);
		else
		{
			for(Direction direction : Direction.values())
			{
				if(blockEntity.getCapability(CapabilityEnergy.ENERGY, direction).isPresent())
				{
					showEnergySignature(blockEntity.getCapability(CapabilityEnergy.ENERGY, direction), player);
					return;
				}
			}
		}
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
		tooltipComponents.add(new TranslatableComponent("tooltip.sgjourney.pda.info").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	private void scanEntity(Player user, Entity target)
	{
		user.sendMessage(target.getName().copy().withStyle(ChatFormatting.YELLOW), Util.NIL_UUID);
		
		if(target instanceof LivingEntity livingEntity)
		{
			if(canUseGoauldTech(livingEntity))
				user.sendMessage(new TranslatableComponent("message.sgjourney.pda.has_naquadah_in_bloodstream").withStyle(ChatFormatting.DARK_GREEN), Util.NIL_UUID);
			
			if(canUseAncientTech(livingEntity))
				user.sendMessage(new TranslatableComponent("message.sgjourney.pda.has_ancient_gene").withStyle(ChatFormatting.AQUA), Util.NIL_UUID);
		}
	}
}
