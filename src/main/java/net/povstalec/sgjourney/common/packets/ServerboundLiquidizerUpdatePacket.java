package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;

public record ServerboundLiquidizerUpdatePacket(BlockPos blockPos, boolean inputTank) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundLiquidizerUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_liquidizer_update"));
	
	public static final StreamCodec<FriendlyByteBuf, ServerboundLiquidizerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundLiquidizerUpdatePacket::blockPos,
			ByteBufCodecs.BOOL, ServerboundLiquidizerUpdatePacket::inputTank,
			ServerboundLiquidizerUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundLiquidizerUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			final BlockEntity blockEntity = ctx.player().level().getBlockEntity(packet.blockPos);
			if(blockEntity instanceof AbstractNaquadahLiquidizerEntity<?> liquidizer)
			{
				if(packet.inputTank)
					liquidizer.dumpInputFluidTank();
				else
					liquidizer.dumpOutputFluidTank();
			}
		});
	}
}


