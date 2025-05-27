package net.povstalec.sgjourney.common.compatibility.jei;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.recipe.AdvancedCrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

@JeiPlugin
public class SGJourneyJEIPlugin implements IModPlugin
{
	private static final ResourceLocation PLUGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "jei_plugin");

	private static Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return PLUGIN_LOCATION;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(new CrystallizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new AdvancedCrystallizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		RecipeManager recipeManager = Objects.requireNonNull(minecraft.level).getRecipeManager();
		
		List<CrystallizerRecipe> crystallizerRecipes = recipeManager.getAllRecipesFor(CrystallizerRecipe.Type.INSTANCE);
		registration.addRecipes(new RecipeType<>(CrystallizerRecipeCategory.RECIPE_ID, CrystallizerRecipe.class), crystallizerRecipes);

		List<AdvancedCrystallizerRecipe> advancedCrystallizerRecipes = recipeManager.getAllRecipesFor(AdvancedCrystallizerRecipe.Type.INSTANCE);
		registration.addRecipes(new RecipeType<>(AdvancedCrystallizerRecipeCategory.RECIPE_ID, AdvancedCrystallizerRecipe.class), advancedCrystallizerRecipes);
	}
	@Override
	public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration)
	{
		// Crystallizers
		BlockInit.CRYSTALLIZER.ifPresent(crystallizerBlock -> {
			var item = crystallizerBlock.asItem();
			if (item != null) {
				registration.addRecipeCatalyst(new ItemStack(item), CrystallizerRecipeCategory.CRYSTALLIZING_TYPE);
			}
		});
		BlockInit.ADVANCED_CRYSTALLIZER.ifPresent(crystallizerBlock -> {
			var item = crystallizerBlock.asItem();
			if (item != null) {
				registration.addRecipeCatalyst(new ItemStack(item), AdvancedCrystallizerRecipeCategory.ADVANCED_CRYSTALLIZING_TYPE);
			}
		});
	}
	@Override
	public void registerItemSubtypes(ISubtypeRegistration reg) {
		reg.registerSubtypeInterpreter(
				ItemInit.STARGATE_VARIANT_CRYSTAL.get(),
				(stack, ctx) -> {
					CompoundTag tag = stack.getTag();
					return tag != null && tag.contains("Variant")
							? tag.getString("Variant")
							: IIngredientSubtypeInterpreter.NONE;
				});
		reg.registerSubtypeInterpreter(
				ItemInit.STARGATE_UPGRADE_CRYSTAL.get(),
				(stack, ctx) -> {
					CompoundTag tag = stack.getTag();
					return tag != null && tag.contains("Type")
							? tag.getString("Type")
							: IIngredientSubtypeInterpreter.NONE;
				});
	}

}
