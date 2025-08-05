package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.UUID;

public record ClientboundArcheologistNotebookOpenScreenPacket(UUID playerId, boolean mainHand, CompoundTag tag) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundArcheologistNotebookOpenScreenPacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_archeologist_notebook_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundArcheologistNotebookOpenScreenPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientboundArcheologistNotebookOpenScreenPacket::playerId,
            ByteBufCodecs.BOOL, ClientboundArcheologistNotebookOpenScreenPacket::mainHand,
            ByteBufCodecs.COMPOUND_TAG, ClientboundArcheologistNotebookOpenScreenPacket::tag,
            ClientboundArcheologistNotebookOpenScreenPacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
    
    public static void handle(ClientboundArcheologistNotebookOpenScreenPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            ClientAccess.openArcheologistNotebookScreen(packet.playerId, packet.mainHand, packet.tag);
        });
    }
}


