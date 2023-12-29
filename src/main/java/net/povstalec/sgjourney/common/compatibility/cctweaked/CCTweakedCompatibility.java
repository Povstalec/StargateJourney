package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.PegasusStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.StargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;

public class CCTweakedCompatibility
{
	public static void registerUniverseStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
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
		}
	}
	
	public static void registerMilkyWayStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerMethod(new MilkyWayStargateMethods.GetCurrentSymbol());
		peripheral.registerMethod(new MilkyWayStargateMethods.IsCurrentSymbol());
		
		peripheral.registerMethod(new MilkyWayStargateMethods.GetRotation());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateClockwise());
		peripheral.registerMethod(new MilkyWayStargateMethods.RotateAntiClockwise());
		peripheral.registerMethod(new MilkyWayStargateMethods.EndRotation());

		peripheral.registerMethod(new MilkyWayStargateMethods.RaiseChevron());
		peripheral.registerMethod(new MilkyWayStargateMethods.LowerChevron());
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
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
		}
	}
	
	public static void registerPegasusStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
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
		}
	}
	
	public static void registerTollanStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
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
		}
	}
	
	public static void registerClassicStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
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
		}
	}
}
