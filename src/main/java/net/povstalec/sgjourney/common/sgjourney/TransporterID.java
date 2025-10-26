package net.povstalec.sgjourney.common.sgjourney;

import java.util.Arrays;
import java.util.Objects;

public class TransporterID
{
	protected int[] idArray;
	
	public TransporterID(int... idArray)
	{
		this.idArray =  idArray;
	}
	
	public int getLength()
	{
		return idArray.length;
	}
	
	public int getSymbol(int number)
	{
		if(number < 0 || number >= getLength())
			return 0;
		
		return idArray[number];
	}
	
	
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof TransporterID otherID)
			return Arrays.equals(this.idArray, otherID.idArray);
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(getSymbol(0), getSymbol(1), getSymbol(2), getSymbol(3), getSymbol(4), getSymbol(5), getSymbol(6), getSymbol(7));
	}
}
