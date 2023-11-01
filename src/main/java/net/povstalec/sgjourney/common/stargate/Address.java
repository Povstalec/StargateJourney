package net.povstalec.sgjourney.common.stargate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class Address
{
	public static final String ADDRESS_DIVIDER = "-";
	public static final int MIN_ADDRESS_LENGTH = 6;
	public static final int MAX_ADDRESS_LENGTH = 9;
	
	protected int[] addressArray = new int[0];
	
	public Address() {}
	
	public Address(int[] addressArray)
	{
		if(addressArray.length < MAX_ADDRESS_LENGTH && differentNumbers(addressArray))
			this.addressArray = addressArray;
	}
	
	public Address(String addressString)
	{
		int[] addressArray = addressStringToIntArray(addressString);
		
		if(addressArray.length < MAX_ADDRESS_LENGTH && differentNumbers(addressArray))
			this.addressArray = addressArray;
	}
	
	public Address addSymbol(int symbol)
	{
		if(symbol <= 0)
			return this;
		
		if(!canGrow())
			return this;
		
		this.addressArray = ArrayHelper.growIntArray(this.addressArray, symbol);
		
		return this;
	}
	
	public int[] getArray()
	{
		return this.addressArray;
	}
	
	public int getAddressLength()
	{
		return addressArray.length;
	}
	
	public boolean isComplete()
	{
		return getAddressLength() >= MIN_ADDRESS_LENGTH;
	}
	
	public boolean canGrow()
	{
		return getAddressLength() < MAX_ADDRESS_LENGTH;
	}
	
	@Override
	public String toString()
	{
		return addressIntArrayToString(getArray());
	}
	
	public Address reset()
	{
		addressArray = new int[0];
		
		return this;
	}
	
	//============================================================================================
	//*******************************************Static*******************************************
	//============================================================================================
	
	public static int[] randomAddress(int size, int limit, long seed)
	{
		return randomAddress(0, size, limit, seed);
	}
	
	public static int[] randomAddress(int prefix, int size, int limit, long seed)
	{
		size = size > MAX_ADDRESS_LENGTH ? MAX_ADDRESS_LENGTH : size;
		
		Random random = new Random(seed);
		int[] address = new int[size];
		boolean isValid = false;
		
		while(!isValid)
		{
			for(int i = 0; i < size; i++)
			{
				if(i == 0 && prefix > 0 && prefix < limit)
					address[i] = prefix;
				else
					address[i] = random.nextInt(1, limit);
			}
			if(differentNumbers(address))
				isValid = true;
		}
		
		return address;
	}
	
	//TODO use this somewhere
	public static int[] randomAddress(int size)
	{
		Random random = new Random();
		int[] address = new int[size];
		boolean isValid = false;
		
		while(!isValid)
		{
			for(int i = 0; i < size; i++)
			{
				address[i] = random.nextInt(1, 39);
			}
			if(differentNumbers(address))
				isValid = true;
		}
		
		return address;
	}
	
	public static int[] addressStringToIntArray(String addressString)
	{
		if(addressString == null)
			return new int[0];
		
		String[] stringArray = addressString.split(ADDRESS_DIVIDER);
		int[] intArray = new int[0];
		
		for(int i = 1; i < stringArray.length; i++)
		{

			int number = Character.getNumericValue(stringArray[i].charAt(0));
			int length = stringArray[i].length();
			if(length > 1)
				number = number * 10 + Character.getNumericValue(stringArray[i].charAt(1));
			
			intArray = ArrayHelper.growIntArray(intArray, number);
		}
		
		return intArray;
	}
	
	public static String addressIntArrayToString(int[] array)
	{
		String address = ADDRESS_DIVIDER;
		
		for(int i = 0; i < array.length; i++)
		{
			address = address + array[i] + ADDRESS_DIVIDER;
		}
		return address;
	}
	
	private static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
	
	public static boolean addressContainsSymbol(int[] address, int symbol)
	{
		for(int i = 0; i < address.length; i++)
		{
			if(address[i] == symbol)
				return true;
		}
		
		return false;
	}
}
