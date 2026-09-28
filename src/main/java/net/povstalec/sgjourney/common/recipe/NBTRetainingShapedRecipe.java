package net.povstalec.sgjourney.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.RecipeTypeInit;
import net.povstalec.sgjourney.common.items.blocks.EnergyBlockItem;
import net.povstalec.sgjourney.common.items.blocks.InterfaceBlockItem;
import org.jetbrains.annotations.NotNull;

public class NBTRetainingShapedRecipe extends ShapedRecipe
{
	public static final RecipeType<NBTRetainingShapedRecipe> TYPE = new RecipeType<>(){};
	
	private final ItemStack result;
	
	public NBTRetainingShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification)
	{
		super(group, category, pattern, result, showNotification);
		
		this.result = result;
	}
	
	@Override
	public @NotNull ItemStack assemble(CraftingInput input, @NotNull HolderLookup.Provider registries)
	{
		long energy = 0;
		long energyTarget = -1;
		
		for(int j = 0; j < input.size(); ++j)
		{
			ItemStack containerStack = input.getItem(j);
			
			// Retain Energy
			IEnergyStorage energyStorage = containerStack.getCapability(Capabilities.EnergyStorage.ITEM);
			
			if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				energy += sgjourneyEnergy.getTrueEnergyStored();
			else if(energyStorage != null)
				energy += energyStorage.getEnergyStored();
			
			if(containerStack.getItem() instanceof EnergyBlockItem energyBlockItem)
			{
				energy += energyBlockItem.getEnergy(containerStack);
				
				// Retain Energy Target
				if(energyBlockItem instanceof InterfaceBlockItem interfaceBlockItem)
					energyTarget = Math.max(energyTarget, interfaceBlockItem.getEnergyTarget(containerStack));
			}
		}
		
		// Result section
		
		ItemStack result = this.getResultItem(registries).copy();
		
		// Retain Energy
		final long totalEnergy = energy;
		IEnergyStorage energyStorage = result.getCapability(Capabilities.EnergyStorage.ITEM);
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
			sgjourneyEnergy.setEnergy(Math.min(totalEnergy, sgjourneyEnergy.getTrueMaxEnergyStored()));
		else if(energyStorage != null)
			energyStorage.receiveEnergy(SGJourneyEnergy.regularEnergy(totalEnergy), false);
		
		if(result.getItem() instanceof EnergyBlockItem energyBlockItem)
		{
			energyBlockItem.setEnergy(result, totalEnergy);
			
			// Retain Energy Target
			if(energyBlockItem instanceof InterfaceBlockItem interfaceBlockItem)
				interfaceBlockItem.setEnergyTarget(result, energyTarget);
		}
		
		return result;
	}
	
	@Override
	public @NotNull RecipeSerializer<?> getSerializer()
	{
		return RecipeTypeInit.NBT_RETAINING_SHAPED_RECIPE_SERIALIZER.get();
	}
	
	public static final class Serializer implements RecipeSerializer<NBTRetainingShapedRecipe>
	{
		public static final NBTRetainingShapedRecipe.Serializer INSTANCE = new NBTRetainingShapedRecipe.Serializer();
		
		public static final MapCodec<NBTRetainingShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.STRING.optionalFieldOf("group", "").forGetter(NBTRetainingShapedRecipe::getGroup),
					CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(NBTRetainingShapedRecipe::category),
					ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
					ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
					Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(NBTRetainingShapedRecipe::showNotification)
				)
				.apply(instance, NBTRetainingShapedRecipe::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, NBTRetainingShapedRecipe> STREAM_CODEC = StreamCodec.of(
			NBTRetainingShapedRecipe.Serializer::toNetwork, NBTRetainingShapedRecipe.Serializer::fromNetwork
		);
		
		@Override
		public @NotNull MapCodec<NBTRetainingShapedRecipe> codec() {
			return CODEC;
		}
		
		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, NBTRetainingShapedRecipe> streamCodec() {
			return STREAM_CODEC;
		}
		
		private static NBTRetainingShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
			ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
			ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
			boolean flag = buffer.readBoolean();
			return new NBTRetainingShapedRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf buffer, NBTRetainingShapedRecipe recipe) {
			buffer.writeUtf(recipe.getGroup());
			buffer.writeEnum(recipe.category());
			ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
			ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
			buffer.writeBoolean(recipe.showNotification());
		}
	}
}
