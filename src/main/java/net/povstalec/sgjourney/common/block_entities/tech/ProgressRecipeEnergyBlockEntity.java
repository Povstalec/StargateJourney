package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.recipe.ProgressRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class ProgressRecipeEnergyBlockEntity<R extends ProgressRecipe<I>, I extends RecipeInput> extends EnergySlotBlockEntity
{
	public static final String PROGRESS = "progress";
	
	protected I recipeInput;
	protected int maxProgress = 100;
	protected int progress = 0;
	
	public ProgressRecipeEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, I recipeInput)
	{
		super(type, pos, state);
		
		this.recipeInput = recipeInput;
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		updateSimpleContainer();
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt, registries);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = this.saveWithoutMetadata(registries);
		tag.putInt("max_progress", maxProgress);
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		super.onDataPacket(connection, packet, registries);
		maxProgress = packet.getTag().getInt("max_progress");
	}
	
	/**
	 * Updates the Simple Container used for recipes with new information about the Block Entity's inventory (should be called any time there is a change to the inventory)
	 */
	protected abstract void updateSimpleContainer();
	
	protected abstract RecipeType<R> getRecipeType();
	
	protected Stream<RecipeHolder<?>> getAvailableRecipes()
	{
		return level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.value().getType().equals(getRecipeType()));
	}
	
	protected Optional<RecipeHolder<R>> getRecipe()
	{
		if(level == null)
			return Optional.empty();
		
		return level.getRecipeManager().getRecipeFor(getRecipeType(), recipeInput, level);
	}
	
	public int getMaxProgress()
	{
		return maxProgress;
	}
	
	public int getProgress()
	{
		return progress;
	}
	
	public void resetProgress()
	{
		this.progress = 0;
	}
	
	/**
	 * @return Energy required to increase progress by one unit in one tick
	 */
	public abstract long energyPerProgressTick();
	
	public void doProgress()
	{
		getRecipe().ifPresentOrElse(recipe -> // Has base ingredients, progress
		{
			maxProgress = recipe.value().getProgressTime(); // Update max progress time
			
			if(progress < recipe.value().getProgressTime()) // Progress recipe
			{
				if(energyStorage.hasEnergy(energyPerProgressTick()))
				{
					energyStorage.depleteEnergy(energyPerProgressTick(), false);
					progress++;
					setChanged();
				}
			}
			else if(progress >= recipe.value().getProgressTime()) // Wait until it's possible to output
			{
				if(canOutput(recipe.value())) // Check if there's space for the output
				{
					depleteIngredients(recipe.value());
					createOutput(recipe.value());
					resetProgress();
					setChanged();
				}
			}
		}, this::resetProgress); // Doesn't have base ingredients, stop progress
	}
	
	public abstract boolean canOutput(R recipe);
	
	public abstract void depleteIngredients(R recipe);
	
	public abstract void createOutput(R recipe);
	
	public static void tick(Level level, BlockPos pos, BlockState state, ProgressRecipeEnergyBlockEntity<?, ?> recipeBlockEntity)
	{
		EnergySlotBlockEntity.tick(level, pos, state, recipeBlockEntity);
		
		if(level.isClientSide())
			return;
		
		recipeBlockEntity.doProgress();
	}
	
	public static boolean isSameFluidOrEmpty(FluidStack tankFluid, FluidStack testedFluid)
	{
		if(tankFluid.isEmpty())
			return true;
		
		return testedFluid.getFluid().isSame(tankFluid.getFluid());
	}
}
