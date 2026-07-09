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
import net.povstalec.sgjourney.common.config.CommonCableConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.data.ConduitNetworks;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CableBlockEntity extends BlockEntity
{
	public static final String NETWORK_ID = "network_id";
	
	
	public final SGJourneyEnergy energyStorage = createEnergyStorage();
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
	
	private int networkID = 0;
	private ConduitNetworks.ConduitNetwork cableNetwork = null;
	
	private List<Direction> connectedSides = null;
	
	public CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
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
	
	public abstract long maxTransfer();
	
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
				IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).resolve().orElse(null);
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
			BlockEntity blockEntity = level.getBlockEntity(outputPos);
			if(blockEntity != null)
			{
				IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).resolve().orElse(null);
				
				if(energy != null && energy.canReceive() && energy.receiveEnergy(Integer.MAX_VALUE, true) > 0)
					outputs++;
			}
		}
		return outputs;
	}
	
	public long outputEnergy(Direction direction, long toOutput, boolean simulate, boolean zeroPointEnergy)
	{
		BlockEntity blockEntity = level.getBlockEntity(getBlockPos().relative(direction));
		if(blockEntity == null)
			return 0;
		
		IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).resolve().orElse(null);
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
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ENERGY && side != null)
			return lazyEnergyHandler.cast();
		
		return super.getCapability(capability, side);
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
