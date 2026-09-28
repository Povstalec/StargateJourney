package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;
import net.povstalec.sgjourney.common.recipe.NBTRetainingShapedRecipe;

public class RecipeTypeInit
{
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, StargateJourney.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, StargateJourney.MODID);
	
	// Recipe Types
	
	public static final DeferredHolder<RecipeType<?>, RecipeType<NBTRetainingShapedRecipe>> NBT_RETAINING_SHAPED_RECIPE_TYPE = TYPES.register("nbt_retaining_shaped_recipe", () -> NBTRetainingShapedRecipe.TYPE);
	
	public static final DeferredHolder<RecipeType<?>, RecipeType<LiquidizingRecipe.NaquadahLiquidizer>> LIQUIDIZING_TYPE = TYPES.register("naquadah_liquidizing", () -> LiquidizingRecipe.NaquadahLiquidizer.TYPE);
	public static final DeferredHolder<RecipeType<?>, RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer>> HEAVY_LIQUIDIZING_TYPE = TYPES.register("naquadah_heavy_liquidizing", () -> LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE);
	
	public static final DeferredHolder<RecipeType<?>, RecipeType<CrystallizingRecipe.Crystallizer>> CRYSTALLIZING_TYPE = TYPES.register("crystallizing", () -> CrystallizingRecipe.Crystallizer.TYPE);
	public static final DeferredHolder<RecipeType<?>, RecipeType<CrystallizingRecipe.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZING_TYPE = TYPES.register("advanced_crystallizing", () -> CrystallizingRecipe.AdvancedCrystallizer.TYPE);
	
	// Recipe Serializers
	
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<NBTRetainingShapedRecipe>> NBT_RETAINING_SHAPED_RECIPE_SERIALIZER = SERIALIZERS.register("nbt_retaining_shaped_recipe", () -> NBTRetainingShapedRecipe.Serializer.INSTANCE);
	
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LiquidizingRecipe.NaquadahLiquidizer>> LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_liquidizing", () -> LiquidizingRecipe.NaquadahLiquidizerSerializer.INSTANCE);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LiquidizingRecipe.HeavyNaquadahLiquidizer>> HEAVY_LIQUIDIZING_SERIALIZER = SERIALIZERS.register("naquadah_heavy_liquidizing", () -> LiquidizingRecipe.HeavyNaquadahLiquidizerSerializer.INSTANCE);
	
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystallizingRecipe.Crystallizer>> CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("crystallizing", () -> CrystallizingRecipe.CrystallizerSerializer.INSTANCE);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrystallizingRecipe.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZING_SERIALIZER = SERIALIZERS.register("advanced_crystallizing", () -> CrystallizingRecipe.AdvancedCrystallizerSerializer.INSTANCE);
	
	
	
	public static void register(IEventBus eventBus)
	{
		TYPES.register(eventBus);
		SERIALIZERS.register(eventBus);
	}
}
