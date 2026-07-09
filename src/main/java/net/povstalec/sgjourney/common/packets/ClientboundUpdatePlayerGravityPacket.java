package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public record ClientboundUpdatePlayerGravityPacket(double gravity)
{
	public ClientboundUpdatePlayerGravityPacket(FriendlyByteBuf buffer)
	{
		this(buffer.readDouble());
	}
	
	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeDouble(gravity);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> ClientAccess.updatePlayerGravity(gravity));
		return true;
	}
}


