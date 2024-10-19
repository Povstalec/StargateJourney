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

public abstract class StargateIrisItem extends Item
{
	// Vanilla Materials
	public static final ResourceLocation COPPER_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/copper_iris.png");
	public static final ResourceLocation IRON_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/iron_iris.png");
	public static final ResourceLocation GOLD_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/golden_iris.png");
	public static final ResourceLocation DIAMOND_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/diamond_iris.png");
	public static final ResourceLocation NETHERITE_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/netherite_iris.png");
	// Stargate Journey Materials
	public static final ResourceLocation NAQUADAH_ALLOY_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/naquadah_alloy_iris.png");
	public static final ResourceLocation TRINIUM_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/trinium_iris.png");
	// Modded Materials
	public static final ResourceLocation BRONZE_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/bronze_iris.png");
	public static final ResourceLocation STEEL_IRIS = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/steel_iris.png");
	
	public static final String DURABILITY = "durability";
	public static final String TEXTURE = "texture";
	
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
		return stack.hasTag() && stack.getTag().contains(TEXTURE);
	}
	
	@Nullable
	public static ResourceLocation getIrisTexture(ItemStack stack)
	{
		if(stack.getItem() instanceof StargateIrisItem irisItem)
		{
			CompoundTag tag = stack.getOrCreateTag();
			if(tag.contains(TEXTURE))
			{
				String texture = tag.getString(TEXTURE);
				
				if(ResourceLocation.isValidResourceLocation(texture))
					return new ResourceLocation(texture);
			}
			else
				return irisItem.getIrisTexture();
		}
		
		return null;
	}
	
	public static int getDurability(ItemStack stack)
	{
		if(stack.getItem() instanceof StargateIrisItem irisItem)
		{
			int durability;
			
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(DURABILITY))
				tag.putInt(DURABILITY, irisItem.getMaxDurability());
			
			durability = tag.getInt(DURABILITY);
			
			return durability;
		}
		else
			return 0;
	}
	
	/**
	 * If durability goes below 1, returns false
	 * @param stack
	 * @return
	 */
	public static boolean decreaseDurability(ItemStack stack)
	{
		if(stack.getItem() instanceof StargateIrisItem irisItem)
		{
			int durability;
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(DURABILITY))
				tag.putInt(DURABILITY, irisItem.getMaxDurability());
			
			durability = tag.getInt(DURABILITY);
			
			durability--;
			
			tag.putInt(DURABILITY, durability);
			
			if(durability >= 1)
				return true;
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
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.hasTag() && isAdvanced.isAdvanced())
		{
			int durability = getDurability(stack);
			
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.iris.durability").append(Component.literal(": " + durability + " / " + getMaxDurability())));
		}
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
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
