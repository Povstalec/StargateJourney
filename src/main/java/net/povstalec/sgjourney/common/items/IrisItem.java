package net.povstalec.sgjourney.common.items;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.CommonIrisConfig;

public abstract class IrisItem extends Item
{
	// Vanilla Materials
	public static final ResourceLocation COPPER_IRIS = new ResourceLocation("textures/block/copper_block.png");
	public static final ResourceLocation IRON_IRIS = new ResourceLocation("textures/block/iron_block.png");
	public static final ResourceLocation GOLD_IRIS = new ResourceLocation("textures/block/gold_block.png");
	public static final ResourceLocation DIAMOND_IRIS = new ResourceLocation("textures/block/diamond_block.png");
	public static final ResourceLocation NETHERITE_IRIS = new ResourceLocation("textures/block/netherite_block.png");
	// Stargate Journey Materials
	public static final ResourceLocation NAQUADAH_ALLOY_IRIS = new ResourceLocation("textures/block/diamond_block.png");
	public static final ResourceLocation TRINIUM_IRIS = new ResourceLocation("textures/block/diamond_block.png");
	// Modded Materials
	public static final ResourceLocation BRONZE_IRIS = new ResourceLocation("textures/block/iron_block.png");
	public static final ResourceLocation STEEL_IRIS = new ResourceLocation("textures/block/diamond_block.png");
	
	public static final String DURABILITY = "durability";
	public static final String TEXTURE = "texture";
	
	private ResourceLocation irisTexture;
	
	public IrisItem(Properties properties, ResourceLocation irisTexture)
	{
		super(properties);
		
		this.irisTexture = irisTexture;
	}
	
	public ResourceLocation getIrisTexture()
	{
		return irisTexture;
	}
	
	public abstract int getMaxDurability();
	
	
	
	@Nullable
	public static ResourceLocation getIrisTexture(ItemStack stack)
	{
		if(stack.getItem() instanceof IrisItem irisItem)
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
	
	public static long getDurability(ItemStack stack)
	{
		if(stack.getItem() instanceof IrisItem irisItem)
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
	
	public static void decreaseDurability(ItemStack stack)
	{
		if(stack.getItem() instanceof IrisItem irisItem)
		{
			int durability;
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(DURABILITY))
				tag.putInt(DURABILITY, irisItem.getMaxDurability());
			
			durability = tag.getInt(DURABILITY);
			
			durability--;
			
			tag.putInt(DURABILITY, durability);
		}
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
	
	
	
	public static class Copper extends IrisItem
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
	
	public static class Iron extends IrisItem
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

	public static class Gold extends IrisItem
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

	public static class Diamond extends IrisItem
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

	public static class Netherite extends IrisItem
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

	public static class NaquadahAlloy extends IrisItem
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

	public static class Trinium extends IrisItem
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

	public static class Bronze extends IrisItem
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

	public static class Steel extends IrisItem
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
