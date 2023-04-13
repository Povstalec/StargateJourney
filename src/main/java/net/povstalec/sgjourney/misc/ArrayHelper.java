package net.povstalec.sgjourney.misc;

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
}
