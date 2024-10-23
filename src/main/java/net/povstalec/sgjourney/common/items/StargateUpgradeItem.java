package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.init.ItemInit;

public class StargateUpgradeItem extends Item
{
	public static final String TYPE = "Type";
	
	public StargateUpgradeItem(Properties properties)
	{
		super(properties);
	}

	public Optional<AbstractStargateBaseBlock> getStargateBaseBlock(ItemStack stack)
	{
		Optional<AbstractStargateBaseBlock> stargate = Optional.empty();
		
		Optional<String> string = getStargateString(stack);
		
		if(string.isPresent())
		{
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(string.get()));
			if(block instanceof AbstractStargateBaseBlock stargateBlock)
				stargate = Optional.of(stargateBlock);
		}
		
		return stargate;
	}
    
    public static Optional<String> getStargateString(ItemStack stack)
	{
    	Optional<String> stargate = Optional.empty();
    	
		if(stack.getItem() instanceof StargateUpgradeItem)
		{
			if(stack.getOrCreateTag().contains(TYPE))
			{
				String stargateString = stack.getTag().getString(TYPE);
				stargate = Optional.of(stargateString);
			}
		}
		
		return stargate;
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        if(stack.hasTag())
        {
            if(stack.getOrCreateTag().contains(TYPE))
            {
            	String variant = stack.getOrCreateTag().getString(TYPE);
            	
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_type").append(Component.literal(": " + variant)).withStyle(ChatFormatting.GREEN));
            }
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_upgrade.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	public static <StargateBlock extends AbstractStargateBlock> ItemStack stargateType(StargateBlock stargate)
	{
		ItemStack stack = new ItemStack(ItemInit.STARGATE_UPGRADE_CRYSTAL.get());
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString(TYPE, ForgeRegistries.BLOCKS.getKey(stargate).toString());
		stack.setTag(compoundtag);
		
		return stack;
	}
}
