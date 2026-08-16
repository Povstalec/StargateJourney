package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class SGJourneyRecipe<C extends Container> implements Recipe<C>
{
	private static final String FLUID_ID = "id";
	private static final String AMOUNT = "amount";
	
	protected final ResourceLocation recipeID;
	
	public SGJourneyRecipe(ResourceLocation recipeID)
	{
		this.recipeID = recipeID;
	}
	
	public abstract void toNetwork(FriendlyByteBuf friendlyByteBuf);
	
	@Override
	public @NotNull ResourceLocation getId()
	{
		return recipeID;
	}
	
	@Override
	public boolean isSpecial()
	{
		return true; // Prevents Minecraft from screaming "Unknown recipe category" everywhere
	}
	
	public static FluidStack deserializeFluidStack(JsonObject jsonObject, FluidStack defaultFluidStack)
	{
		if(!jsonObject.has(FLUID_ID))
			return defaultFluidStack;
		
		String fluidName = jsonObject.get(FLUID_ID).getAsString();
		ResourceLocation fluidLocation = ResourceLocation.tryParse(fluidName);
		if(fluidLocation == null)
			return defaultFluidStack;
		
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidLocation);
		if(fluid == null)
			return defaultFluidStack;
		
		int amount = jsonObject.has(AMOUNT) ? jsonObject.get(AMOUNT).getAsInt() : defaultFluidStack.getAmount();
		return new FluidStack(fluid, amount);
	}
}
