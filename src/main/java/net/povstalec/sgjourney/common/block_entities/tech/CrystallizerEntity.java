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
		SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
		ArrayList<ItemStack> list = new ArrayList<>();
		
		for(int i = 0; i < itemStackHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemStackHandler.getStackInSlot(i));
		}
		
		Optional<RecipeHolder<CrystallizerRecipe>> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.CRYSTALLIZING, new CrystalRecipeInput(itemStackHandler.getStackInSlot(0), itemStackHandler.getStackInSlot(1), itemStackHandler.getStackInSlot(2)), level);
		
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
		SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
		
		for (int i = 0; i < itemStackHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemStackHandler.getStackInSlot(i));
		}
		
		Optional<RecipeHolder<CrystallizerRecipe>> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.CRYSTALLIZING, new CrystalRecipeInput(itemStackHandler.getStackInSlot(0), itemStackHandler.getStackInSlot(1), itemStackHandler.getStackInSlot(2)), level);
		
		if(hasIngredients())
		{
			useUpItems(recipe.get().value(), 0);
			if(recipe.get().value().depletePrimary())
				useUpItems(recipe.get().value(), 1);
			if(recipe.get().value().depleteSecondary())
				useUpItems(recipe.get().value(), 2);
			itemStackHandler.setStackInSlot(3, recipe.get().value().getResultItem(null));
			
			this.progress = 0;
		}
	}
	
	protected void useUpItems(CrystallizerRecipe recipe, int slot)
	{
		itemStackHandler.extractItem(slot, recipe.getAmountInSlot(slot), false);
	}
}
