package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.SyncedConfig;
import net.povstalec.sgjourney.common.config.*;

import java.util.function.Supplier;

public class ClientboundUpdateConfigValuesPacket
{
	//============================================================================================
	//***************************************Block Entities***************************************
	//============================================================================================
	
	// DHD
	private final long universe_dhd_energy_buffer_capacity;
	private final long milky_way_dhd_energy_buffer_capacity;
	private final long pegasus_dhd_energy_buffer_capacity;
	private final long classic_dhd_energy_buffer_capacity;
	// Transporter
	private final long ancient_transport_rings_energy_capacity;
	private final long goauld_transport_rings_energy_capacity;
	// Transporter Controller
	private final long goauld_ring_panel_energy_capacity;
	// Naquadah Generators
	private final long naquadah_reactor_capacity;
	private final long naquadah_generator_mark_i_capacity;
	private final long naquadah_generator_mark_ii_capacity;
	// Tech
	private final long naquadah_liquidizer_energy_capacity;
	private final long heavyNaquadahLiquidizerEnergyCapacity;
	private final long crystallizer_energy_capacity;
	private final long advanced_crystallizer_energy_capacity;
	
	//============================================================================================
	//********************************************Items*******************************************
	//============================================================================================
	
	// Crystals
	private final long energy_crystal_capacity;
	private final long advanced_energy_crystal_capacity;
	private final long energy_crystal_max_transfer;
	private final long advanced_energy_crystal_max_transfer;
	private final long energy_crystal_energy_target_increase;
	private final long advanced_energy_crystal_energy_target_increase;
	
	private final long transfer_crystal_max_transfer;
	private final long advanced_transfer_crystal_max_transfer;
	
	private final int memory_crystal_capacity;
	private final int advanced_memory_crystal_capacity;
	
	// Tech
	private final boolean fusion_core_infinite_energy;
	private final long fusion_core_energy_from_fuel;
	private final int fusion_core_fuel_capacity;
	
	private final int vial_capacity;
	
	private final int personal_shield_capacity;
	
	private final long naquadah_power_cell_buffer_capacity;
	
	// Cable
	private final long naquadah_wire_max_transfer;
	private final long small_naquadah_cable_max_transfer;
	private final long medium_naquadah_cable_max_transfer;
	private final long large_naquadah_cable_max_transfer;
	
	private final boolean naquadah_wire_transfers_zero_point_energy;
	private final boolean small_naquadah_cable_transfers_zero_point_energy;
	private final boolean medium_naquadah_cable_transfers_zero_point_energy;
	private final boolean large_naquadah_cable_transfers_zero_point_energy;
	
	
	
