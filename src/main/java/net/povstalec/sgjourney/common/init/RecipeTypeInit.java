package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.recipe.AdvancedCrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.HeavyLiquidizerRecipe;
import net.povstalec.sgjourney.common.recipe.LiquidizerRecipe;

public class RecipeTypeInit
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StargateJourney.MODID);
	
	public static final RegistryObject<RecipeSerializer<LiquidizerRecipe>> LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_liquidizing", () -> LiquidizerRecipe.Serializer.INSTANCE);
	public static final RegistryObject<RecipeSerializer<HeavyLiquidizerRecipe>> HEAVY_LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_heavy_liquidizing", () -> HeavyLiquidizerRecipe.Serializer.INSTANCE);
	
	public static final RegistryObject<RecipeSerializer<CrystallizerRecipe>> CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("crystallizing", () -> CrystallizerRecipe.Serializer.INSTANCE);
	public static final RegistryObject<RecipeSerializer<AdvancedCrystallizerRecipe>> ADVANCED_CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("advanced_crystallizing", () -> AdvancedCrystallizerRecipe.Serializer.INSTANCE);
	
	public static void register(IEventBus eventBus)
	{
		SERIALIZERS.register(eventBus);
	}
}
