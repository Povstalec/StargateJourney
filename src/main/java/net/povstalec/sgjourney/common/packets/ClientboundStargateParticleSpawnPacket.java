package net.povstalec.sgjourney.common.packets;

import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public record ClientboundStargateParticleSpawnPacket(BlockPos blockPos, Map<StargatePart, BlockState> blockStates) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundStargateParticleSpawnPacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_particle_spawn"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundStargateParticleSpawnPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundStargateParticleSpawnPacket::blockPos,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new,
                    NeoForgeStreamCodecs.enumCodec(StargatePart.class),
                    ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)), ClientboundStargateParticleSpawnPacket::blockStates,
            ClientboundStargateParticleSpawnPacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
    
	private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

    public static void handle(ClientboundStargateParticleSpawnPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.spawnStargateParticles(packet.blockPos, packet.blockStates);
        });
    }
}
