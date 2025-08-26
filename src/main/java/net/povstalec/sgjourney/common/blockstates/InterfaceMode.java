package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public enum InterfaceMode implements StringRepresentable
{
	OFF("off", Component.translatable("block.sgjourney.interface.mode.off").withStyle(ChatFormatting.DARK_GRAY),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.off.usage")),
	
	RING_SEGMENT("ring_segment", Component.translatable("block.sgjourney.interface.mode.ring_segment").withStyle(ChatFormatting.GREEN),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.ring_segment.usage"),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_segment.usage_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_segment.usage_2").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_segment.usage_3").withStyle(ChatFormatting.YELLOW)),
	
	RING_ROTATION("ring_rotation", Component.translatable("block.sgjourney.interface.mode.ring_rotation").withStyle(ChatFormatting.DARK_GREEN),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.ring_rotation.usage"),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_rotation.usage_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_rotation.usage_2").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.ring_rotation.usage_3").withStyle(ChatFormatting.YELLOW)),
	
	CHEVRONS_ACTIVE("chevrons_active", Component.translatable("block.sgjourney.interface.mode.chevrons_active").withStyle(ChatFormatting.RED),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.chevrons_active.usage"),
			Component.translatable("tooltip.sgjourney.interface.mode.chevrons_active.usage_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.chevrons_active.usage_2").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.chevrons_active.usage_3").withStyle(ChatFormatting.YELLOW)),
	
	WORMHOLE_ACTIVE("wormhole_active", Component.translatable("block.sgjourney.interface.mode.wormhole_active").withStyle(ChatFormatting.AQUA),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.wormhole_active.usage"),
			Component.translatable("tooltip.sgjourney.interface.mode.wormhole_active.usage_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.wormhole_active.usage_2").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.wormhole_active.usage_3").withStyle(ChatFormatting.YELLOW)),
	
	IRIS("iris", Component.translatable("block.sgjourney.interface.mode.iris").withStyle(ChatFormatting.YELLOW),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.iris.usage.output"),
			Component.translatable("tooltip.sgjourney.interface.mode.iris.usage.output_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.iris.usage.output_2").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.iris.usage.output_3").withStyle(ChatFormatting.YELLOW),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.iris.usage.input"),
			Component.translatable("tooltip.sgjourney.interface.mode.iris.usage.input_1").withStyle(ChatFormatting.YELLOW),
			Component.translatable("tooltip.sgjourney.interface.mode.iris.usage.input_2").withStyle(ChatFormatting.YELLOW)),
	
	SHIELDING("shielding", Component.translatable("block.sgjourney.interface.mode.shielding").withStyle(ChatFormatting.BLUE),
			ComponentHelper.usage("tooltip.sgjourney.interface.mode.shielding.usage"));
	
	private final String id;
	private final Component name;
	private final Component[] usage;
	
	InterfaceMode(String id, Component name, Component... usage)
	{
		this.id = id;
		this.name = name;
		this.usage = usage;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.id;
	}
	
	public Component getName()
	{
		return name;
	}
	
	public Component[] getUsage()
	{
		return usage;
	}
	
	public InterfaceMode next(boolean isAdvanced)
	{
		return switch(this)
		{
		case OFF -> RING_SEGMENT;
		case RING_SEGMENT -> RING_ROTATION;
		case RING_ROTATION -> CHEVRONS_ACTIVE;
		case CHEVRONS_ACTIVE -> WORMHOLE_ACTIVE;
		case WORMHOLE_ACTIVE -> IRIS;
		case IRIS -> isAdvanced ? SHIELDING : OFF;
		
		default -> OFF;
		};
	}
	
	public InterfaceMode previous(boolean isAdvanced)
	{
		return switch(this)
		{
			case OFF -> isAdvanced ? SHIELDING : IRIS;
			case RING_SEGMENT -> OFF;
			case RING_ROTATION -> RING_SEGMENT;
			case CHEVRONS_ACTIVE -> RING_ROTATION;
			case WORMHOLE_ACTIVE -> CHEVRONS_ACTIVE;
			case IRIS -> WORMHOLE_ACTIVE;
			case SHIELDING -> IRIS;
		};
	}
}
