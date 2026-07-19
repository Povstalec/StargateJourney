package net.povstalec.sgjourney.common.compatibility.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import org.jetbrains.annotations.NotNull;

public abstract class CrystallizingRecipeCategory<T extends CrystallizingRecipe> implements IRecipeCategory<T>
{
	protected ResourceLocation texture;
	protected final IDrawable background;
	protected final IDrawable icon;
	
	public CrystallizingRecipeCategory(IGuiHelper helper, ResourceLocation texture, ItemStack iconBase)
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
		ItemStack stack1 = recipe.getIngredients().get(0).getItems()[0].copy();
		ItemStack stack2 = recipe.getIngredients().get(1).getItems()[0].copy();
		ItemStack stack3 = recipe.getIngredients().get(2).getItems()[0].copy();
		
		stack1.setCount(recipe.getAmountInSlot(0));
		stack2.setCount(recipe.getAmountInSlot(1));
		stack3.setCount(recipe.getAmountInSlot(2));
		
		// Input Tank
		builder.addSlot(RecipeIngredientRole.INPUT, 34, 17)
				.addFluidStack(recipe.getInputFluid().getFluid(), recipe.getInputFluid().getAmount())
				.setFluidRenderer(recipe.getInputFluid().getAmount(), false, 16, 52);
		
		// Upper Slot
		builder.addSlot(RecipeIngredientRole.INPUT, 71, 17).addItemStack(stack1);
		// Lower Left Slot
		builder.addSlot(recipe.depletePrimary() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 55, 53).addItemStack(stack2);
		// Lower Right Slot
		builder.addSlot(recipe.depleteSecondary() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 87, 53).addItemStack(stack3);
		
		// Result Slot
		builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 36).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}
	
	//============================================================================================
	//****************************************Crystallizer****************************************
	//============================================================================================
	
	public static class Crystallizer extends CrystallizingRecipeCategory<CrystallizingRecipe.Crystallizer>
	{
		public static final ResourceLocation RECIPE_ID = StargateJourney.sgjourneyLocation("crystallizing");
		
		public static final RecipeType<CrystallizingRecipe.Crystallizer> TYPE = new RecipeType<>(RECIPE_ID, CrystallizingRecipe.Crystallizer.class);
		
		public Crystallizer(IGuiHelper helper)
		{
			super(helper, StargateJourney.sgjourneyLocation("textures/gui/jei/crystallizer_gui.png"), new ItemStack(BlockInit.CRYSTALLIZER.get()));
		}
		
		@Override
		public @NotNull RecipeType<CrystallizingRecipe.Crystallizer> getRecipeType()
		{
			return TYPE;
		}
		
		@Override
		public @NotNull Component getTitle()
		{
			return Component.translatable("block.sgjourney.crystallizer");
		}
	}
	
	//============================================================================================
	//***********************************Advanced Crystallizer************************************
	//============================================================================================
	
	public static class AdvancedCrystallizer extends CrystallizingRecipeCategory<CrystallizingRecipe.AdvancedCrystallizer>
	{
		public static final ResourceLocation RECIPE_ID = StargateJourney.sgjourneyLocation("advanced_crystallizing");
		
		public static final RecipeType<CrystallizingRecipe.AdvancedCrystallizer> TYPE = new RecipeType<>(RECIPE_ID, CrystallizingRecipe.AdvancedCrystallizer.class);
		
		public AdvancedCrystallizer(IGuiHelper helper)
		{
			super(helper, StargateJourney.sgjourneyLocation("textures/gui/jei/advanced_crystallizer_gui.png"), new ItemStack(BlockInit.ADVANCED_CRYSTALLIZER.get()));
		}
		
		@Override
		public @NotNull RecipeType<CrystallizingRecipe.AdvancedCrystallizer> getRecipeType()
		{
			return TYPE;
		}
		
		@Override
		public @NotNull Component getTitle()
		{
			return Component.translatable("block.sgjourney.advanced_crystallizer");
		}
	}
}
