package net.povstalec.sgjourney.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.factions.GoauldFaction;

import javax.annotation.Nonnull;

public final class Factions extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-factions";
	
	private static final String GOAULD = "goauld";
	
	private MinecraftServer server;
	
	// TODO Goa'uld factions
	private static GoauldFaction  goauldFaction = new GoauldFaction();
	
	
	
	public void tickFactions(int ticks)
	{
		//goauldFaction.tickFaction(server, ticks);
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		tag.put(GOAULD, serializeGoauldFactions());
		return tag;
	}
	
	private CompoundTag serializeGoauldFactions()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		//TODO
		
		return connectionsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		deserializeConnections(tag.getCompound(GOAULD));
	}
	
	private void deserializeConnections(CompoundTag tag)
	{
		//TODO
	}
	
	//============================================================================================
	//******************************************Factions******************************************
	//============================================================================================
	
	public Factions(MinecraftServer server)
	{
		this.server = server;
	}
	
	public static Factions create(MinecraftServer server)
	{
		return new Factions(server);
	}
	
	public static Factions load(MinecraftServer server, CompoundTag tag)
	{
		Factions data = create(server);
		
		data.server = server;
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		tag = serialize();
		
		return tag;
	}
	
	public static SavedData.Factory<Factions> dataFactory(MinecraftServer server)
	{
		return new SavedData.Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
	}

    @Nonnull
	public static Factions get(Level level)
    {
        if(level.isClientSide())
            throw new RuntimeException("Don't access this client-side!");
    	
    	return Factions.get(level.getServer());
    }

    @Nonnull
	public static Factions get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
    }
}
