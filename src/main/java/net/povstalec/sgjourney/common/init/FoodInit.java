package net.povstalec.sgjourney.common.init;

import net.minecraft.world.food.FoodProperties;

public class FoodInit
{
	public static final FoodProperties RAW_GOAULD = new FoodProperties.Builder()
			.nutrition(2).saturationModifier(0.1F).build();
	public static final FoodProperties COOKED_GOAULD = new FoodProperties.Builder()
			.nutrition(5).saturationModifier(0.6F).build();
	
	public static final FoodProperties GOAULD_BURGER = new FoodProperties.Builder()
			.nutrition(8).saturationModifier(0.8F).build();
	public static final FoodProperties JAFFA_CAKE = new FoodProperties.Builder()
			.nutrition(2).saturationModifier(0.1F).build();
}
