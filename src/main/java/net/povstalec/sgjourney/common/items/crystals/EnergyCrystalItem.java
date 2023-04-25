package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemEnergyProvider;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyCrystalItem extends Item
{
	public static final String CRYSTAL_MODE = "CrystalMode";
	public static final String ENERGY_LIMIT = "EnergyLimit";
	public static final String ENERGY = "Energy";
	public static final String TRANSFER_LIMIT = "TransferLimit";
	
	public final int maxEnergy;
	public final int maxTransfer;
	public final int maxRegularTransfer;
	
	public EnergyCrystalItem(Properties properties, int maxEnergy, int maxTransfer, int maxRegularTransfer)
	{
		super(properties);
		this.maxEnergy = maxEnergy;
		this.maxTransfer = maxTransfer;
		this.maxRegularTransfer = maxRegularTransfer;
	}
	
	public enum CrystalMode
	{
		ENERGY_STORAGE,
		ENERGY_TRANSFER;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getCrystalMode(stack) == CrystalMode.ENERGY_STORAGE;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / maxEnergy);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getEnergy(stack) / maxEnergy);
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public static CompoundTag tagSetup(CrystalMode crystalMode, int energy, int maxTransfer)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString(CRYSTAL_MODE, crystalMode.toString().toUpperCase());
		tag.putInt(ENERGY, energy);
		tag.putInt(TRANSFER_LIMIT, maxTransfer);
		
		return tag;
	}
	
	public static CrystalMode getCrystalMode(ItemStack stack)
	{
		CrystalMode mode;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(CRYSTAL_MODE))
			tag.putString(CRYSTAL_MODE, CrystalMode.ENERGY_STORAGE.toString().toUpperCase());
		
		mode = CrystalMode.valueOf(tag.getString(CRYSTAL_MODE));
		
		return mode;
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
	
	public static int getMaxTransfer(ItemStack stack)
	{
		if(stack.getItem() instanceof EnergyCrystalItem crystal)
		{
			int maxTransfer;
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(TRANSFER_LIMIT))
				tag.putInt(TRANSFER_LIMIT, crystal.maxTransfer);
			
			maxTransfer = tag.getInt(TRANSFER_LIMIT);
			
			return maxTransfer;
		}
		
		return 0;
	}
	
	public int getMaxTransfer()
	{
		return this.maxTransfer;
	}
	
	public int getMaxStorage()
	{
		return this.maxEnergy;
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemEnergyProvider(stack)
				{
					@Override
					public long capacity()
					{
						return maxEnergy;
					}

					@Override
					public long maxReceive()
					{
						return maxRegularTransfer;
					}

					@Override
					public long maxExtract()
					{
						return maxRegularTransfer;
					}
				};
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	CrystalMode mode = getCrystalMode(stack);
        
        switch(mode)
        {
        case ENERGY_STORAGE:
        	int energy = getEnergy(stack);
        	tooltipComponents.add(Component.literal("Energy: " + energy +  "/" + maxEnergy + "FE").withStyle(ChatFormatting.DARK_RED));
        	break;
        case ENERGY_TRANSFER:
        	int maxEnergyTransfer = getMaxTransfer(stack);
        	tooltipComponents.add(Component.literal("Max Energy Transfer: " + maxEnergyTransfer + " FE").withStyle(ChatFormatting.RED));
        	break;
        	
        }
        

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
