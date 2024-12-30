package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.SoundAccess;

public abstract class ClientBoundSoundPackets
{
    public static record OpenWormhole(BlockPos blockPos, boolean incoming) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<OpenWormhole> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_open_wormhole_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, OpenWormhole> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, OpenWormhole::blockPos,
				ByteBufCodecs.BOOL, OpenWormhole::incoming,
				OpenWormhole::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(OpenWormhole packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
			{
				SoundAccess.playWormholeOpenSound(packet.blockPos, packet.incoming);
			});
		}
    }
    
    public static record IdleWormhole(BlockPos blockPos, boolean incoming) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<IdleWormhole> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_idle_wormhole_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, IdleWormhole> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, IdleWormhole::blockPos,
				ByteBufCodecs.BOOL, IdleWormhole::incoming,
				IdleWormhole::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(IdleWormhole packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
			{
				SoundAccess.playWormholeIdleSound(packet.blockPos, packet.incoming);
			});
		}
    }
    
    public static record CloseWormhole(BlockPos blockPos, boolean incoming) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<CloseWormhole> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_close_wormhole_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, CloseWormhole> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, CloseWormhole::blockPos,
				ByteBufCodecs.BOOL, CloseWormhole::incoming,
				CloseWormhole::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(CloseWormhole packet, IPayloadContext ctx)
		{
			ctx.enqueueWork(() ->
			{
				SoundAccess.playWormholeCloseSound(packet.blockPos, packet.incoming);
			});
		}
    }
    
    public static record IrisThud(BlockPos blockPos) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<IrisThud> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_iris_thud_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, IrisThud> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, IrisThud::blockPos,
				IrisThud::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
  
		public static void handle(IrisThud packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playIrisThudSound(packet.blockPos);
            });
        }
    }

    public static record Chevron(BlockPos blockPos, short chevron, boolean incoming, boolean open, boolean encode) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<Chevron> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_chevron_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, Chevron> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, Chevron::blockPos,
				ByteBufCodecs.SHORT, Chevron::chevron,
				ByteBufCodecs.BOOL, Chevron::incoming,
				ByteBufCodecs.BOOL, Chevron::open,
				ByteBufCodecs.BOOL, Chevron::encode,
				Chevron::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(Chevron packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playChevronSound(packet.blockPos, packet.chevron, packet.incoming, packet.open, packet.encode);
            });
        }
    }

    public static record Fail(BlockPos blockPos) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<Fail> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_fail_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, Fail> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, Fail::blockPos,
				Fail::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(Fail packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playFailSound(packet.blockPos);
            });
        }
    }
    
    public static record StargateRotation(BlockPos blockPos, boolean stop) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<StargateRotation> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_rotation"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, StargateRotation> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, StargateRotation::blockPos,
				ByteBufCodecs.BOOL, StargateRotation::stop,
				StargateRotation::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(StargateRotation packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playRotationSound(packet.blockPos, packet.stop);
            });
        }
    }
    
    public static record UniverseStart(BlockPos blockPos) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<UniverseStart> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_universe_start_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, UniverseStart> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, UniverseStart::blockPos,
				UniverseStart::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(UniverseStart packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playUniverseStartSound(packet.blockPos);
            });
        }
    }
    
    public static record MilkyWayBuildup(BlockPos blockPos) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<MilkyWayBuildup> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_milky_way_buildup_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, MilkyWayBuildup> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, MilkyWayBuildup::blockPos,
				MilkyWayBuildup::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(MilkyWayBuildup packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playMilkyWayBuildupSound(packet.blockPos);
            });
        }
    }
    
    public static record MilkyWayStop(BlockPos blockPos) implements CustomPacketPayload
    {
		public static final CustomPacketPayload.Type<MilkyWayStop> TYPE =
				new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_milky_way_stop_sound"));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, MilkyWayStop> STREAM_CODEC = StreamCodec.composite(
				BlockPos.STREAM_CODEC, MilkyWayStop::blockPos,
				MilkyWayStop::new
		);
		
		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
		{
			return TYPE;
		}
		
		public static void handle(MilkyWayStop packet, IPayloadContext ctx)
        {
            ctx.enqueueWork(() ->
            {
            	SoundAccess.playMilkyWayStopSound(packet.blockPos);
            });
        }
    }
}


