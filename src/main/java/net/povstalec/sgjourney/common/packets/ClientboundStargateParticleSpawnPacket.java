package net.povstalec.sgjourney.common.packets;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public class ClientboundStargateParticleSpawnPacket
{
	public final BlockPos pos;
    public final HashMap<StargatePart, BlockState> blockStates;

    public ClientboundStargateParticleSpawnPacket(BlockPos pos, HashMap<StargatePart, BlockState> blockStates)
    {
        this.pos = pos;
        this.blockStates = blockStates;
    }

    public ClientboundStargateParticleSpawnPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), new HashMap<StargatePart, BlockState>(buffer.readMap((buf) -> buf.readEnum(StargatePart.class), buf -> buf.readWithCodec(BlockState.CODEC))));
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeMap(this.blockStates, FriendlyByteBuf::writeEnum, (buf, state) -> buf.writeWithCodec(BlockState.CODEC, state));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.spawnStargateParticles(this.pos, this.blockStates);
        });
        return true;
    }
}
