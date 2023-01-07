package net.povstalec.sgjourney.stargate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.povstalec.sgjourney.StargateJourney;

public class Addressing
{
	public static int[] randomAddress(int size, long seed)
	{
		Random random = new Random(seed);
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
	
	//TODO use this
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
	
	public static int[] growIntArray(int[] array, int x)
	{
		int[] newarray = new int[array.length + 1];
		
		for (int i = 0; i < array.length; i++)
		{
			newarray[i] = array[i];
		}
		
		newarray[array.length] = x;
		
		return newarray;
	}
	
	public static int[] convertTo7chevronAddress(int[] address)
	{
		// 18
		int[] convertor = {	address[0] - 10, // 8
							address[0] + 15, // 33
							address[0] - 3, // 15
							-address[0] + 7, // -11
							address[0] + 1, // 19
							-address[0] + 17}; // -1
		int[] convertedAddress = new int[6];
		
		for(int i = 0; i < 6; i++)
		{
			convertedAddress[i] = address[i + 1] + convertor[i];
			
			if(convertedAddress[i] < 1)
				convertedAddress[i] = convertedAddress[i] + 38;
			else if(convertedAddress[i] > 38)
				convertedAddress[i] = convertedAddress[i] - 38;
		}

		StargateJourney.LOGGER.info("Converted Address to: " + addressIntArrayToString(convertedAddress));
		return convertedAddress;
	}
	
	public static int[] convertTo8chevronAddress(int symbol, int[] address)
	{
		int[] convertor = {	symbol - 10,
							symbol + 15,
							symbol - 3,
							-symbol + 7,
							symbol + 1,
							-symbol + 17};
		int[] convertedAddress = new int[7];
		convertedAddress[0] = symbol;
		
		for(int i = 1; i < 7; i++)
		{
			convertedAddress[i] = address[i - 1] - convertor[i - 1];
			
			if(convertedAddress[i] < 1)
				convertedAddress[i] = convertedAddress[i] + 38;
			else if(convertedAddress[i] > 38)
				convertedAddress[i] = convertedAddress[i] - 38;
		}

		StargateJourney.LOGGER.info("Converted Address to: " + addressIntArrayToString(convertedAddress));
		return convertedAddress;
	}
	
	private static int[] addressStringToIntArray(String addressString)
	{
		String[] stringArray = addressString.split("-");
		int[] intArray = new int[0];
		
		for(int i = 1; i < stringArray.length; i++)
		{
			int number = Character.getNumericValue(stringArray[i].charAt(0));
			intArray = growIntArray(intArray, number);
		}
		
		return intArray;
	}
	
	public static String addressIntArrayToString(int[] array, int offset)
	{
		String address = "-";
		
		for(int i = offset; i < array.length; i++)
		{
			address = address + array[i] + "-";
		}
		return address;
	}
	
	public static String addressIntArrayToString(int[] array)
	{
		return addressIntArrayToString(array, 0);
	}
	
	private static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
}
