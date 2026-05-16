package net.povstalec.sgjourney.common.items.crystals;

import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CrystalCache
{
	public static final Type[] ALL = new Type[] { Type.CONTROL, Type.MEMORY, Type.MATERIALIZATION, Type.ENERGY, Type.TRANSFER, Type.COMMUNICATION };
	
	public enum Type
	{
		CONTROL,
		MEMORY,
		MATERIALIZATION,
		ENERGY,
		TRANSFER,
		COMMUNICATION
	}
	
	@Nullable
	private Crystals<ControlCrystalItem> controlCrystals = null;
	@Nullable
	private Crystals<MemoryCrystalItem> memoryCrystals = null;
	@Nullable
	private Crystals<MaterializationCrystalItem> materializationCrystals = null;
	@Nullable
	private Crystals<EnergyCrystalItem> energyCrystals = null;
	@Nullable
	private Crystals<TransferCrystalItem> transferCrystals = null;
	@Nullable
	private Crystals<CommunicationCrystalItem> communicationCrystals = null;
	
	public CrystalCache(Type... supportedTypes)
	{
		for(Type type : supportedTypes)
		{
			switch(type)
			{
				case CONTROL:
					controlCrystals = new Crystals<>();
					break;
				case MEMORY:
					memoryCrystals = new Crystals<>();
					break;
				case MATERIALIZATION:
					materializationCrystals = new Crystals<>();
					break;
				case ENERGY:
					energyCrystals = new Crystals<>();
					break;
				case TRANSFER:
					transferCrystals = new Crystals<>();
					break;
				case COMMUNICATION:
					communicationCrystals = new Crystals<>();
					break;
				default:
			}
		}
	}
	
	@Nullable
	public Crystals<ControlCrystalItem> controlCrystals()
	{
		return controlCrystals;
	}
	
	@Nullable
	public Crystals<MemoryCrystalItem> memoryCrystals()
	{
		return memoryCrystals;
	}
	
	@Nullable
	public Crystals<MaterializationCrystalItem> materializationCrystals()
	{
		return materializationCrystals;
	}
	
	@Nullable
	public Crystals<EnergyCrystalItem> energyCrystals()
	{
		return energyCrystals;
	}
	
	@Nullable
	public Crystals<TransferCrystalItem> transferCrystals()
	{
		return transferCrystals;
	}
	
	@Nullable
	public Crystals<CommunicationCrystalItem> communicationCrystals()
	{
		return communicationCrystals;
	}
	
	public void addCrystal(int slot, AbstractCrystalItem crystal)
	{
		if(controlCrystals != null && crystal instanceof ControlCrystalItem controlCrystal)
			addControlCrystal(slot, controlCrystal);
		else if(memoryCrystals != null && crystal instanceof MemoryCrystalItem memoryCrystal)
			addMemoryCrystal(slot, memoryCrystal);
		else if(materializationCrystals != null && crystal instanceof MaterializationCrystalItem materializationCrystal)
			addMaterializationCrystal(slot, materializationCrystal);
		else if(energyCrystals != null && crystal instanceof EnergyCrystalItem energyCrystal)
			addEnergyCrystal(slot, energyCrystal);
		else if(transferCrystals != null && crystal instanceof TransferCrystalItem transferCrystal)
			addTransferCrystal(slot, transferCrystal);
		else if(communicationCrystals != null && crystal instanceof CommunicationCrystalItem communicationCrystal)
			addCommunicationCrystal(slot, communicationCrystal);
	}
	
	public void addControlCrystal(int slot, ControlCrystalItem controlCrystal)
	{
		if(controlCrystals != null)
			controlCrystals.addCrystal(slot, controlCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Control Crystals!");
	}
	
	public void addMemoryCrystal(int slot, MemoryCrystalItem memoryCrystal)
	{
		if(memoryCrystals != null)
			memoryCrystals.addCrystal(slot, memoryCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Memory Crystals!");
	}
	
	public void addMaterializationCrystal(int slot, MaterializationCrystalItem materializationCrystal)
	{
		if(materializationCrystals != null)
			materializationCrystals.addCrystal(slot, materializationCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Materialization Crystals!");
	}
	
	public void addEnergyCrystal(int slot, EnergyCrystalItem energyCrystal)
	{
		if(energyCrystals != null)
			energyCrystals.addCrystal(slot, energyCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Energy Crystals!");
	}
	
	public void addTransferCrystal(int slot, TransferCrystalItem transferCrystal)
	{
		if(transferCrystals != null)
			transferCrystals.addCrystal(slot, transferCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Transfer Crystals!");
	}
	
	public void addCommunicationCrystal(int slot, CommunicationCrystalItem communicationCrystal)
	{
		if(communicationCrystals != null)
			communicationCrystals.addCrystal(slot, communicationCrystal);
		else
			StargateJourney.LOGGER.error("This crystal cache does not support Communication Crystals!");
	}
	
	public void reset()
	{
		if(controlCrystals != null)
			controlCrystals.reset();
		if(memoryCrystals != null)
			memoryCrystals.reset();
		if(materializationCrystals != null)
			materializationCrystals.reset();
		if(energyCrystals != null)
			energyCrystals.reset();
		if(transferCrystals != null)
			transferCrystals.reset();
		if(communicationCrystals != null)
			communicationCrystals.reset();
	}
	
	public static class Crystals<T extends AbstractCrystalItem>
	{
		private int regularCount = 0;
		private int advancedCount = 0;
		private final Map<Integer, T> crystals = new HashMap<>();
		
		public void addCrystal(int slot, T crystal)
		{
			crystals.put(slot, crystal);
			
			if(crystal.isAdvanced())
				advancedCount++;
			else
				regularCount++;
		}
		
		public List<Integer> getSlots()
		{
			return crystals.keySet().stream().toList();
		}
		
		public void forEach(BiConsumer<Integer, T> consumer)
		{
			crystals.forEach(consumer);
		}
		
		public int count()
		{
			return crystals.size();
		}
		
		public int count(boolean advanced)
		{
			return advanced ? advancedCount : regularCount;
		}
		
		public void reset()
		{
			crystals.clear();
			regularCount = 0;
			advancedCount = 0;
		}
	}
}
