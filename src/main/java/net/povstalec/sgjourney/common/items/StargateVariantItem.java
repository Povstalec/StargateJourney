package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.StargateJourney;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class StargateVariantItem extends Item
{
	public static final String VARIANT = "Variant";
	
	public StargateVariantItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return stack.hasTag();
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        if(stack.hasTag())
        {
            if(stack.getOrCreateTag().contains(VARIANT))
            {
            	String variant = stack.getOrCreateTag().getString(VARIANT);
            	
            	Minecraft minecraft = Minecraft.getInstance();
        		ClientPacketListener clientPacketListener = minecraft.getConnection();
        		
        		if(ResourceLocation.tryParse(variant) != null && clientPacketListener != null)
        		{
        			RegistryAccess registries = clientPacketListener.registryAccess();
        			Registry<StargateVariant> stargateVariantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
        	    	ResourceLocation variantLocation = StargateJourney.location(variant);
        			
        			if(stargateVariantRegistry.containsKey(variantLocation))
        			{
        				ResourceLocation baseStargate = stargateVariantRegistry.get(variantLocation).getBaseStargate();
        				
        				if(ForgeRegistries.BLOCKS.containsKey(baseStargate))
        				{
        					Optional<Reference<Block>> blockReference = ForgeRegistries.BLOCKS.getDelegate(stargateVariantRegistry.get(variantLocation).getBaseStargate());
        					
        					if(blockReference.isPresent())
            					tooltipComponents.add(Component.translatable("tooltip.sgjourney.requires").append(Component.literal(": ").append(blockReference.get().get().getName())).withStyle(ChatFormatting.LIGHT_PURPLE));
        				}
        				
        				if(isAdvanced.isAdvanced())
        					tooltipComponents.add(Component.translatable("tooltip.sgjourney.requires").append(Component.literal(": " + baseStargate.toString())).withStyle(ChatFormatting.BLUE));
        			}
        		}
            	
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.variant").append(Component.literal(": " + variant)).withStyle(ChatFormatting.GREEN));
            }
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_variant.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

	public static Optional<String> getVariantString(ItemStack stack)
	{
		Optional<String> variant = Optional.empty();
		
		if(stack.getItem() instanceof StargateVariantItem)
		{
			if(stack.getOrCreateTag().contains(VARIANT))
			{
				String variantString = stack.getTag().getString(VARIANT);
				variant = Optional.of(variantString);
			}
		}
		
		return variant;
	}
	
	public static <StargateBlock extends AbstractStargateBlock> ItemStack stargateVariant(String variant)
	{
		ItemStack stack = new ItemStack(ItemInit.STARGATE_VARIANT_CRYSTAL.get());
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString(VARIANT, variant);
		stack.setTag(compoundtag);
		
		return stack;
	}
}
