package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;

public record ServerboundCrystallizerUpdatePacket(BlockPos blockPos) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundCrystallizerUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_crystallizer_update"));
	
	public static final StreamCodec<FriendlyByteBuf, ServerboundCrystallizerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundCrystallizerUpdatePacket::blockPos,
			ServerboundCrystallizerUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundCrystallizerUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			final BlockEntity blockEntity = ctx.player().level().getBlockEntity(packet.blockPos);
			if(blockEntity instanceof AbstractCrystallizerEntity<?> crystallizer)
				crystallizer.dumpInputFluidTank();
		});
	}
}


