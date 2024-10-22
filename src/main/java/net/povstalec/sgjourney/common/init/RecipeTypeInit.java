package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.recipe.AdvancedCrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

public class RecipeTypeInit
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, StargateJourney.MODID);
	
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystallizerRecipe>> CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("crystallizing", () -> CrystallizerRecipe.Serializer.INSTANCE);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AdvancedCrystallizerRecipe>> ADVANCED_CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("advanced_crystallizing", () -> AdvancedCrystallizerRecipe.Serializer.INSTANCE);
	
	public static void register(IEventBus eventBus)
	{
		SERIALIZERS.register(eventBus);
	}
}
