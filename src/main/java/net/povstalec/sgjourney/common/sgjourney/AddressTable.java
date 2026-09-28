package net.povstalec.sgjourney.common.sgjourney;

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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public record AddressTable(boolean includeGeneratedAddresses, List<WeightedAddress> dimensions)
{
	public static final ResourceLocation ADDRESS_TABLES_LOCATION = StargateJourney.sgjourneyLocation("address_table");
	public static final ResourceKey<Registry<AddressTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(ADDRESS_TABLES_LOCATION);
	public static final Codec<ResourceKey<AddressTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<AddressTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.BOOL.fieldOf("include_generated_addresses").forGetter(AddressTable::includeGeneratedAddresses),
		WeightedAddress.CODEC.listOf().fieldOf("addresses").forGetter(AddressTable::dimensions)
	).apply(instance, AddressTable::new));
	
	@Nullable
	public static AddressTable getAddressTable(MinecraftServer server, @Nullable ResourceKey<AddressTable> addressTable)
	{
		if(addressTable == null)
			return null;
		
		final RegistryAccess registries = server.registryAccess();
		final Registry<AddressTable> registry = registries.registryOrThrow(AddressTable.REGISTRY_KEY);
		
		return registry.get(addressTable);
	}
	
	@Nullable
	public static Address randomAddress(@Nullable ServerLevel level, @Nullable AddressTable addressTable)
	{
		if(level == null || addressTable == null)
			return null;
		
		WeightedAddress output = null;
		int totalWeight = 0;
		
		if(addressTable.includeGeneratedAddresses())
		{
			Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> localGalaxyMap = Universe.get(level).getGalaxiesFromDimension(level.dimension());
			
			if(localGalaxyMap != null)
			{
				Universe universe = Universe.get(level);
				
				// Dimensions in generated Address Regions (not added by Datapacks) or those that specify they should be added to an Address Table
				List<ResourceKey<Level>> generatedDimensions = Universe.get(level).getDimensionsAddedToAddressTables(spaceLocation ->
				{
					// Filter it so that only Dimensions reachable from the starting Dimension by a 7-Chevron Address are included
					for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> localGalaxyEntry : localGalaxyMap.entrySet())
					{
						Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> otherGalaxyMap = universe.getGalaxiesFromAddressRegionKey(spaceLocation.getAddressRegionKey());
						if(otherGalaxyMap != null)
						{
							for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> otherGalaxyEntry : otherGalaxyMap.entrySet())
							{
								if(localGalaxyEntry.getKey().equals(otherGalaxyEntry.getKey()))
									return true;
							}
						}
					}
					
					return false;
				});
				
				for(ResourceKey<Level> dimensionKey : generatedDimensions)
				{
					totalWeight += 1;
					
					if(level.getRandom().nextFloat() <= 1F / totalWeight)
						output = new WeightedAddress(new Address.Dimension(dimensionKey), 1);
				}
			}
		}
		
		// Dimensions in Address Regions added by Datapacks
		List<WeightedAddress> datapackDimensions = addressTable.dimensions();
		for(WeightedAddress address : datapackDimensions)
		{
			// Only add the Address if the Dimension exists
			if(address.addressDimension().right().isPresent() || level.getServer().getLevel(address.addressDimension().left().get().getDimension()) != null)
			{
				totalWeight += address.weight();
				
				if(level.getRandom().nextFloat() <= (float) address.weight() / totalWeight)
					output = address;
			}
		}
		
		if(output == null)
			return null;
		
		if(output.addressDimension().right().isPresent())
			return output.addressDimension().right().get().clone();
		
		return output.addressDimension().left().get().clone();
	}
	
	
	
	public record WeightedAddress(Either<Address.Dimension, Address.Immutable> addressDimension, int weight)
	{
		public static final Codec<WeightedAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(Address.Dimension.CODEC, Address.Immutable.CODEC).fieldOf("address").forGetter(weightedAddress -> weightedAddress.addressDimension),
			Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("weight", 1).forGetter(weightedAddress -> weightedAddress.weight)
		).apply(instance, WeightedAddress::new));
		
		public WeightedAddress(Address.Dimension dimensionAddress, int weight)
		{
			this(Either.left(dimensionAddress), weight);
		}
		
		public WeightedAddress(Address.Immutable address, int weight)
		{
			this(Either.right(address), weight);
		}
	}
}
