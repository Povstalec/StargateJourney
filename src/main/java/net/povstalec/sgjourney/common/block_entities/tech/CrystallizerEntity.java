package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.recipe.CrystalRecipeInput;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

public class CrystallizerEntity extends AbstractCrystallizerEntity
{
	public CrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTALLIZER.get(), pos, state);
	}

	@Override
	public Fluid getDesiredFluid()
	{
		return FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	}
	
	protected boolean hasIngredients()
	{
		Level level = this.getLevel();
		SimpleContainer inventory = new SimpleContainer(5);
		
		inventory.setItem(0, crystalBaseHandler.getStackInSlot(0));
		inventory.setItem(1, primaryIngredientHandler.getStackInSlot(0));
		inventory.setItem(2, secondaryIngredientHandler.getStackInSlot(0));
		inventory.setItem(3, outputHandler.getStackInSlot(0));
		inventory.setItem(4, fluidInputHandler.getStackInSlot(0));
		
		Optional<RecipeHolder<CrystallizerRecipe>> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.CRYSTALLIZING,
						new CrystalRecipeInput(crystalBaseHandler.getStackInSlot(0), primaryIngredientHandler.getStackInSlot(0), secondaryIngredientHandler.getStackInSlot(0)), level);
		
		if(!recipe.isPresent())
			return false;
		
		// Only allows creating Stargate Upgrade Crystals when it's enabled in the config
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get() &&
				recipe.get().value().getResultItem(null).getItem() instanceof StargateUpgradeItem)
			return false;
		
		return hasSpaceInOutputSlot(inventory, recipe.get().value().getResultItem(null));
	}
	
	protected void crystallize()
	{
		Level level = this.getLevel();
		SimpleContainer inventory = new SimpleContainer(5);
		
		inventory.setItem(0, crystalBaseHandler.getStackInSlot(0));
		inventory.setItem(1, primaryIngredientHandler.getStackInSlot(0));
		inventory.setItem(2, secondaryIngredientHandler.getStackInSlot(0));
		inventory.setItem(3, outputHandler.getStackInSlot(0));
		inventory.setItem(4, fluidInputHandler.getStackInSlot(0));
		
		Optional<RecipeHolder<CrystallizerRecipe>> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.CRYSTALLIZING,
						new CrystalRecipeInput(crystalBaseHandler.getStackInSlot(0), primaryIngredientHandler.getStackInSlot(0), secondaryIngredientHandler.getStackInSlot(0)), level);
		
		if(hasIngredients())
		{
			useUpItems(recipe.get().value(), 0);
			if(recipe.get().value().depletePrimary())
				useUpItems(recipe.get().value(), 1);
			if(recipe.get().value().depleteSecondary())
				useUpItems(recipe.get().value(), 2);
			outputHandler.setStackInSlot(0, recipe.get().value().getResultItem(null));
			
			this.progress = 0;
		}
	}
	
	protected void useUpItems(CrystallizerRecipe recipe, int slot)
	{
		switch(slot)
		{
			case 1:
				primaryIngredientHandler.extractItem(0, recipe.getAmountInSlot(1), false);
				break;
			case 2:
				secondaryIngredientHandler.extractItem(0, recipe.getAmountInSlot(2), false);
				break;
			default:
				crystalBaseHandler.extractItem(0, recipe.getAmountInSlot(0), false);
		}
	}
}
