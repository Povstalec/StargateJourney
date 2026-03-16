package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	private static final String INCORRECT_FILE_NAME = StargateJourney.MODID + "-block_enties"; //I wish there was a way to replace this
	
	public static final String STARGATES = "Stargates";
	public static final String TRANSPORT_RINGS = "TransportRings";
	public static final String TRANSPORTERS = "Transporters"; //TODO Replace TransportRings with this
	
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
		
		SG stargate = stargateEntity.getStargateType().constructStargate();
		stargate.loadFromBlockEntity(stargateEntity);
		
		this.stargateMap.put(address, stargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Stargate {} to BlockEntityList", address);
		
		return stargate;
	}
	
	public void removeStargate(Address address)
	{
		if(!this.stargateMap.containsKey(address))
		{
			StargateJourney.LOGGER.error("{} not found in BlockEntityList", address);
			return;
		}
		this.stargateMap.remove(address);
		StargateJourney.LOGGER.debug("Removed Stargate {} from BlockEntityList", address);
		setDirty();
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
	
	@Nullable
	public Stargate getStargate(Address address)
	{
		return stargateMap.get(address);
	}
	
	public Address.Immutable generate9ChevronAddress()
	{
		Random random = new Random();
		Address.Immutable address;
		do
		{
			address = Address.Immutable.randomAddress(8, 36, random.nextLong());
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
		if(transporterEntity.getID() == null)
			transporterEntity.setID(generateTransporterID());
		
		TransporterID transporterID = transporterEntity.getID();
		
		if(this.transporterMap.containsKey(transporterID))
			return this.transporterMap.get(transporterID); // Returns an existing Transporter
		
		if(transporterEntity.getLevel() == null)
			return null;
		
		T transporter = transporterEntity.getTransporterType().constructTransporter();
		transporter.loadFromBlockEntity(transporterEntity);
		
		this.transporterMap.put(transporterID, transporter);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Transporter {} to BlockEntityList", transporterID);
		
		return transporter;
	}
	
	public void removeTransporter(TransporterID transporterID)
	{
		if(!this.transporterMap.containsKey(transporterID))
		{
			StargateJourney.LOGGER.error("{} not found in BlockEntityList", transporterID);
			return;
		}
		this.transporterMap.remove(transporterID);
		StargateJourney.LOGGER.debug("Removed Transporter {} from BlockEntityList", transporterID);
		setDirty();
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
	
	@Nullable
	public Transporter getTransporter(TransporterID transporterID)
	{
		return transporterMap.get(transporterID);
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
				stargate.serializeNBT(stargateTag);
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
			transporter.serializeNBT(transporterTag);
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
				BlockEntityStargate<?> stargate = stargateEntity.getStargateType().constructStargate();
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
	
	private void tryDeserializeStargate(MinecraftServer server, String addressString, CompoundTag stargateTag)
	{
		Address.Immutable address = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(addressString));
		if(address.getType() == Address.Type.ADDRESS_9_CHEVRON)
		{
			StargateType.getTypeFromTag(stargateTag).ifPresentOrElse(type ->
			{
				Stargate stargate = type.constructStargate();
				stargate.deserializeNBT(server, address, stargateTag);
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
		
		stargates.getAllKeys().forEach(addressString -> tryDeserializeStargate(server, addressString, stargates.getCompound(addressString)));
		
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
				
				BlockEntityTransporter<?> transporter = transporterEntity.getTransporterType().constructTransporter();
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
	
	private void tryDeserializeTransporter(MinecraftServer server, String id, CompoundTag transporterTag)
	{
		if(TransporterID.canBeTransformedToID(id))
		{
			try
			{
				TransporterID.Immutable transporterID = new TransporterID.Immutable(id);
				
				TransporterType.getTypeFromTag(transporterTag).ifPresentOrElse(type ->
				{
					Transporter transporter = type.constructTransporter();
					transporter.deserializeNBT(server, transporterID, transporterTag);
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
		//TODO Transport Rings deserialization for legacy reasons
		CompoundTag transportersTag = blockEntityList.getCompound(blockEntityList.contains(TRANSPORT_RINGS) ? TRANSPORT_RINGS : TRANSPORTERS);
		
		transportersTag.getAllKeys().forEach(transporterString -> tryDeserializeTransporter(server, transporterString, transportersTag.getCompound(transporterString)));
		
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

	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
		return tag;
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
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), INCORRECT_FILE_NAME);
    }
}
