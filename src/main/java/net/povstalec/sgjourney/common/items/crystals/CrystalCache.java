package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public abstract class CrystalCache<T extends BlockEntity>
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
	
	protected final T parent;
	
	protected boolean isDirty = true;
	
	protected final int maxNeighbors;
	protected final Map<Integer, Slot<?>> slots;
	
	protected final Set<Type> supportedTypes = new HashSet<>();
	
	@Nullable
	protected Crystals<ControlCrystalItem> controlCrystals = null;
	@Nullable
	protected Crystals<MemoryCrystalItem> memoryCrystals = null;
	@Nullable
	protected Crystals<MaterializationCrystalItem> materializationCrystals = null;
	@Nullable
	protected Crystals<EnergyCrystalItem> energyCrystals = null;
	@Nullable
	protected Crystals<TransferCrystalItem> transferCrystals = null;
	@Nullable
	protected Crystals<CommunicationCrystalItem> communicationCrystals = null;
	
	public CrystalCache(T parent, int size, int maxNeighbors, Type... supportedTypes)
	{
		this.parent = parent;
		
		this.maxNeighbors = maxNeighbors;
		this.slots = new HashMap<>(size);
		
		for(Type type : supportedTypes)
		{
			this.supportedTypes.add(type);
			switch(type)
			{
				case CONTROL -> controlCrystals = new Crystals<>(type);
				case MEMORY -> memoryCrystals = new Crystals<>(type);
				case MATERIALIZATION -> materializationCrystals = new Crystals<>(type);
				case ENERGY -> energyCrystals = new Crystals<>(type);
				case TRANSFER -> transferCrystals = new Crystals<>(type);
				case COMMUNICATION -> communicationCrystals = new Crystals<>(type);
			}
		}
	}
	
	public int size()
	{
		return slots.size();
	}
	
	public void setDirty()
	{
		isDirty = true;
	}
	
	public boolean isDirty()
	{
		return isDirty;
	}
	
	public boolean isSupported(Type type)
	{
		return supportedTypes.contains(type);
	}
	
	public abstract int[] getNeighbors(int index);
	
	private void reset()
	{
		slots.clear();
		
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
		
		onReset();
	}
	
	protected abstract void onReset();
	
	protected abstract void fetchCrystals();
	
	protected abstract void updateFromCrystals();
	
	public final void recalculateCrystals()
	{
		isDirty = false;
		reset();
		fetchCrystals();
		updateFromCrystals();
		
		//TODO Handle everything here
	}
	
	public boolean hasCrystal(int index)
	{
		return slots.containsKey(index);
	}
	
	@Nullable
	public Slot<?> getSlot(int index)
	{
		return slots.get(index);
	}
	
	@Nullable
	public Crystals<ControlCrystalItem> controlCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return controlCrystals;
	}
	
	@Nullable
	public Crystals<MemoryCrystalItem> memoryCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return memoryCrystals;
	}
	
	@Nullable
	public Crystals<MaterializationCrystalItem> materializationCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return materializationCrystals;
	}
	
	@Nullable
	public Crystals<EnergyCrystalItem> energyCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return energyCrystals;
	}
	
	@Nullable
	public Crystals<TransferCrystalItem> transferCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return transferCrystals;
	}
	
	@Nullable
	public Crystals<CommunicationCrystalItem> communicationCrystals()
	{
		if(isDirty)
			recalculateCrystals();
		
		return communicationCrystals;
	}
	
	public void addCrystal(int slot, AbstractCrystalItem crystal)
	{
		switch(crystal.getType())
		{
			case CONTROL:
				if(controlCrystals != null)
					addControlCrystal(slot, (ControlCrystalItem) crystal);
				break;
			case MEMORY:
				if(memoryCrystals != null)
					addMemoryCrystal(slot, (MemoryCrystalItem) crystal);
				break;
			case MATERIALIZATION:
				if(materializationCrystals != null)
					addMaterializationCrystal(slot, (MaterializationCrystalItem) crystal);
				break;
			case ENERGY:
				if(energyCrystals != null)
					addEnergyCrystal(slot, (EnergyCrystalItem) crystal);
				break;
			case TRANSFER:
				if(transferCrystals != null)
					addTransferCrystal(slot, (TransferCrystalItem) crystal);
				break;
			case COMMUNICATION:
				if(communicationCrystals != null)
					addCommunicationCrystal(slot, (CommunicationCrystalItem) crystal);
				break;
		}
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
	
	public class Crystals<C extends AbstractCrystalItem>
	{
		public final Type type;
		
		private int regularCount = 0;
		private int advancedCount = 0;
		private final List<Slot<C>> crystals = new ArrayList<>(1);
		
		public Crystals(Type type)
		{
			this.type = type;
		}
		
		public void addCrystal(int index, C crystal)
		{
			Slot<C> slot = new Slot<>(index, crystal);
			slots.put(index, slot);
			crystals.add(slot);
			
			if(crystal.isAdvanced())
				advancedCount++;
			else
				regularCount++;
		}
		
		public List<Slot<C>> getSlots()
		{
			return crystals;
		}
		
		public void forEach(Consumer<Slot<C>> consumer)
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
	
	public class Slot<C extends AbstractCrystalItem>
	{
		public final int index;
		public final C crystal;
		
		public Slot(int index, C crystal)
		{
			this.index = index;
			this.crystal = crystal;
		}
		
		public void forEachNeighbor(Consumer<Slot<?>> consumer)
		{
			for(int neighborIndex : getNeighbors(index))
			{
				if(hasCrystal(neighborIndex))
					consumer.accept(getSlot(neighborIndex));
			}
		}
		
		public void forEachNeighborOfType(Type type, Consumer<Slot<?>> consumer)
		{
			for(int neighborIndex : getNeighbors(index))
			{
				if(hasCrystal(neighborIndex) && getSlot(neighborIndex).crystal.getType() == type)
					consumer.accept(getSlot(neighborIndex));
			}
		}
		
		public int countNeighborsOfType(Type type)
		{
			int count = 0;
			
			for(int neighborIndex : getNeighbors(index))
			{
				if(hasCrystal(neighborIndex) && getSlot(neighborIndex).crystal.getType() == type)
					count++;
			}
			
			return count;
		}
		
		public int countNeighborsOfType(Type type, boolean advanced)
		{
			int count = 0;
			
			for(int neighborIndex : getNeighbors(index))
			{
				Slot<?> slot = getSlot(neighborIndex);
				if(slot != null && slot.crystal.getType() == type && slot.crystal.isAdvanced() == advanced)
					count++;
			}
			
			return count;
		}
	}
	
	public static abstract class Generic9<T extends BlockEntity> extends CrystalCache<T>
	{
		public static final int[][] NEIGHBORS = {
				{1, 3, 5, 7},
				{8, 2},
				{0, 1, 3},
				{2, 4},
				{0, 3, 5},
				{4, 6},
				{0, 5, 7},
				{6, 8},
				{0, 7, 1},
				{}
		};
		
		public Generic9(T parent, Type... supportedTypes)
		{
			super(parent, 9, 4, supportedTypes);
		}
		
		@Override
		public int[] getNeighbors(int index)
		{
			return switch(index)
			{
				case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> NEIGHBORS[index];
				default -> NEIGHBORS[9];
			};
		}
	}
	
	public static abstract class Generic6<T extends BlockEntity> extends CrystalCache<T>
	{
		public static final int[][] NEIGHBORS = {
				{1, 2},
				{0, 3},
				{0, 3, 4},
				{1, 2, 5},
				{2, 5},
				{3, 4},
				{}
		};
		
		public Generic6(T parent, Type... supportedTypes)
		{
			super(parent, 6, 3, supportedTypes);
		}
		
		@Override
		public int[] getNeighbors(int index)
		{
			return switch(index)
			{
				case 0, 1, 2, 3, 4, 5 -> NEIGHBORS[index];
				default -> NEIGHBORS[6];
			};
		}
	}
}
