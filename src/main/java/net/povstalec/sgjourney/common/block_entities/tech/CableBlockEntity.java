package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.blocks.tech.CableBlock;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.data.ConduitNetworks;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class CableBlockEntity extends BlockEntity
{
	public static final String NETWORK_ID = "network_id";
	
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
	//private Set<BlockPos> outputs;
	
	private int networkID = 0;
	private ConduitNetworks.ConduitNetwork cableNetwork = null;
	
	private List<Direction> connectedSides = null;
	
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
	
	@Override
	public void invalidateCaps()
	{
		lazyEnergyHandler.invalidate();
		super.invalidateCaps();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.networkID = nbt.getInt(NETWORK_ID);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putInt(NETWORK_ID, this.networkID);
	}
	
	public long transfer()
	{
		return Long.MAX_VALUE; // TODO Add config to change this
	}
	
	public final SGJourneyEnergy ENERGY_STORAGE = new SGJourneyEnergy(transfer(), transfer(), transfer())
	{
		@Override
		public long receiveLongEnergy(long maxReceive, boolean simulate)
		{
			return transferEnergy(Math.min(transfer(), maxReceive), simulate);
		}
		
		@Override
		public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
		{
			return transferEnergy(Math.min(transfer(), maxReceive), simulate);
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
	
	public void setNetworkID(int networkID)
	{
		this.networkID = networkID;
		this.cableNetwork = null;
	}
	
	public int networkID()
	{
		return networkID;
	}
	
	@Nullable
	private ConduitNetworks.ConduitNetwork getNetwork()
	{
		if(cableNetwork != null)
			return cableNetwork;
		
		if(networkID == 0)
			return null;
		
		cableNetwork = ConduitNetworks.get(getLevel()).getCableNetwork(networkID);
		return cableNetwork;
	}
	
	public void update()
	{
		this.connectedSides = null;
	}
	
	public boolean isOutput()
	{
		for(Direction direction : getConnectedSides())
		{
			BlockPos outputPos = getBlockPos().relative(direction);
			BlockEntity blockEntity =  level.getBlockEntity(outputPos);
			if(blockEntity != null && !(blockEntity instanceof CableBlockEntity))
			{
				IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).resolve().orElse(null);
				if(energy != null)
				{
					if(energy.canReceive())
						return true;
				}
			}
		}
		
		return false;
	}
	
	public List<Direction> getConnectedSides()
	{
		if(this.connectedSides == null)
		{
			this.connectedSides = new ArrayList<>();
			for(Direction direction : Direction.values())
			{
				if(CableBlock.connectionTypeSide(getLevel(), getBlockPos(), direction) == CableBlock.ConnectorType.BLOCK)
					this.connectedSides.add(direction);
			}
		}
		
		return this.connectedSides;
	}
	
	public int validOutputs()
	{
		int outputs = 0;
		for(Direction direction : getConnectedSides())
		{
			BlockPos outputPos = getBlockPos().relative(direction);
			BlockEntity blockEntity =  level.getBlockEntity(outputPos);
			if(blockEntity != null)
			{
				IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).resolve().orElse(null);
				
				if(energy.canReceive())
				{
					if(energy instanceof SGJourneyEnergy sgjourneyEnergy && sgjourneyEnergy.getTrueEnergyStored() < sgjourneyEnergy.getTrueMaxEnergyStored())
						outputs++;
					else if(energy.getEnergyStored() < energy.getMaxEnergyStored())
						outputs++;
				}
			}
		}
		return outputs;
	}
	
	public long outputEnergy(Direction direction, long toOutput, boolean simulate)
	{
		BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(direction));
		if(blockEntity == null)
			return 0;
		
		IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).resolve().orElse(null);
		if(energy == null || !energy.canReceive())
			return 0;
		
		if(energy instanceof SGJourneyEnergy sgjourneyEnergy)
			return sgjourneyEnergy.receiveLongEnergy(toOutput, simulate);
		else
			return energy.receiveEnergy(SGJourneyEnergy.regularEnergy(toOutput), simulate);
	}
	
	/**
	 * Transfers energy through the local grid
	 * @param toTransfer Energy to transfer
	 * @param simulate If TRUE, the insertion will only be simulated.
	 * @return amount of energy successfully transferred
	 */
	public long transferEnergy(long toTransfer, boolean simulate)
	{
		ConduitNetworks.ConduitNetwork cableNetwork = getNetwork();
		if(cableNetwork == null)
			return 0;
		
		return cableNetwork.transferEnergy(getLevel(), toTransfer, simulate);
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
