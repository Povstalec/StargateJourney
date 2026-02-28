package net.povstalec.sgjourney.common.init;

import net.minecraft.world.food.FoodProperties;

public class FoodInit
{
	public static final FoodProperties RAW_GOAULD = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.1F).meat().build();
	public static final FoodProperties COOKED_GOAULD = new FoodProperties.Builder()
			.nutrition(5).saturationMod(0.6F).meat().build();
	
	public static final FoodProperties JAFFA_BURGER = new FoodProperties.Builder()
			.nutrition(8).saturationMod(0.8F).build();
	public static final FoodProperties JAFFA_CAKE = new FoodProperties.Builder()
			.nutrition(2).saturationMod(0.1F).build();
}
