package net.povstalec.sgjourney.common.compatibility.cctweaked;

import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.MilkyWayStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.PegasusStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.TollanStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.UniverseStargateMethods;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.BasicStargatePeripheral;

public class CCTweakedCompatibility
{
	public static void registerUniverseStargateMethods(StargatePeripheralWrapper wrapper)
	{
		BasicStargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerStargateMethod(new UniverseStargateMethods.EngageSymbol());
	}
	
	public static void registerMilkyWayStargateMethods(StargatePeripheralWrapper wrapper)
	{
		BasicStargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.GetCurrentSymbol());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.IsCurrentSymbol());
		
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.GetRotation());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RotateClockwise());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RotateAntiClockwise());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.EndRotation());

		peripheral.registerStargateMethod(new MilkyWayStargateMethods.RaiseChevron());
		peripheral.registerStargateMethod(new MilkyWayStargateMethods.LowerChevron());
	}
	
	public static void registerPegasusStargateMethods(StargatePeripheralWrapper wrapper)
	{
		BasicStargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerStargateMethod(new PegasusStargateMethods.EngageSymbol());
	}
	
	public static void registerTollanStargateMethods(StargatePeripheralWrapper wrapper)
	{
		BasicStargatePeripheral peripheral = wrapper.getPeripheral();
		
		peripheral.registerStargateMethod(new TollanStargateMethods.EngageSymbol());
	}
	
	public static void registerClassicStargateMethods(StargatePeripheralWrapper wrapper)
	{
		//BasicStargatePeripheral peripheral = wrapper.getPeripheral();
	}
}
