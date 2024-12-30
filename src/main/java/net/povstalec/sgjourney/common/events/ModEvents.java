package net.povstalec.sgjourney.common.events;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadah;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.RingRemoteItem;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;
import net.povstalec.sgjourney.common.items.VialItem;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;

public class ModEvents
{
	@EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class Server
	{
		@SubscribeEvent
		public static void entityAttributeEvent(EntityAttributeCreationEvent event)
		{
			//event.put(EntityInit.GOAULD.get(), Goauld.createAttributes().build());
		}
		
		@SubscribeEvent
		public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
		{
			// Item Capabilities
			
			// Energy
			event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new EnergyCrystalItem.Energy(stack), ItemInit.ENERGY_CRYSTAL, ItemInit.ADVANCED_ENERGY_CRYSTAL);
			event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> new ZeroPointModule.Energy(stack), ItemInit.ENERGY_CRYSTAL, ItemInit.ZPM);
			
			// Items
			event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new RingRemoteItem.ItemHandler(stack, DataComponents.CONTAINER), ItemInit.RING_REMOTE);
			event.registerItem(Capabilities.ItemHandler.ITEM, (stack, context) -> new StaffWeaponItem.ItemHandler(stack, DataComponents.CONTAINER), ItemInit.MATOK);
			
			// Fluids
			event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new VialItem.FluidHandler(() -> DataComponentInit.FLUID.get(), stack), ItemInit.VIAL);
			event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new PersonalShieldItem.FluidHandler(() -> DataComponentInit.FLUID.get(), stack), ItemInit.PERSONAL_SHIELD_EMITTER);
			
			
			
			
			// Block Entity Capabilities
			
			// Energy
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.UNIVERSE_STARGATE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.MILKY_WAY_STARGATE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.PEGASUS_STARGATE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.TOLLAN_STARGATE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.CLASSIC_STARGATE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.MILKY_WAY_DHD.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.PEGASUS_DHD.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.CLASSIC_DHD.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.TRANSPORT_RINGS.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.BASIC_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BlockEntityInit.ZPM_HUB.get(), (blockEntity, direction) -> blockEntity.getEnergyHandler(direction));
			
			// Items
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.MILKY_WAY_DHD.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.PEGASUS_DHD.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.CRYSTALLIZER.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.ADVANCED_CRYSTALLIZER.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.NAQUADAH_LIQUIDIZER.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.RING_PANEL.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.ZPM_HUB.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), (blockEntity, direction) -> blockEntity.getItemHandler(direction));
			
			// Fluids
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityInit.CRYSTALLIZER.get(), (blockEntity, direction) -> blockEntity.getFluidHandler(direction));
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityInit.ADVANCED_CRYSTALLIZER.get(), (blockEntity, direction) -> blockEntity.getFluidHandler(direction));
			
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityInit.NAQUADAH_LIQUIDIZER.get(), (blockEntity, direction) -> blockEntity.getFluidHandler(direction));
			event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), (blockEntity, direction) -> blockEntity.getFluidHandler(direction));
			
			
			
			// ComputerCraft
			event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.BASIC_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheral(direction));
			event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheral(direction));
			event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheral(direction));
			
			event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.TRANSCEIVER.get(), (blockEntity, direction) -> blockEntity.getPeripheral(direction));
			
			
			
			// Entity Capabilities
			event.registerEntity(BloodstreamNaquadah.BLOODSTREAM_NAQUADAH_CAPABILITY, EntityType.VILLAGER, (entity, context) -> new BloodstreamNaquadah());
			event.registerEntity(BloodstreamNaquadah.BLOODSTREAM_NAQUADAH_CAPABILITY, EntityType.PLAYER, (entity, context) -> new BloodstreamNaquadah());
			
			event.registerEntity(AncientGene.ANCIENT_GENE_CAPABILITY, EntityType.VILLAGER, (entity, context) -> new AncientGene());
			event.registerEntity(AncientGene.ANCIENT_GENE_CAPABILITY, EntityType.PLAYER, (entity, context) -> new AncientGene());
		}
	}
	
	@EventBusSubscriber(value = Dist.CLIENT, modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class Client
	{
		@SubscribeEvent
		public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
		{
			event.register((stack, layer) -> 
			{
				if(layer == 0)
					return -1;
				
				FluidStack fluidStack = VialItem.getFluidStack(stack);
				IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
		        return renderProperties.getTintColor(fluidStack);
			}, ItemInit.VIAL.get());
		}
	}
}
