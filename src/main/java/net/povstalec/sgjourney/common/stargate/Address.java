package net.povstalec.sgjourney.common.stargate;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class Address
{
	public static final String ADDRESS_DIVIDER = "-";
	public static final int MIN_ADDRESS_LENGTH = 6;
	public static final int MAX_ADDRESS_LENGTH = 9;
	
	protected int[] addressArray = new int[0];
	protected boolean isBuffer = false;
	protected Optional<String> dimension = Optional.empty();
	
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
	
	public enum Type
	{
		ADDRESS_INVALID,
		ADDRESS_9_CHEVRON,
		ADDRESS_8_CHEVRON,
		ADDRESS_7_CHEVRON;
		
		public static final Address.Type fromInt(int addressLength)
		{
			switch(addressLength)
			{
			case 7:
				return ADDRESS_7_CHEVRON;
			case 8:
				return ADDRESS_8_CHEVRON;
			case 9:
				return ADDRESS_9_CHEVRON;
			default:
				return ADDRESS_INVALID;
			}
		}
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
		this.dimension = Optional.empty();
		
		if(addressArray.length < getMaxAddressLength() &&
				ArrayHelper.differentNumbers(addressArray) &&
				ArrayHelper.isArrayPositive(addressArray, this.isBuffer))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromString(String addressString)
	{
		this.dimension = Optional.empty();
		
		int[] addressArray = addressStringToIntArray(addressString);
		
		if(addressArray.length < getMaxAddressLength() && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromTable(Map<Double, Double> addressTable)
	{
		this.dimension = Optional.empty();
		
		int[] addressArray = ArrayHelper.tableToArray(addressTable);
		
		if(addressArray.length < getMaxAddressLength() && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	//TODO Replace String with ResourceKey<Level> eventually
	public Address fromDimension(ServerLevel level, String dimension)
	{
		String galaxy = StargateJourney.EMPTY;
		Set<String> galaxies = Universe.get(level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys();
		
		Iterator<String> iterator = galaxies.iterator();
		if(iterator.hasNext())
			galaxy = iterator.next();
		
		fromString(Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension));
		
		this.dimension = Optional.of(dimension);
		
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
	
	public int getMaxAddressLength()
	{
		return this.isBuffer ? MAX_ADDRESS_LENGTH + 1 : MAX_ADDRESS_LENGTH;
	}
	
	public boolean canGrow()
	{
		return getLength() < getMaxAddressLength() - 1;
	}
	
	public boolean isBuffer()
	{
		return this.isBuffer;
	}
	
	public boolean isFromDimension()
	{
		return this.dimension.isPresent();
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
