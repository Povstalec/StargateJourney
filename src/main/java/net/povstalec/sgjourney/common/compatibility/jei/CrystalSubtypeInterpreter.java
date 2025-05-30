package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;

import java.util.Optional;
import java.util.stream.Stream;

public class CrystalSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final CrystalSubtypeInterpreter INSTANCE = new CrystalSubtypeInterpreter();
    private CrystalSubtypeInterpreter() { }

    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        String subtypeInfo = "";
        if (ingredient.getItem() instanceof StargateVariantItem) {
            ResourceLocation resourceLocation = StargateVariantItem.getVariant(ingredient);
            if (resourceLocation != null) subtypeInfo = resourceLocation.toString();
        } else if (ingredient.getItem() instanceof StargateUpgradeItem) {
            Optional<String> resourceLocation = StargateUpgradeItem.getStargateString(ingredient);
            if (resourceLocation.isPresent()) subtypeInfo = resourceLocation.get();
        }
        //subtypeInfo = subtypeInfo;
        return subtypeInfo;
    }
}
