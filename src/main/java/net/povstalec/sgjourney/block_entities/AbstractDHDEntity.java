package net.povstalec.sgjourney.block_entities;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.init.SoundInit;

public abstract class AbstractDHDEntity extends BlockEntity
{
	private AbstractStargateEntity target;
	private double distance;
	
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
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
		{
			return handler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	/*
	 * Searches for the nearest Stargate to the DHD
	 */
	public void getNearestStargate(int maxDistance)
	{
		this.target = null;
		this.distance = maxDistance;
		
		//TODO expand the chunk radius
		Map<BlockPos, BlockEntity> entityMap = level.getChunkAt(this.getBlockPos()).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap1 = level.getChunkAt(this.getBlockPos().north(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap2 = level.getChunkAt(this.getBlockPos().north(16).east(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap3 = level.getChunkAt(this.getBlockPos().north(16).west(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap4 = level.getChunkAt(this.getBlockPos().south(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap5 = level.getChunkAt(this.getBlockPos().south(16).east(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap6 = level.getChunkAt(this.getBlockPos().south(16).west(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap7 = level.getChunkAt(this.getBlockPos().east(16)).getBlockEntities();
		Map<BlockPos, BlockEntity> entityMap8 = level.getChunkAt(this.getBlockPos().west(16)).getBlockEntities();
		
		entityMap.putAll(entityMap1);
		entityMap.putAll(entityMap2);
		entityMap.putAll(entityMap3);
		entityMap.putAll(entityMap4);
		entityMap.putAll(entityMap5);
		entityMap.putAll(entityMap6);
		entityMap.putAll(entityMap7);
		entityMap.putAll(entityMap8);
		
		entityMap.forEach(this::findStargate);
		
		System.out.println(entityMap);
		if(target != null)
			System.out.println("Closest Stargate at X: " + target.getBlockPos().getX() + " Y: " + target.getBlockPos().getY() + " Z: " + target.getBlockPos().getZ());
	}
	
	/*
	 * Searches through the Stargate list
	 */
	private void findStargate(BlockPos pos, BlockEntity entity)
	{
		if(entity instanceof AbstractStargateEntity stargate)
		{
			int x = Math.abs(pos.getX() - this.getBlockPos().getX());
			int y = Math.abs(pos.getY() - this.getBlockPos().getY());
			int z = Math.abs(pos.getZ() - this.getBlockPos().getZ());
			
			double stargateDistance = Math.sqrt(x*x + y*y + z*z);
			
			if(stargateDistance <= distance)
			{
				this.distance = stargateDistance;
				this.target = stargate;
			}
		}
	}
	
	/*
	 * Engages the next Stargate chevron
	 */
	public void engageChevron(int symbol)
	{
		if(target != null)
		{
			if(symbol == 0)
				level.playSound((Player)null, this.getBlockPos(), SoundInit.MILKY_WAY_DHD_ENTER.get(), SoundSource.BLOCKS, 0.25F, 1F);
			target.encodeChevron(symbol);
		}
		else
			System.out.println("Stargate not found");
	}
	
}
