package net.povstalec.sgjourney.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.CableBlockEntity;
import net.povstalec.sgjourney.common.blocks.tech.CableBlock;
import net.povstalec.sgjourney.common.sgjourney.Address;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class is designed to save all Conduit Networks (primarily Cables) along with their coordinates and dimensions.
 * @author Povstalec
 *
 */
public class ConduitNetworks extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-conduits";
	
	public static final String CABLES = "cables";
	
	private MinecraftServer server;
	
	protected HashMap<Integer, ConduitNetwork> cableMap = new HashMap<>();
	
	private int createCableNetwork()
	{
		Random random = new Random();
		int id = 0;
		
		while(id == 0 || cableMap.containsKey(id))
		{
			id = random.nextInt();
		}
		
		return id;
	}
	
	@Nullable
	public ConduitNetwork getCableNetwork(int id)
	{
		if(id == 0)
			return null;
		
		return cableMap.get(id);
	}
	
	public void update(Level level, BlockPos pos)
	{
		int id = createCableNetwork();
		Set<CableBlockEntity> cables = findConnectedCables(level, pos);
		
		if(!cables.isEmpty())
		{
			ConduitNetwork newNetwork = new ConduitNetwork(id);
			for(CableBlockEntity cable : cables)
			{
				cableMap.remove(cable.networkID()); // Remove any old networks, because we'll be replacing them with a new one
				
				if(cable.isOutput())
					newNetwork.outputs.add(cable.getBlockPos());
				cable.setNetworkID(id);
			}
			
			if(!newNetwork.outputs.isEmpty())
				cableMap.put(id, newNetwork);
		}
		this.setDirty();
	}
	
	public void removeCable(Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof CableBlockEntity cable)
			cableMap.remove(cable.networkID());
	}
	
	public void printConduits()
	{
		for(Map.Entry<Integer, ConduitNetwork> entry : cableMap.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	//================================================================================================
	
	private CompoundTag serializeCables()
	{
		CompoundTag cables = new CompoundTag();
		
		this.cableMap.forEach((id, cableNetwork) ->
		{
			if(!cableNetwork.outputs.isEmpty())
				cables.put(id.toString(), cableNetwork.serializeNBT());
		});
		
		return cables;
	}
	
	public CompoundTag serialize()
	{
		CompoundTag conduitNetworks = new CompoundTag();
		CompoundTag cables = serializeCables();
		
		conduitNetworks.put(CABLES, cables);
		
		return conduitNetworks;
	}
	
	private void deserializeCables(CompoundTag blockEntityList)
	{
		CompoundTag cables = blockEntityList.getCompound(CABLES);
		for(String idString : cables.getAllKeys())
		{
			int id = Integer.valueOf(idString);
			ConduitNetwork cableNetwork = new ConduitNetwork(id);
			cableNetwork.deserializeNBT(cables.getCompound(idString));
			this.cableMap.put(id, cableNetwork);
		}
	}
	
	public void deserialize(CompoundTag tag)
	{
		deserializeCables(tag);
	}
	
	//================================================================================================
	
	public ConduitNetworks(MinecraftServer server)
	{
		this.server = server;
	}

	public static ConduitNetworks create(MinecraftServer server)
	{
		return new ConduitNetworks(server);
	}
	
	public static ConduitNetworks load(MinecraftServer server, CompoundTag tag)
	{
		ConduitNetworks data = create(server);

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
	public static ConduitNetworks get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return ConduitNetworks.get(level.getServer());
	}

    @Nonnull
	public static ConduitNetworks get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
	
	
	/**
	 * Breadth First Search through the cable network. Returns a set of all found Cable Block Entities
	 * @param level
	 * @param startingPos
	 */
	public static Set<CableBlockEntity> findConnectedCables(Level level, BlockPos startingPos)
	{
		Set<CableBlockEntity> cables = new HashSet<>();
		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new LinkedList<>();
		queue.add(startingPos);
		
		while(!queue.isEmpty())
		{
			BlockPos pos = queue.remove();
			visited.add(pos);
			
			if(level.getBlockState(pos).getBlock() instanceof CableBlock)
			{
				if(level.getBlockEntity(pos) instanceof CableBlockEntity cable)
					cables.add(cable);
				
				for(Direction direction : Direction.values())
				{
					BlockPos visitPos = pos.relative(direction);
					
					if(!visited.contains(visitPos))
						queue.add(visitPos);
				}
			}
		}
		
		return cables;
	}
	
	
	
	public static class ConduitNetwork implements INBTSerializable<CompoundTag>
	{
		private int id;
		private Set<BlockPos> outputs;
		
		public ConduitNetwork(int id)
		{
			this.id = id;
			this.outputs = new HashSet<>();
		}
		
		/**
		 * Transfers energy through the local grid
		 * @param level Level in which we're transferring
		 * @param toTransfer Energy to transfer
		 * @param simulate If TRUE, the insertion will only be simulated.
		 * @return amount of energy successfully transferred
		 */
		public long transferEnergy(Level level, long toTransfer, boolean simulate)
		{
			if(toTransfer <= 0 || outputs.isEmpty())
				return 0;
			
			int totalOutputs = 0;
			
			for(BlockPos pos : outputs)
			{
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if(blockEntity instanceof CableBlockEntity cable)
					totalOutputs += cable.validOutputs();
			}
			
			if(totalOutputs == 0)
				return 0;
			
			long transferred = 0;
			long amount = toTransfer / totalOutputs;
			
			for(BlockPos pos : outputs)
			{
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if(blockEntity instanceof CableBlockEntity cable)
				{
					for(Direction direction : cable.getConnectedSides())
					{
						transferred += cable.outputEnergy(direction, amount, simulate);
					}
				}
			}
			
			return transferred;
		}
		
		@Override
		public String toString()
		{
			return outputs.toString();
		}
		
		@Override
		public CompoundTag serializeNBT()
		{
			CompoundTag tag = new CompoundTag();
			int i = 0;
			for(BlockPos pos : outputs)
			{
				tag.putIntArray(String.valueOf(i),  new int[]{pos.getX(), pos.getY(), pos.getZ()});
				i++;
			}
			
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundTag tag)
		{
			for(String key : tag.getAllKeys())
			{
				int[] xyz = tag.getIntArray(key);
				outputs.add(new BlockPos(xyz[0], xyz[1], xyz[2]));
			}
		}
	}
}
