package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.SyncedConfig;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;

import java.util.function.Supplier;

public class ClientboundUpdateConfigValuesPacket
{
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
	
	public ClientboundUpdateConfigValuesPacket(FriendlyByteBuf buffer)
	{
		// DHD
		universeDHDEnergyCapacity = buffer.readLong();
		milkyWayDHDEnergyCapacity = buffer.readLong();
		pegasusDHDEnergyCapacity = buffer.readLong();
		classicDHDEnergyCapacity = buffer.readLong();
		// Transporter
		ancientTransportRingsEnergyCapacity = buffer.readLong();
		goauldTransportRingsEnergyCapacity = buffer.readLong();
		// Transporter Controller
		goauldRingPanelEnergyCapacity = buffer.readLong();
		// Naquadah Generators
		naquadahReactorEnergyCapacity = buffer.readLong();
		naquadahGeneratorMarkIEnergyCapacity = buffer.readLong();
		naquadahGeneratorMarkIIEnergyCapacity = buffer.readLong();
		// Tech
		naquadahLiquidizerEnergyCapacity = buffer.readLong();
		heavyNaquadahLiquidizerEnergyCapacity = buffer.readLong();
		crystallizerEnergyCapacity = buffer.readLong();
		advancedCrystallizerEnergyCapacity = buffer.readLong();
	}
	
	public void encode(FriendlyByteBuf buffer)
	{
		// DHD
		buffer.writeLong(universeDHDEnergyCapacity);
		buffer.writeLong(milkyWayDHDEnergyCapacity);
		buffer.writeLong(pegasusDHDEnergyCapacity);
		buffer.writeLong(classicDHDEnergyCapacity);
		// Transporter
		buffer.writeLong(ancientTransportRingsEnergyCapacity);
		buffer.writeLong(goauldTransportRingsEnergyCapacity);
		// Transporter Controller
		buffer.writeLong(goauldRingPanelEnergyCapacity);
		// Naquadah Generators
		buffer.writeLong(naquadahReactorEnergyCapacity);
		buffer.writeLong(naquadahGeneratorMarkIEnergyCapacity);
		buffer.writeLong(naquadahGeneratorMarkIIEnergyCapacity);
		// Tech
		buffer.writeLong(naquadahLiquidizerEnergyCapacity);
		buffer.writeLong(heavyNaquadahLiquidizerEnergyCapacity);
		buffer.writeLong(crystallizerEnergyCapacity);
		buffer.writeLong(advancedCrystallizerEnergyCapacity);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			// DHD
			SyncedConfig.universeDHDEnergyCapacity = universeDHDEnergyCapacity;
			SyncedConfig.milkyWayDHDEnergyCapacity = milkyWayDHDEnergyCapacity;
			SyncedConfig.pegasusDHDEnergyCapacity = pegasusDHDEnergyCapacity;
			SyncedConfig.classicDHDEnergyCapacity = classicDHDEnergyCapacity;
			// Transporter
			SyncedConfig.ancientTransportRingsEnergyCapacity = ancientTransportRingsEnergyCapacity;
			SyncedConfig.goauldTransportRingsEnergyCapacity = goauldTransportRingsEnergyCapacity;
			// Transporter Controller
			SyncedConfig.goauldRingPanelEnergyCapacity = goauldRingPanelEnergyCapacity;
			// Naquadah Generators
			SyncedConfig.naquadahReactorEnergyCapacity = naquadahReactorEnergyCapacity;
			SyncedConfig.naquadahGeneratorMarkIEnergyCapacity = naquadahGeneratorMarkIEnergyCapacity;
			SyncedConfig.naquadahGeneratorMarkIIEnergyCapacity = naquadahGeneratorMarkIIEnergyCapacity;
			// Tech
			SyncedConfig.naquadahLiquidizerEnergyCapacity = naquadahLiquidizerEnergyCapacity;
			SyncedConfig.heavyNaquadahLiquidizerEnergyCapacity = heavyNaquadahLiquidizerEnergyCapacity;
			SyncedConfig.crystallizerEnergyCapacity = crystallizerEnergyCapacity;
			SyncedConfig.advancedCrystallizerEnergyCapacity = advancedCrystallizerEnergyCapacity;
		});
		return true;
	}
}


