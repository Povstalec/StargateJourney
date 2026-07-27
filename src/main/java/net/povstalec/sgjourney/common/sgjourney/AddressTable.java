package net.povstalec.sgjourney.common.sgjourney;

import java.util.*;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;

public class AddressTable
{
	public static final ResourceLocation ADDRESS_TABLES_LOCATION = StargateJourney.sgjourneyLocation("address_table");
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
	
	public static Address randomAddress(ServerLevel level, AddressTable addressTable)
	{
		if(level == null || addressTable == null)
			return null;
		
		List<WeightedAddress> addresses = new ArrayList<>();
		int totalWeight = 0;
		
		// Dimensions that are not in Datapack Solar Systems
		if(addressTable.includeGeneratedAddresses())
		{
			Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxyMap = Universe.get(level).getGalaxiesFromDimension(level.dimension());
			
			if(galaxyMap != null)
			{
				Universe universe = Universe.get(level);
				for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxyEntry : galaxyMap.entrySet())
				{
					Galaxy galaxy = universe.getGalaxy(galaxyEntry.getKey());
					if(galaxy != null)
					{
						// Get Addresses from all generated Address Regions in the Galaxies that this Dimension is located in
						for(Map.Entry<Address.Immutable, AddressRegion> addressRegionEntry : galaxy.getAddressRegions(entry -> entry.getValue().isGenerated))
						{
							for(SpaceLocation spaceLocation : addressRegionEntry.getValue().getSpaceLocations())
							{
								WeightedAddress address = new WeightedAddress(new Address.Dimension(spaceLocation.getDimension()), 1);
								addresses.add(address);
								totalWeight += address.weight();
							}
						}
					}
					
				}
			}
			
			
			List<ResourceKey<Level>> generatedDimensions =  Universe.get(level).getDimensionsWithGeneratedAddressRegions();
			for(ResourceKey<Level> dimensionKey : generatedDimensions)
			{
				WeightedAddress address = new WeightedAddress(new Address.Dimension(dimensionKey), 1);
				addresses.add(address);
				totalWeight += address.weight();
			}
		}
		
		// Dimensions in Solar Systems added by Datapacks
		List<WeightedAddress> datapackDimensions = addressTable.getDimensions();
		for(WeightedAddress address : datapackDimensions)
		{
			// Only add the address when the dimension exists
			if(address.addressDimension().right().isPresent() || level.getServer().getLevel(address.addressDimension().left().get().getDimension()) != null)
			{
				addresses.add(address);
				totalWeight += address.weight();
			}
		}
		
		Random random = new Random();
		Iterator<WeightedAddress> iterator = addresses.iterator();
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
			return weightedAddress.addressDimension().right().get().clone();
		
		return weightedAddress.addressDimension().left().get().clone();
	}
	
	
	
	public static class WeightedAddress
	{
		private final Either<Address.Dimension, Address.Immutable> addressDimension;
		private final int weight;
		
		public static final Codec<WeightedAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.either(Address.Dimension.CODEC, Address.Immutable.CODEC).fieldOf("address").forGetter(weightedAddress -> weightedAddress.addressDimension),
				Codec.INT.fieldOf("weight").forGetter(weightedAddress -> weightedAddress.weight)
		).apply(instance, WeightedAddress::new));
		
		public WeightedAddress(Either<Address.Dimension, Address.Immutable> addressDimension, int weight)
		{
			this.addressDimension = addressDimension;
			this.weight = weight;
		}
		
		public WeightedAddress(Address.Dimension dimensionAddress, int weight)
		{
			this(Either.left(dimensionAddress), weight);
		}
		
		public WeightedAddress(Address.Immutable address, int weight)
		{
			this(Either.right(address), weight);
		}
		
		public Either<Address.Dimension, Address.Immutable> addressDimension()
		{
			return addressDimension;
		}
		
		public int weight()
		{
			return weight;
		}
	}
}
