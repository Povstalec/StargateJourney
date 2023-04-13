package net.povstalec.sgjourney.block_entities.dhd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.init.ItemInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.misc.ArrayHelper;

public abstract class AbstractDHDEntity extends EnergyBlockEntity
{
	private AbstractStargateEntity stargate = null;
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	private boolean hasControlCrystal = false;
	
	private int[] memoryCrystals = new int[0];
	
	private int[] energyCrystals = new int[0];
	private int desiredEnergyLevel = 0;
	private int maxEnergyTransfer = 0;
	
	private int[] communicationCrystals = new int[0];
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		this.recalculateCrystals();
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
		nbt.put("Inventory", itemHandler.serializeNBT());
		super.saveAdditional(nbt);
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(9)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
					recalculateCrystals();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					if(slot == 0)
						return stack.getItem() == ItemInit.LARGE_CONTROL_CRYSTAL.get();
					else
						return isValidCrystal(stack);
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
	
	private boolean isValidCrystal(ItemStack stack)
	{
		if(stack.getItem() == ItemInit.MEMORY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ENERGY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.COMMUNICATION_CRYSTAL.get())
			return true;
		
		return false;
	}
	
	public void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		this.hasControlCrystal = !itemHandler.getStackInSlot(0).isEmpty();
		this.memoryCrystals = new int[0];
		this.energyCrystals = new int[0];
		this.desiredEnergyLevel = 0;
		this.maxEnergyTransfer = 0;
		this.communicationCrystals = new int[0];
		
		// Check where the Crystals are and save their positions
		for(int i = 1; i < 9; i++)
		{
			Item item = itemHandler.getStackInSlot(i).getItem();
			
			if(item == ItemInit.MEMORY_CRYSTAL.get())
				this.memoryCrystals = ArrayHelper.growIntArray(this.memoryCrystals, i);
			else if(item == ItemInit.ENERGY_CRYSTAL.get())
				this.energyCrystals = ArrayHelper.growIntArray(this.energyCrystals, i);
			else if(item == ItemInit.COMMUNICATION_CRYSTAL.get())
				this.communicationCrystals = ArrayHelper.growIntArray(this.communicationCrystals, i);
		}
		
		// Set up Energy Crystals
		for(int i = 0; i < this.energyCrystals.length; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(energyCrystals[i]);
			
			if(!stack.isEmpty())
			{
				EnergyCrystalItem.CrystalMode mode = EnergyCrystalItem.getCrystalMode(stack);
				
				switch(mode)
				{
				case ENERGY_STORAGE:
					this.desiredEnergyLevel += EnergyCrystalItem.MAX_ENERGY;
					break;
				case ENERGY_TRANSFER:
					this.maxEnergyTransfer += EnergyCrystalItem.MAX_TRANSFER;
					break;
				}
			}
		}
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
		{
			return handler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		return false;
	}

	@Override
	protected long capacity()
	{
		return 0;
	}

	@Override
	protected long maxReceive()
	{
		return 0;
	}

	@Override
	protected long maxExtract()
	{
		return 0;
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		if(stargate.getEnergyStored() < this.desiredEnergyLevel)
		{
			long needed = this.desiredEnergyLevel - stargate.getEnergyStored();
			
			long energySent = needed > this.maxEnergyTransfer ? this.maxEnergyTransfer : needed;
			
			stargate.receiveEnergy(energySent, false);
		}
	}
	
	/*
	 * Searches for the nearest Stargate to the DHD
	 */
	public AbstractStargateEntity getNearestStargate(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
						stargates.add(stargate);
				});
			}
		}
		
		Iterator<AbstractStargateEntity> iterator = stargates.iterator();
		double bestDistance = (double) maxDistance;
		AbstractStargateEntity bestStargate = null;
		
		while(iterator.hasNext())
		{
			AbstractStargateEntity stargate = iterator.next();
			
			double distance = distance(this.getBlockPos(), stargate.getCenterPos());
			
			if(distance <= bestDistance)
			{
				bestDistance = distance;
				bestStargate = stargate;
			}
			
		}
		
		return bestStargate;
	}
	
	private double distance(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		double stargateDistance = Math.sqrt(x*x + y*y + z*z);
		
		return stargateDistance;
	}
	
	/*
	 * Engages the next Stargate chevron
	 */
	public void engageChevron(int symbol)
	{
		if(stargate != null)
		{
			if(symbol == 0)
				level.playSound((Player)null, this.getBlockPos(), SoundInit.MILKY_WAY_DHD_ENTER.get(), SoundSource.BLOCKS, 0.25F, 1F);
			stargate.engageSymbol(symbol);
		}
		else
			System.out.println("Stargate not found");
	}
	
	public void disconnectFromStargate()
	{
		if(this.stargate != null)
			setStargateConnection(this.stargate, false);
	}
	
	private void setStargateConnection(AbstractStargateEntity stargate, boolean hasDHD)
	{
		stargate.setDHD(hasDHD, this.hasControlCrystal);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;

		AbstractStargateEntity nearbyStargate = dhd.getNearestStargate(dhd.communicationCrystals.length * 16 + 16);
		
		if(dhd.stargate != null && nearbyStargate != null && dhd.stargate != nearbyStargate)
			dhd.setStargateConnection(dhd.stargate, false);
		
		dhd.stargate = nearbyStargate;
		
		if(dhd.stargate != null)
		{
			dhd.setStargateConnection(dhd.stargate, true);
			
			dhd.outputEnergy(null);
		}
    }
}
