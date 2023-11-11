package net.povstalec.sgjourney.common.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
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
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

public class CrystallizerRecipeCategory implements IRecipeCategory<CrystallizerRecipe>
{
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(StargateJourney.MODID, "crystallizing");
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/crystallizer_gui.png");
	
	public static final RecipeType<CrystallizerRecipe> CRYSTALLIZING_TYPE = new RecipeType<>(RECIPE_ID, CrystallizerRecipe.class);
	
	private final IDrawable background;
	private final IDrawable icon;
	
	public CrystallizerRecipeCategory(IGuiHelper helper)
	{
		this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockInit.CRYSTALLIZER.get()));
	}
	
	@Override
	public RecipeType<CrystallizerRecipe> getRecipeType()
	{
		return CRYSTALLIZING_TYPE;
	}

	@Override
	public Component getTitle()
	{
		return Component.translatable("block.sgjourney.crystallizer");
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
	public void setRecipe(IRecipeLayoutBuilder builder, CrystallizerRecipe recipe, IFocusGroup focuses)
	{
		ItemStack stack1 = recipe.getIngredients().get(0).getItems()[0];
		ItemStack stack2 = recipe.getIngredients().get(1).getItems()[0];
		ItemStack stack3 = recipe.getIngredients().get(2).getItems()[0];
		
		stack1.setCount(recipe.getAmountInSlot(0));
		stack2.setCount(recipe.getAmountInSlot(1));
		stack3.setCount(recipe.getAmountInSlot(2));
		
		builder.addSlot(RecipeIngredientRole.INPUT, 80, 20).addItemStack(stack1);
		builder.addSlot(RecipeIngredientRole.INPUT, 67, 50).addItemStack(stack2);
		builder.addSlot(RecipeIngredientRole.INPUT, 93, 50).addItemStack(stack3);
		
		builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 36).addItemStack(recipe.getResultItem());
	}
	
	@Override
	public void draw(CrystallizerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY)
	{
		stack.pushPose();
		
		
		
		stack.popPose();
	}
}
