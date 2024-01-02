package net.povstalec.sgjourney.common.items.crystals;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ControlCrystalItem extends AbstractCrystalItem
{
	public ControlCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum FunctionTarget
	{
		CRYSTAL_INTERFACE,
		STARGATE,
		DHD,
		TRANSPORT_RINGS;
	}
	
	public enum Function
	{
		SEND_REDSTONE_SIGNAL(FunctionTarget.CRYSTAL_INTERFACE),
		SAVE_TO_MEMORY_CRYSTAL(FunctionTarget.CRYSTAL_INTERFACE),
		
		INPUT_SYMBOL(FunctionTarget.STARGATE);
		
		Function(FunctionTarget target)
		{
			
		}
	}
	
	public static class Large extends ControlCrystalItem
	{
		public Large(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public boolean isLarge()
		{
			return true;
		}

		@Override
		public Optional<Component> descriptionInDHD()
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.control.large").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
	}
	
	public static class Advanced extends ControlCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}

		@Override
		public Optional<Component> descriptionInDHD()
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.control.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
	}
}
