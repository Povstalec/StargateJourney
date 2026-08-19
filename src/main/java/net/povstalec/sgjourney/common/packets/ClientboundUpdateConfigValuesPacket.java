package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.SyncedConfig;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateConfigValuesPacket implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundUpdateConfigValuesPacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_update_config_values"));
	
	public static final StreamCodec<ByteBuf, ClientboundUpdateConfigValuesPacket> STREAM_CODEC = createStreamCodec();
	
	// DHD
	private final long universeDHDEnergyCapacity;
	private final long milkyWayDHDEnergyCapacity;
	private final long pegasusDHDEnergyCapacity;
	private final long classicDHDEnergyCapacity;
	// Transporter
	private final long ancientTransportRingsEnergyCapacity;
	private final long goauldTransportRingsEnergyCapacity;
	// Transporter Controller
	private final long goauldRingPanelEnergyCapacity;
	// Naquadah Generators
	private final long naquadahReactorEnergyCapacity;
	private final long naquadahGeneratorMarkIEnergyCapacity;
	private final long naquadahGeneratorMarkIIEnergyCapacity;
	// Tech
	private final long naquadahLiquidizerEnergyCapacity;
	private final long heavyNaquadahLiquidizerEnergyCapacity;
	private final long crystallizerEnergyCapacity;
	private final long advancedCrystallizerEnergyCapacity;
	
	public ClientboundUpdateConfigValuesPacket()
	{
		// DHD
		universeDHDEnergyCapacity = CommonDHDConfig.universe_dhd_energy_buffer_capacity.get();
		milkyWayDHDEnergyCapacity = CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
		pegasusDHDEnergyCapacity = CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
		classicDHDEnergyCapacity = CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
		// Transporter
		ancientTransportRingsEnergyCapacity = CommonTransporterConfig.ancient_transport_rings_energy_capacity.get();
		goauldTransportRingsEnergyCapacity = CommonTransporterConfig.goauld_transport_rings_energy_capacity.get();
		// Transporter Controller
		goauldRingPanelEnergyCapacity = CommonTransporterConfig.goauld_ring_panel_energy_capacity.get();
		// Naquadah Generators
		naquadahReactorEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get();
		naquadahGeneratorMarkIEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
		naquadahGeneratorMarkIIEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
		// Tech
		naquadahLiquidizerEnergyCapacity = CommonTechConfig.naquadah_liquidizer_energy_capacity.get();
		heavyNaquadahLiquidizerEnergyCapacity = CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get();
		crystallizerEnergyCapacity = CommonTechConfig.crystallizer_energy_capacity.get();
		advancedCrystallizerEnergyCapacity = CommonTechConfig.advanced_crystallizer_energy_capacity.get();
	}
	
	public ClientboundUpdateConfigValuesPacket(ByteBuf buffer)
	{
		// DHD
		universeDHDEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		milkyWayDHDEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		pegasusDHDEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		classicDHDEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Transporter
		ancientTransportRingsEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		goauldTransportRingsEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Transporter Controller
		goauldRingPanelEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Naquadah Generators
		naquadahReactorEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		naquadahGeneratorMarkIEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		naquadahGeneratorMarkIIEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Tech
		naquadahLiquidizerEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		heavyNaquadahLiquidizerEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		crystallizerEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		advancedCrystallizerEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
	}
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public void encode(ByteBuf buffer)
	{
		// DHD
		ByteBufCodecs.VAR_LONG.encode(buffer, universeDHDEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, milkyWayDHDEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, pegasusDHDEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, classicDHDEnergyCapacity);
		// Transporter
		ByteBufCodecs.VAR_LONG.encode(buffer, ancientTransportRingsEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, goauldTransportRingsEnergyCapacity);
		// Transporter Controller
		ByteBufCodecs.VAR_LONG.encode(buffer, goauldRingPanelEnergyCapacity);
		// Naquadah Generators
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadahReactorEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadahGeneratorMarkIEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadahGeneratorMarkIIEnergyCapacity);
		// Tech
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadahLiquidizerEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, heavyNaquadahLiquidizerEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, crystallizerEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, advancedCrystallizerEnergyCapacity);
	}
	
	public static void handle(ClientboundUpdateConfigValuesPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			// DHD
			SyncedConfig.universeDHDEnergyCapacity = packet.universeDHDEnergyCapacity;
			SyncedConfig.milkyWayDHDEnergyCapacity = packet.milkyWayDHDEnergyCapacity;
			SyncedConfig.pegasusDHDEnergyCapacity = packet.pegasusDHDEnergyCapacity;
			SyncedConfig.classicDHDEnergyCapacity = packet.classicDHDEnergyCapacity;
			// Transporter
			SyncedConfig.ancientTransportRingsEnergyCapacity = packet.ancientTransportRingsEnergyCapacity;
			SyncedConfig.goauldTransportRingsEnergyCapacity = packet.goauldTransportRingsEnergyCapacity;
			// Transporter Controller
			SyncedConfig.goauldRingPanelEnergyCapacity = packet.goauldRingPanelEnergyCapacity;
			// Naquadah Generators
			SyncedConfig.naquadahReactorEnergyCapacity = packet.naquadahReactorEnergyCapacity;
			SyncedConfig.naquadahGeneratorMarkIEnergyCapacity = packet.naquadahGeneratorMarkIEnergyCapacity;
			SyncedConfig.naquadahGeneratorMarkIIEnergyCapacity = packet.naquadahGeneratorMarkIIEnergyCapacity;
			// Tech
			SyncedConfig.naquadahLiquidizerEnergyCapacity = packet.naquadahLiquidizerEnergyCapacity;
			SyncedConfig.heavyNaquadahLiquidizerEnergyCapacity = packet.heavyNaquadahLiquidizerEnergyCapacity;
			SyncedConfig.crystallizerEnergyCapacity = packet.crystallizerEnergyCapacity;
			SyncedConfig.advancedCrystallizerEnergyCapacity = packet.advancedCrystallizerEnergyCapacity;
		});
	}
	
	
	
	private static StreamCodec<ByteBuf, ClientboundUpdateConfigValuesPacket> createStreamCodec()
	{
		return new StreamCodec<>()
		{
			@Override
			public @NotNull ClientboundUpdateConfigValuesPacket decode(@NotNull ByteBuf byteBuf)
			{
				return new ClientboundUpdateConfigValuesPacket(byteBuf);
			}
			
			@Override
			public void encode(@NotNull ByteBuf byteBuf, @NotNull ClientboundUpdateConfigValuesPacket packet)
			{
				packet.encode(byteBuf);
			}
		};
	}
}


