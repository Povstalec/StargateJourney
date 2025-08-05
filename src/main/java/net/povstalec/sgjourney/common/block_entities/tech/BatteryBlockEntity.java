package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.packets.ClientboundBatteryBlockUpdatePacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class BatteryBlockEntity extends EnergyBlockEntity
{
	public static final String INVENTORY = "inventory";
	
	private final ItemStackHandler itemHandler = createHandler();
	private final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
	
	public BatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		itemHandler.deserializeNBT(registries, nbt.getCompound(INVENTORY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.saveAdditional(nbt, registries);
		nbt.put(INVENTORY, itemHandler.serializeNBT(registries));
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IItemHandler getItemHandler()
	{
		return lazyItemHandler.get();
	}
	
	public IItemHandler getItemHandler(Direction side)
	{
		return lazyItemHandler.get();
	}
	
	//============================================================================================
	//******************************************Storage*******************************************
	//============================================================================================
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(2)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
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
					return stack;
				
				return super.insertItem(slot, stack, simulate);
				
			}
		};
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, BatteryBlockEntity battery)
	{
		if(level.isClientSide())
			return;
		
		battery.extractItemEnergy(battery.itemHandler.getStackInSlot(0));
		battery.fillItemEnergy(battery.itemHandler.getStackInSlot(1));
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(battery.getBlockPos()).getPos(),
				new ClientboundBatteryBlockUpdatePacket(battery.getBlockPos(), battery.getEnergyStored()));
	}
	
	
	
	public static class Naquadah extends BatteryBlockEntity
	{
		public Naquadah(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.LARGE_NAQUADAH_BATTERY.get(), pos, state);
		}
		
		@Override
		protected long capacity()
		{
			return CommonTechConfig.large_naquadah_battery_capacity.get();
		}
		
		@Override
		protected long maxReceive()
		{
			return CommonTechConfig.large_naquadah_battery_max_transfer.get();
		}
		
		@Override
		protected long maxExtract()
		{
			return CommonTechConfig.large_naquadah_battery_max_transfer.get();
		}
	}
}
