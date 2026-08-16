package net.povstalec.sgjourney.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.factions.AbstractFaction;
import net.povstalec.sgjourney.common.sgjourney.factions.GoauldFaction;
import net.povstalec.sgjourney.common.sgjourney.factions.JaffaBurgers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class Factions extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-factions";
	
	private static final String GOAULD = "goauld";
	
	private MinecraftServer server;
	
	private List<AbstractFaction> factions = new ArrayList<>();
	
	
	
	public void tickFactions(int ticks)
	{
		for(AbstractFaction faction : factions)
		{
			faction.tickFaction(server, ticks);
		}
		
		JaffaBurgers.handleJaffaBurgers(server, ticks);
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		JaffaBurgers.trySerialize(tag);
		tag.put(GOAULD, serializeGoauldFactions());
		
		return tag;
	}
	
	private CompoundTag serializeGoauldFactions()
	{
		CompoundTag factionsTag = new CompoundTag();
		
		//TODO
		
		return factionsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		JaffaBurgers.tryDeserialize(server, tag);
		deserializeGoauldFactions(tag.getCompound(GOAULD));
	}
	
	private void deserializeGoauldFactions(CompoundTag tag)
	{
		//TODO
	}
	
	//============================================================================================
	//******************************************Factions******************************************
	//============================================================================================
	
	public Factions(MinecraftServer server)
	{
		this.server = server;
		
		//this.factions.add(new GoauldFaction()); //TODO Add factions through datapacks
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

	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
		return tag;
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
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
}
