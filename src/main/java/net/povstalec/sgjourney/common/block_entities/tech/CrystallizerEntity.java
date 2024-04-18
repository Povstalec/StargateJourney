package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
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
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		
		for(int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Optional<CrystallizerRecipe> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.INSTANCE, inventory, level);
		
		if(!recipe.isPresent())
			return false;
		
		// Only allows creating Stargate Upgrade Crystals when it's enabled in the config
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get() &&
				recipe.get().getResultItem().getItem() instanceof StargateUpgradeItem)
			return false;
		
		return hasSpaceInOutputSlot(inventory, recipe.get().getResultItem());
	}
	
	protected void crystallize()
	{
		Level level = this.getLevel();
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Optional<CrystallizerRecipe> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.INSTANCE, inventory, level);
		
		if(hasIngredients())
		{
			useUpItems(recipe.get(), 0);
			if(recipe.get().depletePrimary())
				useUpItems(recipe.get(), 1);
			if(recipe.get().depleteSecondary())
				useUpItems(recipe.get(), 2);
			itemHandler.setStackInSlot(3, recipe.get().getResultItem());
			
			this.progress = 0;
		}
	}
	
	protected void useUpItems(CrystallizerRecipe recipe, int slot)
	{
		itemHandler.extractItem(slot, recipe.getAmountInSlot(slot), false);
	}
}
