package net.povstalec.sgjourney.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.DataComponentInit;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntSupplier;

public class StargateIrisItem extends Item
{
	// Vanilla Materials
	public static final ResourceLocation COPPER_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/copper_iris.png");
	public static final ResourceLocation IRON_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/iron_iris.png");
	public static final ResourceLocation GOLD_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/golden_iris.png");
	public static final ResourceLocation DIAMOND_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/diamond_iris.png");
	public static final ResourceLocation NETHERITE_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/netherite_iris.png");
	// Stargate Journey Materials
	public static final ResourceLocation NAQUADAH_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/naquadah_iris.png");
	public static final ResourceLocation NAQUADAH_COPPER_ALLOY_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/naquadah_copper_alloy_iris.png");
	public static final ResourceLocation NAQUADAH_IRON_ALLOY_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/naquadah_iron_alloy_iris.png");
	public static final ResourceLocation TRINIUM_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/trinium_iris.png");
	// Modded Materials
	public static final ResourceLocation BRONZE_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/bronze_iris.png");
	public static final ResourceLocation STEEL_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/steel_iris.png");
	
	private final ResourceLocation irisTexture;
	private final IntSupplier maxDurability;
	
	public StargateIrisItem(Properties properties, ResourceLocation irisTexture, IntSupplier maxDurability)
	{
		super(properties);
		
		this.irisTexture = irisTexture;
		this.maxDurability = maxDurability;
	}
	
	public ResourceLocation getIrisTexture()
	{
		return irisTexture;
	}
	
	public int getMaxDurability()
	{
		return maxDurability.getAsInt();
	}
	
	
	
	public static boolean hasCustomTexture(ItemStack stack)
	{
		return stack.has(DataComponentInit.IRIS_TEXTURE);
	}
	
	@Nullable
	public static ResourceLocation getIrisTexture(ItemStack stack)
	{
		ResourceLocation location = stack.get(DataComponentInit.IRIS_TEXTURE);
		
		if(location != null)
			return location;
		else if(stack.getItem() instanceof StargateIrisItem irisItem)
			return irisItem.getIrisTexture();
		
		return null;
	}
	
	public static int getDurability(ItemStack stack)
	{
		if(stack.getItem() instanceof StargateIrisItem iris)
			return stack.getOrDefault(DataComponentInit.IRIS_DURABILITY, iris.getMaxDurability());
		
		return 0;
	}
	
	/**
	 * If durability goes below 1, returns false
	 * @param stack
	 * @return
	 */
	public static boolean decreaseDurability(ItemStack stack)
	{
		int durability = getDurability(stack);
		if(durability > 0)
		{
			stack.set(DataComponentInit.IRIS_DURABILITY, --durability);
			return durability >= 1;
		}
		
		return false;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getDurability(stack) < getMaxDurability();
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getDurability(stack) / getMaxDurability());
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getDurability(stack) / getMaxDurability());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(tooltipFlag.isAdvanced())
		{
			int durability = getDurability(stack);
			
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.iris.durability").append(Component.literal(": " + durability + " / " + getMaxDurability())));
		}
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
}
