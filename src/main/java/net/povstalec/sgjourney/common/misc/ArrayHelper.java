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
		int[] newarray = new int[array.length + 1];
		
		for (int i = 0; i < array.length; i++)
		{
			newarray[i] = array[i];
		}
		
		newarray[array.length] = x;
		
		return newarray;
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
	
	public static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
	
	public static boolean isArrayPositive(int[] array, boolean includeZero)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(includeZero ? array[i] < 0 : array[i] <= 0)
				return false;
		}
		
		return true;
	}
}
