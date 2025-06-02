package net.povstalec.sgjourney.common.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public class RecipeUtil {
    /* //Must remain for 1.19.2
    public static <T> NonNullList<ItemStack> getAllVariants(Class<T> type, String typeKey) {
        NonNullList<ItemStack> variants = NonNullList.create();
        Level level = Minecraft.getInstance().level;
        if (level == null) return variants;

        for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
            ItemStack result = recipe.getResultItem();
            if (type.isInstance(result.getItem()) && result.getOrCreateTag().contains(typeKey)) {
                variants.add(result);
            }
        }

        return variants;
    }
     */
}
