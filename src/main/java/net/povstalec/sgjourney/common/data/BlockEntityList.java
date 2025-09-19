package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
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
	
	public static final String STARGATES = "stargates";
	public static final String TRANSPORTERS = "transporters";
	
	private MinecraftServer server;
	
	protected HashMap<Address.Immutable, Stargate> stargateMap = new HashMap<Address.Immutable, Stargate>();
	protected HashMap<UUID, Transporter> transporterMap = new HashMap<UUID, Transporter>();
	
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	/**
	 * Adds Stargate to Stargate Network
	 * @param stargate
	 * @return Optional containing Stargate that got added if successful, empty optional if unsuccessful
	 */
	@Nullable
	public Stargate addStargate(AbstractStargateEntity stargate)
	{
		Address.Immutable address = stargate.get9ChevronAddress().immutable();
		
		if(address.getLength() != 8)
			return null;
		
		if(this.stargateMap.containsKey(address))
			return this.stargateMap.get(address); // Returns an existing Stargate
		
		if(stargate.getLevel() == null)
			return null;
		
		if(stargate.getBlockPos() == null)
			return null;
		
		Stargate savedStargate = new SGJourneyStargate(stargate);
		
		this.stargateMap.put(address, savedStargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Stargate " + address.toString() + " to BlockEntityList");
		
		return savedStargate;
	}
	
	public void removeStargate(Address.Immutable id)
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
	
	public boolean containsStargate(Address.Immutable address)
	{
		return stargateMap.containsKey(address);
	}
	
	@Nullable
	public Stargate getStargate(Address.Immutable address)
	{
		if(address.getLength() != 8)
			return null;
		
		Stargate stargate = stargateMap.get(address);
		
		return stargate;
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
			transporterEntity.setID(transporterEntity.generateID());
		
		UUID id = transporterEntity.getID();
		
		if(this.transporterMap.containsKey(id))
			return this.transporterMap.get(id); // Returns an existing Transporter
		
		if(transporterEntity.getLevel() == null)
			return null;
		
		if(transporterEntity.getBlockPos() == null)
			return null;
		
		SGJourneyTransporter transporter = new SGJourneyTransporter(transporterEntity);
		
		this.transporterMap.put(id, transporter);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Transporter " + id + " to BlockEntityList");
		
		return transporter;
	}
	
	public void removeTransporter(UUID id)
	{
		if(!this.transporterMap.containsKey(id))
		{
			StargateJourney.LOGGER.error(id + " not found in BlockEntityList");
			return;
		}
		this.transporterMap.remove(id);
		StargateJourney.LOGGER.debug("Removed Transporter " + id + " from BlockEntityList");
		setDirty();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<UUID, Transporter> getTransporters()
	{
		return (HashMap<UUID, Transporter>) transporterMap.clone();
	}
	
	@Nullable
	public Transporter getTransporter(UUID id)
	{
		Transporter transporter = transporterMap.get(id);
		
		return transporter;
	}
	
	public void printTransporters()
	{
		System.out.println("[Transporters]");
		this.transporterMap.entrySet().stream().forEach(transporterEntry ->
		{
			System.out.println("- " + transporterEntry.getKey() + " " + transporterEntry.getValue().toString());
		});
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
			transportersTag.put(ringsID.toString(), transporter.serializeNBT(server.registryAccess()));
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
			Address.Immutable address = new Address(stargateAddress).immutable();
			
			Stargate stargate = new SGJourneyStargate();
			stargate.deserializeNBT(server, address, stargates.getCompound(stargateAddress));
			
			if(stargate != null)
				this.stargateMap.put(address, stargate);
		});
		
		StargateJourney.LOGGER.debug("Finished deserializing Stargates");
	}
	
	private void deserializeTransporters(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.info("Deserializing Transporters");
		
		CompoundTag transportersTag = blockEntityList.getCompound(TRANSPORTERS);
		
		transportersTag.getAllKeys().stream().forEach(transporterString ->
		{
			Transporter transporter = tryDeserializeTransporter(server, transporterString, transportersTag.getCompound(transporterString));
			
			if(transporter != null && !this.transporterMap.containsKey(transporter.getID()))
				this.transporterMap.put(transporter.getID(), transporter);
		});
		
		StargateJourney.LOGGER.debug("Finished deserializing Transporters");
	}
	
	private static Transporter tryDeserializeTransporter(MinecraftServer server, String id, CompoundTag transporterTag)
	{
		try
		{
			SGJourneyTransporter transporter = new SGJourneyTransporter();
			transporter.deserializeNBT(server, UUID.fromString(id), transporterTag, server.registryAccess());
			return transporter;
		}
		catch(IllegalArgumentException e)
		{
			ResourceKey<Level> dimension = Conversion.stringToDimension(transporterTag.getString(Transporter.DIMENSION));
			BlockPos blockPos = Conversion.intArrayToBlockPos(transporterTag.getIntArray(Transporter.COORDINATES));
			
			if(dimension != null && blockPos != null && server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractTransporterEntity transporter)
			{
				transporter.setID(transporter.generateID());
				return new SGJourneyTransporter(transporter);
			}
			else
				return null;
		}
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
