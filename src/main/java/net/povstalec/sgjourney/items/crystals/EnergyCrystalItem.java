package net.povstalec.sgjourney.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.capabilities.ItemEnergyProvider;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyCrystalItem extends Item
{
	public static final String CRYSTAL_MODE = "CrystalMode";
	public static final String ENERGY_LIMIT = "EnergyLimit";
	public static final String ENERGY = "Energy";
	public static final String TRANSFER_LIMIT = "TransferLimit";
	
	public static final int MAX_ENERGY = 50000;
	public static final int REGULAR_TRANSFER = 1000;
	public static final int MAX_TRANSFER = 5000;
	
	public EnergyCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum CrystalMode
	{
		ENERGY_STORAGE,
		ENERGY_TRANSFER;
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
		int maxTransfer;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(TRANSFER_LIMIT))
			tag.putInt(TRANSFER_LIMIT, MAX_ENERGY);
		
		maxTransfer = tag.getInt(TRANSFER_LIMIT);
		
		return maxTransfer;
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemEnergyProvider(stack)
				{

					@Override
					public int capacity()
					{
						return MAX_ENERGY;
					}

					@Override
					public int maxTransfer()
					{
						return REGULAR_TRANSFER;
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
        	tooltipComponents.add(Component.literal("Energy: " + energy +  "/" + MAX_ENERGY + "FE").withStyle(ChatFormatting.DARK_RED));
        	break;
        case ENERGY_TRANSFER:
        	int maxEnergyTransfer = getMaxTransfer(stack);
        	tooltipComponents.add(Component.literal("Max Energy Transfer: " + maxEnergyTransfer + " FE").withStyle(ChatFormatting.RED));
        	break;
        	
        }
        

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
