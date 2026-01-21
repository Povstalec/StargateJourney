package net.povstalec.sgjourney.common.misc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayHelper
{
	public static int[] growIntArray(int[] array, int x)
	{
		int[] newArray = new int[array.length + 1];
		
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = x;
		
		return newArray;
	}
	
	public static int[] tableToArray(Map<Double, Double> table)
	{
		int[] addressArray = new int[table.size()];
		
		for(int i = 0; i < addressArray.length; i++)
		{
			addressArray[i] = (int) Math.floor(table.get((double) (i + 1)));
		}
		
		return addressArray;
	}
	
	public static int[] integerListToArray(List<Integer> integerList)
	{
		return integerList.stream().mapToInt((integer) -> integer).toArray();
	}
	
	public static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
	
	public static boolean isArrayInBounds(int[] array, int lowestAllowed, int highestAllowed)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] < lowestAllowed || array[i] > highestAllowed)
				return false;
		}
		
		return true;
	}
}
