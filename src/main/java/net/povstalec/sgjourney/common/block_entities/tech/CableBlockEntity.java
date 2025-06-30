package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public abstract class CableBlockEntity extends BlockEntity
{
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
	private Map<BlockPos, Direction> outputs = null;
	
	public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public CableBlockEntity(BlockPos pos, BlockState state)
	{
		this(BlockEntityInit.NAQUADAH_CABLE.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	}
	
	public long transfer()
	{
		return Long.MAX_VALUE;
	}
	
	public final SGJourneyEnergy ENERGY_STORAGE = new SGJourneyEnergy(this.transfer(), this.transfer(), this.transfer())
	{
		@Override
		public long receiveLongEnergy(long maxReceive, boolean simulate)
		{
			return transferEnergy(maxReceive, simulate);
		}
		
		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public boolean canReceive()
		{
			return true;
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			setChanged();
		}
	};
	
	public void bfs(BlockPos startingPos, Consumer<CableBlockEntity> consumer)
	{
		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new LinkedList<>();
		queue.add(startingPos);
		
		while(!queue.isEmpty())
		{
			BlockPos pos = queue.remove();
			visited.add(pos);
			
			if(level.getBlockEntity(pos) instanceof CableBlockEntity cable)
			{
				consumer.accept(cable);
				
				for(Direction direction : Direction.values())
				{
					BlockPos visitPos = cable.getBlockPos().relative(direction);
					
					if(!visited.contains(visitPos))
						queue.add(visitPos);
				}
			}
		}
	}
	
	public void tryCacheOutputs()
	{
		if(this.outputs != null)
			return;
		
		this.outputs = new HashMap<>();
		
		bfs(getBlockPos(), cable ->
		{
			for(Direction direction : Direction.values())
			{
				BlockPos outputPos = cable.getBlockPos().relative(direction);
				BlockEntity blockEntity =  level.getBlockEntity(outputPos);
				if(blockEntity != null && !(blockEntity instanceof CableBlockEntity))
				{
					blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).ifPresent(energy ->
					{
						if(energy.canReceive() && !this.outputs.containsKey(outputPos))
							this.outputs.put(outputPos, direction);
					});
				}
			}
		});
	}
	
	public void invalidate()
	{
		bfs(getBlockPos(), cable -> cable.outputs = null);
	}
	
	/**
	 * Transfers energy through the local grid
	 * @param toTransfer Energy to transfer
	 * @return amount of energy successfully transferred
	 */
	public long transferEnergy(long toTransfer, boolean simulate)
	{
		if(toTransfer <= 0)
			return 0;
		
		tryCacheOutputs();
		if(outputs.isEmpty())
			return 0;
		
		long transferred = 0;
		long amount = toTransfer / outputs.size();
		
		for(Map.Entry<BlockPos, Direction> entry : outputs.entrySet())
		{
			BlockEntity blockEntity = level.getBlockEntity(entry.getKey());
			if(blockEntity != null)
			{
				IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, entry.getValue()).orElse(null);
				if(energy != null && energy.canReceive())
				{
					int transfer = energy.receiveEnergy((int) Math.min(Integer.MAX_VALUE, amount), simulate);
					transferred += transfer;
				}
			}
		}
		
		return transferred;
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ENERGY)
			return lazyEnergyHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	
	
	public static class NaquadahCable extends CableBlockEntity
	{
		public NaquadahCable(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.NAQUADAH_CABLE.get(), pos, state);
		}
	}
}
