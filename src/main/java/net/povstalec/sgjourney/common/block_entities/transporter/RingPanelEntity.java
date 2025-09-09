package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.Transporting;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundRingPanelUpdatePacket;

public class RingPanelEntity extends TransporterControllerEntity
{
	public static final String INVENTORY = "Inventory";
	
	private BlockPos targetPos;
	
	// Used for Client syncing
	public ArrayList<BlockPos> ringsPos = new ArrayList<BlockPos>();
    public ArrayList<Component> ringsName = new ArrayList<Component>();
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	private TransportRingsEntity transportRings;
	
	public RingPanelEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.RING_PANEL.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound(INVENTORY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put(INVENTORY, itemHandler.serializeNBT());
		super.saveAdditional(nbt);
	}
	
	private void drops()
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
					return stack.getItem() == ItemInit.MEMORY_CRYSTAL.get();
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
	
	//============================================================================================
	//**********************************Locating Transport Rings**********************************
	//============================================================================================
	
	//TODO getRingsOnButtons()
	
	public void getNearest6Rings(ServerLevel level, BlockPos pos, double maxDistance)
	{
		if(transportRings == null)
			return;

		List<Transporter> transporters = LocatorHelper.findNearestTransporters(level, pos);
		
		ringsPos.clear();
		ringsName.clear();
		int ringsFound = transporters.size();
		
		int j = 0;
		if(ringsFound > 0 && transporters.get(0).getBlockPos().equals(this.transportRings.getBlockPos()))
			j += 1;
		
		for(int i = 0; i < 6 && j < ringsFound; i++, j++)
		{
			Transporter transporter = transporters.get(j);
			
			ringsPos.add(transporter.getBlockPos());
			ringsName.add(transporter.getName());
		}
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientboundRingPanelUpdatePacket(worldPosition, ringsPos, ringsName));
	}
	
	public TransportRingsEntity findNearestTransportRings(int maxDistance)
	{
		List<TransportRingsEntity> transportRingsList = getNearbyTransportRings(maxDistance);
		
		transportRingsList.sort((transportRingsA, transportRingsB) ->
				Long.compare(CoordinateHelper.Relative.distanceSqr(this.getBlockPos(), transportRingsA.getBlockPos()), CoordinateHelper.Relative.distanceSqr(this.getBlockPos(), transportRingsB.getBlockPos())));
		
		if(!transportRingsList.isEmpty())
		{
			Iterator<TransportRingsEntity> iterator = transportRingsList.iterator();
			
			if(iterator.hasNext())
				return iterator.next();
		}
		
		return null;
	}
	
	public void setTransportRings()
	{
		if(this.getLevel() == null)
			return;
		
		this.transportRings = findNearestTransportRings(16);
	}
	
	public void activateRings(int chosenNumber)
	{
		if(transportRings == null || !transportRings.canTransport()) //TODO Tell the player there are no rings connected
			return;
		
		ItemStack stack = this.itemHandler.getStackInSlot(chosenNumber);
		
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			UUID uuid = MemoryCrystalItem.getFirstUUID(stack);
			Transporting.startTransport(level.getServer(), transportRings.getTransporter(), uuid);
		}
		else
		{
			if(chosenNumber < ringsPos.size())
				targetPos = ringsPos.get(chosenNumber);
			
			if(targetPos == null)
				return;
			
			if(transportRings == null || !transportRings.canTransport())
				return;
			
			BlockEntity targetRings = level.getBlockEntity(targetPos);
			
			if(targetRings instanceof TransportRingsEntity target)
			{
				if(!target.canTransport())
					return;
				
				Transporter transporter = target.getTransporter();
				if(transporter != null)
					transportRings.startTransport(transporter);
			}
		}
	}
	
	public int[] getTargetCoords(int chosenNumber)
	{
		ItemStack stack = this.itemHandler.getStackInSlot(chosenNumber);
		
		if(!stack.isEmpty() && stack.getTag().contains("coordinates"))
			return this.itemHandler.getStackInSlot(chosenNumber).getTag().getIntArray("coordinates");
		
		//TODO FIX THIS
		//CompoundTag ringsTag = BlockEntityList.get(level).getBlockEntities("TransportRings");
		//int[] coords = {ringsTag.getIntArray(this.rings[chosenNumber])[0], ringsTag.getIntArray(this.rings[chosenNumber])[1], ringsTag.getIntArray(this.rings[chosenNumber])[2]};
		return new int[] {0, 0, 0};//coords;
	}
	
}
