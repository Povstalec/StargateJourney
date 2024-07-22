package net.povstalec.sgjourney.common.packets;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.DataResult;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public class ClientboundStargateStateUpdatePacket
{
    public final BlockPos pos;
    public final HashMap<StargatePart, BlockState> blockStates;

    public ClientboundStargateStateUpdatePacket(BlockPos pos, HashMap<StargatePart, BlockState> blockStates)
    {
        this.pos = pos;
        this.blockStates = blockStates;
    }

    public ClientboundStargateStateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), new HashMap<StargatePart, BlockState>(buffer.readMap((buf) -> buf.readEnum(StargatePart.class), buf -> BlockState.CODEC.parse(NbtOps.INSTANCE, buf.readNbt()).result().get())));
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeMap(this.blockStates, FriendlyByteBuf::writeEnum, (buf, state) ->
        {
        	DataResult<Tag> blockStateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state);
			Optional<Tag> result = blockStateTag.result();
			
			if(result.isPresent())
				buf.writeNbt((CompoundTag) result.get());
			else
				buf.writeByte(0); // Basically same as writing an empty CompoundTag
        });
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargateState(this.pos, this.blockStates);
        });
        return true;
    }
}


