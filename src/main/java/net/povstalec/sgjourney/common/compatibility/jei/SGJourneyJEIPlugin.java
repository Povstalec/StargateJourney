package net.povstalec.sgjourney.common.compatibility.jei;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.menu.CrystallizerMenu;
import net.povstalec.sgjourney.common.menu.LiquidizerMenu;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class SGJourneyJEIPlugin implements IModPlugin
{
	private static final ResourceLocation PLUGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "jei_plugin");
	private static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
	
	@Override
	public @NotNull ResourceLocation getPluginUid()
	{
		return PLUGIN_LOCATION;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(new LiquidizingRecipeCategory.NaquadahLiquidizer(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new LiquidizingRecipeCategory.HeavyNaquadahLiquidizer(registration.getJeiHelpers().getGuiHelper()));
		
		registration.addRecipeCategories(new CrystallizingRecipeCategory.Crystallizer(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new CrystallizingRecipeCategory.AdvancedCrystallizer(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		
		List<LiquidizingRecipe.NaquadahLiquidizer> liquidizerRecipes = recipeManager.getAllRecipesFor(LiquidizingRecipe.NaquadahLiquidizer.TYPE);
		registration.addRecipes(new RecipeType<>(LiquidizingRecipeCategory.NaquadahLiquidizer.RECIPE_ID, LiquidizingRecipe.NaquadahLiquidizer.class), liquidizerRecipes);
		
		List<LiquidizingRecipe.HeavyNaquadahLiquidizer> heavyLiquidizerRecipes = recipeManager.getAllRecipesFor(LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE);
		registration.addRecipes(new RecipeType<>(LiquidizingRecipeCategory.HeavyNaquadahLiquidizer.RECIPE_ID, LiquidizingRecipe.HeavyNaquadahLiquidizer.class), heavyLiquidizerRecipes);
		
		List<CrystallizingRecipe.Crystallizer> crystallizerRecipes = recipeManager.getAllRecipesFor(CrystallizingRecipe.Crystallizer.TYPE);
		registration.addRecipes(new RecipeType<>(CrystallizingRecipeCategory.Crystallizer.RECIPE_ID, CrystallizingRecipe.Crystallizer.class), crystallizerRecipes);

		List<CrystallizingRecipe.AdvancedCrystallizer> advancedCrystallizerRecipes = recipeManager.getAllRecipesFor(CrystallizingRecipe.AdvancedCrystallizer.TYPE);
		registration.addRecipes(new RecipeType<>(CrystallizingRecipeCategory.AdvancedCrystallizer.RECIPE_ID, CrystallizingRecipe.AdvancedCrystallizer.class), advancedCrystallizerRecipes);
	}
	@Override
	public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration)
	{
		// Liquidizers
		BlockInit.NAQUADAH_LIQUIDIZER.ifPresent(liquidizerBlock ->
		{
			var item = liquidizerBlock.asItem();
			if(item != null)
				registration.addRecipeCatalyst(new ItemStack(item), LiquidizingRecipeCategory.NaquadahLiquidizer.TYPE);
		});
		BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.ifPresent(liquidizerBlock ->
		{
			var item = liquidizerBlock.asItem();
			if(item != null)
				registration.addRecipeCatalyst(new ItemStack(item), LiquidizingRecipeCategory.HeavyNaquadahLiquidizer.TYPE);
		});
		
		// Crystallizers
		BlockInit.CRYSTALLIZER.ifPresent(crystallizerBlock ->
		{
			var item = crystallizerBlock.asItem();
			if(item != null)
				registration.addRecipeCatalyst(new ItemStack(item), CrystallizingRecipeCategory.Crystallizer.TYPE);
		});
		BlockInit.ADVANCED_CRYSTALLIZER.ifPresent(crystallizerBlock ->
		{
			var item = crystallizerBlock.asItem();
			if(item != null)
				registration.addRecipeCatalyst(new ItemStack(item), CrystallizingRecipeCategory.AdvancedCrystallizer.TYPE);
		});
	}
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration)
	{
		registration.registerSubtypeInterpreter(ItemInit.STARGATE_VARIANT_CRYSTAL.get(), SGJourneyItemSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemInit.STARGATE_UPGRADE_CRYSTAL.get(), SGJourneyItemSubtypeInterpreter.INSTANCE);
	}
	
	//TODO custom recipe transfer handlers
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		/*
		JEI contains "BUG"/missing feature that would allow transfer more than a single item for input slots
		https://github.com/mezz/JustEnoughItems/issues/3146
		Can be solved with own custom RecipeTransferHandler implementation
		 */
		/*registration.addRecipeTransferHandler(LiquidizerMenu.LiquidNaquadah.class, MenuInit.NAQUADAH_LIQUIDIZER.get(), LiquidizingRecipeCategory.NaquadahLiquidizer.TYPE, 36, 1, 0, PLAYER_INVENTORY_SLOT_COUNT);
		registration.addRecipeTransferHandler(LiquidizerMenu.HeavyLiquidNaquadah.class, MenuInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), LiquidizingRecipeCategory.HeavyNaquadahLiquidizer.TYPE, 36, 1, 0, PLAYER_INVENTORY_SLOT_COUNT);
		
		registration.addRecipeTransferHandler(CrystallizerMenu.Crystallizer.class, MenuInit.CRYSTALLIZER.get(), CrystallizingRecipeCategory.Crystallizer.TYPE, 36, 5, 0, PLAYER_INVENTORY_SLOT_COUNT);
		registration.addRecipeTransferHandler(CrystallizerMenu.AdvancedCrystallizer.class, MenuInit.ADVANCED_CRYSTALLIZER.get(), CrystallizingRecipeCategory.AdvancedCrystallizer.TYPE, 36, 5, 0, PLAYER_INVENTORY_SLOT_COUNT);*/
	}
}
