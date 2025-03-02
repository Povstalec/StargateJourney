package net.povstalec.sgjourney.common.compatibility.cctweaked;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.*;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class CCTweakedCompatibility
{
	public static void registerPeripherals(RegisterCapabilitiesEvent event)
	{
		event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.BASIC_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheralWrapper() != null ? blockEntity.getPeripheralWrapper().getPeripheral() : null);
		event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheralWrapper() != null ? blockEntity.getPeripheralWrapper().getPeripheral() : null);
		event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), (blockEntity, direction) -> blockEntity.getPeripheralWrapper() != null ? blockEntity.getPeripheralWrapper().getPeripheral() : null);
		
		event.registerBlockEntity(PeripheralCapability.get(), BlockEntityInit.TRANSCEIVER.get(), (blockEntity, direction) -> blockEntity.getPeripheralWrapper() != null ? blockEntity.getPeripheralWrapper().getPeripheral() : null);
	}
	
	private static void irisMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new IrisMethods.GetIris());
		peripheral.registerMethod(new IrisMethods.CloseIris());
		peripheral.registerMethod(new IrisMethods.OpenIris());
		peripheral.registerMethod(new IrisMethods.StopIris());
		peripheral.registerMethod(new IrisMethods.GetIrisProgress());
		peripheral.registerMethod(new IrisMethods.GetIrisProgressPercentage());
		peripheral.registerMethod(new IrisMethods.GetIrisDurability());
		peripheral.registerMethod(new IrisMethods.GetIrisMaxDurability());
	}
	
	private static void genericRotationMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new RotationMethods.GetCurrentSymbol());
		peripheral.registerMethod(new RotationMethods.IsCurrentSymbol());
		peripheral.registerMethod(new RotationMethods.EncodeChevron());
		// Rotation methods
		peripheral.registerMethod(new RotationMethods.GetRotation());
		peripheral.registerMethod(new RotationMethods.GetRotationDegrees());
		peripheral.registerMethod(new RotationMethods.RotateClockwise());
		peripheral.registerMethod(new RotationMethods.RotateAntiClockwise());
		peripheral.registerMethod(new RotationMethods.EndRotation());
	}
	
	private static void genericBasicMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		peripheral.registerMethod(new StargateMethods.HasDHD());
	}
	
	private static void genericCrystalMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new StargateMethods.EngageSymbol());
		peripheral.registerMethod(new StargateMethods.DialedAddress());
	}
	
	private static void genericAdvancedCrystalMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new StargateMethods.ConnectedAddress());
		peripheral.registerMethod(new StargateMethods.LocalAddress());
		peripheral.registerMethod(new StargateMethods.GetNetwork());
		peripheral.registerMethod(new StargateMethods.SetNetwork());
		peripheral.registerMethod(new StargateMethods.SetRestrictNetwork());
		peripheral.registerMethod(new StargateMethods.GetRestrictNetwork());
		
		/*peripheral.registerMethod(new StargateMethods.SetCFDStatus());
		peripheral.registerMethod(new StargateMethods.SetCFDTarget());
		peripheral.registerMethod(new StargateMethods.GetCFDTarget());
		peripheral.registerMethod(new StargateMethods.GetCFDStatus());*/
	}
	
	private static void filterMethods(StargatePeripheral peripheral)
	{
		peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
		peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
		
		peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
		peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
		peripheral.registerMethod(new StargateFilterMethods.GetWhitelist());
		peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
		
		peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
		peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
		peripheral.registerMethod(new StargateFilterMethods.GetBlacklist());
		peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
	}
	
	
	
	public static void registerUniverseStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		// Iris methods
		irisMethods(peripheral);
		// Rotation methods
		genericRotationMethods(peripheral);
		// Misc Methods
		genericBasicMethods(peripheral);
		
		if(type.hasCrystalMethods())
		{
			genericCrystalMethods(peripheral);
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			genericAdvancedCrystalMethods(peripheral);
			
			filterMethods(peripheral);
		}
	}
	
	public static void registerMilkyWayStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();

		// Iris methods
		irisMethods(peripheral);
		// Misc Methods
		peripheral.registerMethod(new RotationMethods.GetCurrentSymbol());
		peripheral.registerMethod(new RotationMethods.IsCurrentSymbol());
		peripheral.registerMethod(new RotationMethods.EncodeChevron());
		// Rotation methods
		peripheral.registerMethod(new RotationMethods.GetRotation());
		peripheral.registerMethod(new RotationMethods.GetRotationDegrees());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateClockwise());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateAntiClockwise());
		peripheral.registerMethod(new RotationMethods.EndRotation());
		// Dialing methods
		peripheral.registerMethod(new MilkyWayStargateMethods.OpenChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.CloseChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.IsChevronOpen());
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		genericBasicMethods(peripheral);
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			genericCrystalMethods(peripheral);
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			genericAdvancedCrystalMethods(peripheral);
			
			filterMethods(peripheral);
		}
	}
	
	public static void registerPegasusStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		// Iris methods
		irisMethods(peripheral);
		// Misc Methods
		genericBasicMethods(peripheral);
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			genericCrystalMethods(peripheral);
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			genericAdvancedCrystalMethods(peripheral);
			
			peripheral.registerMethod(new PegasusStargateMethods.DynamicSymbols());
			peripheral.registerMethod(new PegasusStargateMethods.OverrideSymbols());
			peripheral.registerMethod(new PegasusStargateMethods.OverridePointOfOrigin());
			peripheral.registerMethod(new StargateMethods.GetRestrictNetwork());
			
			filterMethods(peripheral);
		}
	}
	
	public static void registerTollanStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		genericBasicMethods(peripheral);
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			genericCrystalMethods(peripheral);
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			genericAdvancedCrystalMethods(peripheral);
			
			filterMethods(peripheral);
		}
	}
	
	public static void registerClassicStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		// Iris methods
		irisMethods(peripheral);
		// Rotation methods
		genericRotationMethods(peripheral);
		// Misc Methods
		genericBasicMethods(peripheral);
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			genericCrystalMethods(peripheral);
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			genericAdvancedCrystalMethods(peripheral);
			
			filterMethods(peripheral);
		}
	}
}
