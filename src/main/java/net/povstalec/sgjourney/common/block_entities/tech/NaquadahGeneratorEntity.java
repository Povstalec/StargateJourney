package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.tech.NaquadahGeneratorBlock;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;

public abstract class NaquadahGeneratorEntity extends EnergyBlockEntity
{
	private int reactionProgress = 0;
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	
	public NaquadahGeneratorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, true);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.put("Inventory", itemHandler.serializeNBT());
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public boolean hasNaquadah()
	{
		ItemStack stack = this.itemHandler.getStackInSlot(0);
		
		if(stack.getItem() instanceof NaquadahFuelRodItem)
			return NaquadahFuelRodItem.getFuel(stack) > 0;
		
		return false;
	}
	
	public void setReactionProgress(int reactionProgress)
	{
		this.reactionProgress = reactionProgress;
	}
	
	public int getReactionProgress()
	{
		return this.reactionProgress;
	}
	
	public abstract long getReactionTime();

	public abstract long getEnergyPerTick();

	@Nullable
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof NaquadahGeneratorBlock)
		{
			FrontAndTop orientation = gateState.getValue(NaquadahGeneratorBlock.ORIENTATION);
			
			if(orientation.top() == Direction.UP)
				return orientation.front();
			else
				return orientation.top();
		}

		StargateJourney.LOGGER.error("Couldn't find Direction " + this.getBlockPos().toString());
		return null;
	}
	
	@Nullable
	public Direction getBottomDirection()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(state.getBlock() instanceof NaquadahGeneratorBlock)
		{
			FrontAndTop orientation = state.getValue(NaquadahGeneratorBlock.ORIENTATION);
			return orientation.front().getOpposite();
		}

		StargateJourney.LOGGER.error("Couldn't find Direction " + this.getBlockPos().toString());
		return null;
	}
	
	public boolean isActive()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof NaquadahGeneratorBlock)
			return gateState.getValue(NaquadahGeneratorBlock.ACTIVE);

		StargateJourney.LOGGER.error("Couldn't find Active state" + this.getBlockPos().toString());
		return false;
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return lazyItemHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Storage*******************************************
	//============================================================================================
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					switch(slot)
					{
					case 0:
						return stack.getItem() == ItemInit.NAQUADAH_FUEL_ROD.get();
					default: 
						return false;
					}
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
					{
						return stack;
					}
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected boolean isCorrectEnergySide(Direction side)
	{
		Direction direction = getDirection();
		Direction bottom = getBottomDirection();
		
		if(direction != null && bottom != null)
			return side == bottom || side == direction.getClockWise() || side == direction.getCounterClockWise();
		
		return false;
	}
	
	protected boolean receivesEnergy()
	{
		return false;
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	private void doReaction()
	{
		if(!isActive())
			return;
		
		if(this.hasNaquadah() && reactionProgress == 0)
		{
			if(NaquadahFuelRodItem.depleteFuel(this.itemHandler.getStackInSlot(0)))
				this.progressReaction();
			//else
			//	this.itemHandler.extractItem(0, 1, false);
			//TODO Naquadah Reactor should use Refined Naquadah instead of Naquadah Fuel Rods
			//TODO Add Enriched Naquadah
		}
		
		else if(reactionProgress > 0 && reactionProgress < getReactionTime() && getEnergyStored() < capacity() && canReceive(getEnergyPerTick()))
			this.progressReaction();
		
		else if(reactionProgress >= getReactionTime())
			reactionProgress = 0;
	}
	
	private void progressReaction()
	{
		this.generateEnergy(getEnergyPerTick());
		this.reactionProgress++;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, NaquadahGeneratorEntity generator)
	{
		if(level.isClientSide())
			return;
		
		generator.doReaction();
		
		Direction direction = generator.getDirection();
		if(direction != null)
		{
			generator.outputEnergy(generator.getBottomDirection());
			generator.outputEnergy(direction.getClockWise());
			generator.outputEnergy(direction.getCounterClockWise());
		}
		
		generator.updateClient();
	}
	
	
	
	public static class Reactor extends NaquadahGeneratorEntity
	{
		public Reactor(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.NAQUADAH_REACTOR.get(), pos, state);
		}
		
		@Override
		public long getReactionTime()
		{
			return CommonNaquadahGeneratorConfig.naquadah_reactor_reaction_time.get();
		}
		
		@Override
		public long getEnergyPerTick()
		{
			return CommonNaquadahGeneratorConfig.naquadah_reactor_energy_per_tick.get();
		}
		
		@Override
		public long capacity()
		{
			return CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get();
		}
		
		@Override
		public long maxReceive()
		{
			return CommonNaquadahGeneratorConfig.naquadah_reactor_max_transfer.get();
		}
		
		@Override
		public long maxExtract()
		{
			return CommonNaquadahGeneratorConfig.naquadah_reactor_max_transfer.get();
		}
	}
	
	
	
	public static class MarkI extends NaquadahGeneratorEntity
	{
		public MarkI(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), pos, state);
		}
		
		@Override
		public long getReactionTime()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get();
		}
		
		@Override
		public long getEnergyPerTick()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
		}
		
		@Override
		public long capacity()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
		}
		
		@Override
		public long maxReceive()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
		}
		
		@Override
		public long maxExtract()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
		}
	}
	
	
	
	public static class MarkII extends NaquadahGeneratorEntity
	{
		public MarkII(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), pos, state);
		}
		
		@Override
		public long getReactionTime()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_reaction_time.get();
		}
		
		@Override
		public long getEnergyPerTick()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
		}
		
		@Override
		public long capacity()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
		}
		
		@Override
		public long maxReceive()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_max_transfer.get();
		}
		
		@Override
		public long maxExtract()
		{
			return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_max_transfer.get();
		}
	}
}
