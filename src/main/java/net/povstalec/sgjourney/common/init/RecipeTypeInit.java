package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.recipe.AdvancedCrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

public class RecipeTypeInit
{
	/* -------- serializers -------- */
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
			DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StargateJourney.MODID);

	public static final RegistryObject<RecipeSerializer<CrystallizerRecipe>> CRYSTALLIZING_SERIALIZER =
			SERIALIZERS.register(CrystallizerRecipe.Type.ID, () -> CrystallizerRecipe.Serializer.INSTANCE);

	public static final RegistryObject<RecipeSerializer<AdvancedCrystallizerRecipe>> ADVANCED_CRYSTALLIZING_SERIALIZER =
			SERIALIZERS.register(AdvancedCrystallizerRecipe.Type.ID, () -> AdvancedCrystallizerRecipe.Serializer.INSTANCE);

	/* -------- recipe types -------- */
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
			DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StargateJourney.MODID);

	public static final RegistryObject<RecipeType<CrystallizerRecipe>> CRYSTALLIZING =
			RECIPE_TYPES.register(CrystallizerRecipe.Type.ID,
					() -> CrystallizerRecipe.Type.INSTANCE);

	public static final RegistryObject<RecipeType<AdvancedCrystallizerRecipe>> ADVANCED_CRYSTALLIZING =
			RECIPE_TYPES.register(AdvancedCrystallizerRecipe.Type.ID,
					() -> AdvancedCrystallizerRecipe.Type.INSTANCE);

	public static void register(IEventBus bus)
	{
		SERIALIZERS.register(bus);
		RECIPE_TYPES.register(bus);
	}
}

