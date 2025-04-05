package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;

import java.util.ArrayList;

public class StargateFilterFunctions
{
	//============================================================================================
	//*********************************Advanced Crystal Interface*********************************
	//============================================================================================
	
	public static int getFilterType(AbstractStargateEntity stargate)
	{
		return stargate.addressFilterInfo().getFilterType().getIntegerValue();
	}
	
	public static int setFilterType(AbstractStargateEntity stargate, int filterType)
	{
		return stargate.addressFilterInfo().setFilterType(filterType).getIntegerValue();
	}
	
	public static String addToWhitelist(AbstractStargateEntity stargate, int[] addressArray, boolean isVisible)
	{
		// Should Handle the following exceptions
		/*if(addressArray.length < 6)
			throw new LuaException("Array is too short (minimum length: 6)");
		
		else if(addressArray.length > 8)
			throw new LuaException("Array is too long (maximum length: 8)");
		
		else if(!ArrayHelper.differentNumbers(addressArray))
			throw new LuaException("Array contains duplicate numbers");
		
		else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
			throw new LuaException("Array contains numbers which are out of bounds <1,47>");*/
		
		if(stargate.addressFilterInfo().addToWhitelist(new Address(addressArray).immutable(), isVisible))
			return "Address whitelisted successfully";
		else
			return "Address visibility changed successfully";
	}
	
	public static String addToBlacklist(AbstractStargateEntity stargate, int[] addressArray, boolean isVisible)
	{
		// Should Handle the following exceptions
		/*if(addressArray.length < 6)
			throw new LuaException("Array is too short (minimum length: 6)");
		
		else if(addressArray.length > 8)
			throw new LuaException("Array is too long (maximum length: 8)");
		
		else if(!ArrayHelper.differentNumbers(addressArray))
			throw new LuaException("Array contains duplicate numbers");
		
		else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
			throw new LuaException("Array contains numbers which are out of bounds <1,47>");*/
		
		if(stargate.addressFilterInfo().addToBlacklist(new Address(addressArray).immutable(), isVisible))
			return "Address blacklisted successfully";
		else
			return "Address visibility changed successfully";
	}
	
	public static String removeFromWhitelist(AbstractStargateEntity stargate, int[] addressArray)
	{
		// Should Handle the following exceptions
		/*if(addressArray.length < 6)
			throw new LuaException("Array is too short (minimum length: 6)");
		
		else if(addressArray.length > 8)
			throw new LuaException("Array is too long (maximum length: 8)");
		
		else if(!ArrayHelper.differentNumbers(addressArray))
			throw new LuaException("Array contains duplicate numbers");
		
		else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
			throw new LuaException("Array contains numbers which are out of bounds <1,47>");*/
		
		if(stargate.addressFilterInfo().removeFromWhitelist(new Address(addressArray).immutable()))
			return "Address removed from whitelist successfully";
		else
			return "Address is not whitelisted";
	}
	
	public static String removeFromBlacklist(AbstractStargateEntity stargate, int[] addressArray)
	{
		// Should Handle the following exceptions
		/*if(addressArray.length < 6)
			throw new LuaException("Array is too short (minimum length: 6)");
		
		else if(addressArray.length > 8)
			throw new LuaException("Array is too long (maximum length: 8)");
		
		else if(!ArrayHelper.differentNumbers(addressArray))
			throw new LuaException("Array contains duplicate numbers");
		
		else if(!ArrayHelper.isArrayInBounds(addressArray, 1, 47))
			throw new LuaException("Array contains numbers which are out of bounds <1,47>");*/
		
		if(stargate.addressFilterInfo().removeFromBlacklist(new Address(addressArray).immutable()))
			return "Address removed from blacklist successfully";
		else
			return "Address is not blacklisted";
	}
	
	public static ArrayList<Address.Immutable> getPublicWhitelist(AbstractStargateEntity stargate)
	{
		ArrayList<Address.Immutable> addresses = new ArrayList<Address.Immutable>();
		for(AddressFilterInfo.HiddenAddress address : stargate.addressFilterInfo().getWhitelist())
		{
			if(address.isVisible())
				addresses.add(address.address());
		}
		
		return addresses;
	}
	
	public static ArrayList<Address.Immutable> getPublicBlacklist(AbstractStargateEntity stargate)
	{
		ArrayList<Address.Immutable> addresses = new ArrayList<Address.Immutable>();
		for(AddressFilterInfo.HiddenAddress address : stargate.addressFilterInfo().getBlacklist())
		{
			if(address.isVisible())
				addresses.add(address.address());
		}
		
		return addresses;
	}
	
	public static String clearWhitelist(AbstractStargateEntity stargate)
	{
		stargate.addressFilterInfo().clearWhitelist();
		
		return "Whitelist cleared";
	}
	
	public static String clearBlacklist(AbstractStargateEntity stargate)
	{
		stargate.addressFilterInfo().clearBlacklist();
		
		return "Blacklist cleared";
	}
}
