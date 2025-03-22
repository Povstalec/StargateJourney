package net.povstalec.sgjourney.common.packets;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.stargate.StargateConnection;

public record ClientboundStargateStateUpdatePacket(BlockPos blockPos, StargateConnection.State connectionState, boolean canSinkGate, Map<StargatePart, BlockState> blockStates) implements CustomPacketPayload
{
    private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    
    public static final CustomPacketPayload.Type<ClientboundStargateStateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_state_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStargateStateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundStargateStateUpdatePacket>()
    {
        public ClientboundStargateStateUpdatePacket decode(RegistryFriendlyByteBuf buffer)
        {
            return new ClientboundStargateStateUpdatePacket(FriendlyByteBuf.readBlockPos(buffer), StargateConnection.State.fromByte(buffer.readByte()), buffer.readBoolean(),
                    new HashMap<StargatePart, BlockState>(buffer.readMap((buf) -> buf.readEnum(StargatePart.class), buf -> buf.readWithCodec(BUILTIN_CONTEXT_OPS, BlockState.CODEC, NbtAccounter.unlimitedHeap()))));
        }
        
        public void encode(RegistryFriendlyByteBuf buffer, ClientboundStargateStateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buffer, packet.blockPos);
            buffer.writeByte(packet.connectionState.byteValue());
            buffer.writeBoolean(packet.canSinkGate);
            buffer.writeMap(packet.blockStates, FriendlyByteBuf::writeEnum, (buf, state) -> buf.writeWithCodec(BUILTIN_CONTEXT_OPS, BlockState.CODEC, state));
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundStargateStateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateStargateState(packet.blockPos, packet.connectionState, packet.canSinkGate, packet.blockStates);
        });
    }
}


