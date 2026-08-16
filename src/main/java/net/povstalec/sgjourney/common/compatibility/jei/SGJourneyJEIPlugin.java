package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class SGJourneyJEIPlugin implements IModPlugin
{
	private static final ResourceLocation PLUGIN_LOCATION = StargateJourney.sgjourneyLocation("jei_plugin");
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
	public void registerRecipes(@NotNull IRecipeRegistration registration)
	{
		RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		
		// Naquadah Liquidizer
		
		List<RecipeHolder<LiquidizingRecipe.NaquadahLiquidizer>> naquadahLiquidizerHolders = recipeManager.getAllRecipesFor(LiquidizingRecipe.NaquadahLiquidizer.TYPE);
		ArrayList<LiquidizingRecipe.NaquadahLiquidizer> naquadahLiquidizingRecipes = new ArrayList<>();
		for(RecipeHolder<LiquidizingRecipe.NaquadahLiquidizer> holder : naquadahLiquidizerHolders)
		{
			naquadahLiquidizingRecipes.add(holder.value());
		}
		registration.addRecipes(LiquidizingRecipeCategory.NaquadahLiquidizer.TYPE, naquadahLiquidizingRecipes);
		
		// Heavy Naquadah Liquidizer
		
		List<RecipeHolder<LiquidizingRecipe.HeavyNaquadahLiquidizer>> heavyNaquadahLiquidizerHolders = recipeManager.getAllRecipesFor(LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE);
		ArrayList<LiquidizingRecipe.HeavyNaquadahLiquidizer> heavyNaquadahLiquidizingRecipes = new ArrayList<>();
		for(RecipeHolder<LiquidizingRecipe.HeavyNaquadahLiquidizer> holder : heavyNaquadahLiquidizerHolders)
		{
			heavyNaquadahLiquidizingRecipes.add(holder.value());
		}
		registration.addRecipes(LiquidizingRecipeCategory.HeavyNaquadahLiquidizer.TYPE, heavyNaquadahLiquidizingRecipes);
		
		// Crystallizer
		
		List<RecipeHolder<CrystallizingRecipe.Crystallizer>> crystallizerHolders = recipeManager.getAllRecipesFor(CrystallizingRecipe.Crystallizer.TYPE);
		ArrayList<CrystallizingRecipe.Crystallizer> crystallizerRecipes = new ArrayList<>();
		for(RecipeHolder<CrystallizingRecipe.Crystallizer> holder : crystallizerHolders)
		{
			crystallizerRecipes.add(holder.value());
		}
		registration.addRecipes(CrystallizingRecipeCategory.Crystallizer.TYPE, crystallizerRecipes);
		
		// Advanced Crystallizer

		List<RecipeHolder<CrystallizingRecipe.AdvancedCrystallizer>> advancedCrystallizerHolders = recipeManager.getAllRecipesFor(CrystallizingRecipe.AdvancedCrystallizer.TYPE);
		ArrayList<CrystallizingRecipe.AdvancedCrystallizer> advancedCrystallizerRecipes = new ArrayList<>();
		for(RecipeHolder<CrystallizingRecipe.AdvancedCrystallizer> holder : advancedCrystallizerHolders)
		{
			advancedCrystallizerRecipes.add(holder.value());
		}
		registration.addRecipes(CrystallizingRecipeCategory.AdvancedCrystallizer.TYPE, advancedCrystallizerRecipes);
	}
	
	@Override
	public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration)
	{
		// Crystallizers
		
		var naquadahLiquidizerItem = BlockInit.NAQUADAH_LIQUIDIZER.asItem();
		if(naquadahLiquidizerItem != null)
			registration.addRecipeCatalyst(new ItemStack(naquadahLiquidizerItem), LiquidizingRecipeCategory.NaquadahLiquidizer.TYPE);
		
		var heavyNaquadahLiquidizerItem = BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.asItem();
		if(heavyNaquadahLiquidizerItem != null)
			registration.addRecipeCatalyst(new ItemStack(heavyNaquadahLiquidizerItem), LiquidizingRecipeCategory.HeavyNaquadahLiquidizer.TYPE);
		
		// Crystallizers
		
		var crystallizerItem = BlockInit.CRYSTALLIZER.asItem();
		if(crystallizerItem != null)
			registration.addRecipeCatalyst(new ItemStack(crystallizerItem), CrystallizingRecipeCategory.Crystallizer.TYPE);
		
		var advancedCrystallizerItem = BlockInit.ADVANCED_CRYSTALLIZER.asItem();
		if(advancedCrystallizerItem != null)
			registration.addRecipeCatalyst(new ItemStack(advancedCrystallizerItem), CrystallizingRecipeCategory.AdvancedCrystallizer.TYPE);
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
