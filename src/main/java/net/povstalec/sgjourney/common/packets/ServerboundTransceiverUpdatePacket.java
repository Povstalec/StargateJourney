package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;
import net.povstalec.sgjourney.StargateJourney;

public record ServerboundTransceiverUpdatePacket(BlockPos blockPos, boolean remove, boolean toggleFrequency, int number, boolean transmit) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundTransceiverUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_transceiver_update"));
	
	public static final StreamCodec<ByteBuf, ServerboundTransceiverUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundTransceiverUpdatePacket::blockPos,
			ByteBufCodecs.BOOL, ServerboundTransceiverUpdatePacket::remove,
			ByteBufCodecs.BOOL, ServerboundTransceiverUpdatePacket::toggleFrequency,
			ByteBufCodecs.VAR_INT, ServerboundTransceiverUpdatePacket::number,
			ByteBufCodecs.BOOL, ServerboundTransceiverUpdatePacket::transmit,
			ServerboundTransceiverUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundTransceiverUpdatePacket packet, IPayloadContext ctx)
    {
    	ctx.enqueueWork(() -> {
    		final BlockEntity blockEntity = ctx.player().level().getBlockEntity(packet.blockPos);
    		
    		if(blockEntity instanceof TransceiverEntity transceiver)
    		{
    			if(packet.transmit)
    				transceiver.sendTransmission();
    			else if(packet.toggleFrequency)
    				transceiver.toggleFrequency();
    			else if(packet.remove)
    				transceiver.removeFromCode();
    			else
    				transceiver.addToCode(packet.number);
    		}
    	});
    }
}


