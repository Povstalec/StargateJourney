package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

public abstract class AbstractCrystalItem extends Item
{
	public AbstractCrystalItem(Properties properties)
	{
		super(properties);
	}

	public boolean isLarge()
	{
		return false;
	}

	public boolean isAdvanced()
	{
		return false;
	}

	public boolean isRegular()
	{
		return !isAdvanced() && !isLarge();
	}

	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.empty();
	}

	public Optional<Component> descriptionInRing()
	{
		return Optional.empty();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(descriptionInDHD(stack).isPresent())
		{
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.crystal.in_dhd").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
			tooltipComponents.add(descriptionInDHD(stack).get());
		}

		/*if(descriptionInRing().isPresent())
		{
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.crystal.in_ring").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
			tooltipComponents.add(descriptionInRing().get());
		}*/

		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	public static class Storage
	{
		private int[] crystals = new int[0];

		private int[] advancedCrystals = new int[0];
		
		public Storage() {}
		
		public void addCrystal(boolean isAdvanced, int slot)
		{
			if(!isAdvanced)
				crystals = ArrayHelper.growIntArray(this.crystals, slot);
			else
				advancedCrystals = ArrayHelper.growIntArray(this.advancedCrystals, slot);
		}
		
		public void reset()
		{
			this.crystals = new int[0];
			this.advancedCrystals = new int[0];
		}
		
		public int[] getCrystals()
		{
			return this.crystals;
		}
		
		public int[] getAdvancedCrystals()
		{
			return this.advancedCrystals;
		}
	}
}
