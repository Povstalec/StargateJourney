package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import org.jetbrains.annotations.Nullable;

public class ControlCrystalItem extends AbstractCrystalItem
{
	public static final int AUTOCLOSE_SECONDS = 10;
	public static final int AUTOCLOSE_TICKS = AUTOCLOSE_SECONDS * 20;
	
	public ControlCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.CONTROL;
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
	
	/*public enum Instruction
	{
		IF, //TODO () -> function(), (dhd) -> dhd.function(), (stargate)-> stargate.function(), ...
		ELSE_IF,
		ELSE
	}*/
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.control_crystal.description"));
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
		public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
		{
			tooltipComponents.add(ComponentHelper.tickTimer("tooltip.sgjourney.control_crystal_large.autoclose", AUTOCLOSE_TICKS, ChatFormatting.DARK_PURPLE));
			tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.control_crystal_large.description"));
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
	}
}
