package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemEnergyProvider;
import net.povstalec.sgjourney.common.config.CommonTechConfig;

public class EnergyCrystalItem extends AbstractCrystalItem
{
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
	
	public static long getEnergy(ItemStack stack)
	{
		long energy;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(ENERGY))
			tag.putInt(ENERGY, 0);
		
		if(tag.getTagType(ENERGY) == Tag.TAG_INT) // TODO This is here for legacy reasons because it was originally an int
			energy = tag.getInt(ENERGY);
		else
			energy = tag.getLong(ENERGY);
		
		return energy;
	}
	
	public long getCapacity()
	{
		return CommonTechConfig.energy_crystal_capacity.get();
	}
	
	public long getTransfer()
	{
		return CommonTechConfig.advanced_energy_crystal_max_transfer.get();
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
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.energy").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		long energy = getEnergy(stack);
		
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
		public long getCapacity()
		{
			return CommonTechConfig.advanced_energy_crystal_capacity.get();
		}

		@Override
		public long getTransfer()
		{
			return CommonTechConfig.advanced_energy_crystal_max_transfer.get();
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}

		@Override
		public Optional<Component> descriptionInDHD(ItemStack stack)
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.energy.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
	}
}
