package net.povstalec.sgjourney.common.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.AddressRegion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for accessing Stargate Network related data that should persist through Stellar Updates and even deletion of the file Stargate Network is saved in
 */
public class StargateNetworkSettings extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network_settings";
	
	private static final String USE_DATAPACK_ADDRESSES = "use_datapack_addresses"; //TODO For legacy reasons
	private static final String RANDOMIZE_ADDRESSES = "randomize_addresses";
	private static final String GENERATE_RANDOM_ADDRESS_REGIONS = "generate_random_address_regions";
	private static final String RANDOM_ADDRESS_FROM_SEED = "random_address_from_seed";
	private static final String PRIORITIZE_PRIMARY_STARGATES = "prioritize_primary_stargates";
	
	private static final String PRIMARY_ADDRESSES = "primary_addresses";
	
	@Nullable
	private Boolean randomizeAddresses = null;
	@Nullable
	private Boolean generateRandomAddressRegions = null;
	@Nullable
	private Boolean randomAddressFromSeed = null;
	@Nullable
	private Boolean prioritizePrimaryStargates = null;
	
	private final Map<ResourceKey<AddressRegion>, Address.Immutable> primaryAddresses = new HashMap<>();
	
	//============================================================================================
	//******************************************Versions******************************************
	//============================================================================================
	
	public boolean randomizeAddresses()
	{
		if(randomizeAddresses == null)
			return CommonStargateNetworkConfig.randomize_addresses.get();
		
		return randomizeAddresses;
	}
	
	public boolean generateRandomAddressRegions()
	{
		if(generateRandomAddressRegions == null)
			return CommonStargateNetworkConfig.generate_random_address_regions.get();
		
		return generateRandomAddressRegions;
	}
	
	public boolean randomAddressFromSeed()
	{
		if(randomAddressFromSeed == null)
			return CommonStargateNetworkConfig.random_addresses_from_seed.get();
		
		return randomAddressFromSeed;
	}
	
	public boolean prioritizePrimaryStargates()
	{
		if(prioritizePrimaryStargates == null)
			return CommonStargateNetworkConfig.primary_stargate.get();
		
		return prioritizePrimaryStargates;
	}
	
	
	
	public void setRandomizeAddresses(boolean randomizeAddresses)
	{
		this.randomizeAddresses = randomizeAddresses;
		this.setDirty();
	}
	
	public void setGenerateRandomAddressRegions(boolean generateRandomAddressRegions)
	{
		this.generateRandomAddressRegions = generateRandomAddressRegions;
		this.setDirty();
	}
	
	public void setRandomAddressFromSeed(boolean randomAddressFromSeed)
	{
		this.randomAddressFromSeed = randomAddressFromSeed;
		this.setDirty();
	}
	
	public void setPrioritizePrimaryStargates(boolean prioritizePrimaryStargates)
	{
		this.prioritizePrimaryStargates = prioritizePrimaryStargates;
		this.setDirty();
	}
	
	
	
	public void setPrimaryAddress(ResourceKey<AddressRegion> addressRegionKey, @Nullable Address.Immutable primaryAddress)
	{
		this.primaryAddresses.put(addressRegionKey, primaryAddress);
		this.setDirty();
	}
	
	@Nullable
	public Address.Immutable getPrimaryAddress(ResourceKey<AddressRegion> addressRegionKey)
	{
		return this.primaryAddresses.get(addressRegionKey);
	}
	
	//============================================================================================
	//**********************************Stargate Network Settings*********************************
	//============================================================================================
	
	public static StargateNetworkSettings create()
	{
		return new StargateNetworkSettings();
	}
	
	private void deserializePrimaryAddresses(CompoundTag tag)
	{
		for(String key : tag.getAllKeys())
		{
			ResourceKey<AddressRegion> addressRegionKey = Conversion.stringToAddressRegionKey(key);
			Address.Immutable address = new Address.Immutable(tag.getIntArray(key));
			
			if(addressRegionKey != null && address.getType() == Address.Type.ADDRESS_9_CHEVRON)
				primaryAddresses.put(addressRegionKey, address);
		}
	}
	
	public static StargateNetworkSettings load(CompoundTag tag)
	{
		StargateNetworkSettings settings = create();
		
		if(tag.contains(RANDOMIZE_ADDRESSES))
			settings.randomizeAddresses = tag.getBoolean(RANDOMIZE_ADDRESSES);
		else if(tag.contains(USE_DATAPACK_ADDRESSES))
			settings.randomizeAddresses = !tag.getBoolean(USE_DATAPACK_ADDRESSES);
		
		if(tag.contains(GENERATE_RANDOM_ADDRESS_REGIONS))
			settings.generateRandomAddressRegions = tag.getBoolean(GENERATE_RANDOM_ADDRESS_REGIONS);
		
		if(tag.contains(RANDOM_ADDRESS_FROM_SEED))
			settings.randomAddressFromSeed = tag.getBoolean(RANDOM_ADDRESS_FROM_SEED);
		
		if(tag.contains(PRIORITIZE_PRIMARY_STARGATES))
			settings.prioritizePrimaryStargates = tag.getBoolean(PRIORITIZE_PRIMARY_STARGATES);
		
		settings.deserializePrimaryAddresses(tag.getCompound(PRIMARY_ADDRESSES));
		
		return settings;
	}
	
	private CompoundTag serializePrimaryAddresses()
	{
		CompoundTag tag = new CompoundTag();
		for(Map.Entry<ResourceKey<AddressRegion>, Address.Immutable> entry : primaryAddresses.entrySet())
		{
			entry.getValue().saveToCompoundTag(tag, entry.getKey().location().toString());
		}
		return tag;
	}

	public @NotNull CompoundTag save(CompoundTag tag)
	{
		if(randomizeAddresses != null)
			tag.putBoolean(RANDOMIZE_ADDRESSES, randomizeAddresses);
		
		if(generateRandomAddressRegions != null)
			tag.putBoolean(GENERATE_RANDOM_ADDRESS_REGIONS, generateRandomAddressRegions);
		
		if(randomAddressFromSeed != null)
			tag.putBoolean(RANDOM_ADDRESS_FROM_SEED, randomAddressFromSeed);
		
		if(prioritizePrimaryStargates != null)
			tag.putBoolean(PRIORITIZE_PRIMARY_STARGATES, prioritizePrimaryStargates);
		
		tag.put(PRIMARY_ADDRESSES, serializePrimaryAddresses());
		
		return tag;
	}

    @Nonnull
	public static StargateNetworkSettings get(Level level)
    {
        if(level.isClientSide())
            throw new RuntimeException("Don't access this client-side!");
    	
    	return StargateNetworkSettings.get(level.getServer());
    }

    @Nonnull
	public static StargateNetworkSettings get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(StargateNetworkSettings::load, StargateNetworkSettings::create, FILE_NAME);
    }
}
