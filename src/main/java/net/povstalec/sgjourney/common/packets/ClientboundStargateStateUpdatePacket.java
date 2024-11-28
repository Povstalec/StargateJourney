package net.povstalec.sgjourney.common.packets;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public record ClientboundStargateStateUpdatePacket(BlockPos blockPos, boolean canSinkGate, Map<StargatePart, BlockState> blockStates) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundStargateStateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_state_update"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundStargateStateUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundStargateStateUpdatePacket::blockPos,
            ByteBufCodecs.BOOL, ClientboundStargateStateUpdatePacket::canSinkGate,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new,
                    NeoForgeStreamCodecs.enumCodec(StargatePart.class),
                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)), ClientboundStargateStateUpdatePacket::blockStates,
            ClientboundStargateStateUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundStargateStateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateStargateState(packet.blockPos, packet.canSinkGate, packet.blockStates);
        });
    }
}


