package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;

import java.util.Optional;

/**
 * Credit for all this goes to cookta2012
 */
public class SGJourneyItemSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack>
{
	public static final SGJourneyItemSubtypeInterpreter INSTANCE = new SGJourneyItemSubtypeInterpreter();
	
	public SGJourneyItemSubtypeInterpreter() {}
	
	@Override
	public String apply(ItemStack ingredient, UidContext context)
	{
		String subtypeInfo = "";
		if(ingredient.getItem() instanceof StargateVariantItem)
		{
			ResourceLocation resourceLocation = StargateVariantItem.getVariant(ingredient);
			if(resourceLocation != null)
				subtypeInfo = resourceLocation.toString();
		}
		else if (ingredient.getItem() instanceof StargateUpgradeItem)
		{
			Optional<String> resourceLocation = StargateUpgradeItem.getStargateString(ingredient);
			if(resourceLocation.isPresent())
				subtypeInfo = resourceLocation.get();
		}
		
		return subtypeInfo;
	}
}
