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
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;
import org.jetbrains.annotations.NotNull;

public abstract class LiquidizingRecipeCategory<T extends LiquidizingRecipe> implements IRecipeCategory<T>
{
	protected ResourceLocation texture;
	protected final IDrawable background;
	protected final IDrawable icon;
	
	public LiquidizingRecipeCategory(IGuiHelper helper, ResourceLocation texture, ItemStack iconBase)
	{
		this.texture = texture;
		this.background = helper.createDrawable(this.texture, 0, 0, 176, 85);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconBase);
	}
	
	@Override
	public @NotNull IDrawable getBackground()
	{
		return this.background;
	}
	
	@Override
	public @NotNull IDrawable getIcon()
	{
		return this.icon;
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, @NotNull IFocusGroup focuses)
	{
		ItemStack stack = recipe.getIngredients().get(0).getItems()[0].copy();
		
		// Input Tank
		builder.addSlot(RecipeIngredientRole.INPUT, 34, 17)
				.addFluidStack(recipe.getInputFluid().getFluid(), recipe.getInputFluid().getAmount())
				.setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52);
		
		// Upper Slot
		builder.addSlot(RecipeIngredientRole.INPUT, 62, 17).addItemStack(stack);
		
		// Output Tank
		builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 17)
				.addFluidStack(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount())
				.setFluidRenderer(recipe.getOutputFluid().getAmount(), false, 16, 52);
	}
	
	//============================================================================================
	//*************************************Naquadah Liquidizer************************************
	//============================================================================================
	
	public static class NaquadahLiquidizer extends LiquidizingRecipeCategory<LiquidizingRecipe.NaquadahLiquidizer>
	{
		public static final ResourceLocation RECIPE_ID = new ResourceLocation(StargateJourney.MODID, "naquadah_liquidizing");
		
		public static final RecipeType<LiquidizingRecipe.NaquadahLiquidizer> TYPE = new RecipeType<>(RECIPE_ID, LiquidizingRecipe.NaquadahLiquidizer.class);
		
		public NaquadahLiquidizer(IGuiHelper helper)
		{
			super(helper, new ResourceLocation(StargateJourney.MODID, "textures/gui/jei/naquadah_liquidizer_gui.png"), new ItemStack(BlockInit.NAQUADAH_LIQUIDIZER.get()));
		}
		
		@Override
		public @NotNull RecipeType<LiquidizingRecipe.NaquadahLiquidizer> getRecipeType()
		{
			return TYPE;
		}
		
		@Override
		public @NotNull Component getTitle()
		{
			return Component.translatable("block.sgjourney.naquadah_liquidizer");
		}
	}
	
	//============================================================================================
	//**********************************Heavy Naquadah Liquidizer*********************************
	//============================================================================================
	
	public static class HeavyNaquadahLiquidizer extends LiquidizingRecipeCategory<LiquidizingRecipe.HeavyNaquadahLiquidizer>
	{
		public static final ResourceLocation RECIPE_ID = new ResourceLocation(StargateJourney.MODID, "naquadah_heavy_liquidizing");
		
		public static final RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer> TYPE = new RecipeType<>(RECIPE_ID, LiquidizingRecipe.HeavyNaquadahLiquidizer.class);
		
		public HeavyNaquadahLiquidizer(IGuiHelper helper)
		{
			super(helper, new ResourceLocation(StargateJourney.MODID, "textures/gui/jei/heavy_naquadah_liquidizer_gui.png"), new ItemStack(BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get()));
		}
		
		@Override
		public @NotNull RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer> getRecipeType()
		{
			return TYPE;
		}
		
		@Override
		public @NotNull Component getTitle()
		{
			return Component.translatable("block.sgjourney.heavy_naquadah_liquidizer");
		}
	}
}
