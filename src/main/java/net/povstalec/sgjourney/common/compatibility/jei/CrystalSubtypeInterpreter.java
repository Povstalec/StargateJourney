package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.items.StargateVariantItem;

import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class CrystalSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final CrystalSubtypeInterpreter INSTANCE = new CrystalSubtypeInterpreter();
    private CrystalSubtypeInterpreter() { }

    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (ingredient.getItem() instanceof StargateVariantItem) {
            return ingredient.get(DataComponentInit.STARGATE_VARIANT);
        } else {
            return ingredient.getItem() instanceof StargateUpgradeItem ? ingredient.get(DataComponentInit.STARGATE_UPGRADE) : null;
        }
    }

    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        String subtypeInfo = "";
        ResourceLocation path;
        if (ingredient.getItem() instanceof StargateVariantItem) {
            path = (ResourceLocation)ingredient.get(DataComponentInit.STARGATE_VARIANT);
            if (path != null) {
                subtypeInfo = path.toString();
            }
        } else if (ingredient.getItem() instanceof StargateUpgradeItem) {
            path = (ResourceLocation)ingredient.get(DataComponentInit.STARGATE_UPGRADE);
            if (path != null) {
                subtypeInfo = path.toString();
            }
        }

        return subtypeInfo;
    }
}
