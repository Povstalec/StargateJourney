package net.povstalec.sgjourney.common.config;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundUpdateConfigValuesPacket;

/**
 * Class containing values to be synced with the Client whenever it joins a Server
 */
public class SyncedConfig
{
	public static final SyncedValues SYNCED_VALUES = new SyncedValues();
	
	//============================================================================================
	//***************************************Block Entities***************************************
	//============================================================================================
	
	// DHD
	public static SyncedValue<Long> universe_dhd_energy_buffer_capacity = SYNCED_VALUES.create(CommonDHDConfig.universe_dhd_energy_buffer_capacity.get());
	public static SyncedValue<Long> milky_way_dhd_energy_buffer_capacity = SYNCED_VALUES.create(CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get());
	public static SyncedValue<Long> pegasus_dhd_energy_buffer_capacity = SYNCED_VALUES.create(CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get());
	public static SyncedValue<Long> classic_dhd_energy_buffer_capacity = SYNCED_VALUES.create(CommonDHDConfig.classic_dhd_energy_buffer_capacity.get());
	
	// Transporter
	public static SyncedValue<Long> ancient_transport_rings_energy_capacity = SYNCED_VALUES.create(CommonTransporterConfig.ancient_transport_rings_energy_capacity.get());
	public static SyncedValue<Long> goauld_transport_rings_energy_capacity = SYNCED_VALUES.create(CommonTransporterConfig.goauld_transport_rings_energy_capacity.get());
	
	// Transporter Controller
	public static SyncedValue<Long> goauld_ring_panel_energy_capacity = SYNCED_VALUES.create(CommonTransporterConfig.goauld_ring_panel_energy_capacity.get());
	
	// Naquadah Generators
	public static SyncedValue<Long> naquadah_reactor_capacity = SYNCED_VALUES.create(CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get());
	public static SyncedValue<Long> naquadah_generator_mark_i_capacity = SYNCED_VALUES.create(CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get());
	public static SyncedValue<Long> naquadah_generator_mark_ii_capacity = SYNCED_VALUES.create(CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get());
	
	// Tech
	public static SyncedValue<Long> naquadah_liquidizer_energy_capacity = SYNCED_VALUES.create(CommonTechConfig.naquadah_liquidizer_energy_capacity.get());
	public static SyncedValue<Long> heavy_naquadah_liquidizer_energy_capacity = SYNCED_VALUES.create(CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get());
	public static SyncedValue<Long> crystallizer_energy_capacity = SYNCED_VALUES.create(CommonTechConfig.crystallizer_energy_capacity.get());
	public static SyncedValue<Long> advanced_crystallizer_energy_capacity = SYNCED_VALUES.create(CommonTechConfig.advanced_crystallizer_energy_capacity.get());
	
	// ZPM
	public static SyncedValue<Long> zpm_plug_max_transfer = SYNCED_VALUES.create(CommonZPMConfig.zpm_plug_max_transfer.get());
	public static SyncedValue<Long> zpm_port_max_transfer = SYNCED_VALUES.create(CommonZPMConfig.zpm_port_max_transfer.get());
	public static SyncedValue<Long> zpm_hub_max_transfer = SYNCED_VALUES.create(CommonZPMConfig.zpm_hub_max_transfer.get());
	
	//============================================================================================
	//********************************************Items*******************************************
	//============================================================================================
	
	// Crystals
	public static SyncedValue<Long> energy_crystal_capacity = SYNCED_VALUES.create(CommonCrystalConfig.energy_crystal_capacity.get());
	public static SyncedValue<Long> advanced_energy_crystal_capacity = SYNCED_VALUES.create(CommonCrystalConfig.advanced_energy_crystal_capacity.get());
	public static SyncedValue<Long> energy_crystal_max_transfer = SYNCED_VALUES.create(CommonCrystalConfig.energy_crystal_max_transfer.get());
	public static SyncedValue<Long> advanced_energy_crystal_max_transfer = SYNCED_VALUES.create(CommonCrystalConfig.advanced_energy_crystal_max_transfer.get());
	public static SyncedValue<Long> energy_crystal_energy_target_increase = SYNCED_VALUES.create(CommonCrystalConfig.energy_crystal_energy_target_increase.get());
	public static SyncedValue<Long> advanced_energy_crystal_energy_target_increase = SYNCED_VALUES.create(CommonCrystalConfig.advanced_energy_crystal_energy_target_increase.get());
	
