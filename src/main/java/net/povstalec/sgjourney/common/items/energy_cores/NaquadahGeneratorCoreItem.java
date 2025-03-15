package net.povstalec.sgjourney.common.items.energy_cores;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;

import java.util.List;

public class NaquadahGeneratorCoreItem extends Item implements IEnergyCore
{
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
		return energyCore.getOrDefault(DataComponentInit.REACTION_PROGRESS, 0L);
	}
	
	public long getMaxReactionProgress()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get(); //TODO Add separate config
	}
	
	public long doReaction(ItemStack energyCore, ItemStack input)
	{
		long progress = reactionProgress(energyCore);
		if(progress < getMaxReactionProgress())
		{
			energyCore.set(DataComponentInit.REACTION_PROGRESS, progress + 1);
			
			return maxGeneratedEnergy(energyCore, input);
		}
		
		return 0;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.naquadah_generator_core.reaction_progress").append(Component.literal(": " + reactionProgress(stack) + " / " + getMaxReactionProgress())).withStyle(ChatFormatting.GREEN));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.naquadah_generator_core.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
			energyCore.set(DataComponentInit.REACTION_PROGRESS, 1L);
		}
		
		return energy;
	}
}
