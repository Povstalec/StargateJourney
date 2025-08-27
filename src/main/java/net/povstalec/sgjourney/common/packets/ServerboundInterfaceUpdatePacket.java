package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.tech_interface.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;

public record ServerboundInterfaceUpdatePacket(BlockPos pos, long energyTarget, InterfaceMode mode) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundInterfaceUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_interface_update"));
	
	public static final StreamCodec<FriendlyByteBuf, ServerboundInterfaceUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ServerboundInterfaceUpdatePacket::pos,
			ByteBufCodecs.VAR_LONG, ServerboundInterfaceUpdatePacket::energyTarget,
			NeoForgeStreamCodecs.enumCodec(InterfaceMode.class), ServerboundInterfaceUpdatePacket::mode,
			ServerboundInterfaceUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundInterfaceUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			Level level = ctx.player().level();
			final BlockEntity blockEntity = level.getBlockEntity(packet.pos);
			
			if(blockEntity instanceof AbstractInterfaceEntity interfaceEntity)
				interfaceEntity.setEnergyTarget(packet.energyTarget);
			
			BlockState state = level.getBlockState(packet.pos);
			if(level.getBlockState(packet.pos).getBlock() instanceof AbstractInterfaceBlock interfaceBlock)
				interfaceBlock.setMode(state, level, packet.pos, packet.mode);
		});
	}
}


