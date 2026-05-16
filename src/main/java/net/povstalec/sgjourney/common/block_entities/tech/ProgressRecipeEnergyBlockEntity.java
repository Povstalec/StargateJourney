package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.recipe.ProgressRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class ProgressRecipeEnergyBlockEntity<R extends ProgressRecipe> extends EnergySlotBlockEntity
{
	public static final String PROGRESS = "progress";
	
	protected SimpleContainer simpleContainer;
	protected int maxProgress = 100;
	protected int progress = 0;
	
	public ProgressRecipeEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, SimpleContainer simpleContainer)
	{
		super(type, pos, state);
		
		this.simpleContainer = simpleContainer;
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		updateSimpleContainer();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = this.saveWithoutMetadata();
		tag.putInt("max_progress", maxProgress);
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(connection, packet);
		maxProgress = packet.getTag().getInt("max_progress");
	}
	
	/**
	 * Updates the Simple Container used for recipes with new information about the Block Entity's inventory (should be called any time there is a change to the inventory)
	 */
	protected abstract void updateSimpleContainer();
	
	protected abstract RecipeType<R> getRecipeType();
	
	protected Optional<R> getRecipe()
	{
		if(level == null)
			return Optional.empty();
		
		return level.getRecipeManager().getRecipeFor(getRecipeType(), simpleContainer, level);
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
			maxProgress = recipe.getProgressTime(); // Update max progress time
			
			if(progress < recipe.getProgressTime()) // Progress recipe
			{
				if(energyStorage.hasEnergy(energyPerProgressTick()))
				{
					energyStorage.depleteEnergy(energyPerProgressTick(), false);
					progress++;
				}
			}
			else if(progress >= recipe.getProgressTime()) // Wait until it's possible to output
			{
				if(hasExtraIngredients(recipe) && canOutput(recipe)) // Check if there's space for the output 'n stuff
				{
					depleteIngredients(recipe);
					createOutput(recipe);
					resetProgress();
				}
			}
			
			setChanged();
		}, this::resetProgress); // Doesn't have base ingredients, stop progress
	}
	
	public abstract boolean hasExtraIngredients(R recipe);
	
	public abstract boolean canOutput(R recipe);
	
	public abstract void depleteIngredients(R recipe);
	
	public abstract void createOutput(R recipe);
	
	public static void tick(Level level, BlockPos pos, BlockState state, ProgressRecipeEnergyBlockEntity<?> recipeBlockEntity)
	{
		EnergySlotBlockEntity.tick(level, pos, state, recipeBlockEntity);
		
		if(level.isClientSide())
			return;
		
		recipeBlockEntity.doProgress();
	}
}
