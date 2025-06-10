package net.povstalec.sgjourney.common.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonIrisConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;

public abstract class StargateIrisItem extends Item
{
	// Vanilla Materials
	public static final ResourceLocation COPPER_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/copper_iris.png");
	public static final ResourceLocation IRON_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/iron_iris.png");
	public static final ResourceLocation GOLD_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/golden_iris.png");
	public static final ResourceLocation DIAMOND_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/diamond_iris.png");
	public static final ResourceLocation NETHERITE_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/netherite_iris.png");
	// Stargate Journey Materials
	public static final ResourceLocation NAQUADAH_ALLOY_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/naquadah_alloy_iris.png");
	public static final ResourceLocation TRINIUM_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/trinium_iris.png");
	// Modded Materials
	public static final ResourceLocation BRONZE_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/bronze_iris.png");
	public static final ResourceLocation STEEL_IRIS = StargateJourney.sgjourneyLocation("textures/entity/stargate/iris/steel_iris.png");
	
	private ResourceLocation irisTexture;
	
	public StargateIrisItem(Properties properties, ResourceLocation irisTexture)
	{
		super(properties);
		
		this.irisTexture = irisTexture;
	}
	
	public ResourceLocation getIrisTexture()
	{
		return irisTexture;
	}
	
	public abstract int getMaxDurability();
	
	
	
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
		if(getDurability(stack) > 0)
		{
			int durability = getDurability(stack);
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
	
	
	
	public static class Copper extends StargateIrisItem
	{
		public Copper(Properties properties)
		{
			super(properties, COPPER_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.copper_iris_durability.get();
		}
	}
	
	public static class Iron extends StargateIrisItem
	{
		public Iron(Properties properties)
		{
			super(properties, IRON_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.iron_iris_durability.get();
		}
	}

	public static class Gold extends StargateIrisItem
	{
		public Gold(Properties properties)
		{
			super(properties, GOLD_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.gold_iris_durability.get();
		}
	}

	public static class Diamond extends StargateIrisItem
	{
		public Diamond(Properties properties)
		{
			super(properties, DIAMOND_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.diamond_iris_durability.get();
		}
	}

	public static class Netherite extends StargateIrisItem
	{
		public Netherite(Properties properties)
		{
			super(properties, NETHERITE_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.netherite_iris_durability.get();
		}
	}

	public static class NaquadahAlloy extends StargateIrisItem
	{
		public NaquadahAlloy(Properties properties)
		{
			super(properties, NAQUADAH_ALLOY_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.naquadah_alloy_iris_durability.get();
		}
	}

	public static class Trinium extends StargateIrisItem
	{
		public Trinium(Properties properties)
		{
			super(properties, TRINIUM_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.trinium_iris_durability.get();
		}
	}

	public static class Bronze extends StargateIrisItem
	{
		public Bronze(Properties properties)
		{
			super(properties, BRONZE_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.bronze_iris_durability.get();
		}
	}

	public static class Steel extends StargateIrisItem
	{
		public Steel(Properties properties)
		{
			super(properties, STEEL_IRIS);
		}
		
		public int getMaxDurability()
		{
			return CommonIrisConfig.steel_iris_durability.get();
		}
	}
}
