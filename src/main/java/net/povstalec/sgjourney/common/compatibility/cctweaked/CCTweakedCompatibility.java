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
			peripheral.registerStargateMethod(new StargateMethods.EngageSymbol());
			peripheral.registerStargateMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerStargateMethod(new StargateMethods.LocalAddress());
		}
	}
	
	public static void registerMilkyWayStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.GetCurrentSymbol());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.IsCurrentSymbol());
		
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.GetRotation());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RotateClockwise());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RotateAntiClockwise());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.EndRotation());

		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RaiseChevron());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.LowerChevron());
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.EngageSymbol());
			peripheral.registerStargateMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerStargateMethod(new StargateMethods.LocalAddress());
		}
	}
	
	public static void registerPegasusStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.EngageSymbol());
			peripheral.registerStargateMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerStargateMethod(new StargateMethods.LocalAddress());
			
			peripheral.registerStargateMethod(new PegasusStargateMethods.DynamicSymbols());
			peripheral.registerStargateMethod(new PegasusStargateMethods.OverrideSymbols());
			peripheral.registerStargateMethod(new PegasusStargateMethods.OverridePointOfOrigin());
		}
	}
	
	public static void registerTollanStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.EngageSymbol());
			peripheral.registerStargateMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerStargateMethod(new StargateMethods.LocalAddress());
		}
	}
	
	public static void registerClassicStargateMethods(StargatePeripheralWrapper wrapper)
	{
		StargatePeripheral peripheral = wrapper.getPeripheral();
		
		AbstractInterfaceEntity.InterfaceType type = wrapper.getType();
		
		if(type.hasCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.EngageSymbol());
			peripheral.registerStargateMethod(new StargateMethods.DialedAddress());
		}
		
		if(type.hasAdvancedCrystalMethods())
		{
			peripheral.registerStargateMethod(new StargateMethods.ConnectedAddress());
			peripheral.registerStargateMethod(new StargateMethods.LocalAddress());
		}
	}
}
