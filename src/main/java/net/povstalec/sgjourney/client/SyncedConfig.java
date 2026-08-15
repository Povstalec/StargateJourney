package net.povstalec.sgjourney.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundUpdateConfigValuesPacket;

/**
 * Class containing values to be synced with the Client whenever it joins a server
 */
public class SyncedConfig
{
	// DHD
	public static long milkyWayDHDEnergyCapacity = CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
	public static long pegasusDHDEnergyCapacity = CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
	public static long classicDHDEnergyCapacity = CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
	
	// Transporter
	public static long ancientTransportRingsEnergyCapacity = CommonTransporterConfig.ancient_transport_rings_energy_capacity.get();
	public static long goauldTransportRingsEnergyCapacity = CommonTransporterConfig.goauld_transport_rings_energy_capacity.get();
	
	// Transporter Controller
	public static long goauldRingPanelEnergyCapacity = CommonTransporterConfig.goauld_ring_panel_energy_capacity.get();
	
	// Naquadah Generators
	public static long naquadahReactorEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get();
	public static long naquadahGeneratorMarkIEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
	public static long naquadahGeneratorMarkIIEnergyCapacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
	
	// Tech
	public static long naquadahLiquidizerEnergyCapacity = CommonTechConfig.naquadah_liquidizer_energy_capacity.get();
	public static long heavyNaquadahLiquidizerEnergyCapacity = CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get();
	public static long crystallizerEnergyCapacity = CommonTechConfig.crystallizer_energy_capacity.get();
	public static long advancedCrystallizerEnergyCapacity = CommonTechConfig.advanced_crystallizer_energy_capacity.get();
	
	public static void syncConfig(ServerPlayer player)
	{
		PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundUpdateConfigValuesPacket());
	}
}
