package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.recipe.*;

public class RecipeTypeInit
{
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StargateJourney.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StargateJourney.MODID);
	
	
	
	public static final RegistryObject<RecipeType<LiquidizingRecipe.NaquadahLiquidizer>> LIQUIDIZING_TYPE = TYPES.register("naquadah_liquidizing", () -> LiquidizingRecipe.NaquadahLiquidizer.TYPE);
	public static final RegistryObject<RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer>> HEAVY_LIQUIDIZING_TYPE = TYPES.register("naquadah_heavy_liquidizing", () -> LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE);
	
	public static final RegistryObject<RecipeType<CrystallizingRecipe.Crystallizer>> CRYSTALLIZING_TYPE = TYPES.register("crystallizing", () -> CrystallizingRecipe.Crystallizer.TYPE);
	public static final RegistryObject<RecipeType<CrystallizingRecipe.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZING_TYPE = TYPES.register("advanced_crystallizing", () -> CrystallizingRecipe.AdvancedCrystallizer.TYPE);
	
	
	
	public static final RegistryObject<RecipeSerializer<LiquidizingRecipe.NaquadahLiquidizer>> LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_liquidizing", () -> LiquidizingRecipe.NaquadahLiquidizerSerializer.INSTANCE);
	public static final RegistryObject<RecipeSerializer<LiquidizingRecipe.HeavyNaquadahLiquidizer>> HEAVY_LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_heavy_liquidizing", () -> LiquidizingRecipe.HeavyNaquadahLiquidizerSerializer.INSTANCE);
	
	public static final RegistryObject<RecipeSerializer<CrystallizingRecipe.Crystallizer>> CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("crystallizing", () -> CrystallizingRecipe.CrystallizerSerializer.INSTANCE);
	public static final RegistryObject<RecipeSerializer<CrystallizingRecipe.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("advanced_crystallizing", () -> CrystallizingRecipe.AdvancedCrystallizerSerializer.INSTANCE);
	
	
	
	public static void register(IEventBus eventBus)
	{
		TYPES.register(eventBus);
		SERIALIZERS.register(eventBus);
	}
}
