package net.povstalec.sgjourney.common.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.RecipeTypeInit;
import net.povstalec.sgjourney.common.items.blocks.EnergyBlockItem;
import net.povstalec.sgjourney.common.items.blocks.InterfaceBlockItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class NBTRetainingShapedRecipe extends ShapedRecipe
{
	public static int MAX_WIDTH = 3;
	public static int MAX_HEIGHT = 3;
	
	public NBTRetainingShapedRecipe(ResourceLocation location, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result)
	{
		super(location, group, category, width, height, ingredients, result);
	}
	
	@Override
	public @NotNull ItemStack assemble(CraftingContainer container)
	{
		long energy = 0;
		long energyTarget = -1;
		
		for(int j = 0; j < container.getContainerSize(); ++j)
		{
			ItemStack containerStack = container.getItem(j);
			
			// Retain Energy
			IEnergyStorage energyStorage = containerStack.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
			
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
		
		ItemStack result = this.getResultItem().copy();
		
		// Retain Energy
		final long totalEnergy = energy;
		result.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage ->
		{
			if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				sgjourneyEnergy.setEnergy(Math.min(totalEnergy, sgjourneyEnergy.getTrueMaxEnergyStored()));
			else
				energyStorage.receiveEnergy(SGJourneyEnergy.regularEnergy(totalEnergy), false);
		});
		
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
		return RecipeTypeInit.NBT_RETAINING_SHAPED_RECIPE.get();
	}
	
	public static final class Serializer implements RecipeSerializer<NBTRetainingShapedRecipe>
	{
		public static final NBTRetainingShapedRecipe.Serializer INSTANCE = new NBTRetainingShapedRecipe.Serializer();
		
		@Override
		public NBTRetainingShapedRecipe fromJson(@NotNull ResourceLocation recipeID, @NotNull JsonObject serializedRecipe)
		{
			String s = GsonHelper.getAsString(serializedRecipe, "group", "");
			CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(serializedRecipe, "category", null), CraftingBookCategory.MISC);
			Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "key"));
			String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(serializedRecipe, "pattern")));
			int i = astring[0].length();
			int j = astring.length;
			NonNullList<Ingredient> nonnulllist = dissolvePattern(astring, map, i, j);
			ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "result"));
			return new NBTRetainingShapedRecipe(recipeID, s, craftingbookcategory, i, j, nonnulllist, itemstack);
		}
		
		@Override
		public NBTRetainingShapedRecipe fromNetwork(@NotNull ResourceLocation recipeID, @NotNull FriendlyByteBuf friendlyByteBuf)
		{
			int i = friendlyByteBuf.readVarInt();
			int j = friendlyByteBuf.readVarInt();
			String s = friendlyByteBuf.readUtf();
			CraftingBookCategory craftingbookcategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
			
			for(int k = 0; k < nonnulllist.size(); ++k) {
				nonnulllist.set(k, Ingredient.fromNetwork(friendlyByteBuf));
			}
			
			ItemStack itemstack = friendlyByteBuf.readItem();
			return new NBTRetainingShapedRecipe(recipeID, s, craftingbookcategory, i, j, nonnulllist, itemstack);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, NBTRetainingShapedRecipe recipe)
		{
			friendlyByteBuf.writeVarInt(recipe.getWidth());
			friendlyByteBuf.writeVarInt(recipe.getHeight());
			friendlyByteBuf.writeUtf(recipe.getGroup());
			friendlyByteBuf.writeEnum(recipe.category());
			
			for(Ingredient ingredient : recipe.getIngredients())
			{
				ingredient.toNetwork(friendlyByteBuf);
			}
			
			friendlyByteBuf.writeItem(recipe.getResultItem());
		}
	}
	
	public static NonNullList<Ingredient> dissolvePattern(String[] p_44203_, Map<String, Ingredient> p_44204_, int p_44205_, int p_44206_) {
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_44205_ * p_44206_, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(p_44204_.keySet());
		set.remove(" ");
		
		for(int i = 0; i < p_44203_.length; ++i) {
			for(int j = 0; j < p_44203_[i].length(); ++j) {
				String s = p_44203_[i].substring(j, j + 1);
				Ingredient ingredient = p_44204_.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}
				
				set.remove(s);
				nonnulllist.set(j + p_44205_ * i, ingredient);
			}
		}
		
		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}
	
	public static String[] shrink(String... p_44187_) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;
		
		for(int i1 = 0; i1 < p_44187_.length; ++i1) {
			String s = p_44187_[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if (j1 < 0) {
				if (k == i1) {
					++k;
				}
				
				++l;
			} else {
				l = 0;
			}
		}
		
		if (p_44187_.length == l) {
			return new String[0];
		} else {
			String[] astring = new String[p_44187_.length - l - k];
			
			for(int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = p_44187_[k1 + k].substring(i, j + 1);
			}
			
			return astring;
		}
	}
	
	public static int firstNonSpace(String p_44185_) {
		int i;
		for(i = 0; i < p_44185_.length() && p_44185_.charAt(i) == ' '; ++i) {
		}
		
		return i;
	}
	
	public static int lastNonSpace(String p_44201_) {
		int i;
		for(i = p_44201_.length() - 1; i >= 0 && p_44201_.charAt(i) == ' '; --i) {
		}
		
		return i;
	}
	
	public static String[] patternFromJson(JsonArray p_44197_) {
		String[] astring = new String[p_44197_.size()];
		if (astring.length > MAX_HEIGHT) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
		} else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for(int i = 0; i < astring.length; ++i) {
				String s = GsonHelper.convertToString(p_44197_.get(i), "pattern[" + i + "]");
				if (s.length() > MAX_WIDTH) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
				}
				
				if (i > 0 && astring[0].length() != s.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}
				
				astring[i] = s;
			}
			
			return astring;
		}
	}
	
	public static Map<String, Ingredient> keyFromJson(JsonObject p_44211_) {
		Map<String, Ingredient> map = Maps.newHashMap();
		
		for(Map.Entry<String, JsonElement> entry : p_44211_.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}
			
			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}
			
			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}
		
		map.put(" ", Ingredient.EMPTY);
		return map;
	}
}
