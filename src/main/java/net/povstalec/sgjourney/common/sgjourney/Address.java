package net.povstalec.sgjourney.common.sgjourney;

import java.util.*;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class Address implements Cloneable, Comparable<Address>
{
	public static final String ADDRESS = "address";
	public static final String SYMBOLS = "symbols";
	
	public static final String ADDRESS_TYPE = "address_type";
	public static final String GALAXY = "galaxy";
	public static final String DIMENSION = "dimension";
	
	public static final String ADDRESS_DIVIDER = "-";
	public static final int MIN_DIALED_ADDRESS_LENGTH = 6;
	public static final int MAX_ADDRESS_LENGTH = 9;
	public static final int POINT_OF_ORIGIN = 0;
	public static final int MIN_SYMBOL = POINT_OF_ORIGIN;
	public static final int MAX_SYMBOL = 47;
	public static final int ADDRESS_GENERATION_SYMBOLS = 36; // Max symbol (exclusive) allowed for normal generation purposes
	
	protected int[] addressArray = new int[0];
	
	public Address() {}
	
	public Address(int... addressArray) throws IllegalArgumentException
	{
		try
		{
			verifyValidity(addressArray);
			this.addressArray = addressArray;
		}
		catch(IllegalArgumentException e)
		{
			StargateJourney.LOGGER.error("Error parsing address " + addressIntArrayToString(addressArray), e);
		}
	}
	
	public Address(Address other)
	{
		this(other.addressArray);
	}
	
	public Address(String addressString)
	{
		this(addressStringToIntArray(addressString));
	}
	
	public Address(Map<Double, Double> addressTable)
	{
		this(ArrayHelper.tableToArray(addressTable));
	}
	
	public Address(List<Integer> addressList)
	{
		this(ArrayHelper.integerListToArray(addressList));
	}
	
	/**
	 * Verifies the validity of the provided Address array
	 * @param addressArray Integer Array representing the Address
	 * @throws IllegalArgumentException Throws an exception if the provided array is not a valid Address array
	 */
	public static void verifyValidity(int[] addressArray) throws IllegalArgumentException
	{
		if(addressArray.length > MAX_ADDRESS_LENGTH)
			throw new IllegalArgumentException("Address is too long <0, 9>");
		
		if(!ArrayHelper.differentNumbers(addressArray))
			throw new IllegalArgumentException("Address contains duplicate symbols");
		
		for(int i = 0; i < addressArray.length; i++)
		{
			if(addressArray[i] < MIN_SYMBOL || addressArray[i] > MAX_SYMBOL)
				throw new IllegalArgumentException("Address symbol " + addressArray[i] + " out of bounds <0, 47>");
			else if(addressArray[i] == POINT_OF_ORIGIN && i != addressArray.length - 1)
				throw new IllegalArgumentException("No symbols allowed in Address after Point of Origin");
		}
	}
	
	public int getLength()
	{
		return addressArray.length;
	}
	
	/**
	 * @param index Index of the symbol we're looking at
	 * @return The symbol at the specified index or 0 if there is no symbol there
	 */
	public int symbolAt(int index)
	{
		return ArrayHelper.zeroNumAt(addressArray, index);
	}
	
	public int lastSymbol()
	{
		if(addressArray.length == 0)
			return 0;
		
		return addressArray[addressArray.length - 1];
	}
	
	public boolean isEmpty()
	{
		return addressArray.length == 0;
	}
	
	public boolean hasPointOfOrigin()
	{
		return addressArray.length > 0 && lastSymbol() == POINT_OF_ORIGIN; // Point of Origin is guaranteed to be the last
	}
	
	/**
	 * @return Number of symbols in the address, excluding the Point of Origin
	 */
	public int regularSymbolCount()
	{
		if(hasPointOfOrigin())
			return addressArray.length - 1;
		else
			return addressArray.length;
	}
	
	public Address.Type getType()
	{
		if(hasPointOfOrigin())
			return Address.Type.fromLength(addressArray.length);
		else if(addressArray.length == MAX_ADDRESS_LENGTH)
			return Address.Type.fromLength(MAX_ADDRESS_LENGTH);
		else
			return Address.Type.fromLength(addressArray.length + 1);
	}
	
	public ChatFormatting getChatFormatting()
	{
		return switch(this.getType())
		{
			case ADDRESS_7_CHEVRON -> ChatFormatting.GOLD;
			case ADDRESS_8_CHEVRON -> ChatFormatting.LIGHT_PURPLE;
			case ADDRESS_9_CHEVRON -> ChatFormatting.AQUA;
			default -> ChatFormatting.GRAY;
		};
	}
	
	public MutableComponent toComponent(boolean copyToClipboard)
	{
		Style style = Style.EMPTY;
		if(copyToClipboard)
		{
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.command.click_to_copy.address")));
			style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.toString()));
		}
		
		return Component.literal(addressIntArrayToString(this.addressArray)).setStyle(style.applyFormat(getChatFormatting()));
	}
	
	public boolean containsRegularSymbol(int symbol)
	{
		for(int i = 0; i < regularSymbolCount(); i++)
		{
			if(symbolAt(i) == symbol)
				return true;
		}
		
		return false;
	}
	
	public boolean containsSymbol(int symbol)
	{
		if(symbol == 0)
			return hasPointOfOrigin();
		
		return containsRegularSymbol(symbol);
	}
	
	public boolean canBeDialed()
	{
		if(hasPointOfOrigin())
			return addressArray.length >= MIN_DIALED_ADDRESS_LENGTH;
		
		return addressArray.length == MAX_ADDRESS_LENGTH;
	}
	
	public boolean hasPointOfOriginOrMaxLength()
	{
		return hasPointOfOrigin() || addressArray.length == MAX_ADDRESS_LENGTH;
	}
	
	public void saveToCompoundTag(CompoundTag tag, String addressKey)
	{
		tag.putIntArray(addressKey, addressArray);
	}
	
	/**
	 * @return Copy of the address array
	 */
	public int[] toArray()
	{
		return this.addressArray.clone();
	}
	
	public List<Integer> toList()
	{
		return Arrays.stream(this.addressArray).boxed().toList();
	}
	
	@Override
	public String toString()
	{
		return addressIntArrayToString(this.addressArray);
	}
	
	@Override
	public Address clone()
	{
		try
		{
			Address address = (Address) super.clone();
			address.addressArray = this.addressArray.clone();
			return address;
		}
		catch(CloneNotSupportedException e)
		{
			StargateJourney.LOGGER.error("Could not clone Address {}", String.valueOf(e));
			return null;
		}
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(this == object)
			return true;
		else if(object instanceof Address address)
		{
			int length = Math.max(this.getLength(), address.getLength());
			for(int i = 0; i < length; i++)
			{
				if(this.symbolAt(i) != address.symbolAt(i))
					return false;
			}
			
			return true;
		}
		else if(object instanceof AddressFilterInfo.HiddenAddress hiddenAddress)
			return equals(hiddenAddress.address());
		else if(object instanceof int[] array)
		{
			int length = Math.max(this.getLength(), array.length);
			for(int i = 0; i < length; i++)
			{
				if(this.symbolAt(i) != ArrayHelper.zeroNumAt(array, i))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(symbolAt(0), symbolAt(1), symbolAt(2), symbolAt(3), symbolAt(4), symbolAt(5), symbolAt(6), symbolAt(7), symbolAt(8));
	}
	
	@Override
	public int compareTo(@NotNull Address other)
	{
		return Arrays.compare(this.addressArray, other.addressArray);
	}
	
	// Static functions
	
	private static boolean isAllowedInAddress(char character)
	{
		return character == '-' || Character.isDigit(character);
	}
	
	public static boolean canBeTransformedToAddress(String addressString)
	{
		for(int i = 0; i < addressString.length(); i++)
		{
			if(!isAllowedInAddress(addressString.charAt(i)))
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
		StringBuilder address = new StringBuilder(ADDRESS_DIVIDER);
		
		for(int symbol : array)
		{
			address.append(symbol).append(ADDRESS_DIVIDER);
		}
		return address.toString();
	}
	
	public static int[] randomAddressArray(int prefix, int size, int limit, long seed)
	{
		if(size > MAX_ADDRESS_LENGTH)
			throw new IllegalArgumentException("Address is too long <0, 9>");
		
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
		
		return addressArray;
	}
	
	
	
	public enum Type
	{
		ADDRESS_INVALID((byte) 0),
		ADDRESS_9_CHEVRON((byte) 9),
		ADDRESS_8_CHEVRON((byte) 8),
		ADDRESS_7_CHEVRON((byte) 7);
		
		private final byte value;
		
		Type(byte value)
		{
			this.value = value;
		}
		
		public byte byteValue()
		{
			return value;
		}
		
		public boolean below(Address.Type type)
		{
			return this.byteValue() < type.byteValue();
		}
		
		public static Address.Type fromLength(int addressLength)
		{
			return switch(addressLength)
			{
				case 7 -> ADDRESS_7_CHEVRON;
				case 8 -> ADDRESS_8_CHEVRON;
				case 9 -> ADDRESS_9_CHEVRON;
				default -> ADDRESS_INVALID;
			};
		}
	}
	
	//============================================================================================
	//*************************************Immutable Address**************************************
	//============================================================================================
	
	public static final class Immutable extends Address
	{
		public static final Codec<Address.Immutable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.listOf().fieldOf(SYMBOLS).forGetter(address -> ArrayHelper.arrayToIntegerList(address.addressArray))
		).apply(instance, Address.Immutable::new));
		
		public Immutable() {}
		
		public Immutable(int... addressArray)
		{
			super(addressArray);
		}
		
		public Immutable(Address other)
		{
			super(other.addressArray);
		}
		
		public Immutable(String addressString)
		{
			super(addressString);
		}
		
		public Immutable(Map<Double, Double> addressTable)
		{
			super(addressTable);
		}
		
		public Immutable(List<Integer> addressList)
		{
			super(addressList);
		}
		
		@Override
		public Immutable clone()
		{
			return (Immutable) super.clone();
		}
		
		// Static functions
		
		public static Immutable randomAddress(int size, int limit, long seed)
		{
			return randomAddress(0, size, limit, seed);
		}
		
		public static Immutable randomAddress(int prefix, int size, int limit, long seed)
		{
			return new Immutable(randomAddressArray(prefix, size, limit, seed));
		}
		
		public static Immutable read(StringReader reader)
		{
			int i = reader.getCursor();
			
			while(reader.canRead() && isAllowedInAddress(reader.peek()))
			{
				reader.skip();
			}
			
			String string = reader.getString().substring(i, reader.getCursor());
			
			return new Immutable(string);
		}
		
		/**
		 * Extends any non-empty address with a Point of Origin, or leaves it as it is if it already has one
		 * @param address Original address
		 * @return Address with point of origin
		 */
		public static Address.Immutable extendWithPointOfOrigin(Address.Immutable address)
		{
			// The second check is here in case the last symbol is not the usual Point of Origin
			if(!address.hasPointOfOrigin() && !address.isEmpty() && address.addressArray.length < MAX_ADDRESS_LENGTH)
				return new Immutable(ArrayHelper.growIntArray(address.addressArray, POINT_OF_ORIGIN));
			
			return address;
		}
		
		@Nullable
		public static Address.Immutable loadFromCompoundTag(CompoundTag tag, String addressKey)
		{
			if(tag.contains(addressKey, Tag.TAG_INT_ARRAY))
				return new Address.Immutable(tag.getIntArray(addressKey));
			
			return null;
		}
	}
	
	//============================================================================================
	//**************************************Mutable Address***************************************
	//============================================================================================
	
	public static final class Mutable extends Address
	{
		public static final Codec<Mutable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.listOf().fieldOf(SYMBOLS).forGetter(address -> ArrayHelper.arrayToIntegerList(address.addressArray))
		).apply(instance, Mutable::new));
		
		public Mutable() {}
		
		public Mutable(int... addressArray)
		{
			super(addressArray);
		}
		
		public Mutable(Address otherAddress)
		{
			super(otherAddress.addressArray);
		}
		
		public Mutable(String addressString)
		{
			super(addressString);
		}
		
		public Mutable(Map<Double, Double> addressTable)
		{
			super(addressTable);
		}
		
		public Mutable(List<Integer> addressList)
		{
			super(addressList);
		}
		
		public Mutable reset()
		{
			addressArray = new int[0];
			return this;
		}
		
		/**
		 * @return Raw address array
		 */
		public int[] getArray()
		{
			return addressArray;
		}
		
		public boolean addSymbol(int symbol)
		{
			// Can't grow if it contains 0
			if(!canGrow())
				return false;
			
			if(symbol < 0)
				return false;
			
			this.addressArray = ArrayHelper.growIntArray(this.addressArray, symbol);
			return true;
		}
		
		public boolean canGrow()
		{
			if(hasPointOfOrigin())
				return false;
			
			return addressArray.length < MAX_ADDRESS_LENGTH;
		}
		
		public Mutable fromArray(int... addressArray)
		{
			try
			{
				verifyValidity(addressArray);
				this.addressArray = addressArray;
			}
			catch(IllegalArgumentException e)
			{
				StargateJourney.LOGGER.error("Error parsing address {}", addressIntArrayToString(addressArray), e);
			}
			
			return this;
		}
		
		public Mutable fromAddress(Address otherAddress)
		{
			return fromArray(otherAddress.addressArray);
		}
		
		public Mutable fromString(String addressString)
		{
			return fromArray(addressStringToIntArray(addressString));
		}
		
		public Mutable fromTable(Map<Double, Double> addressTable)
		{
			return fromArray(ArrayHelper.tableToArray(addressTable));
		}
		
		public Mutable fromIntegerList(List<Integer> integerList)
		{
			return fromArray(ArrayHelper.integerListToArray(integerList));
		}
		
		@Override
		public Mutable clone()
		{
			return (Mutable) super.clone();
		}
		
		// Static functions
		
		public static Mutable randomAddress(int size, int limit, long seed)
		{
			return randomAddress(0, size, limit, seed);
		}
		
		public static Mutable randomAddress(int prefix, int size, int limit, long seed)
		{
			return new Mutable(randomAddressArray(prefix, size, limit, seed));
		}
		
		public static Mutable read(StringReader reader)
		{
			int i = reader.getCursor();
			
			while(reader.canRead() && isAllowedInAddress(reader.peek()))
			{
				reader.skip();
			}
			
			String string = reader.getString().substring(i, reader.getCursor());
			
			return new Mutable(string);
		}
		
		@Nullable
		public static Address.Mutable loadFromCompoundTag(CompoundTag tag, String addressKey)
		{
			if(tag.contains(addressKey, Tag.TAG_INT_ARRAY))
				return new Address.Mutable(tag.getIntArray(addressKey));
			
			return null;
		}
	}
	
	//============================================================================================
	//*************************************Dimension Address**************************************
	//============================================================================================
	
	public static final class Dimension extends Address
	{
		public static final Codec<Dimension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(address -> address.dimension),
				Galaxy.RESOURCE_KEY_CODEC.optionalFieldOf("galaxy").forGetter(address -> Optional.ofNullable(address.galaxyKey)),
				Codec.intRange(7, 9).optionalFieldOf("address_type", 7).forGetter(address -> (int) address.addressType.value)
		).apply(instance, Dimension::new));
		
		private ResourceKey<Level> dimension;
		@Nullable
		private ResourceKey<Galaxy> galaxyKey;
		private Address.Type addressType;
		
		public Dimension() {}
		
		public Dimension(ResourceKey<Level> dimension)
		{
			this(dimension, Optional.empty(), Type.ADDRESS_7_CHEVRON);
		}
		
		private Dimension(ResourceKey<Level> dimension, Optional<ResourceKey<Galaxy>> galaxyKey, int addressType)
		{
			this(dimension, galaxyKey, Address.Type.fromLength(addressType));
		}
		
		public Dimension(ResourceKey<Level> dimension, Optional<ResourceKey<Galaxy>> galaxyKey, Address.Type addressType)
		{
			this.dimension = dimension;
			this.galaxyKey = galaxyKey.orElse(null);
			this.addressType = addressType;
		}
		
		public Dimension(ResourceKey<Level> dimension, Optional<ResourceKey<Galaxy>> galaxyKey, int... addressArray)
		{
			super(addressArray);
			
			this.dimension = dimension;
			this.galaxyKey = galaxyKey.orElse(null);
			this.addressType = super.getType();
		}
		
		public ResourceKey<Level> getDimension()
		{
			return this.dimension;
		}
		
		public ResourceKey<Galaxy> getGalaxy()
		{
			return this.galaxyKey;
		}
		
		@Nullable
		private Address.Immutable generate7ChevronAddress(MinecraftServer server)
		{
			if(this.galaxyKey != null)
				return Universe.get(server).getAddressInGalaxyFromDimension(this.galaxyKey, this.dimension);
			else
			{
				Galaxy galaxy = Universe.get(server).getGalaxyFromDimension(this.dimension);
				if(galaxy != null)
					return Universe.get(server).getAddressInGalaxyFromDimension(galaxy.getResourceKey(), this.dimension);
				else
					return null;
			}
		}
		
		@Nullable
		private Address.Immutable generate8ChevronAddress(MinecraftServer server)
		{
			return Universe.get(server).getExtragalacticAddressFromDimension(this.dimension);
		}
		
		@Nullable
		private Address.Immutable generate9ChevronAddress(MinecraftServer server)
		{
			List<Stargate> stargatesInDimension = StargateNetwork.get(server).getStargatesInDimension(this.dimension);
			
			if(stargatesInDimension.isEmpty())
				return null;
			
			return stargatesInDimension.get(0).get9ChevronAddress();
		}
		
		@Nullable
		private Address.Immutable generateAddress(MinecraftServer server)
		{
			return switch(this.addressType)
			{
				case ADDRESS_8_CHEVRON -> generate8ChevronAddress(server);
				case ADDRESS_9_CHEVRON -> generate9ChevronAddress(server);
				default -> generate7ChevronAddress(server);
			};
		}
		
		public void generate(MinecraftServer server)
		{
			Address address = generateAddress(server);
			
			if(address != null)
				this.addressArray = address.addressArray.clone();
		}
		
		@Override
		public Address.Dimension clone()
		{
			Dimension address = (Dimension) super.clone();
			address.dimension = this.dimension; // Shallow copy
			return address;
		}
		
		@Override
		public void saveToCompoundTag(CompoundTag tag, String addressKey)
		{
			CompoundTag addressTag = new CompoundTag();
			
			if(this.addressArray.length > 0)
				addressTag.putIntArray(ADDRESS, this.addressArray);
			addressTag.putString(DIMENSION, this.dimension.location().toString());
			addressTag.putByte(ADDRESS_TYPE, this.addressType.byteValue());
			
			if(this.galaxyKey != null)
				addressTag.putString(GALAXY, this.galaxyKey.location().toString());
			
			tag.put(addressKey, addressTag);
		}
		
		public void saveToCompoundTagAsArray(CompoundTag tag, String addressKey)
		{
			super.saveToCompoundTag(tag, addressKey);
		}
		
		public static Address.Dimension loadFromCompoundTag(CompoundTag tag, String addressKey, String dimensionKey, String galaxyKey) //TODO For legacy reasons
		{
			int[] addressArray = tag.getIntArray(addressKey);
			ResourceKey<Level> dimension = Conversion.stringToDimension(tag.getString(dimensionKey));
			ResourceKey<Galaxy> galaxy = Conversion.stringToGalaxyKey(tag.getString(galaxyKey));
			Address.Type type = tag.contains(ADDRESS_TYPE, Tag.TAG_BYTE) ? Address.Type.fromLength(tag.getByte(ADDRESS_TYPE)) : Address.Type.ADDRESS_7_CHEVRON;
			
			
			if(tag.contains(addressKey, Tag.TAG_INT_ARRAY))
				return new Address.Dimension(dimension, Optional.ofNullable(galaxy), addressArray);
			else
				return new Address.Dimension(dimension, Optional.ofNullable(galaxy), type);
		}
		
		@Nullable
		public static Address.Dimension loadFromCompoundTag(CompoundTag tag, String addressKey)
		{
			if(!tag.contains(addressKey, Tag.TAG_COMPOUND))
				return null;
			
			return loadFromCompoundTag(tag.getCompound(addressKey), ADDRESS, DIMENSION, GALAXY);
		}
	}
	
	//============================================================================================
	//************************************Randomizable Address************************************
	//============================================================================================
	
	public record Randomizable<A extends Address>(A address, boolean isRandomizable)
	{
		public Randomizable(A address)
		{
			this(address, false);
		}
		
		public static <A extends Address> RandomizableCodec<A> codec(Codec<A> addressCodec)
		{
			return new RandomizableCodec<>(addressCodec);
		}
	}
	
	public static final class RandomizableCodec<A extends Address> implements Codec<Randomizable<A>>
	{
		private final Codec<A> addressCodec;
		
		private RandomizableCodec(Codec<A> addressCodec)
		{
			this.addressCodec = addressCodec;
		}
		
		@Override
		public <T> DataResult<Pair<Randomizable<A>, T>> decode(final DynamicOps<T> ops, final T input)
		{
			return addressCodec.decode(ops, input).flatMap(addressPair ->
					Codec.BOOL.fieldOf("randomizable").codec().decode(ops, addressPair.getSecond()).map(boolPair ->
							Pair.of(new Randomizable<>(addressPair.getFirst(), boolPair.getFirst()), boolPair.getSecond())
					)
			);
		}
		
		@Override
		public <T> DataResult<T> encode(final Randomizable<A> input, final DynamicOps<T> ops, final T prefix)
		{
			return Codec.BOOL.fieldOf("randomizable").codec().encode(input.isRandomizable(), ops, prefix).flatMap(f -> addressCodec.encode(input.address(), ops, f));
		}
		
		@Override
		public boolean equals(final Object other)
		{
			if(this == other)
				return true;
			
			if(other instanceof RandomizableCodec<?> otherAddress)
				return Objects.equals(this.addressCodec, otherAddress.addressCodec);
			
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(this.addressCodec);
		}
		
		@Override
		public @NotNull String toString()
		{
			return "Address.RandomizableCodec[" + this.addressCodec + ']';
		}
	}
}
