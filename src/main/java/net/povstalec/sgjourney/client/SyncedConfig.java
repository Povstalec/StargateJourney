package net.povstalec.sgjourney.client;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.config.*;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundUpdateConfigValuesPacket;

/**
 * Class containing values to be synced with the Client whenever it joins a server
 */
public class SyncedConfig
{
	//============================================================================================
	//***************************************Block Entities***************************************
	//============================================================================================
	
	// DHD
	public static long universe_dhd_energy_buffer_capacity = CommonDHDConfig.universe_dhd_energy_buffer_capacity.get();
	public static long milky_way_dhd_energy_buffer_capacity = CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
	public static long pegasus_dhd_energy_buffer_capacity = CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
	public static long classic_dhd_energy_buffer_capacity = CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
	
	// Transporter
	public static long ancient_transport_rings_energy_capacity = CommonTransporterConfig.ancient_transport_rings_energy_capacity.get();
	public static long goauld_transport_rings_energy_capacity = CommonTransporterConfig.goauld_transport_rings_energy_capacity.get();
	
	// Transporter Controller
	public static long goauld_ring_panel_energy_capacity = CommonTransporterConfig.goauld_ring_panel_energy_capacity.get();
	
	// Naquadah Generators
	public static long naquadah_reactor_capacity = CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get();
	public static long naquadah_generator_mark_i_capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
	public static long naquadah_generator_mark_ii_capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
	
	// Tech
	public static long naquadah_liquidizer_energy_capacity = CommonTechConfig.naquadah_liquidizer_energy_capacity.get();
	public static long heavy_naquadah_liquidizer_energy_capacity = CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get();
	public static long crystallizer_energy_capacity = CommonTechConfig.crystallizer_energy_capacity.get();
	public static long advanced_crystallizer_energy_capacity = CommonTechConfig.advanced_crystallizer_energy_capacity.get();
	
	//============================================================================================
	//********************************************Items*******************************************
	//============================================================================================
	
	// Crystals
	public static long energy_crystal_capacity = CommonCrystalConfig.energy_crystal_capacity.get();
	public static long advanced_energy_crystal_capacity = CommonCrystalConfig.advanced_energy_crystal_capacity.get();
	public static long energy_crystal_max_transfer = CommonCrystalConfig.energy_crystal_max_transfer.get();
	public static long advanced_energy_crystal_max_transfer = CommonCrystalConfig.advanced_energy_crystal_max_transfer.get();
	public static long energy_crystal_energy_target_increase = CommonCrystalConfig.energy_crystal_energy_target_increase.get();
	public static long advanced_energy_crystal_energy_target_increase = CommonCrystalConfig.advanced_energy_crystal_energy_target_increase.get();
	
	public static long transfer_crystal_max_transfer = CommonCrystalConfig.transfer_crystal_max_transfer.get();
	public static long advanced_transfer_crystal_max_transfer = CommonCrystalConfig.advanced_transfer_crystal_max_transfer.get();
	
	public static int memory_crystal_capacity = CommonCrystalConfig.memory_crystal_capacity.get();
	public static int advanced_memory_crystal_capacity = CommonCrystalConfig.advanced_memory_crystal_capacity.get();
	
	// Tech
	public static boolean fusion_core_infinite_energy = CommonTechConfig.fusion_core_infinite_energy.get();
	public static long fusion_core_energy_from_fuel = CommonTechConfig.fusion_core_energy_from_fuel.get();
	public static int fusion_core_fuel_capacity = CommonTechConfig.fusion_core_fuel_capacity.get();
	
	public static int vial_capacity = CommonTechConfig.vial_capacity.get();
	
	public static int personal_shield_capacity = CommonTechConfig.personal_shield_capacity.get();
	
	public static long naquadah_power_cell_buffer_capacity = CommonTechConfig.naquadah_power_cell_buffer_capacity.get();
	
	// Cable
	public static long naquadah_wire_max_transfer = CommonCableConfig.naquadah_wire_max_transfer.get();
	public static long small_naquadah_cable_max_transfer = CommonCableConfig.small_naquadah_cable_max_transfer.get();
	public static long medium_naquadah_cable_max_transfer = CommonCableConfig.medium_naquadah_cable_max_transfer.get();
	public static long large_naquadah_cable_max_transfer = CommonCableConfig.large_naquadah_cable_max_transfer.get();
	
	public static boolean naquadah_wire_transfers_zero_point_energy = CommonCableConfig.naquadah_wire_transfers_zero_point_energy.get();
	public static boolean small_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.small_naquadah_cable_transfers_zero_point_energy.get();
	public static boolean medium_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.medium_naquadah_cable_transfers_zero_point_energy.get();
	public static boolean large_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.large_naquadah_cable_transfers_zero_point_energy.get();
	
	
	public static void syncConfig(ServerPlayer player)
	{
		PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundUpdateConfigValuesPacket());
	}
}
