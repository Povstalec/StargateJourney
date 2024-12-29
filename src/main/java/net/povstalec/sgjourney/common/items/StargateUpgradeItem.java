package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.init.DataComponentInit;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.init.ItemInit;

public class StargateUpgradeItem extends Item
{
	public StargateUpgradeItem(Properties properties)
	{
		super(properties);
	}

	public Optional<AbstractStargateBaseBlock> getStargateBaseBlock(ItemStack stack)
	{
		ResourceLocation location = stack.get(DataComponentInit.STARGATE_UPGRADE);
		
		if(location != null)
		{
			Block block = BuiltInRegistries.BLOCK.get(location);
			if(block instanceof AbstractStargateBaseBlock stargateBlock)
				return Optional.of(stargateBlock);
		}
		
		return Optional.empty();
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
		ResourceLocation location = stack.get(DataComponentInit.STARGATE_UPGRADE);
		
		if(location != null)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_type").append(Component.literal(": " + location.toString())).withStyle(ChatFormatting.GREEN));
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_upgrade.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	public static <StargateBlock extends AbstractStargateBlock> ItemStack stargateType(StargateBlock stargate)
	{
		ItemStack stack = new ItemStack(ItemInit.STARGATE_UPGRADE_CRYSTAL.get());
		stack.set(DataComponentInit.STARGATE_UPGRADE, BuiltInRegistries.BLOCK.getKey(stargate));
		
		return stack;
	}
}
