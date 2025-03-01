package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;

import javax.annotation.Nullable;

public class AddressTable
{
	public static final ResourceLocation ADDRESS_TABLES_LOCATION = new ResourceLocation(StargateJourney.MODID, "address_table");
	public static final ResourceKey<Registry<AddressTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(ADDRESS_TABLES_LOCATION);
	public static final Codec<ResourceKey<AddressTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<AddressTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("include_generated_addresses").forGetter(AddressTable::includeGeneratedAddresses),
			WeightedAddress.CODEC.listOf().fieldOf("addresses").forGetter(AddressTable::getDimensions)
			).apply(instance, AddressTable::new));
	
	private final boolean includeGeneratedAddresses;
	private final List<WeightedAddress> dimensions;
	
	public AddressTable(boolean includeGeneratedAddresses, List<WeightedAddress> dimensions)
	{
		this.includeGeneratedAddresses = includeGeneratedAddresses;
		this.dimensions = dimensions;
	}
	
	public boolean includeGeneratedAddresses()
	{
		return includeGeneratedAddresses;
	}
	
	public List<WeightedAddress> getDimensions()
	{
		return dimensions;
	}
	
	public static AddressTable getAddressTable(Level level, ResourceLocation addressTable)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<AddressTable> registry = registries.registryOrThrow(AddressTable.REGISTRY_KEY);
        
        return registry.get(addressTable);
	}
	
	public static Address randomAddress(MinecraftServer server, AddressTable addressTable)
	{
		if(server == null || addressTable == null)
			return null;
		
		List<WeightedAddress> addresses = new ArrayList<WeightedAddress>();
		int totalWeight = 0;
		
		// Dimensions that are not in Datapack Solar Systems
		if(addressTable.includeGeneratedAddresses())
		{
			List<ResourceKey<Level>> generatedDimensions =  Universe.get(server).getDimensionsWithGeneratedSolarSystems();
			for(ResourceKey<Level> dimensionKey : generatedDimensions)
			{
				WeightedAddress address = new WeightedAddress(dimensionKey, 1);
				addresses.add(address);
				totalWeight += address.weight();
			}
		}
		
		// Dimensions in Solar Systems added by Datapacks
		List<WeightedAddress> datapackDimensions = addressTable.getDimensions();
		for(WeightedAddress address : datapackDimensions)
		{
			// Only add the address when the dimension exists
			if(address.addressDimension().right().isPresent() || server.getLevel(address.addressDimension().left().get()) != null)
			{
				addresses.add(address);
				totalWeight += address.weight();
			}
		}
		
		Random random = new Random();
		Iterator<WeightedAddress> iterator = datapackDimensions.iterator();
		WeightedAddress weightedAddress = null;
		for(int weight = random.nextInt(0, totalWeight + 1); iterator.hasNext();)
		{
			weightedAddress = iterator.next();
			weight -= weightedAddress.weight();
			
			if(weight <= 0)
				break;
		}
		
		if(weightedAddress == null)
			return null;
		
		if(weightedAddress.addressDimension().right().isPresent())
			return weightedAddress.addressDimension().right().get();
		
		return new Address(server, weightedAddress.addressDimension().left().get());
	}
	
	
	
	public static class WeightedAddress
	{
		private final Either<ResourceKey<Level>, Address> addressDimension;
		private final int weight;
		@Nullable
		private ResourceKey<Galaxy> galaxy;
		
		public static final Codec<WeightedAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.either(Level.RESOURCE_KEY_CODEC, Address.CODEC).fieldOf("address").forGetter(weightedAddress -> weightedAddress.addressDimension),
				Codec.INT.fieldOf("weight").forGetter(weightedAddress -> weightedAddress.weight),
				Galaxy.RESOURCE_KEY_CODEC.optionalFieldOf("galaxy").forGetter(weightedAddress -> Optional.ofNullable(weightedAddress.galaxy))
		).apply(instance, WeightedAddress::new));
		
		public WeightedAddress(Either<ResourceKey<Level>, Address> addressDimension, int weight, Optional<ResourceKey<Galaxy>> galaxy)
		{
			this.addressDimension = addressDimension;
			this.weight = weight;
			
			this.galaxy = galaxy.orElse(null);
		}
		
		public WeightedAddress(ResourceKey<Level> dimension, int weight)
		{
			this(Either.left(dimension), weight, Optional.empty());
		}
		
		public WeightedAddress(Address address, int weight)
		{
			this(Either.right(address), weight, Optional.empty());
		}
		
		public Either<ResourceKey<Level>, Address> addressDimension()
		{
			return addressDimension;
		}
		
		public int weight()
		{
			return weight;
		}
	}
}
