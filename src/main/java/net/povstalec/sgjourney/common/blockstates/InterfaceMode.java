package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum InterfaceMode implements StringRepresentable
{
	OFF("off", Component.translatable("block.sgjourney.interface.mode.off").withStyle(ChatFormatting.DARK_GRAY)),
	RING_SEGMENT("ring_segment", Component.translatable("block.sgjourney.interface.mode.ring_segment").withStyle(ChatFormatting.GREEN)),
	RING_ROTATION("ring_rotation", Component.translatable("block.sgjourney.interface.mode.ring_rotation").withStyle(ChatFormatting.DARK_GREEN)),
	CHEVRONS_ACTIVE("chevrons_active", Component.translatable("block.sgjourney.interface.mode.chevrons_active").withStyle(ChatFormatting.RED)),
	WORMHOLE_ACTIVE("wormhole_active", Component.translatable("block.sgjourney.interface.mode.wormhole_active").withStyle(ChatFormatting.AQUA)),
	IRIS("iris", Component.translatable("block.sgjourney.interface.mode.iris").withStyle(ChatFormatting.YELLOW)),
	SHIELDING("shielding", Component.translatable("block.sgjourney.interface.mode.shielding").withStyle(ChatFormatting.BLUE));
	
	private final String id;
	private final Component translationName;
	
	InterfaceMode(String id, Component translationName)
	{
		this.id = id;
		this.translationName = translationName;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.id;
	}
	
	public Component getModeTranslation()
	{
		return translationName;
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
}
