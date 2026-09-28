package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;

public class ServerboundEngravingUpdatePacket implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundEngravingUpdatePacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_engraving"));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEngravingUpdatePacket> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, packet -> packet.pos,
		ByteBufCodecs.STRING_UTF8, packet -> packet.pointOfOrigin,
		ByteBufCodecs.STRING_UTF8, packet -> packet.symbols,
		Address.STREAM_CODEC, packet -> packet.address,
		ServerboundEngravingUpdatePacket::new
	);
	
	private final BlockPos pos;
	private String pointOfOrigin = "";
	private String symbols = "";
	@Nullable
	private Address address = null;
	
	public ServerboundEngravingUpdatePacket(BlockPos pos)
	{
		this.pos = pos;
	}
	
	private ServerboundEngravingUpdatePacket(BlockPos pos, String pointOfOrigin, String symbols, @Nullable Address address)
	{
		this(pos);
		
		this.pointOfOrigin = pointOfOrigin;
		this.symbols = symbols;
		this.address = address;
	}
	
	public ServerboundEngravingUpdatePacket withPointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin.location().toString();
		return this;
	}
	
	public ServerboundEngravingUpdatePacket withSymbols(ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols.location().toString();
		return this;
	}
	
	public ServerboundEngravingUpdatePacket withAddress(Address address)
	{
		this.address = address;
		return this;
	}
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundEngravingUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			final Player player = ctx.player();
			Level level = player.level();
			BlockState state = level.getBlockState(packet.pos);
			if(state.getBlock() instanceof SpecialEngravableBlock gravableBlock)
			{
				if(!packet.pointOfOrigin.isEmpty())
					gravableBlock.setPointOfOrigin(level, packet.pos, state, Conversion.stringToPointOfOrigin(packet.pointOfOrigin));
				if(!packet.symbols.isEmpty())
					gravableBlock.setSymbols(level, packet.pos, state, Conversion.stringToSymbols(packet.symbols));
				if(packet.address != null)
					gravableBlock.setAddress(level, packet.pos, state,packet. address);
				
				gravableBlock.useGraver((ServerPlayer) player, packet.pos);
			}
		});
	}
}


