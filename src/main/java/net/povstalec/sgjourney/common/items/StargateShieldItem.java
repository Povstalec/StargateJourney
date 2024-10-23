package net.povstalec.sgjourney.common.items;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;

public abstract class StargateShieldItem extends Item
{
	// Vanilla Materials
	public static final ResourceLocation COPPER_IRIS = StargateJourney.location("textures/block/copper_block.png");

	public static final String TEXTURE = "texture";
	
	private ResourceLocation shieldTexture;
	
	public StargateShieldItem(Properties properties, ResourceLocation shieldTexture)
	{
		super(properties);
		
		this.shieldTexture = shieldTexture;
	}
	
	public ResourceLocation getShieldTexture()
	{
		return shieldTexture;
	}
	
	
	
	@Nullable
	public static ResourceLocation getShieldTexture(ItemStack stack)
	{
		if(stack.getItem() instanceof StargateShieldItem irisItem)
		{
			CompoundTag tag = stack.getOrCreateTag();
			if(tag.contains(TEXTURE))
			{
				String texture = tag.getString(TEXTURE);
				
				if(ResourceLocation.isValidResourceLocation(texture))
					return ResourceLocation.parse(texture);
			}
			else
				return irisItem.getShieldTexture();
		}
		
		return null;
	}
}
