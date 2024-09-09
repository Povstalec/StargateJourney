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
	SHIELDING("shielding", Component.translatable("block.sgjourney.interface.mode.shielding").withStyle(ChatFormatting.YELLOW));
	
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
}
