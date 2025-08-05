package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Credit for all this goes to cookta2012
 */
public class SGJourneyItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack>
{
	public static final SGJourneyItemSubtypeInterpreter INSTANCE = new SGJourneyItemSubtypeInterpreter();
	
	public SGJourneyItemSubtypeInterpreter() {}
	
	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context)
	{
		if(ingredient.getItem() instanceof StargateVariantItem)
			return ingredient.get(DataComponentInit.STARGATE_VARIANT);
		else
			return ingredient.getItem() instanceof StargateUpgradeItem ? ingredient.get(DataComponentInit.STARGATE_UPGRADE) : null;
	}
	
	@Override
	public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context)
	{
		String subtypeInfo = "";
		ResourceLocation path;
		if(ingredient.getItem() instanceof StargateVariantItem)
		{
			path = ingredient.get(DataComponentInit.STARGATE_VARIANT);
			if(path != null)
				subtypeInfo = path.toString();
		}
		else if(ingredient.getItem() instanceof StargateUpgradeItem)
		{
			path = ingredient.get(DataComponentInit.STARGATE_UPGRADE);
			if(path != null)
				subtypeInfo = path.toString();
		}
		
		return subtypeInfo;
	}
}