	public ClientboundUpdateConfigValuesPacket()
	{
		//============================================================================================
		//***************************************Block Entities***************************************
		//============================================================================================
		
		// DHD
		universe_dhd_energy_buffer_capacity = CommonDHDConfig.universe_dhd_energy_buffer_capacity.get();
		milky_way_dhd_energy_buffer_capacity = CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
		pegasus_dhd_energy_buffer_capacity = CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
		classic_dhd_energy_buffer_capacity = CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
		// Transporter
		ancient_transport_rings_energy_capacity = CommonTransporterConfig.ancient_transport_rings_energy_capacity.get();
		goauld_transport_rings_energy_capacity = CommonTransporterConfig.goauld_transport_rings_energy_capacity.get();
		// Transporter Controller
		goauld_ring_panel_energy_capacity = CommonTransporterConfig.goauld_ring_panel_energy_capacity.get();
		// Naquadah Generators
		naquadah_reactor_capacity = CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get();
		naquadah_generator_mark_i_capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
		naquadah_generator_mark_ii_capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
		// Tech
		naquadah_liquidizer_energy_capacity = CommonTechConfig.naquadah_liquidizer_energy_capacity.get();
		heavyNaquadahLiquidizerEnergyCapacity = CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get();
		crystallizer_energy_capacity = CommonTechConfig.crystallizer_energy_capacity.get();
		advanced_crystallizer_energy_capacity = CommonTechConfig.advanced_crystallizer_energy_capacity.get();
		
		//============================================================================================
		//********************************************Items*******************************************
		//============================================================================================
		
		// Crystals
		energy_crystal_capacity = CommonCrystalConfig.energy_crystal_capacity.get();
		advanced_energy_crystal_capacity = CommonCrystalConfig.advanced_energy_crystal_capacity.get();
		energy_crystal_max_transfer = CommonCrystalConfig.energy_crystal_max_transfer.get();
		advanced_energy_crystal_max_transfer = CommonCrystalConfig.advanced_energy_crystal_max_transfer.get();
		energy_crystal_energy_target_increase = CommonCrystalConfig.energy_crystal_energy_target_increase.get();
		advanced_energy_crystal_energy_target_increase = CommonCrystalConfig.advanced_energy_crystal_energy_target_increase.get();
		
		transfer_crystal_max_transfer = CommonCrystalConfig.transfer_crystal_max_transfer.get();
		advanced_transfer_crystal_max_transfer = CommonCrystalConfig.advanced_transfer_crystal_max_transfer.get();
		
		memory_crystal_capacity = CommonCrystalConfig.memory_crystal_capacity.get();
		advanced_memory_crystal_capacity = CommonCrystalConfig.advanced_memory_crystal_capacity.get();
		
		// Tech
		fusion_core_infinite_energy = CommonTechConfig.fusion_core_infinite_energy.get();
		fusion_core_energy_from_fuel = CommonTechConfig.fusion_core_energy_from_fuel.get();
		fusion_core_fuel_capacity = CommonTechConfig.fusion_core_fuel_capacity.get();
		
		vial_capacity = CommonTechConfig.vial_capacity.get();
		
		personal_shield_capacity = CommonTechConfig.personal_shield_capacity.get();
		
		naquadah_power_cell_buffer_capacity = CommonTechConfig.naquadah_power_cell_buffer_capacity.get();
		
		// Cable
		naquadah_wire_max_transfer = CommonCableConfig.naquadah_wire_max_transfer.get();
		small_naquadah_cable_max_transfer = CommonCableConfig.small_naquadah_cable_max_transfer.get();
		medium_naquadah_cable_max_transfer = CommonCableConfig.medium_naquadah_cable_max_transfer.get();
		large_naquadah_cable_max_transfer = CommonCableConfig.large_naquadah_cable_max_transfer.get();
		
		naquadah_wire_transfers_zero_point_energy = CommonCableConfig.naquadah_wire_transfers_zero_point_energy.get();
		small_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.small_naquadah_cable_transfers_zero_point_energy.get();
		medium_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.medium_naquadah_cable_transfers_zero_point_energy.get();
		large_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.large_naquadah_cable_transfers_zero_point_energy.get();
	}
	
	public ClientboundUpdateConfigValuesPacket(FriendlyByteBuf buffer)
	{
		//============================================================================================
		//***************************************Block Entities***************************************
		//============================================================================================
		
		// DHD
		universe_dhd_energy_buffer_capacity = buffer.readLong();
		milky_way_dhd_energy_buffer_capacity = buffer.readLong();
		pegasus_dhd_energy_buffer_capacity = buffer.readLong();
		classic_dhd_energy_buffer_capacity = buffer.readLong();
		// Transporter
		ancient_transport_rings_energy_capacity = buffer.readLong();
		goauld_transport_rings_energy_capacity = buffer.readLong();
		// Transporter Controller
		goauld_ring_panel_energy_capacity = buffer.readLong();
		// Naquadah Generators
		naquadah_reactor_capacity = buffer.readLong();
		naquadah_generator_mark_i_capacity = buffer.readLong();
		naquadah_generator_mark_ii_capacity = buffer.readLong();
		// Tech
		naquadah_liquidizer_energy_capacity = buffer.readLong();
		heavyNaquadahLiquidizerEnergyCapacity = buffer.readLong();
		crystallizer_energy_capacity = buffer.readLong();
		advanced_crystallizer_energy_capacity = buffer.readLong();
		
		//============================================================================================
		//********************************************Items*******************************************
		//============================================================================================
		
		// Crystals
		energy_crystal_capacity = buffer.readLong();
		advanced_energy_crystal_capacity = buffer.readLong();
		energy_crystal_max_transfer = buffer.readLong();
		advanced_energy_crystal_max_transfer = buffer.readLong();
		energy_crystal_energy_target_increase = buffer.readLong();
		advanced_energy_crystal_energy_target_increase = buffer.readLong();
		
		transfer_crystal_max_transfer = buffer.readLong();
		advanced_transfer_crystal_max_transfer = buffer.readLong();
		
		memory_crystal_capacity = buffer.readInt();
		advanced_memory_crystal_capacity = buffer.readInt();
		
		// Tech
		fusion_core_infinite_energy = buffer.readBoolean();
		fusion_core_energy_from_fuel = buffer.readLong();
		fusion_core_fuel_capacity = buffer.readInt();
		
		vial_capacity = buffer.readInt();
		
		personal_shield_capacity = buffer.readInt();
		
		naquadah_power_cell_buffer_capacity = buffer.readLong();
		
		// Cable
		naquadah_wire_max_transfer = buffer.readLong();
		small_naquadah_cable_max_transfer = buffer.readLong();
		medium_naquadah_cable_max_transfer = buffer.readLong();
		large_naquadah_cable_max_transfer = buffer.readLong();
		
		naquadah_wire_transfers_zero_point_energy = buffer.readBoolean();
		small_naquadah_cable_transfers_zero_point_energy = buffer.readBoolean();
		medium_naquadah_cable_transfers_zero_point_energy = buffer.readBoolean();
		large_naquadah_cable_transfers_zero_point_energy = buffer.readBoolean();
	}
	
