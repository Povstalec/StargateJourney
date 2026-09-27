package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.config.*;

import java.util.function.Supplier;

public class ClientboundUpdateConfigValuesPacket
{
	private final SyncedValues syncedValues;
	
	public ClientboundUpdateConfigValuesPacket()
	{
		this.syncedValues = SyncedConfig.SYNCED_VALUES;
	}
	
	public ClientboundUpdateConfigValuesPacket(FriendlyByteBuf buffer)
	{
		this.syncedValues = SyncedConfig.SYNCED_VALUES.copy();
		this.syncedValues.read(buffer);
	}
	
	public void encode(FriendlyByteBuf buffer)
	{
		this.syncedValues.write(buffer);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> SyncedConfig.SYNCED_VALUES.updateFrom(this.syncedValues));
		return true;
	}
}


