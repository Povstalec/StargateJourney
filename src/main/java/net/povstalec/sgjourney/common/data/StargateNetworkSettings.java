package net.povstalec.sgjourney.common.data;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;

public class StargateNetworkSettings extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network_settings";
	
	private static final String USE_DATAPACK_ADDRESSES = "UseDatapackAddresses";
	private static final String GENERATE_RANDOM_SOLAR_SYSTEMS = "GenerateRandomSolarSystems";
	private static final String RANDOM_ADDRESS_FROM_SEED = "RandomAddressFromSeed";
	
	private CompoundTag stargateNetworkSettings = new CompoundTag();
	
	//============================================================================================
	//******************************************Versions******************************************
	//============================================================================================
	
	public void updateSettings()
	{
		CompoundTag network = stargateNetworkSettings.copy();

		StargateJourney.LOGGER.info("Attempting to update settings");
		
		if(!network.contains(USE_DATAPACK_ADDRESSES))
		{
			boolean useDatapackAddresses = CommonStargateNetworkConfig.use_datapack_addresses.get();
			StargateJourney.LOGGER.info("Use Datapack Addresses updated to " + useDatapackAddresses);
			stargateNetworkSettings.putBoolean(USE_DATAPACK_ADDRESSES, useDatapackAddresses);
		}
		
		if(!network.contains(GENERATE_RANDOM_SOLAR_SYSTEMS))
		{
			boolean generateRandomSolarSystems = CommonStargateNetworkConfig.generate_random_solar_systems.get();
			StargateJourney.LOGGER.info("Generate random Solar Systems updated to " + generateRandomSolarSystems);
			stargateNetworkSettings.putBoolean(GENERATE_RANDOM_SOLAR_SYSTEMS, generateRandomSolarSystems);
		}
		
		if(!network.contains(RANDOM_ADDRESS_FROM_SEED))
		{
			boolean randomAddressFromSeed = CommonStargateNetworkConfig.random_addresses_from_seed.get();
			StargateJourney.LOGGER.info("Random Address from Seed updated to " + randomAddressFromSeed);
			stargateNetworkSettings.putBoolean(RANDOM_ADDRESS_FROM_SEED, randomAddressFromSeed);
		}

		this.setDirty();
	}
	
	public boolean useDatapackAddresses()
	{
		return this.stargateNetworkSettings.copy().getBoolean(USE_DATAPACK_ADDRESSES);
	}
	
	public boolean generateRandomSolarSystems()
	{
		return this.stargateNetworkSettings.copy().getBoolean(GENERATE_RANDOM_SOLAR_SYSTEMS);
	}
	
	public boolean randomAddressFromSeed()
	{
		return this.stargateNetworkSettings.copy().getBoolean(RANDOM_ADDRESS_FROM_SEED);
	}
	
	//============================================================================================
	//**********************************Stargate Network Settings*********************************
	//============================================================================================
	
	public static StargateNetworkSettings create(MinecraftServer server)
	{
		return new StargateNetworkSettings();
	}
	
	public static StargateNetworkSettings load(MinecraftServer server, CompoundTag tag)
	{
		StargateNetworkSettings data = create(server);
		
		data.stargateNetworkSettings = tag;
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.stargateNetworkSettings.copy();
		
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
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
}
