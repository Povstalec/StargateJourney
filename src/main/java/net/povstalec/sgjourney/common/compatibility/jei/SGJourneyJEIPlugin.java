package net.povstalec.sgjourney.common.compatibility.jei;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.recipe.AdvancedCrystallizerRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

@JeiPlugin
public class SGJourneyJEIPlugin implements IModPlugin
{
	private static final ResourceLocation PLUGIN_LOCATION = StargateJourney.sgjourneyLocation("jei_plugin");

	private static Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public ResourceLocation getPluginUid()
	{
		return PLUGIN_LOCATION;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(new CrystallizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()), new AdvancedCrystallizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		RecipeManager recipeManager = Objects.requireNonNull(minecraft.level).getRecipeManager();
		
		List<RecipeHolder<CrystallizerRecipe>> crystallizerRecipes = recipeManager.getAllRecipesFor(CrystallizerRecipe.Type.CRYSTALLIZING);
		RecipeType<RecipeHolder<CrystallizerRecipe>> crystallizingType = RecipeType.createRecipeHolderType(CrystallizerRecipeCategory.RECIPE_ID);
		registration.addRecipes(crystallizingType, crystallizerRecipes);

		List<RecipeHolder<AdvancedCrystallizerRecipe>> advancedCrystallizerRecipes = recipeManager.getAllRecipesFor(AdvancedCrystallizerRecipe.Type.ADVANCED_CRYSTALLIZING);
		RecipeType<RecipeHolder<AdvancedCrystallizerRecipe>> advancedCrystallizingType = RecipeType.createRecipeHolderType(AdvancedCrystallizerRecipeCategory.RECIPE_ID);
		registration.addRecipes(advancedCrystallizingType, advancedCrystallizerRecipes);
	}
	@Override
	public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration)
	{
		// Crystallizers
		var item1 = BlockInit.CRYSTALLIZER.asItem();
		if(item1 != null)
			registration.addRecipeCatalyst(new ItemStack(item1), CrystallizerRecipeCategory.CRYSTALLIZING_TYPE);
		
		var item2 = BlockInit.ADVANCED_CRYSTALLIZER.asItem();
		if(item2 != null)
			registration.addRecipeCatalyst(new ItemStack(item2), AdvancedCrystallizerRecipeCategory.ADVANCED_CRYSTALLIZING_TYPE);
	}
}
