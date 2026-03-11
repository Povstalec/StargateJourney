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
import net.minecraft.world.level.material.Fluids;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.recipe.LiquidizerRecipe;

public class LiquidizerRecipeCategory implements IRecipeCategory<LiquidizerRecipe>
{
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(StargateJourney.MODID, "liquidizing");
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/jei/naquadah_liquidizer_gui.png");
	
	public static final RecipeType<LiquidizerRecipe> LIQUIDIZING_TYPE = new RecipeType<>(RECIPE_ID, LiquidizerRecipe.class);
	
	private final IDrawable background;
	private final IDrawable icon;
	
	public LiquidizerRecipeCategory(IGuiHelper helper)
	{
		this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.NAQUADAH_LIQUIDIZER.get()));
	}
	
	@Override
	public RecipeType<LiquidizerRecipe> getRecipeType()
	{
		return LIQUIDIZING_TYPE;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable("block.sgjourney.naquadah_liquidizer");
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
	public void setRecipe(IRecipeLayoutBuilder builder, LiquidizerRecipe recipe, IFocusGroup focuses)
	{
		ItemStack stack = recipe.getIngredients().get(0).getItems()[0].copy();

		// Lava Tank
		builder.addSlot(RecipeIngredientRole.INPUT, 12, 20)
				.addFluidStack(Fluids.LAVA, recipe.inputAmount)
				.setFluidRenderer(recipe.inputAmount, false, 16, 54);

		// Upper Slot
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 20).addItemStack(stack);
		
		// Liquid Naquadah Tank
		builder.addSlot(RecipeIngredientRole.OUTPUT, 148, 20)
				.addFluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), recipe.outputAmount)
				.setFluidRenderer(recipe.outputAmount, false, 16, 54);
	}
}