	public static SyncedValue<Long> transfer_crystal_max_transfer = SYNCED_VALUES.create(CommonCrystalConfig.transfer_crystal_max_transfer.get());
	public static SyncedValue<Long> advanced_transfer_crystal_max_transfer = SYNCED_VALUES.create(CommonCrystalConfig.advanced_transfer_crystal_max_transfer.get());
	
	public static SyncedValue<Integer> memory_crystal_capacity = SYNCED_VALUES.create(CommonCrystalConfig.memory_crystal_capacity.get());
	public static SyncedValue<Integer> advanced_memory_crystal_capacity = SYNCED_VALUES.create(CommonCrystalConfig.advanced_memory_crystal_capacity.get());
	
	// Tech
	public static SyncedValue<Boolean> fusion_core_infinite_energy = SYNCED_VALUES.create(CommonTechConfig.fusion_core_infinite_energy.get());
	public static SyncedValue<Long> fusion_core_energy_from_fuel = SYNCED_VALUES.create(CommonTechConfig.fusion_core_energy_from_fuel.get());
	public static SyncedValue<Integer> fusion_core_fuel_capacity = SYNCED_VALUES.create(CommonTechConfig.fusion_core_fuel_capacity.get());
	
	public static SyncedValue<Integer> vial_capacity = SYNCED_VALUES.create(CommonTechConfig.vial_capacity.get());
	
	public static SyncedValue<Integer> personal_shield_capacity = SYNCED_VALUES.create(CommonTechConfig.personal_shield_capacity.get());
	
	public static SyncedValue<Long> naquadah_power_cell_buffer_capacity = SYNCED_VALUES.create(CommonTechConfig.naquadah_power_cell_buffer_capacity.get());
	
	// ZPM
	public static SyncedValue<Integer> zpm_max_entropy = SYNCED_VALUES.create(CommonZPMConfig.zpm_max_entropy.get());
	public static SyncedValue<Long> zpm_energy_per_entropy_level = SYNCED_VALUES.create(CommonZPMConfig.zpm_energy_per_entropy_level.get());
	public static SyncedValue<Boolean> dhd_holds_zpm = SYNCED_VALUES.create(CommonZPMConfig.dhd_holds_zpm.get());
	
	// Cable
	public static SyncedValue<Long> naquadah_wire_max_transfer = SYNCED_VALUES.create(CommonCableConfig.naquadah_wire_max_transfer.get());
	public static SyncedValue<Long> small_naquadah_cable_max_transfer = SYNCED_VALUES.create(CommonCableConfig.small_naquadah_cable_max_transfer.get());
	public static SyncedValue<Long> medium_naquadah_cable_max_transfer = SYNCED_VALUES.create(CommonCableConfig.medium_naquadah_cable_max_transfer.get());
	public static SyncedValue<Long> large_naquadah_cable_max_transfer = SYNCED_VALUES.create(CommonCableConfig.large_naquadah_cable_max_transfer.get());
	
	public static SyncedValue<Boolean> naquadah_wire_transfers_zero_point_energy = SYNCED_VALUES.create(CommonCableConfig.naquadah_wire_transfers_zero_point_energy.get());
	public static SyncedValue<Boolean> small_naquadah_cable_transfers_zero_point_energy = SYNCED_VALUES.create(CommonCableConfig.small_naquadah_cable_transfers_zero_point_energy.get());
	public static SyncedValue<Boolean> medium_naquadah_cable_transfers_zero_point_energy = SYNCED_VALUES.create(CommonCableConfig.medium_naquadah_cable_transfers_zero_point_energy.get());
	public static SyncedValue<Boolean> large_naquadah_cable_transfers_zero_point_energy = SYNCED_VALUES.create(CommonCableConfig.large_naquadah_cable_transfers_zero_point_energy.get());
	
	
	public static void syncConfig(ServerPlayer player)
	{
		PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundUpdateConfigValuesPacket());
	}
}