	public void encode(FriendlyByteBuf buffer)
	{
		//============================================================================================
		//***************************************Block Entities***************************************
		//============================================================================================
		
		// DHD
		buffer.writeLong(universe_dhd_energy_buffer_capacity);
		buffer.writeLong(milky_way_dhd_energy_buffer_capacity);
		buffer.writeLong(pegasus_dhd_energy_buffer_capacity);
		buffer.writeLong(classic_dhd_energy_buffer_capacity);
		// Transporter
		buffer.writeLong(ancient_transport_rings_energy_capacity);
		buffer.writeLong(goauld_transport_rings_energy_capacity);
		// Transporter Controller
		buffer.writeLong(goauld_ring_panel_energy_capacity);
		// Naquadah Generators
		buffer.writeLong(naquadah_reactor_capacity);
		buffer.writeLong(naquadah_generator_mark_i_capacity);
		buffer.writeLong(naquadah_generator_mark_ii_capacity);
		// Tech
		buffer.writeLong(naquadah_liquidizer_energy_capacity);
		buffer.writeLong(heavyNaquadahLiquidizerEnergyCapacity);
		buffer.writeLong(crystallizer_energy_capacity);
		buffer.writeLong(advanced_crystallizer_energy_capacity);
		
		//============================================================================================
		//********************************************Items*******************************************
		//============================================================================================
		
		// Crystals
		buffer.writeLong(energy_crystal_capacity);
		buffer.writeLong(advanced_energy_crystal_capacity);
		buffer.writeLong(energy_crystal_max_transfer);
		buffer.writeLong(advanced_energy_crystal_max_transfer);
		buffer.writeLong(energy_crystal_energy_target_increase);
		buffer.writeLong(advanced_energy_crystal_energy_target_increase);
		
		buffer.writeLong(transfer_crystal_max_transfer);
		buffer.writeLong(advanced_transfer_crystal_max_transfer);
		
		buffer.writeInt(memory_crystal_capacity);
		buffer.writeInt(advanced_memory_crystal_capacity);
		
		// Tech
		buffer.writeBoolean(fusion_core_infinite_energy);
		buffer.writeLong(fusion_core_energy_from_fuel);
		buffer.writeInt(fusion_core_fuel_capacity);
		
		buffer.writeInt(vial_capacity);
		
		buffer.writeInt(personal_shield_capacity);
		
		buffer.writeLong(naquadah_power_cell_buffer_capacity);
		
		// Cable
		buffer.writeLong(naquadah_wire_max_transfer);
		buffer.writeLong(small_naquadah_cable_max_transfer);
		buffer.writeLong(medium_naquadah_cable_max_transfer);
		buffer.writeLong(large_naquadah_cable_max_transfer);
		
		buffer.writeBoolean(naquadah_wire_transfers_zero_point_energy);
		buffer.writeBoolean(small_naquadah_cable_transfers_zero_point_energy);
		buffer.writeBoolean(medium_naquadah_cable_transfers_zero_point_energy);
		buffer.writeBoolean(large_naquadah_cable_transfers_zero_point_energy);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			//============================================================================================
			//***************************************Block Entities***************************************
			//============================================================================================
			
			// DHD
			SyncedConfig.universe_dhd_energy_buffer_capacity = universe_dhd_energy_buffer_capacity;
			SyncedConfig.milky_way_dhd_energy_buffer_capacity = milky_way_dhd_energy_buffer_capacity;
			SyncedConfig.pegasus_dhd_energy_buffer_capacity = pegasus_dhd_energy_buffer_capacity;
			SyncedConfig.classic_dhd_energy_buffer_capacity = classic_dhd_energy_buffer_capacity;
			// Transporter
			SyncedConfig.ancient_transport_rings_energy_capacity = ancient_transport_rings_energy_capacity;
			SyncedConfig.goauld_transport_rings_energy_capacity = goauld_transport_rings_energy_capacity;
			// Transporter Controller
			SyncedConfig.goauld_ring_panel_energy_capacity = goauld_ring_panel_energy_capacity;
			// Naquadah Generators
			SyncedConfig.naquadah_reactor_capacity = naquadah_reactor_capacity;
			SyncedConfig.naquadah_generator_mark_i_capacity = naquadah_generator_mark_i_capacity;
			SyncedConfig.naquadah_generator_mark_ii_capacity = naquadah_generator_mark_ii_capacity;
			// Tech
			SyncedConfig.naquadah_liquidizer_energy_capacity = naquadah_liquidizer_energy_capacity;
			SyncedConfig.heavy_naquadah_liquidizer_energy_capacity = heavyNaquadahLiquidizerEnergyCapacity;
			SyncedConfig.crystallizer_energy_capacity = crystallizer_energy_capacity;
			SyncedConfig.advanced_crystallizer_energy_capacity = advanced_crystallizer_energy_capacity;
			
			//============================================================================================
			//********************************************Items*******************************************
			//============================================================================================
			
			// Crystals
			SyncedConfig.energy_crystal_capacity = CommonCrystalConfig.energy_crystal_capacity.get();
			SyncedConfig.advanced_energy_crystal_capacity = CommonCrystalConfig.advanced_energy_crystal_capacity.get();
			SyncedConfig.energy_crystal_max_transfer = CommonCrystalConfig.energy_crystal_max_transfer.get();
			SyncedConfig.advanced_energy_crystal_max_transfer = CommonCrystalConfig.advanced_energy_crystal_max_transfer.get();
			SyncedConfig.energy_crystal_energy_target_increase = CommonCrystalConfig.energy_crystal_energy_target_increase.get();
			SyncedConfig.advanced_energy_crystal_energy_target_increase = CommonCrystalConfig.advanced_energy_crystal_energy_target_increase.get();
			
			SyncedConfig.transfer_crystal_max_transfer = CommonCrystalConfig.transfer_crystal_max_transfer.get();
			SyncedConfig.advanced_transfer_crystal_max_transfer = CommonCrystalConfig.advanced_transfer_crystal_max_transfer.get();
			
			SyncedConfig.memory_crystal_capacity = CommonCrystalConfig.memory_crystal_capacity.get();
			SyncedConfig.advanced_memory_crystal_capacity = CommonCrystalConfig.advanced_memory_crystal_capacity.get();
			
			// Tech
			SyncedConfig.fusion_core_infinite_energy = CommonTechConfig.fusion_core_infinite_energy.get();
			SyncedConfig.fusion_core_energy_from_fuel = CommonTechConfig.fusion_core_energy_from_fuel.get();
			SyncedConfig.fusion_core_fuel_capacity = CommonTechConfig.fusion_core_fuel_capacity.get();
			
			SyncedConfig.vial_capacity = CommonTechConfig.vial_capacity.get();
			
			SyncedConfig.personal_shield_capacity = CommonTechConfig.personal_shield_capacity.get();
			
			SyncedConfig.naquadah_power_cell_buffer_capacity = CommonTechConfig.naquadah_power_cell_buffer_capacity.get();
			
			// Cable
			SyncedConfig.naquadah_wire_max_transfer = CommonCableConfig.naquadah_wire_max_transfer.get();
			SyncedConfig.small_naquadah_cable_max_transfer = CommonCableConfig.small_naquadah_cable_max_transfer.get();
			SyncedConfig.medium_naquadah_cable_max_transfer = CommonCableConfig.medium_naquadah_cable_max_transfer.get();
			SyncedConfig.large_naquadah_cable_max_transfer = CommonCableConfig.large_naquadah_cable_max_transfer.get();
			
			SyncedConfig.naquadah_wire_transfers_zero_point_energy = CommonCableConfig.naquadah_wire_transfers_zero_point_energy.get();
			SyncedConfig.small_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.small_naquadah_cable_transfers_zero_point_energy.get();
			SyncedConfig.medium_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.medium_naquadah_cable_transfers_zero_point_energy.get();
			SyncedConfig.large_naquadah_cable_transfers_zero_point_energy = CommonCableConfig.large_naquadah_cable_transfers_zero_point_energy.get();
		});
		return true;
	}
}


