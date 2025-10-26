package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public class GenericStargateFunctions
{
	//============================================================================================
	//**************************************Basic Interface***************************************
	//============================================================================================
	
	public static int getStargateGeneration(AbstractStargateEntity stargate)
	{
		return stargate.getGeneration().getGen();
	}
	
	public static String getStargateType(AbstractStargateEntity stargate)
	{
		return BlockEntityType.getKey(stargate.getType()).toString();
	}
	
	public static boolean isStargateConnected(AbstractStargateEntity stargate)
	{
		return stargate.isConnected();
	}
	
	public static boolean isStargateDialingOut(AbstractStargateEntity stargate)
	{
		return stargate.isDialingOut();
	}
	
	public static boolean isWormholeOpen(AbstractStargateEntity stargate)
	{
		return stargate.isWormholeOpen();
	}
	
	public static long getStargateEnergy(AbstractStargateEntity stargate)
	{
		return stargate.getEnergyStored();
	}
	
	public static int getChevronsEngaged(AbstractStargateEntity stargate)
	{
		return stargate.getChevronsEngaged();
	}
	
	public static int getOpenTime(AbstractStargateEntity stargate)
	{
		return stargate.getOpenTime();
	}
	
	// Returns true if Stargate was connected and then got disconnected, otherwise returns false
	public static boolean disconnectStargate(AbstractStargateEntity stargate)
	{
		boolean wasConnected = stargate.isConnected();
		
		stargate.disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
		
		boolean isConnected = stargate.isConnected();
		
		return !isConnected && (wasConnected != isConnected);
	}
	
	public static StargateInfo.Feedback getRecentFeedback(AbstractStargateEntity stargate)
	{
		return stargate.getRecentFeedback();
	}
	
	public static boolean sendStargateMessage(AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, String message)
	{
		if(!interfaceEntity.getInterfaceType().hasAdvancedCrystalMethods() && !stargate.isWormholeOpen())
			return false;
		
		return stargate.sendStargateMessage(message);
	}
	
	public static String getVariant(AbstractStargateEntity stargate)
	{
		return stargate.getVariant().toString();
	}
	
	public static String getPointOfOrigin(AbstractStargateEntity stargate)
	{
		return stargate.symbolInfo().pointOfOrigin().toString();
	}
	
	public static String getSymbols(AbstractStargateEntity stargate)
	{
		return stargate.symbolInfo().symbols().toString();
	}
	
	//============================================================================================
	//*************************************Crystal Interface**************************************
	//============================================================================================
	
	public static StargateInfo.Feedback engageSymbol(AbstractInterfaceEntity interfaceEntity, AbstractStargateEntity stargate, int desiredSymbol, boolean engageDirectly)
	{
		return interfaceEntity.getInterfaceType().hasAdvancedCrystalMethods() && engageDirectly ? stargate.engageSymbol(desiredSymbol) : stargate.dhdEngageSymbol(desiredSymbol);
	}
	
	public static Address.Mutable getDialedAddress(AbstractStargateEntity stargate)
	{
		// Will only display the dialed Address
		return !stargate.isConnected() || stargate.isDialingOut() ? stargate.getAddress() : new Address.Mutable();
	}
	
	public static void setChevronConfiguration(AbstractStargateEntity stargate, int[] configurationArray)
	{
		// Should Handle the following exceptions
		/*if(configurationArray.length < 8)
			throw new LuaException("Array is too short (required length: 8)");
		else if(configurationArray.length > 8)
			throw new LuaException("Array is too long (required length: 8)");
		else if(!ArrayHelper.differentNumbers(configurationArray))
			throw new LuaException("Array contains duplicate numbers");
		else if(!ArrayHelper.isArrayInBounds(configurationArray, 1, 8))
			throw new LuaException("Array contains numbers which are out of bounds <1,8>");*/
		
		stargate.setEngagedChevrons(configurationArray);
		
		// Return message: "Chevron configuration set successfully"
	}
	
	public static boolean remapSymbol(AbstractStargateEntity stargate, int originalSymbol, int newSymbol)
	{
		return stargate.remapSymbol(originalSymbol, newSymbol);
	}
	
	public static int getMappedSymbol(AbstractStargateEntity stargate, int symbol)
	{
		return stargate.getMappedSymbol(symbol);
	}
	
	public static boolean hasDHD(AbstractStargateEntity stargate)
	{
		return stargate.dhdInfo().hasDHD();
	}
	
	//============================================================================================
	//*********************************Advanced Crystal Interface*********************************
	//============================================================================================
	
	public static Address getConnectedAddress(AbstractStargateEntity stargate)
	{
		return stargate.getAddress();
	}
	
	public static Address.Immutable getLocalAddress(AbstractStargateEntity stargate)
	{
		return stargate.get9ChevronAddress();
	}
	
	public static int getNetwork(AbstractStargateEntity stargate)
	{
		return stargate.getNetwork();
	}
	
	public static void setNetwork(AbstractStargateEntity stargate, int network)
	{
		stargate.setNetwork(network);
	}
	
	public static void setRestrictNetwork(AbstractStargateEntity stargate, boolean restrictNetwork)
	{
		stargate.setRestrictNetwork(restrictNetwork);
	}
	
	public static boolean isNetworkRestricted(AbstractStargateEntity stargate)
	{
		return stargate.getRestrictNetwork();
	}
}
