package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import net.povstalec.sgjourney.common.config.SyncedValues;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateConfigValuesPacket implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundUpdateConfigValuesPacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_update_config_values"));
	
	public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateConfigValuesPacket> STREAM_CODEC = createStreamCodec();
	
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
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public void encode(FriendlyByteBuf buffer)
	{
		this.syncedValues.write(buffer);
	}
	
	public static void handle(ClientboundUpdateConfigValuesPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> SyncedConfig.SYNCED_VALUES.updateFrom(packet.syncedValues));
	}
	
	
	
	private static StreamCodec<FriendlyByteBuf, ClientboundUpdateConfigValuesPacket> createStreamCodec()
	{
		return new StreamCodec<>()
		{
			@Override
			public @NotNull ClientboundUpdateConfigValuesPacket decode(@NotNull FriendlyByteBuf byteBuf)
			{
				return new ClientboundUpdateConfigValuesPacket(byteBuf);
			}
			
			@Override
			public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull ClientboundUpdateConfigValuesPacket packet)
			{
				packet.encode(byteBuf);
			}
		};
	}
}


