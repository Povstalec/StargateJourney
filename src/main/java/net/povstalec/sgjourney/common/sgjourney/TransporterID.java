package net.povstalec.sgjourney.common.sgjourney;

import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

import java.util.*;

public abstract class TransporterID
{
	public static final String TRANSPORTER_ID = "transporter_id";
	public static final String DIVIDER = "-";
	
	public static final byte FULL_ID_LENGTH = 9;
	public static final byte MIN_SYMBOL = 1;
	public static final byte MAX_SYMBOL = 6;
	
	protected int[] idArray = new int[0];
	
	public TransporterID(int... idArray)
	{
		try
		{
			verifyValidity(idArray);
			this.idArray =  idArray;
		}
		catch(IllegalArgumentException e)
		{
			StargateJourney.LOGGER.error("Error parsing address " + idIntArrayToString(idArray), e);
		}
	}
	
	public TransporterID(TransporterID other)
	{
		this(other.idArray);
	}
	
	public TransporterID(String idString)
	{
		this(idStringToIntArray(idString));
	}
	
	public TransporterID(Map<Double, Double> idTable)
	{
		this(ArrayHelper.tableToArray(idTable));
	}
	
	public TransporterID(List<Integer> idList)
	{
		this(ArrayHelper.integerListToArray(idList));
	}
	
	public static void verifyValidity(int[] idArray) throws IllegalArgumentException
	{
		if(idArray.length > FULL_ID_LENGTH)
			throw new IllegalArgumentException("Transporter ID is too long <0, 9>");
		
		for(int j : idArray)
		{
			if(j < MIN_SYMBOL || j > MAX_SYMBOL)
				throw new IllegalArgumentException("Transporter ID symbol " + j + " out of bounds <1, 6>");
		}
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
	
	public void saveToCompoundTag(CompoundTag tag, String name)
	{
		tag.putIntArray(name, idArray);
	}
	
	/**
	 * @return Copy of the ID array
	 */
	public int[] toArray()
	{
		return this.idArray.clone();
	}
	
	public List<Integer> toList()
	{
		return Arrays.stream(this.idArray).boxed().toList();
	}
	
	@Override
	public String toString()
	{
		return idIntArrayToString(this.idArray);
	}
	
	public static int[] idStringToIntArray(String addressString)
	{
		if(addressString == null || !canBeTransformedToID(addressString))
			return new int[0];
		
		String[] stringArray = addressString.split(DIVIDER);
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
	
	public static String idIntArrayToString(int[] array)
	{
		StringBuilder address = new StringBuilder(DIVIDER);
		
		for(int symbol : array)
		{
			address.append(symbol).append(DIVIDER);
		}
		return address.toString();
	}
	
	public static int[] randomTransporterIDArray(long seed)
	{
		Random random = new Random(seed);
		int[] addressArray = new int[FULL_ID_LENGTH];
		
		for(int i = 0; i < FULL_ID_LENGTH; i++)
		{
			addressArray[i] = random.nextInt(1, MAX_SYMBOL + 1);
		}
		
		return addressArray;
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
		return Objects.hash(getSymbol(0), getSymbol(1), getSymbol(2), getSymbol(3), getSymbol(4), getSymbol(5), getSymbol(6), getSymbol(7), getSymbol(8));
	}
	
	// Static functions
	
	private static boolean isAllowedInID(char character)
	{
		return character == '-' || Character.isDigit(character);
	}
	
	public static boolean canBeTransformedToID(String idString)
	{
		for(int i = 0; i < idString.length(); i++)
		{
			if(!isAllowedInID(idString.charAt(i)))
				return false;
		}
		
		return true;
	}
	
	//============================================================================================
	//**********************************Immutable Transporter ID**********************************
	//============================================================================================
	
	public static class Immutable extends TransporterID
	{
		
		public Immutable(int... idArray)
		{
			super(idArray);
		}
		
		public Immutable(TransporterID other)
		{
			super(other);
		}
		
		public Immutable(String idString)
		{
			super(idString);
		}
		
		public Immutable(Map<Double, Double> idTable)
		{
			super(idTable);
		}
		
		public Immutable(List<Integer> idList)
		{
			super(idList);
		}
		
		// Static functions
		
		public static TransporterID.Immutable randomID(long seed)
		{
			return new Immutable(randomTransporterIDArray(seed));
		}
	}
	
	//============================================================================================
	//***********************************Mutable Transporter ID***********************************
	//============================================================================================
}
