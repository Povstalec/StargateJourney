package net.povstalec.sgjourney.common.items;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.DataComponentInit;

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
		ResourceLocation location = stack.get(DataComponentInit.IRIS_TEXTURE);
		
		if(location != null)
			return location;
		else if(stack.getItem() instanceof StargateIrisItem irisItem)
			return irisItem.getIrisTexture();
		
		return null;
	}
}
