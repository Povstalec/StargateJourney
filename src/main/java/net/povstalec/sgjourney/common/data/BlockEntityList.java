package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;
import net.povstalec.sgjourney.common.sgjourney.transporter.BlockEntityTransporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.SGJourneyTransporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;

/**
 * This class is designed to save all Block Entities along with their coordinates and dimensions. 
 * @author Povstalec
 *
 */
public class BlockEntityList extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-block_entities";
	
	public static final String STARGATES = "stargates";
	public static final String TRANSPORTERS = "transporters";
	
	private MinecraftServer server;
	
	protected HashMap<Address.Immutable, Stargate> stargateMap = new HashMap<Address.Immutable, Stargate>();
	protected HashMap<TransporterID, Transporter> transporterMap = new HashMap<TransporterID, Transporter>();
	
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	/**
	 * Adds Stargate to Stargate Network
	 * @param stargateEntity Stargate Block Entity to add to the Stargate Network
	 * @return Stargate that got added if successful, null if unsuccessful
	 */
	@Nullable
	public <SG extends BlockEntityStargate<?>> Stargate addStargate(AbstractStargateEntity<SG> stargateEntity)
	{
		Address.Immutable address = stargateEntity.get9ChevronAddress();
		
		if(address.getType() != Address.Type.ADDRESS_9_CHEVRON)
		{
			StargateJourney.LOGGER.error("Could not add Stargate to network because address {} is not a 9-chevron address", address);
			return null;
		}
		
		if(this.stargateMap.containsKey(address))
			return this.stargateMap.get(address); // Returns an existing Stargate
		
		if(stargateEntity.getLevel() == null)
		{
			StargateJourney.LOGGER.error("Could not add Stargate to network because level is null");
			return null;
		}
		
		SG stargate = stargateEntity.getStargateType().constructStargate(server);
		stargate.loadFromBlockEntity(stargateEntity);
		
		this.stargateMap.put(address, stargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Stargate {} to BlockEntityList", address);
		
		return stargate;
	}
	
	public boolean removeStargate(Address address)
	{
		if(!this.stargateMap.containsKey(address))
		{
			StargateJourney.LOGGER.error("Stargate {} not found in BlockEntityList", address);
			return false;
		}
		this.stargateMap.remove(address);
		StargateJourney.LOGGER.debug("Removed Stargate {} from BlockEntityList", address);
		setDirty();
		return true;
	}
	
	public void printStargates()
	{
		System.out.println("[Stargates]");
		this.stargateMap.entrySet().stream().forEach(stargateEntry ->
		{
			System.out.println("- " + stargateEntry.getValue().toString());
		});
	}

    @SuppressWarnings("unchecked")
	public HashMap<Address.Immutable, Stargate> getStargates()
	{
		return (HashMap<Address.Immutable, Stargate>) stargateMap.clone();
	}
	
	public int getStargateCount()
	{
		return stargateMap.size();
	}
	
	public boolean containsStargate(Address address)
	{
		return stargateMap.containsKey(address);
	}
	
	/**
	 * @param address 9-Chevron Address of the Stargate being searched for
	 * @return Stargate with the specified 9-Chevron Address or null if none is found
	 */
	@Nullable
	public Stargate getStargate(Address address)
	{
		return stargateMap.get(address);
	}
	
	/**
	 * @param clazz Class of the Stargate being searched for
	 * @param address 9-Chevron Address of the Stargate being searched for
	 * @return Stargate with the specified 9-Chevron Address or null if none is found
	 * @throws ClassCastException if the found Stargate is not an instance of the provided class.
	 */
	@Nullable
	public <SG extends Stargate> SG getStargate(Class<SG> clazz, Address address)
	{
		Stargate stargate = getStargate(address);
		if(clazz.isInstance(stargate))
			return clazz.cast(stargate);
		
		throw new ClassCastException("Stargate " + address + " is not an instance of class " + clazz + "!");
	}
	
	public Address.Immutable generate9ChevronAddress(RandomSource randomSource)
	{
		Address.Immutable address;
		do
		{
			address = Address.Immutable.randomAddress(8, Address.ADDRESS_GENERATION_SYMBOLS, randomSource.nextLong());
		} while(containsStargate(address));
		
		return address;
	}
	
	@Nullable
	public Stargate getRandomStargate(RandomSource randomSource)
	{
		int size = this.stargateMap.size();
		
		if(size == 0)
			return null;
		
		return (Stargate) this.stargateMap.values().toArray()[randomSource.nextInt(0, size)];
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	/**
	 * Adds Stargate to Transporter Network
	 * @param transporterEntity Transporter Block Entity to add to the Transporter Network
	 * @return Transporter that got added if successful, null if unsuccessful
	 */
	public <T extends BlockEntityTransporter<?>> Transporter addTransporter(AbstractTransporterEntity<T> transporterEntity)
	{
		if(!transporterEntity.getID().isValid())
			transporterEntity.setID(generateTransporterID());
		
		TransporterID transporterID = transporterEntity.getID();
		
		if(this.transporterMap.containsKey(transporterID))
			return this.transporterMap.get(transporterID); // Returns an existing Transporter
		
		if(transporterEntity.getLevel() == null)
			return null;
		
		T transporter = transporterEntity.getTransporterType().constructTransporter(server);
		transporter.loadFromBlockEntity(transporterEntity);
		
		this.transporterMap.put(transporterID, transporter);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Transporter {} to BlockEntityList", transporterID);
		
		return transporter;
	}
	
	public boolean removeTransporter(TransporterID transporterID)
	{
		if(!this.transporterMap.containsKey(transporterID))
		{
			StargateJourney.LOGGER.error("Transporter {} not found in BlockEntityList", transporterID);
			return false;
		}
		this.transporterMap.remove(transporterID);
		StargateJourney.LOGGER.debug("Removed Transporter {} from BlockEntityList", transporterID);
		setDirty();
		return true;
	}
	
	public void printTransporters()
	{
		System.out.println("[Transporters]");
		this.transporterMap.forEach((key, value) -> System.out.println("- " + key + " " + value.toString()));
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<TransporterID, Transporter> getTransporters()
	{
		return (HashMap<TransporterID, Transporter>) transporterMap.clone();
	}
	
	public int getTransporterCount()
	{
		return transporterMap.size();
	}
	
	public boolean containsTransporter(TransporterID transporterID)
	{
		return transporterMap.containsKey(transporterID);
	}
	
	/**
	 * @param transporterID Transporter ID of the Transporter being searched for
	 * @return Transporter with the specified Transporter ID or null if none is found
	 */
	@Nullable
	public Transporter getTransporter(TransporterID transporterID)
	{
		return transporterMap.get(transporterID);
	}
	
	/**
	 * @param clazz Class of the Transporter being searched for
	 * @param transporterID Transporter ID of the Transporter being searched for
	 * @return Transporter with the specified Transporter ID or null if none is found
	 * @throws ClassCastException if the found Transporter is not an instance of the provided class.
	 */
	@Nullable
	public <T extends Transporter> T getTransporter(Class<T> clazz, TransporterID transporterID)
	{
		Transporter transporter = getTransporter(transporterID);
		if(clazz.isInstance(transporter))
			return clazz.cast(transporter);
		
		throw new ClassCastException("Transporter " + transporterID + " is not an instance of class " + clazz + "!");
	}
	
	public TransporterID.Immutable generateTransporterID()
	{
		Random random = new Random();
		TransporterID.Immutable transporterID;
		do
		{
			transporterID = TransporterID.Immutable.randomID(random.nextLong());
		} while(containsTransporter(transporterID));
		
		return transporterID;
	}
	
	//================================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag blockEntityList = new CompoundTag();
		CompoundTag stargates = serializeStargates();
		CompoundTag transportRings = serializeTransporters();
		
		blockEntityList.put(STARGATES, stargates);
		blockEntityList.put(TRANSPORTERS, transportRings);
		
		return blockEntityList;
	}
	
	private CompoundTag serializeStargates()
	{
		CompoundTag stargates = new CompoundTag();
		
		this.stargateMap.forEach((stargateID, stargate) -> 
		{
			if(stargate != null)
			{
				CompoundTag stargateTag = new CompoundTag();
				stargate.serializeNBT(stargateTag, server.registryAccess());
				StargateType.addTypeToTag(stargate.getStargateType(), stargateTag);
				
				stargates.put(stargateID.toString(), stargateTag);
			}
			else
				StargateJourney.LOGGER.error("Cannot serialize Stargate {} because it's null", stargateID);
		});
		
		return stargates;
	}
	
	private CompoundTag serializeTransporters()
	{
		CompoundTag transportersTag = new CompoundTag();
		
		this.transporterMap.forEach((ringsID, transporter) -> 
		{
			CompoundTag transporterTag = new CompoundTag();
			transporter.serializeNBT(transporterTag, server.registryAccess());
			TransporterType.addTypeToTag(transporter.getTransporterType(), transporterTag);
			
			transportersTag.put(ringsID.toString(), transporterTag);
		});
		
		return transportersTag;
	}
	
	public void deserialize(CompoundTag tag)
	{
		CompoundTag blockEntityList = tag.copy();
		
		deserializeStargates(blockEntityList);
		deserializeTransporters(blockEntityList);
	}
	
	// Stargates
	
	private void loadStargateFromBlockEntity(Address.Immutable address, CompoundTag stargateTag)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(stargateTag.getString(Stargate.DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(stargateTag.getIntArray(BlockEntityStargate.COORDINATES));
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && blockPos != null)
		{
			if(level.getBlockEntity(blockPos) instanceof AbstractStargateEntity<? extends BlockEntityStargate<?>> stargateEntity)
			{
				BlockEntityStargate<?> stargate = stargateEntity.getStargateType().constructStargate(server);
				stargate.loadFromBlockEntity((stargateEntity));
				this.stargateMap.put(address, stargate);
				setDirty(); // A pretty important change happened
			}
			else
				StargateJourney.LOGGER.error("No Stargate Entity at the location specified in Compound Tag");
		}
		else
			StargateJourney.LOGGER.error("No valid position or Dimension data found in Compound Tag");
	}
	
	private void tryDeserializeStargate(String addressString, CompoundTag stargateTag)
	{
		Address.Immutable address = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(addressString));
		if(address.getType() == Address.Type.ADDRESS_9_CHEVRON)
		{
			StargateType.getTypeFromTag(stargateTag).ifPresentOrElse(type ->
			{
				Stargate stargate = type.constructStargate(server);
				stargate.deserializeNBT(address, stargateTag, server.registryAccess());
				this.stargateMap.put(address, stargate);
			}, () ->
			{
				StargateJourney.LOGGER.error("Compound Tag {} does not contain Stargate Type entry, attempting to retrieve data from Block Entity", addressString);
				loadStargateFromBlockEntity(address, stargateTag);
			});
		}
	}
	
	private void deserializeStargates(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.debug("Deserializing Stargates");
		CompoundTag stargates = blockEntityList.getCompound(STARGATES);
		
		stargates.getAllKeys().forEach(addressString -> tryDeserializeStargate(addressString, stargates.getCompound(addressString)));
		
		StargateJourney.LOGGER.debug("Finished deserializing Stargates");
	}
	
	// Transporters
	
	private void loadTransporterFromBlockEntity(TransporterID.Immutable transporterID, CompoundTag transporterTag, boolean setEntityID)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(transporterTag.getString(Transporter.DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(transporterTag.getIntArray(BlockEntityTransporter.COORDINATES));
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && blockPos != null)
		{
			if(level.getBlockEntity(blockPos) instanceof AbstractTransporterEntity<? extends BlockEntityTransporter<?>> transporterEntity)
			{
				if(setEntityID)
					transporterEntity.setID(transporterID);
				
				BlockEntityTransporter<?> transporter = transporterEntity.getTransporterType().constructTransporter(server);
				transporter.loadFromBlockEntity(transporterEntity);
				this.transporterMap.put(transporterID, transporter);
				setDirty(); // A pretty important change happened
			}
			else
				StargateJourney.LOGGER.error("No Transporter Entity at the location specified in Compound Tag");
		}
		else
			StargateJourney.LOGGER.error("No valid position or Dimension data found in Compound Tag");
	}
	
	private void tryDeserializeTransporter(String id, CompoundTag transporterTag)
	{
		if(TransporterID.canBeTransformedToID(id))
		{
			try
			{
				TransporterID.Immutable transporterID = new TransporterID.Immutable(id);
				
				TransporterType.getTypeFromTag(transporterTag).ifPresentOrElse(type ->
				{
					Transporter transporter = type.constructTransporter(server);
					transporter.deserializeNBT(transporterID, transporterTag, server.registryAccess());
					this.transporterMap.put(transporterID, transporter);
				}, () ->
				{
					StargateJourney.LOGGER.error("Compound Tag {} does not contain Transporter Type entry, attempting to retrieve data from Block Entity", id);
					loadTransporterFromBlockEntity(transporterID, transporterTag, false);
				});
			}
			catch(IllegalArgumentException e) { StargateJourney.LOGGER.error("Cannot deserialize Transporter {} because it is invalid", id, e); }
		}
		else
		{
			StargateJourney.LOGGER.error("{} cannot be transformed to Transporter ID, generating new one", id);
			loadTransporterFromBlockEntity(generateTransporterID(), transporterTag, true);
		}
	}
	
	private void deserializeTransporters(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.debug("Deserializing Transporters");
		CompoundTag transportersTag = blockEntityList.getCompound(TRANSPORTERS);
		
		transportersTag.getAllKeys().forEach(transporterString -> tryDeserializeTransporter(transporterString, transportersTag.getCompound(transporterString)));
		
		StargateJourney.LOGGER.debug("Finished deserializing Transporters");
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public BlockEntityList(MinecraftServer server)
	{
		this.server = server;
	}

	public static BlockEntityList create(MinecraftServer server)
	{
		return new BlockEntityList(server);
	}
	
	public static BlockEntityList load(MinecraftServer server, CompoundTag tag)
	{
		BlockEntityList data = create(server);

		data.server = server;
		
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		tag = serialize();
		
		return tag;
	}

	public static SavedData.Factory<BlockEntityList> dataFactory(MinecraftServer server)
	{
		return new SavedData.Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
	}
	
	@Nonnull
	public static BlockEntityList get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return BlockEntityList.get(level.getServer());
	}

    @Nonnull
	public static BlockEntityList get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
    }
}
