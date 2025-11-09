package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.blocks.tech.CableBlock;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonCableConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.data.ConduitNetworks;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class CableBlockEntity extends BlockEntity
{
	public static final String NETWORK_ID = "network_id";
	
	protected SGJourneyEnergy ENERGY_STORAGE = createEnergyStorage();
	private Lazy<IEnergyStorage> lazyEnergyHandler = Lazy.of(() -> ENERGY_STORAGE);
	
	private int networkID = 0;
	private ConduitNetworks.ConduitNetwork cableNetwork = null;
	
	private List<Direction> connectedSides = null;
	
	public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		lazyEnergyHandler.invalidate();
		super.invalidateCapabilities();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		this.networkID = tag.getInt(NETWORK_ID);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putInt(NETWORK_ID, this.networkID);
	}
	
	public abstract long maxTransfer();
	
	@Nonnull
	protected SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(maxTransfer(), maxTransfer(), maxTransfer())
		{
			@Override
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				return transferEnergy(Math.min(maxTransfer(), maxReceive), simulate, false);
			}
			
			@Override
			public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
			{
				return transferEnergy(Math.min(maxTransfer(), maxReceive), simulate, true);
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
	}
	
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
				IEnergyStorage energy = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, outputPos, direction.getOpposite());
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
				IEnergyStorage energy = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, outputPos, direction.getOpposite());
				if(energy != null && energy.canReceive())
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
	
	public long outputEnergy(Direction direction, long toOutput, boolean simulate, boolean zeroPointEnergy)
	{
		BlockPos outputPos = getBlockPos().relative(direction);
		BlockEntity blockEntity = level.getBlockEntity(outputPos);
		if(blockEntity == null)
			return 0;
		
		IEnergyStorage energy = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, outputPos, direction.getOpposite());
		if(energy == null || !energy.canReceive())
			return 0;
		
		if(energy instanceof SGJourneyEnergy sgjourneyEnergy)
			return zeroPointEnergy ? sgjourneyEnergy.receiveZeroPointEnergy(toOutput, simulate) : sgjourneyEnergy.receiveLongEnergy(toOutput, simulate);
		else if(zeroPointEnergy && !CommonZPMConfig.other_mods_use_zero_point_energy.get())
			return 0;
		
		return energy.receiveEnergy(SGJourneyEnergy.regularEnergy(toOutput), simulate);
	}
	
	/**
	 * Transfers energy through the local grid
	 * @param toTransfer Energy to transfer
	 * @param simulate If TRUE, the insertion will only be simulated.
	 * @return amount of energy successfully transferred
	 */
	public long transferEnergy(long toTransfer, boolean simulate, boolean zeroPointEnergy)
	{
		if(zeroPointEnergy && !canTransferZeroPointEnergy())
			return 0;
		
		ConduitNetworks.ConduitNetwork cableNetwork = getNetwork();
		if(cableNetwork == null)
			return 0;
		
		return cableNetwork.transferEnergy(getLevel(), toTransfer, simulate, zeroPointEnergy);
	}
	
	public abstract boolean canTransferZeroPointEnergy();
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IEnergyStorage getEnergyHandler(Direction direction)
	{
		return ENERGY_STORAGE;
	}
	
	
	
	public static class NaquadahWire extends CableBlockEntity
	{
		public NaquadahWire(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.NAQUADAH_WIRE.get(), pos, state);
		}
		
		@Override
		public long maxTransfer()
		{
			return CommonCableConfig.naquadah_wire_max_transfer.get();
		}
		
		@Override
		public boolean canTransferZeroPointEnergy()
		{
			return CommonCableConfig.naquadah_wire_transfers_zero_point_energy.get();
		}
	}
	
	public static class SmallNaquadahCable extends CableBlockEntity
	{
		public SmallNaquadahCable(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SMALL_NAQUADAH_CABLE.get(), pos, state);
		}
		
		@Override
		public long maxTransfer()
		{
			return CommonCableConfig.small_naquadah_cable_max_transfer.get();
		}
		
		@Override
		public boolean canTransferZeroPointEnergy()
		{
			return CommonCableConfig.small_naquadah_cable_transfers_zero_point_energy.get();
		}
	}
	
	public static class MediumNaquadahCable extends CableBlockEntity
	{
		public MediumNaquadahCable(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.MEDIUM_NAQUADAH_CABLE.get(), pos, state);
		}
		
		@Override
		public long maxTransfer()
		{
			return CommonCableConfig.medium_naquadah_cable_max_transfer.get();
		}
		
		@Override
		public boolean canTransferZeroPointEnergy()
		{
			return CommonCableConfig.medium_naquadah_cable_transfers_zero_point_energy.get();
		}
	}
	
	public static class LargeNaquadahCable extends CableBlockEntity
	{
		public LargeNaquadahCable(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.LARGE_NAQUADAH_CABLE.get(), pos, state);
		}
		
		@Override
		public long maxTransfer()
		{
			return CommonCableConfig.large_naquadah_cable_max_transfer.get();
		}
		
		@Override
		public boolean canTransferZeroPointEnergy()
		{
			return CommonCableConfig.large_naquadah_cable_transfers_zero_point_energy.get();
		}
	}
}
