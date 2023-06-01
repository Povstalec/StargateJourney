package net.povstalec.sgjourney.common.items.armor;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ItemEnergyProvider;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.AncientTech;

public class PersonalShieldItem extends ArmorItem implements AncientTech
{
	public static final String ENERGY = "Energy";
	
	protected static final int MAX_ENERGY = 100000;
	
	public PersonalShieldItem(ArmorMaterial material, EquipmentSlot slot, Properties properties)
	{
		super(material, slot, properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / MAX_ENERGY);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getEnergy(stack) / MAX_ENERGY);
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemEnergyProvider(stack)
				{
					@Override
					public long capacity()
					{
						return MAX_ENERGY;
					}

					@Override
					public long maxReceive()
					{
						return 1000;
					}

					@Override
					public long maxExtract()
					{
						return MAX_ENERGY;
					}
				};
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
	
	public static void depleteEnergy(ItemStack stack, int energyExtracted)
	{
		stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage ->
		{
			System.out.println(energyExtracted + " " + energyStorage.extractEnergy(energyExtracted, false));
		});
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int energy = getEnergy(stack);
    	tooltipComponents.add(Component.literal("Energy: " + energy +  "/" + MAX_ENERGY + "FE").withStyle(ChatFormatting.DARK_RED));
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	public static ItemStack personalShieldSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.PERSONAL_SHIELD_EMITTER.get());
        CompoundTag tag = stack.getOrCreateTag();
        
        tag.putLong(ENERGY, MAX_ENERGY);
		
		return stack;
	}
}
