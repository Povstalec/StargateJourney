package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.recipe.HeavyLiquidizerRecipe;

public class HeavyLiquidizerRecipeCategory implements IRecipeCategory<HeavyLiquidizerRecipe>
{
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(StargateJourney.MODID, "naquadah_heavy_liquidizing");
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/jei/heavy_naquadah_liquidizer_gui.png");
	
	public static final RecipeType<HeavyLiquidizerRecipe> HEAVY_LIQUIDIZING_TYPE = new RecipeType<>(RECIPE_ID, HeavyLiquidizerRecipe.class);
	
	private final IDrawable background;
	private final IDrawable icon;
	
	public HeavyLiquidizerRecipeCategory(IGuiHelper helper)
	{
		this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.NAQUADAH_LIQUIDIZER.get()));
	}
	
	@Override
	public RecipeType<HeavyLiquidizerRecipe> getRecipeType()
	{
		return HEAVY_LIQUIDIZING_TYPE;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable("block.sgjourney.heavy_naquadah_liquidizer");
	}

	@Override
	public IDrawable getBackground()
	{
		return this.background;
	}

	@Override
	public IDrawable getIcon()
	{
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, HeavyLiquidizerRecipe recipe, IFocusGroup focuses)
	{
		ItemStack stack = recipe.getIngredients().get(0).getItems()[0].copy();

		// Lava Tank
		builder.addSlot(RecipeIngredientRole.INPUT, 12, 20)
				.addFluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), recipe.getInputAmount())
				.setFluidRenderer(recipe.getInputAmount(), false, 16, 54);

		// Upper Slot
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 20).addItemStack(stack);
		
		// Liquid Naquadah Tank
		builder.addSlot(RecipeIngredientRole.OUTPUT, 148, 20)
				.addFluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), recipe.getOutputAmount())
				.setFluidRenderer(recipe.getOutputAmount(), false, 16, 54);
	}
}
