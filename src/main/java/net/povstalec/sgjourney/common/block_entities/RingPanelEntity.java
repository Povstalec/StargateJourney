package net.povstalec.sgjourney.common.block_entities;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundRingPanelUpdatePacket;

public class RingPanelEntity extends BlockEntity
{
	private String[] rings;
	public int ringsFound;
	private BlockPos connectedPos;
	private BlockPos targetPos;
	
	public BlockPos ringsPos[] = new BlockPos[6];
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public RingPanelEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.RING_PANEL.get(), pos, state);
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
		
	public void drops()
	{
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Containers.dropContents(this.level, this.worldPosition, inventory);
	}
		
	@Override
	public void setRemoved() 
	{
		super.setRemoved();
		handler.invalidate();
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
					switch(slot)
					{
					case 0:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
					case 1:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
					case 2:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
					case 3:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
					case 4:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
					case 5:
						return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
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
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return handler.cast();
		
		return super.getCapability(capability, side);
	}
	
	public CompoundTag getNearest6Rings(Level level, BlockPos pos, double maxDistance)
	{
		String connected = connectToRings();
		
		CompoundTag tag = TransporterNetwork.get(level).get6ClosestRingsFromTag(level.dimension().location().toString(), pos, maxDistance, connected);
		List<String> tagList = tag.getAllKeys().stream().collect(Collectors.toList());
		
		ringsFound = tag.size();
		for(int i = 0; i < 6; i++)
		{
			
			if(i < ringsFound)
			{
				int[] coords = tag.getCompound(tagList.get(i)).getIntArray("Coordinates");
				ringsPos[i] = new BlockPos(coords[0], coords[1], coords[2]);
			}
			else
				ringsPos[i] = new BlockPos(0, 0, 0);
		}
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientboundRingPanelUpdatePacket(worldPosition, ringsFound, ringsPos[0], ringsPos[1], ringsPos[2], ringsPos[3], ringsPos[4], ringsPos[5]));
		return tag;
	}
	
	public String connectToRings()
	{
		CompoundTag rings = TransporterNetwork.get(level).getClosestRingsFromTag(this.level.dimension().location().toString(), this.getBlockPos(), new CompoundTag(), 32000);
		
		if(rings.isEmpty())
			return null;
		
		List<String> tagList = rings.getAllKeys().stream().collect(Collectors.toList());
		String connected = tagList.get(0);
		int[] coords = rings.getCompound(connected).getIntArray("Coordinates");
		
		this.connectedPos = new BlockPos(coords[0], coords[1], coords[2]);
		
		return connected;
	}
	
	public void activateRings(int chosenNumber)
	{
		ItemStack stack = this.itemHandler.getStackInSlot(chosenNumber);
		
		if(!stack.isEmpty() && stack.getTag().contains("coordinates"))
		{
			int[] coordinates = this.itemHandler.getStackInSlot(chosenNumber).getTag().getIntArray("coordinates");
			targetPos = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
		}
		else
		{
			targetPos = ringsPos[chosenNumber];
		}

		if(targetPos == null)
			return;
		
		if(connectedPos == null)
			return;
		
		BlockEntity localRings = level.getBlockEntity(connectedPos);
		
		if(localRings instanceof TransportRingsEntity rings)
		{
			if(!rings.canTransport())
				return;
			BlockEntity targetRings = level.getBlockEntity(targetPos);
    		
			if(targetRings instanceof TransportRingsEntity target)
			{
				if(!target.canTransport())
					return;
				rings.activate(targetPos);
			}
		}
	}
	
	public int[] getTargetCoords(int chosenNumber)
	{
		ItemStack stack = this.itemHandler.getStackInSlot(chosenNumber);
		
		if(!stack.isEmpty() && stack.getTag().contains("coordinates"))
			return this.itemHandler.getStackInSlot(chosenNumber).getTag().getIntArray("coordinates");

		CompoundTag ringsTag = BlockEntityList.get(level).getBlockEntities("TransportRings");
		int[] coords = {ringsTag.getIntArray(this.rings[chosenNumber])[0], ringsTag.getIntArray(this.rings[chosenNumber])[1], ringsTag.getIntArray(this.rings[chosenNumber])[2]};
		return coords;
	}
	
}
