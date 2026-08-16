package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.*;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransporterPeripheral;

public class CCTweakedCompatibility
{
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	public static class Stargate
	{
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
			peripheral.registerMethod(new StargateMethods.EngageStargate());
		}
		
		private static void genericCrystalMethods(StargatePeripheral peripheral)
		{
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
			peripheral.registerMethod(new StargateMethods.RemapSymbol());
			peripheral.registerMethod(new StargateMethods.GetMappedSymbol());
			peripheral.registerMethod(new StargateMethods.HasDHD());
		}
		
		private static void networkRestrictionMethods(StargatePeripheral peripheral)
		{
			peripheral.registerMethod(new StargateMethods.GetNetworks());
			peripheral.registerMethod(new StargateMethods.AddNetwork());
			peripheral.registerMethod(new StargateMethods.RemoveNetwork());
			peripheral.registerMethod(new StargateMethods.SetRestrictNetwork());
			peripheral.registerMethod(new StargateMethods.GetRestrictNetwork());
		}
		
		private static void genericAdvancedCrystalMethods(StargatePeripheral peripheral)
		{
			peripheral.registerMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerMethod(new StargateMethods.LocalAddress());
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
		
		
		
		public static void registerUniverseStargateMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
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
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
		
		public static void registerMilkyWayStargateMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
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
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
		
		public static void registerPegasusStargateMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
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
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				peripheral.registerMethod(new PegasusStargateMethods.DynamicSymbols());
				peripheral.registerMethod(new PegasusStargateMethods.OverrideSymbols());
				peripheral.registerMethod(new PegasusStargateMethods.OverridePointOfOrigin());
				
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
		
		public static void registerTollanStargateMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
		{
			StargatePeripheral peripheral = wrapper.getPeripheral();
			
			AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
			
			genericBasicMethods(peripheral);
			
			if(type.hasCrystalMethods())
			{
				peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
				genericCrystalMethods(peripheral);
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
		
		public static void registerClassicStargateMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
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
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	public static class Transporter
	{
		private static void genericBasicMethods(TransporterPeripheral peripheral)
		{
			peripheral.registerMethod(new TransporterMethods.GetRecentFeedback());
			peripheral.registerMethod(new TransporterMethods.LocalTransporterID());
			peripheral.registerMethod(new TransporterMethods.DialCoords());
		}
		
		private static void genericCrystalMethods(TransporterPeripheral peripheral)
		{
			peripheral.registerMethod(new TransporterMethods.DialTransporterID());
		}
		
		private static void networkRestrictionMethods(TransporterPeripheral peripheral)
		{
			peripheral.registerMethod(new TransporterMethods.GetNetworks());
			peripheral.registerMethod(new TransporterMethods.AddNetwork());
			peripheral.registerMethod(new TransporterMethods.RemoveNetwork());
			peripheral.registerMethod(new TransporterMethods.SetRestrictNetwork());
			peripheral.registerMethod(new TransporterMethods.GetRestrictNetwork());
		}
		
		private static void genericAdvancedCrystalMethods(TransporterPeripheral peripheral)
		{
			//TODO
			//peripheral.registerMethod(new StargateMethods.ConnectedAddress());
		}
		
		private static void filterMethods(TransporterPeripheral peripheral)
		{
			peripheral.registerMethod(new TransporterFilterMethods.GetFilterType());
			peripheral.registerMethod(new TransporterFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new TransporterFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new TransporterFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new TransporterFilterMethods.GetWhitelist());
			peripheral.registerMethod(new TransporterFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new TransporterFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new TransporterFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new TransporterFilterMethods.GetBlacklist());
			peripheral.registerMethod(new TransporterFilterMethods.ClearBlacklist());
		}
		
		public static void registerTransportRingsMethods(SGJourneyPeripheralWrapper<TransporterPeripheral> wrapper)
		{
			TransporterPeripheral peripheral = wrapper.getPeripheral();
			
			AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
			
			// Misc Methods
			genericBasicMethods(peripheral);
			
			if(type.hasCrystalMethods())
			{
				genericCrystalMethods(peripheral);
				networkRestrictionMethods(peripheral);
			}
			
			if(type.hasAdvancedCrystalMethods())
			{
				genericAdvancedCrystalMethods(peripheral);
				filterMethods(peripheral);
			}
		}
	}
}
