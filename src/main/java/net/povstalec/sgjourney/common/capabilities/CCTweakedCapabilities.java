package net.povstalec.sgjourney.common.capabilities;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CCTweakedCapabilities
{
	public static final Capability<IPeripheral> CAPABILITY_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {});
}
