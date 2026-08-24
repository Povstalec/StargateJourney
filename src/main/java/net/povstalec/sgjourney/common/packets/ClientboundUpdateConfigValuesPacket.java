package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.SyncedConfig;
import net.povstalec.sgjourney.common.config.*;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateConfigValuesPacket implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundUpdateConfigValuesPacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_update_config_values"));
	
	public static final StreamCodec<ByteBuf, ClientboundUpdateConfigValuesPacket> STREAM_CODEC = createStreamCodec();
	
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
	
	public ClientboundUpdateConfigValuesPacket(ByteBuf buffer)
	{
		//============================================================================================
		//***************************************Block Entities***************************************
		//============================================================================================
		
		// DHD
		universe_dhd_energy_buffer_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		milky_way_dhd_energy_buffer_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		pegasus_dhd_energy_buffer_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		classic_dhd_energy_buffer_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Transporter
		ancient_transport_rings_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		goauld_transport_rings_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Transporter Controller
		goauld_ring_panel_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Naquadah Generators
		naquadah_reactor_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		naquadah_generator_mark_i_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		naquadah_generator_mark_ii_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		// Tech
		naquadah_liquidizer_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		heavyNaquadahLiquidizerEnergyCapacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		crystallizer_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		advanced_crystallizer_energy_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		
		//============================================================================================
		//********************************************Items*******************************************
		//============================================================================================
		
		// Crystals
		energy_crystal_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		advanced_energy_crystal_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		energy_crystal_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		advanced_energy_crystal_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		energy_crystal_energy_target_increase = ByteBufCodecs.VAR_LONG.decode(buffer);
		advanced_energy_crystal_energy_target_increase = ByteBufCodecs.VAR_LONG.decode(buffer);
		
		transfer_crystal_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		advanced_transfer_crystal_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		
		memory_crystal_capacity = ByteBufCodecs.VAR_INT.decode(buffer);
		advanced_memory_crystal_capacity = ByteBufCodecs.VAR_INT.decode(buffer);
		
		// Tech
		fusion_core_infinite_energy = ByteBufCodecs.BOOL.decode(buffer);
		fusion_core_energy_from_fuel = ByteBufCodecs.VAR_LONG.decode(buffer);
		fusion_core_fuel_capacity = ByteBufCodecs.VAR_INT.decode(buffer);
		
		vial_capacity = ByteBufCodecs.VAR_INT.decode(buffer);
		
		personal_shield_capacity = ByteBufCodecs.VAR_INT.decode(buffer);
		
		naquadah_power_cell_buffer_capacity = ByteBufCodecs.VAR_LONG.decode(buffer);
		
		// Cable
		naquadah_wire_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		small_naquadah_cable_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		medium_naquadah_cable_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		large_naquadah_cable_max_transfer = ByteBufCodecs.VAR_LONG.decode(buffer);
		
		naquadah_wire_transfers_zero_point_energy = ByteBufCodecs.BOOL.decode(buffer);
		small_naquadah_cable_transfers_zero_point_energy = ByteBufCodecs.BOOL.decode(buffer);
		medium_naquadah_cable_transfers_zero_point_energy = ByteBufCodecs.BOOL.decode(buffer);
		large_naquadah_cable_transfers_zero_point_energy = ByteBufCodecs.BOOL.decode(buffer);
	}
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public void encode(ByteBuf buffer)
	{
		//============================================================================================
		//***************************************Block Entities***************************************
		//============================================================================================
		
		// DHD
		ByteBufCodecs.VAR_LONG.encode(buffer, universe_dhd_energy_buffer_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, milky_way_dhd_energy_buffer_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, pegasus_dhd_energy_buffer_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, classic_dhd_energy_buffer_capacity);
		// Transporter
		ByteBufCodecs.VAR_LONG.encode(buffer, ancient_transport_rings_energy_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, goauld_transport_rings_energy_capacity);
		// Transporter Controller
		ByteBufCodecs.VAR_LONG.encode(buffer, goauld_ring_panel_energy_capacity);
		// Naquadah Generators
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_reactor_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_generator_mark_i_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_generator_mark_ii_capacity);
		// Tech
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_liquidizer_energy_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, heavyNaquadahLiquidizerEnergyCapacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, crystallizer_energy_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, advanced_crystallizer_energy_capacity);
		
		//============================================================================================
		//********************************************Items*******************************************
		//============================================================================================
		
		// Crystals
		ByteBufCodecs.VAR_LONG.encode(buffer, energy_crystal_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, advanced_energy_crystal_capacity);
		ByteBufCodecs.VAR_LONG.encode(buffer, energy_crystal_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, advanced_energy_crystal_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, energy_crystal_energy_target_increase);
		ByteBufCodecs.VAR_LONG.encode(buffer, advanced_energy_crystal_energy_target_increase);
		
		ByteBufCodecs.VAR_LONG.encode(buffer, transfer_crystal_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, advanced_transfer_crystal_max_transfer);
		
		ByteBufCodecs.VAR_INT.encode(buffer, memory_crystal_capacity);
		ByteBufCodecs.VAR_INT.encode(buffer, advanced_memory_crystal_capacity);
		
		// Tech
		ByteBufCodecs.BOOL.encode(buffer, fusion_core_infinite_energy);
		ByteBufCodecs.VAR_LONG.encode(buffer, fusion_core_energy_from_fuel);
		ByteBufCodecs.VAR_INT.encode(buffer, fusion_core_fuel_capacity);
		
		ByteBufCodecs.VAR_INT.encode(buffer, vial_capacity);
		
		ByteBufCodecs.VAR_INT.encode(buffer, personal_shield_capacity);
		
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_power_cell_buffer_capacity);
		
		// Cable
		ByteBufCodecs.VAR_LONG.encode(buffer, naquadah_wire_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, small_naquadah_cable_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, medium_naquadah_cable_max_transfer);
		ByteBufCodecs.VAR_LONG.encode(buffer, large_naquadah_cable_max_transfer);
		
		ByteBufCodecs.BOOL.encode(buffer, naquadah_wire_transfers_zero_point_energy);
		ByteBufCodecs.BOOL.encode(buffer, small_naquadah_cable_transfers_zero_point_energy);
		ByteBufCodecs.BOOL.encode(buffer, medium_naquadah_cable_transfers_zero_point_energy);
		ByteBufCodecs.BOOL.encode(buffer, large_naquadah_cable_transfers_zero_point_energy);
	}
	
	public static void handle(ClientboundUpdateConfigValuesPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			//============================================================================================
			//***************************************Block Entities***************************************
			//============================================================================================
			
			// DHD
			SyncedConfig.universe_dhd_energy_buffer_capacity = packet.universe_dhd_energy_buffer_capacity;
			SyncedConfig.milky_way_dhd_energy_buffer_capacity = packet.milky_way_dhd_energy_buffer_capacity;
			SyncedConfig.pegasus_dhd_energy_buffer_capacity = packet.pegasus_dhd_energy_buffer_capacity;
			SyncedConfig.classic_dhd_energy_buffer_capacity = packet.classic_dhd_energy_buffer_capacity;
			// Transporter
			SyncedConfig.ancient_transport_rings_energy_capacity = packet.ancient_transport_rings_energy_capacity;
			SyncedConfig.goauld_transport_rings_energy_capacity = packet.goauld_transport_rings_energy_capacity;
			// Transporter Controller
			SyncedConfig.goauld_ring_panel_energy_capacity = packet.goauld_ring_panel_energy_capacity;
			// Naquadah Generators
			SyncedConfig.naquadah_reactor_capacity = packet.naquadah_reactor_capacity;
			SyncedConfig.naquadah_generator_mark_i_capacity = packet.naquadah_generator_mark_i_capacity;
			SyncedConfig.naquadah_generator_mark_ii_capacity = packet.naquadah_generator_mark_ii_capacity;
			// Tech
			SyncedConfig.naquadah_liquidizer_energy_capacity = packet.naquadah_liquidizer_energy_capacity;
			SyncedConfig.heavy_naquadah_liquidizer_energy_capacity = packet.heavyNaquadahLiquidizerEnergyCapacity;
			SyncedConfig.crystallizer_energy_capacity = packet.crystallizer_energy_capacity;
			SyncedConfig.advanced_crystallizer_energy_capacity = packet.advanced_crystallizer_energy_capacity;
			
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


