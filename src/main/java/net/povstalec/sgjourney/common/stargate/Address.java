package net.povstalec.sgjourney.common.stargate;

import java.util.Map;
import java.util.Random;

import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class Address
{
	public static final String ADDRESS_DIVIDER = "-";
	public static final int MIN_ADDRESS_LENGTH = 6;
	public static final int MAX_ADDRESS_LENGTH = 9;
	
	protected int[] addressArray = new int[0];
	protected boolean isBuffer = false;
	
	public Address(boolean isBuffer)
	{
		this.isBuffer = isBuffer;
	}
	
	public Address()
	{
		this(false);
	}
	
	public Address(int[] addressArray)
	{
		fromArray(addressArray);
	}
	
	public Address(String addressString)
	{
		fromString(addressString);
	}
	
	public Address(Map<Double, Double> addressTable)
	{
		fromTable(addressTable);
	}
	
	public Address addSymbol(int symbol)
	{
		if(symbol < 0)
			return this;
		
		if(symbol == 0 && !this.isBuffer)
			return this;
		
		if(!canGrow())
			return this;
		
		this.addressArray = ArrayHelper.growIntArray(this.addressArray, symbol);
		
		return this;
	}
	
	public Address fromArray(int[] addressArray)
	{
		if(addressArray.length < MAX_ADDRESS_LENGTH &&
				ArrayHelper.differentNumbers(addressArray) &&
				ArrayHelper.isArrayPositive(addressArray, this.isBuffer))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromString(String addressString)
	{
		int[] addressArray = addressStringToIntArray(addressString);
		
		if(addressArray.length < MAX_ADDRESS_LENGTH && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromTable(Map<Double, Double> addressTable)
	{
		int[] addressArray = ArrayHelper.tableToArray(addressTable);
		
		if(addressArray.length < MAX_ADDRESS_LENGTH && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public int[] toArray()
	{
		return this.addressArray;
	}
	
	public int getLength()
	{
		return addressArray.length;
	}
	
	public int getSymbol(int number)
	{
		if(number < 0 || number > getLength())
			return 0;
		
		return addressArray[number];
	}
	
	public boolean isComplete()
	{
		return getLength() >= MIN_ADDRESS_LENGTH;
	}
	
	public boolean canGrow()
	{
		return getLength() < MAX_ADDRESS_LENGTH;
	}
	
	public boolean isBuffer()
	{
		return this.isBuffer;
	}
	
	@Override
	public String toString()
	{
		return addressIntArrayToString(this.addressArray);
	}
	
	public Address reset()
	{
		addressArray = new int[0];
		
		return this;
	}
	
	public boolean containsSymbol(int symbol)
	{
		for(int i = 0; i < getLength(); i++)
		{
			if(addressArray[i] == symbol)
				return true;
		}
		
		return false;
	}
	
	public Address randomAddress(int size, int limit, long seed)
	{
		return randomAddress(0, size, limit, seed);
	}
	
	public Address randomAddress(int prefix, int size, int limit, long seed)
	{
		size = size > MAX_ADDRESS_LENGTH ? MAX_ADDRESS_LENGTH : size;
		
		Random random = new Random(seed);
		int[] addressArray = new int[size];
		boolean isValid = false;
		
		while(!isValid)
		{
			for(int i = 0; i < size; i++)
			{
				if(i == 0 && prefix > 0 && prefix < limit)
					addressArray[i] = prefix;
				else
					addressArray[i] = random.nextInt(1, limit);
			}
			if(ArrayHelper.differentNumbers(addressArray))
				isValid = true;
		}
		
		this.addressArray = addressArray;
		
		return this;
	}
	
	//============================================================================================
	//*******************************************Static*******************************************
	//============================================================================================
	
	public static boolean canBeTransformedToAddress(String addressString)
	{
		for(int i = 0; i < addressString.length(); i++)
		{
			char character = addressString.charAt(i);
			
			if(!Character.isDigit(character) && character != '-')
				return false;
		}
		
		return true;
	}
	
	public static int[] addressStringToIntArray(String addressString)
	{
		if(addressString == null || !canBeTransformedToAddress(addressString))
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
}
