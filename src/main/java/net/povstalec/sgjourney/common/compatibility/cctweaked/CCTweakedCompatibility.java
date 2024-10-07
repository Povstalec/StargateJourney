package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.PegasusStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.ShieldingMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.StargateFilterMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.StargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;

public class CCTweakedCompatibility
{
	public static void registerUniverseStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		// Iris methods
		peripheral.registerMethod(new ShieldingMethods.CloseIris());
		peripheral.registerMethod(new ShieldingMethods.OpenIris());
		peripheral.registerMethod(new ShieldingMethods.StopIris());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgress());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgressPercentage());
		peripheral.registerMethod(new ShieldingMethods.HasIris());
		// Misc Methods
		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
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
			
			peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
			peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
		}
	}
	
	public static void registerMilkyWayStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();

		// Iris methods
		peripheral.registerMethod(new ShieldingMethods.CloseIris());
		peripheral.registerMethod(new ShieldingMethods.OpenIris());
		peripheral.registerMethod(new ShieldingMethods.StopIris());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgress());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgressPercentage());
		peripheral.registerMethod(new ShieldingMethods.HasIris());
		// Misc Methods
		peripheral.registerMethod(new MilkyWayStargateMethods.GetCurrentSymbol());
		peripheral.registerMethod(new MilkyWayStargateMethods.IsCurrentSymbol());
		// Rotation methods
		peripheral.registerMethod(new MilkyWayStargateMethods.GetRotation());
		peripheral.registerMethod(new MilkyWayStargateMethods.GetRotationDegrees());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateClockwise());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateAntiClockwise());
		peripheral.registerMethod(new MilkyWayStargateMethods.EndRotation());
		// Dialing methods
		peripheral.registerMethod(new MilkyWayStargateMethods.OpenChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.EngageChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.CloseChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.IsChevronOpen());
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
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
			
			peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
			peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
		}
	}
	
	public static void registerPegasusStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		// Iris methods
		peripheral.registerMethod(new ShieldingMethods.CloseIris());
		peripheral.registerMethod(new ShieldingMethods.OpenIris());
		peripheral.registerMethod(new ShieldingMethods.StopIris());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgress());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgressPercentage());
		peripheral.registerMethod(new ShieldingMethods.HasIris());
		// Misc Methods
		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerMethod(new StargateMethods.LocalAddress());
			peripheral.registerMethod(new StargateMethods.GetNetwork());
			peripheral.registerMethod(new StargateMethods.SetNetwork());
			peripheral.registerMethod(new StargateMethods.SetRestrictNetwork());
			
			peripheral.registerMethod(new PegasusStargateMethods.DynamicSymbols());
			peripheral.registerMethod(new PegasusStargateMethods.OverrideSymbols());
			peripheral.registerMethod(new PegasusStargateMethods.OverridePointOfOrigin());
			peripheral.registerMethod(new StargateMethods.GetRestrictNetwork());

			/*peripheral.registerMethod(new StargateMethods.SetCFDStatus());
			peripheral.registerMethod(new StargateMethods.SetCFDTarget());
			peripheral.registerMethod(new StargateMethods.GetCFDTarget());
			peripheral.registerMethod(new StargateMethods.GetCFDStatus());*/
			
			peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
			peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
		}
	}
	
	public static void registerTollanStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
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
			
			peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
			peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
		}
	}
	
	public static void registerClassicStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();

		// Iris methods
		peripheral.registerMethod(new ShieldingMethods.CloseIris());
		peripheral.registerMethod(new ShieldingMethods.OpenIris());
		peripheral.registerMethod(new ShieldingMethods.StopIris());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgress());
		peripheral.registerMethod(new ShieldingMethods.GetIrisProgressPercentage());
		peripheral.registerMethod(new ShieldingMethods.HasIris());
		// Misc Methods
		peripheral.registerMethod(new StargateMethods.GetRecentFeedback());
		peripheral.registerMethod(new StargateMethods.SendStargateMessage());
		peripheral.registerMethod(new StargateMethods.GetStargateVariant());
		peripheral.registerMethod(new StargateMethods.GetPointOfOrigin());
		peripheral.registerMethod(new StargateMethods.GetSymbols());
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerMethod(new StargateMethods.SetChevronConfiguration());
			peripheral.registerMethod(new StargateMethods.EngageSymbol());
			peripheral.registerMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
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
			
			peripheral.registerMethod(new StargateFilterMethods.GetFilterType());
			peripheral.registerMethod(new StargateFilterMethods.SetFilterType());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromWhitelist());
			peripheral.registerMethod(new StargateFilterMethods.ClearWhitelist());
			
			peripheral.registerMethod(new StargateFilterMethods.AddToBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.RemoveFromBlacklist());
			peripheral.registerMethod(new StargateFilterMethods.ClearBlacklist());
		}
	}
}
