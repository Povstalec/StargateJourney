package net.povstalec.sgjourney.common.stargate;

import java.util.*;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

import javax.annotation.Nullable;

public final class Address
{
	public static final String ADDRESS_DIVIDER = "-";
	public static final int MIN_ADDRESS_LENGTH = 6;
	public static final int MAX_ADDRESS_LENGTH = 9;
	
	private int[] addressArray = new int[0];
	private boolean isBuffer = false;
	@Nullable
	private ResourceKey<Level> dimension;
	
	public static final Codec<Address> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.listOf().fieldOf("symbols").forGetter(address -> Arrays.stream(address.addressArray).boxed().collect(Collectors.toList()))
	).apply(instance, Address::new));
	
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
	
	public Address(List<Integer> addressList)
	{
		fromIntegerList(addressList);
	}
	
	public Address(MinecraftServer server, ResourceKey<Level> dimension)
	{
		fromDimension(server, dimension);
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
			case 6:
				return ADDRESS_7_CHEVRON;
			case 7:
				return ADDRESS_8_CHEVRON;
			case 8:
				return ADDRESS_9_CHEVRON;
			default:
				return ADDRESS_INVALID;
			}
		}
	}
	
	public Address addSymbol(int symbol)
	{
		// Can't grow if it contains 0
		if(hasPointOfOrigin())
			return this;
		
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
		this.dimension = null;
		
		if(addressArray.length < getMaxAddressLength() &&
				ArrayHelper.differentNumbers(addressArray) &&
				ArrayHelper.isArrayPositive(addressArray, this.isBuffer))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromString(String addressString)
	{
		this.dimension = null;
		
		int[] addressArray = addressStringToIntArray(addressString);
		
		if(addressArray.length < getMaxAddressLength() && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromTable(Map<Double, Double> addressTable)
	{
		this.dimension = null;
		
		int[] addressArray = ArrayHelper.tableToArray(addressTable);
		
		if(addressArray.length < getMaxAddressLength() && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromIntegerList(List<Integer> integerList)
	{
		this.dimension = null;
		
		int[] addressArray = integerListToArray(integerList);
		
		if(addressArray.length < getMaxAddressLength() && ArrayHelper.differentNumbers(addressArray))
			this.addressArray = addressArray;
		
		return this;
	}
	
	public Address fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		Optional<Galaxy.Serializable> galaxy = Universe.get(server).getGalaxyFromDimension(dimension);
		
		if(galaxy.isPresent())
		{
			Optional<Address.Immutable> address = Universe.get(server).getAddressInGalaxyFromDimension(galaxy.get().getKey().location().toString(), dimension);
			
			if(address.isPresent())
			{
				//TODO Would be nice to use copy here
				fromArray(address.get().toArray());
				this.dimension = dimension;
			}
		}
		
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
		if(number < 0 || number >= getLength())
			return 0;
		
		return addressArray[number];
	}
	
	public boolean isEmpty()
	{
		return getLength() <= 0;
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
	
	public boolean hasPointOfOrigin()
	{
		return this.containsSymbol(0);
	}
	
	public boolean isBuffer()
	{
		return this.isBuffer;
	}
	
	public boolean isFromDimension()
	{
		return this.dimension != null;
	}
	
	@Nullable
	public ResourceKey<Level> getDimension()
	{
		return this.dimension;
	}
	
	public Address.Type getType()
	{
		return Address.Type.fromInt(this.getLength());
	}
	
	@Override
	public String toString()
	{
		return addressIntArrayToString(this.addressArray);
	}
	
	public Component toComponent(boolean copyToClipboard)
	{
		ChatFormatting chatFormatting;
		
		switch(this.getType())
		{
		case ADDRESS_7_CHEVRON:
			chatFormatting = ChatFormatting.GOLD;
			break;
		case ADDRESS_8_CHEVRON:
			chatFormatting = ChatFormatting.LIGHT_PURPLE;
			break;
		case ADDRESS_9_CHEVRON:
			chatFormatting = ChatFormatting.AQUA;
			break;
		default:
			chatFormatting = ChatFormatting.GRAY;
		}
		
		Style style = Style.EMPTY;
		style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
		style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.toString()));
		
		return Component.literal(addressIntArrayToString(this.addressArray)).setStyle(style.applyFormat(chatFormatting));
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
	
	public Address copy()
	{
		Address copyAddress = new Address(this.isBuffer).fromArray(addressArray);
		copyAddress.dimension = this.dimension;
		return copyAddress;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Address address)
			return Arrays.equals(this.addressArray, address.addressArray);
		else if(object instanceof Address.Immutable address)
				return Arrays.equals(this.addressArray, address.addressArray);
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.getSymbol(0), this.getSymbol(1), this.getSymbol(2),
				this.getSymbol(3), this.getSymbol(4), this.getSymbol(5),
				this.getSymbol(6), this.getSymbol(7));
	}
	
	public Address.Immutable immutable()
	{
		return new Address.Immutable(this);
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
	
	public static int[] integerListToArray(List<Integer> integerList)
	{
		return integerList.stream().mapToInt((integer) -> integer).toArray();
	}
	
	
	
	public static final class Immutable
	{
		private final int[] addressArray;
		
		public Immutable(String addressString)
		{
			this.addressArray = new Address(addressString).toArray();
		}
		
		public Immutable(Address address)
		{
			this.addressArray = address.toArray();
		}
		
		public final int getLength()
		{
			return addressArray.length;
		}
		
		public final int getSymbol(int number)
		{
			if(number < 0 || number >= getLength())
				return 0;
			
			return addressArray[number];
		}
		
		public final int[] toArray()
		{
			return addressArray;
		}
		
		@Override
		public final String toString()
		{
			return addressIntArrayToString(this.addressArray);
		}
		
		public final Address.Type getType()
		{
			return Address.Type.fromInt(this.getLength());
		}
		
		public final Component toComponent(boolean copyToClipboard)
		{
			ChatFormatting chatFormatting;
			
			switch(this.getType())
			{
			case ADDRESS_7_CHEVRON:
				chatFormatting = ChatFormatting.GOLD;
				break;
			case ADDRESS_8_CHEVRON:
				chatFormatting = ChatFormatting.LIGHT_PURPLE;
				break;
			case ADDRESS_9_CHEVRON:
				chatFormatting = ChatFormatting.AQUA;
				break;
			default:
				chatFormatting = ChatFormatting.GRAY;
			}
			
			Style style = Style.EMPTY;
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
			style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.toString()));
			
			return Component.literal(addressIntArrayToString(this.addressArray)).setStyle(style.applyFormat(chatFormatting));
		}
		
		public final boolean containsSymbol(int symbol)
		{
			for(int i = 0; i < getLength(); i++)
			{
				if(addressArray[i] == symbol)
					return true;
			}
			
			return false;
		}
		
		public final Address.Immutable copy()
		{
			Address.Immutable copyAddress = new Address(true).fromArray(addressArray).immutable();
			
			return copyAddress;
		}
		
		@Override
		public final boolean equals(Object object)
		{
			if(object instanceof Address.Immutable address)
				return Arrays.equals(this.addressArray, address.addressArray);
			else if(object instanceof Address address)
				return Arrays.equals(this.addressArray, address.addressArray);
			
			return false;
		}
		
		@Override
		public final int hashCode()
		{
			return Objects.hash(this.getSymbol(0), this.getSymbol(1), this.getSymbol(2),
					this.getSymbol(3), this.getSymbol(4), this.getSymbol(5),
					this.getSymbol(6), this.getSymbol(7));
		}
		
		public final Address mutable()
		{
			return new Address(addressArray);
		}
	}
}
