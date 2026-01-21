package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.SGJourneyTransporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

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
	 * @param stargate
	 * @return Stargate that got added if successful, null if unsuccessful
	 */
	@Nullable
	public Stargate addStargate(AbstractStargateEntity stargate)
	{
		Address.Immutable address = stargate.get9ChevronAddress();
		
		if(address.getType() != Address.Type.ADDRESS_9_CHEVRON)
		{
			StargateJourney.LOGGER.error("Could not add Stargate to network because address " + address.toString() + " is not a 9-chevron address");
			return null;
		}
		
		if(this.stargateMap.containsKey(address))
			return this.stargateMap.get(address); // Returns an existing Stargate
		
		if(stargate.getLevel() == null)
		{
			StargateJourney.LOGGER.error("Could not add Stargate to network because level is null");
			return null;
		}
		
		Stargate savedStargate = new SGJourneyStargate(stargate);
		
		this.stargateMap.put(address, savedStargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Stargate " + address.toString() + " to BlockEntityList");
		
		return savedStargate;
	}
	
	public void removeStargate(Address id)
	{
		if(!this.stargateMap.containsKey(id))
		{
			StargateJourney.LOGGER.error(id + " not found in BlockEntityList");
			return;
		}
		this.stargateMap.remove(id);
		StargateJourney.LOGGER.debug("Removed Stargate " + id + " from BlockEntityList");
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
		while(true)
		{
			address = Address.Immutable.randomAddress(8, 36, random.nextLong());
			
			if(!containsStargate(address))
				break;
		}
		
		return address;
	}
	
	@Nullable
	public Stargate getRandomStargate(long seed)
	{
		int size = this.stargateMap.size();
		
		if(size < 1)
			return null;
		
		Random random = new Random(seed);
		
		int randomValue = random.nextInt(0, size);
		
		Stargate randomStargate = (Stargate) this.stargateMap.entrySet().stream().toArray()[randomValue];
		
		return randomStargate;
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	public Transporter addTransporter(AbstractTransporterEntity transporterEntity)
	{
		if(transporterEntity.getID() == null)
			transporterEntity.setID(generateTransporterID());
		
		TransporterID transporterID = transporterEntity.getID();
		
		if(this.transporterMap.containsKey(transporterID))
			return this.transporterMap.get(transporterID); // Returns an existing Transporter
		
		if(transporterEntity.getLevel() == null)
			return null;
		
		if(transporterEntity.getBlockPos() == null)
			return null;
		
		SGJourneyTransporter transporter = new SGJourneyTransporter(transporterEntity);
		
		this.transporterMap.put(transporterID, transporter);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Transporter " + transporterID + " to BlockEntityList");
		
		return transporter;
	}
	
	public void removeTransporter(TransporterID transporterID)
	{
		if(!this.transporterMap.containsKey(transporterID))
		{
			StargateJourney.LOGGER.error(transporterID + " not found in BlockEntityList");
			return;
		}
		this.transporterMap.remove(transporterID);
		StargateJourney.LOGGER.debug("Removed Transporter " + transporterID + " from BlockEntityList");
		setDirty();
	}
	
	public void printTransporters()
	{
		System.out.println("[Transporters]");
		this.transporterMap.entrySet().stream().forEach(transporterEntry ->
		{
			System.out.println("- " + transporterEntry.getKey() + " " + transporterEntry.getValue().toString());
		});
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<TransporterID, Transporter> getTransporters()
	{
		return (HashMap<TransporterID, Transporter>) transporterMap.clone();
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
		while(true)
		{
			transporterID = TransporterID.Immutable.randomID(random.nextLong());
			if(!containsTransporter(transporterID))
				break;
		}
		
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
				CompoundTag stargateTag = stargate.serializeNBT();
				if(stargateTag != null)
					stargates.put(stargateID.toString(), stargateTag);
				else
					StargateJourney.LOGGER.error("Cannot serialize Stargate " + stargateID + " because the tag is null");
			}
			else
				StargateJourney.LOGGER.error("Cannot serialize Stargate " + stargateID + " because it's null");
		});
		
		return stargates;
	}
	
	private CompoundTag serializeTransporters()
	{
		CompoundTag transportersTag = new CompoundTag();
		
		this.transporterMap.forEach((ringsID, transporter) -> 
		{
			transportersTag.put(ringsID.toString(), transporter.serializeNBT());
		});
		
		return transportersTag;
	}
	
	public void deserialize(CompoundTag tag)
	{
		CompoundTag blockEntityList = tag.copy();
		
		deserializeStargates(blockEntityList);
		deserializeTransporters(blockEntityList);
	}
	
	private void deserializeStargates(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.debug("Deserializing Stargates");
		CompoundTag stargates = blockEntityList.getCompound(STARGATES);
		
		stargates.getAllKeys().stream().forEach(stargateAddress ->
		{
			Address.Immutable address = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(stargateAddress));
			if(address.getType() == Address.Type.ADDRESS_9_CHEVRON)
			{
				Stargate stargate = new SGJourneyStargate();
				stargate.deserializeNBT(server, address, stargates.getCompound(stargateAddress));
				this.stargateMap.put(address, stargate);
			}
		});
		
		StargateJourney.LOGGER.debug("Finished deserializing Stargates");
	}
	
	private void deserializeTransporters(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.debug("Deserializing Transporters");
		//TODO Transport Rings deserialization for legacy reasons
		CompoundTag transportersTag = blockEntityList.getCompound(blockEntityList.contains(TRANSPORT_RINGS) ? TRANSPORT_RINGS : TRANSPORTERS);
		
		transportersTag.getAllKeys().stream().forEach(transporterString ->
		{
			Transporter transporter = tryDeserializeTransporter(server, transporterString, transportersTag.getCompound(transporterString));
			
			if(transporter != null && !this.transporterMap.containsKey(transporter.getID()))
				this.transporterMap.put(transporter.getID(), transporter);
		});
		
		StargateJourney.LOGGER.debug("Finished deserializing Transporters");
	}
	
	private Transporter tryDeserializeTransporter(MinecraftServer server, String id, CompoundTag transporterTag)
	{
		if(TransporterID.canBeTransformedToID(id))
		{
			try
			{
				TransporterID.verifyValidity(TransporterID.idStringToIntArray(id));
				
				SGJourneyTransporter transporter = new SGJourneyTransporter();
				transporter.deserializeNBT(server, new TransporterID.Immutable(id), transporterTag);
				return transporter;
			}
			catch(IllegalArgumentException e) { StargateJourney.LOGGER.error("Cannot deserialize Transporter " + id + " because it is invalid", e); }
		}
		
		ResourceKey<Level> dimension = Conversion.stringToDimension(transporterTag.getString(Transporter.DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(transporterTag.getIntArray(Transporter.COORDINATES));
		
		if(dimension != null && blockPos != null && server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
		{
			transporter.setID(generateTransporterID());
			setDirty();
			return new SGJourneyTransporter(transporter);
		}
		else
			return null;
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
