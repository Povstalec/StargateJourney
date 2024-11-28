package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;

public record ServerboundDHDUpdatePacket(BlockPos blockPos, int symbol) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundDHDUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_dhd_update"));
	
	public static final StreamCodec<ByteBuf, ServerboundDHDUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundDHDUpdatePacket::blockPos,
			ByteBufCodecs.VAR_INT, ServerboundDHDUpdatePacket::symbol,
			ServerboundDHDUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundDHDUpdatePacket packet, IPayloadContext ctx)
    {
    	ctx.enqueueWork(() -> {
    		final BlockEntity blockEntity = ctx.player().level().getBlockEntity(packet.blockPos);
    		if(blockEntity instanceof AbstractDHDEntity dhd)
    			dhd.engageChevron(packet.symbol);
    	});
    }
}


