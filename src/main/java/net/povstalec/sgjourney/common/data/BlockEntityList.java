package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Transporter;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

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
	protected HashMap<UUID, Transporter> transporterMap = new HashMap<UUID, Transporter>();
	
	/**
	 * Adds Stargate to Stargate Network
	 * @param stargate
	 * @return Optional containing Stargate that got added if successful, empty optional if unsuccessful
	 */
	public Optional<Stargate> addStargate(AbstractStargateEntity stargate)
	{
		Address.Immutable address = stargate.get9ChevronAddress().immutable();
		
		if(address.getLength() != 8)
			return Optional.empty();
		
		if(this.stargateMap.containsKey(address))
			return Optional.of(this.stargateMap.get(address)); // Returns an existing Stargate
		
		if(stargate.getLevel() == null)
			return Optional.empty();
		
		if(stargate.getBlockPos() == null)
			return Optional.empty();
		
		Stargate savedStargate = new SGJourneyStargate(stargate);
		
		this.stargateMap.put(address, savedStargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Stargate " + address.toString() + " to BlockEntityList");
		
		return Optional.of(savedStargate);
	}
	
	public Optional<Transporter> addTransporter(AbstractTransporterEntity transporterEntity)
	{
		if(transporterEntity.getID() == null)
			transporterEntity.setID(transporterEntity.generateID());
		
		UUID id = transporterEntity.getID();
		
		if(this.transporterMap.containsKey(id))
			return Optional.of(this.transporterMap.get(id)); // Returns an existing Transporter
		
		if(transporterEntity.getLevel() == null)
			return Optional.empty();
		
		if(transporterEntity.getBlockPos() == null)
			return Optional.empty();
		
		Transporter transporter = new Transporter(transporterEntity);
		
		this.transporterMap.put(id, transporter);
		
		this.setDirty();
		
		StargateJourney.LOGGER.debug("Added Transporter " + id + " to BlockEntityList");
		
		return Optional.of(transporter);
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
				stargates.put(stargateID.toString(), stargate.serializeNBT());
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
			transportersTag.put(ringsID.toString(), transporter.serialize());
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
		StargateJourney.LOGGER.debug("Deserializing Transporters");
		// Transport Rings deserialization for legacy reasons
		if(blockEntityList.contains(TRANSPORT_RINGS))
		{
			CompoundTag transportRingsTag = blockEntityList.getCompound(TRANSPORT_RINGS);
			
			transportRingsTag.getAllKeys().stream().forEach(transportRings ->
			{
				//StargateJourney.LOGGER.info("Deserializing Transport Rings " + transportRings);
				Transporter transporter = Transporter.deserialize(server, transportRings, transportRingsTag.getCompound(transportRings));

				if(!this.transporterMap.containsKey(transporter.getID()))
					this.transporterMap.put(transporter.getID(), transporter);
			});
		}
		
		CompoundTag transportersTag = blockEntityList.getCompound(TRANSPORTERS);
		
		transportersTag.getAllKeys().stream().forEach(transporterString ->
		{
			//StargateJourney.LOGGER.info("Deserializing Transporter " + transporterString);
			Transporter transporter = Transporter.deserialize(server, transporterString, transportersTag.getCompound(transporterString));
			
			if(!this.transporterMap.containsKey(transporter.getID()))
				this.transporterMap.put(transporter.getID(), transporter);
		});
		
		StargateJourney.LOGGER.debug("Finished deserializing Transporters");
	}
	
	//================================================================================================
	
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
