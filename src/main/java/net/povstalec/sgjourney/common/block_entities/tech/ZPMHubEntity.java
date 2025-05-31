package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class ZPMHubEntity extends EnergyBlockEntity
{
	public static final String INVENTORY = "inventory";
	
	private static final long maxTransfer = CommonZPMConfig.zpm_hub_max_transfer.get();
	private static final long maxEnergyDisplayed = CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	
	private final ItemStackHandler itemHandler = createHandler();
	private final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
	
	public ZPMHubEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_HUB.get(), pos, state);
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
	
	public IItemHandler getItemHandler(Direction side)
	{
		return lazyItemHandler.get();
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
						return stack.getItem() == ItemInit.ZPM.get();
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
	
	public void drops()
	{
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Containers.dropContents(this.level, this.worldPosition, inventory);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side == Direction.DOWN;
	}
	
	protected boolean receivesEnergy()
	{
		return false;
	}
	
	@Override
	public long capacity()
	{
		return maxEnergyDisplayed;
	}

	@Override
	public long maxReceive()
	{
		return 0;
	}

	@Override
	public long maxExtract()
	{
		return maxTransfer;
	}
	
	@Override
	public void outputEnergy(Direction outputDirection)
	{
		ItemStack stack = itemHandler.getStackInSlot(0);
		
		if(stack.is(ItemInit.ZPM.get()))
		{
			IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if(cap != null)
			{
				if(cap instanceof ZeroPointEnergy zpmEnergy)
				{
					BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(outputDirection));
					
					if(blockEntity == null)
						return;

					IEnergyStorage cap2 = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(outputDirection), null);
					if(cap2 != null)
					{
						if(cap2 instanceof SGJourneyEnergy sgjourneyEnergy)
						{
							long simulatedOutputAmount = zpmEnergy.extractLongEnergy(this.maxExtract(), true);
							long simulatedReceiveAmount = sgjourneyEnergy.receiveLongEnergy(simulatedOutputAmount, true);
							zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
							sgjourneyEnergy.receiveLongEnergy(simulatedReceiveAmount, false);
						}
						else
						{
							int simulatedOutputAmount = zpmEnergy.extractEnergy(SGJourneyEnergy.getRegularEnergy(this.maxExtract()), true);
							int simulatedReceiveAmount = cap2.receiveEnergy(simulatedOutputAmount, true);
							
							zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
							cap2.receiveEnergy(simulatedReceiveAmount, false);
						}
					}
				}
			}
		}
		
		/*if(ZeroPointModule.hasEnergy(stack))
		{
			BlockEntity blockentity = level.getBlockEntity(worldPosition.relative(outputDirection));
			
			if(blockentity == null)
				return;
			else if(blockentity instanceof EnergyBlockEntity energyBE)
			{
				long simulatedReceiveAmount = energyBE.receiveEnergy(this.maxExtract(), true);
				
				long extractedAmount = this.extractEnergy(simulatedReceiveAmount, true);
				
				this.extractEnergy(extractedAmount, false);
				energyBE.receiveEnergy(extractedAmount, false);
			}
			else
			{
				blockentity.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent((energyStorage) ->
				{
					int simulatedReceiveAmount = energyStorage.receiveEnergy(SGJourneyEnergy.getRegularEnergy(this.getMaxExtract()), true);
					
					int extractedAmount = SGJourneyEnergy.getRegularEnergy(ZeroPointModule.extractEnergy(stack, simulatedReceiveAmount));
					energyStorage.receiveEnergy(extractedAmount, false);
				});
			}
		}*/
	}
	
	//This should make sure the energy taken by cables is properly subtracted from the ZPM
	/*@Override
	protected void changeEnergy(long difference, boolean simulate)
	{
		ItemStack stack = itemHandler.getStackInSlot(0);
		
		if(!simulate)
			ZeroPointModule.extractEnergy(stack, difference);
		
		super.changeEnergy(difference, simulate);
	}*/
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, ZPMHubEntity hub)
	{
		if(level.isClientSide)
			return;
		
		hub.outputEnergy(Direction.DOWN);

		//ItemStack stack = hub.itemHandler.getStackInSlot(0);
		//long energy = ZeroPointModule.isNearingEntropy(stack) ? ZeroPointModule.getEnergyInLevel(stack) : ZeroPointModule.getMaxEnergy();
		//hub.setEnergy(energy);
		
		//PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(hub.worldPosition)), new ClientboundNaquadahGeneratorUpdatePacket(hub.worldPosition, hub.getReactionProgress(), hub.getEnergy()));
	}
}
