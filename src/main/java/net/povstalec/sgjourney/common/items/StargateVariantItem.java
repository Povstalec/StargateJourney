package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

import javax.annotation.Nullable;

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
		return stack.has(DataComponentInit.STARGATE_VARIANT);
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        if(stack.has(DataComponentInit.STARGATE_VARIANT))
        {
			ResourceLocation variant = getStargateVariant(stack);
			
			Minecraft minecraft = Minecraft.getInstance();
			ClientPacketListener clientPacketListener = minecraft.getConnection();
			
			if(clientPacketListener != null)
			{
				RegistryAccess registries = clientPacketListener.registryAccess();
				Registry<StargateVariant> stargateVariantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
				
				if(stargateVariantRegistry.containsKey(variant))
				{
					ResourceLocation baseStargate = stargateVariantRegistry.get(variant).getBaseStargate();
					
					if(BuiltInRegistries.BLOCK.containsKey(baseStargate))
					{
						Block block = BuiltInRegistries.BLOCK.get(stargateVariantRegistry.get(variant).getBaseStargate());
						
						if(block != null)
							tooltipComponents.add(Component.translatable("tooltip.sgjourney.requires").append(Component.literal(": ").append(block.getName())).withStyle(ChatFormatting.LIGHT_PURPLE));
					}
					
					if(tooltipFlag.isAdvanced())
						tooltipComponents.add(Component.translatable("tooltip.sgjourney.requires").append(Component.literal(": " + baseStargate.toString())).withStyle(ChatFormatting.BLUE));
				}
			}
			
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.variant").append(Component.literal(": " + variant)).withStyle(ChatFormatting.GREEN));
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.stargate_variant.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	@Nullable
	public static ResourceLocation getStargateVariant(ItemStack stack)
	{
		return stack.get(DataComponentInit.STARGATE_VARIANT);
	}
	
	public static <StargateBlock extends AbstractStargateBlock> ItemStack stargateVariant(ResourceLocation variant)
	{
		ItemStack stack = new ItemStack(ItemInit.STARGATE_VARIANT_CRYSTAL.get());
		stack.set(DataComponentInit.STARGATE_VARIANT, variant);
		
		return stack;
	}
}
