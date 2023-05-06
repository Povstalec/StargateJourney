package net.povstalec.sgjourney.common.misc;

import net.minecraft.util.StringRepresentable;

public enum InterfaceMode implements StringRepresentable
{
	OFF("off"),
	RING_ROTATION("ring_rotation"),
	CHEVRONS_ACTIVE("chevrons_active"),
	WORMHOLE_ACTIVE("wormhole_active");
	
	private final String id;
	
	InterfaceMode(String id)
	{
		this.id = id;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.id;
	}
}
