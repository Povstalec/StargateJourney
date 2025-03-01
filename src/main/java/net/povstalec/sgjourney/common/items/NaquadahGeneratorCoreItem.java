package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NaquadahGeneratorCoreItem extends Item implements IEnergyCore
{
	public static final String REACTION_PROGRESS = "reaction_progress";
	
	public NaquadahGeneratorCoreItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return reactionProgress(stack) > 0;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) reactionProgress(stack) / getMaxReactionProgress());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) reactionProgress(stack) / getMaxReactionProgress());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public long reactionProgress(ItemStack energyCore)
	{
		if(!energyCore.hasTag() || !energyCore.getTag().contains(REACTION_PROGRESS))
			return 0;
		
		return energyCore.getTag().getLong(REACTION_PROGRESS);
	}
	
	public long getMaxReactionProgress()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get(); //TODO Add separate config
	}
	
	public long doReaction(ItemStack energyCore, ItemStack input)
	{
		CompoundTag tag = energyCore.getTag();
		
		if(tag == null || !tag.contains(REACTION_PROGRESS))
			return 0;
		
		long progress = tag.getLong(REACTION_PROGRESS);
		
		if(progress < getMaxReactionProgress())
		{
			progress++;
			tag.putLong(REACTION_PROGRESS, progress);
			
			return maxGeneratedEnergy(energyCore, input);
		}
		
		return 0;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.naquadah_generator_core.reaction_progress").append(Component.literal(": " + reactionProgress(stack) + " / " + getMaxReactionProgress())).withStyle(ChatFormatting.GREEN));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	
	
	@Override
	public long maxGeneratedEnergy(ItemStack energyCore, ItemStack input)
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get(); //TODO Add separate config
	}
	
	@Override
	public long generateEnergy(ItemStack energyCore, ItemStack input)
	{
		// Starts a reaction if fuel rod is found
		long energy = doReaction(energyCore, input);
		if(energy == 0 && input.getItem() instanceof NaquadahFuelRodItem && NaquadahFuelRodItem.getFuel(input) > 0)
		{
			NaquadahFuelRodItem.depleteFuel(input);
			energyCore.getOrCreateTag().putLong(REACTION_PROGRESS, 1);
		}
		
		return energy;
	}
}
