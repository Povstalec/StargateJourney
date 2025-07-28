package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemEnergyProvider;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends Item
{
	public static final String ENERGY = "energy";
	
	public BatteryItem(Item.Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / getCapacity());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getEnergy(stack) / getCapacity());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public static CompoundTag tagSetup(int energy)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(ENERGY, energy);
		
		return tag;
	}
	
	public static long getEnergy(ItemStack stack)
	{
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENERGY, Tag.TAG_LONG))
			return tag.getLong(ENERGY);
		
		return 0;
	}
	
	public long getCapacity()
	{
		return 5000000L;
	}
	
	public long getTransfer()
	{
		return 100000L;
	}
	
	@Override
	public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemEnergyProvider(stack)
		{
			@Override
			public long capacity()
			{
				return getCapacity();
			}
			
			@Override
			public long maxReceive()
			{
				return getTransfer();
			}
			
			@Override
			public long maxExtract()
			{
				return getTransfer();
			}
		};
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(getEnergy(stack), getCapacity()))).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
