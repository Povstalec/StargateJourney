package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemEnergyProvider;

public class EnergyCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_CAPACITY = 50000;
	public static final int DEFAULT_ENERGY_TRANSFER = 1500;

	public static final int ADVANCED_CAPACITY = 100000;
	public static final int ADVANCED_ENERGY_TRANSFER = 3000;
	
	public static final String ENERGY_LIMIT = "EnergyLimit";
	public static final String ENERGY = "Energy";
	
	public EnergyCrystalItem(Properties properties)
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
	
	public static int getEnergy(ItemStack stack)
	{
		int energy;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(ENERGY))
			tag.putInt(ENERGY, 0);
		
		energy = tag.getInt(ENERGY);
		
		return energy;
	}
	
	public int getCapacity()
	{
		return DEFAULT_CAPACITY;
	}
	
	public int getTransfer()
	{
		return DEFAULT_ENERGY_TRANSFER;
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
		int energy = getEnergy(stack);
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy +  "/" + getCapacity() + " FE")).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static final class Advanced extends EnergyCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public int getCapacity()
		{
			return ADVANCED_CAPACITY;
		}

		@Override
		public int getTransfer()
		{
			return ADVANCED_ENERGY_TRANSFER;
		}
	}
}
