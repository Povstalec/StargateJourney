package net.povstalec.sgjourney.block_entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
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
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.init.SoundInit;

public abstract class AbstractDHDEntity extends EnergyBlockEntity
{
	private AbstractStargateEntity stargate = null;
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(6)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return false;
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
		if(stargate.getEnergyStored() < 150000)
		{
			long needed = 150000 - stargate.getEnergyStored();
			
			long energySent = needed > 5000 ? 5000 : needed;
			
			stargate.receiveEnergy(energySent, false);
		}
	}
	
	/*
	 * Searches for the nearest Stargate to the DHD
	 */
	public AbstractStargateEntity getNearestStargate(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -1; x <= 1; x++)
		{
			for(int z = -1; z <= 1; z++)
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
			
			double distance = distance(this.getBlockPos(), stargate.getBlockPos());
			
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
		stargate.setDHD(hasDHD);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;

		AbstractStargateEntity nearbyStargate = dhd.getNearestStargate(16);
		
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
