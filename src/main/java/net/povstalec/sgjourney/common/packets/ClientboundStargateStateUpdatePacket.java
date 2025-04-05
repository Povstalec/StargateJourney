package net.povstalec.sgjourney.common.packets;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

public class ClientboundStargateStateUpdatePacket
{
	private static final RegistryOps<Tag> BUILTIN_CONTEXT_OPS = RegistryOps.create(NbtOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
	
    public final BlockPos pos;
    public final StargateConnection.State connectionState;
    public final boolean canSinkGate;
    public final HashMap<StargatePart, BlockState> blockStates;

    public ClientboundStargateStateUpdatePacket(BlockPos pos, StargateConnection.State connectionState, boolean canSinkGate, HashMap<StargatePart, BlockState> blockStates)
    {
        this.pos = pos;
        this.connectionState = connectionState;
        this.canSinkGate = canSinkGate;
        this.blockStates = blockStates;
    }

    public ClientboundStargateStateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), StargateConnection.State.fromByte(buffer.readByte()), buffer.readBoolean(), new HashMap<StargatePart, BlockState>(buffer.readMap((buf) -> buf.readEnum(StargatePart.class), buf -> buf.readWithCodec(BUILTIN_CONTEXT_OPS, BlockState.CODEC))));
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeByte(this.connectionState.byteValue());
        buffer.writeBoolean(this.canSinkGate);
        buffer.writeMap(this.blockStates, FriendlyByteBuf::writeEnum, (buf, state) -> buf.writeWithCodec(BUILTIN_CONTEXT_OPS, BlockState.CODEC, state));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargateState(this.pos, this.connectionState, this.canSinkGate, this.blockStates);
        });
        return true;
    }
}


