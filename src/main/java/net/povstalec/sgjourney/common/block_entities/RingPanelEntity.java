package net.povstalec.sgjourney.common.block_entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
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
import net.povstalec.sgjourney.common.block_entities.tech.TransportRingsEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.packets.ClientboundRingPanelUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Transporter;

public class RingPanelEntity extends BlockEntity implements ProtectedBlockEntity
{
	public static final String INVENTORY = "inventory";
	
	private BlockPos targetPos;
	
	// Used for Client syncing
	public ArrayList<BlockPos> ringsPos = new ArrayList<BlockPos>();
    public ArrayList<Component> ringsName = new ArrayList<Component>();
	
	private final ItemStackHandler itemStackHandler = createHandler();
	private final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemStackHandler);

	protected boolean isProtected = false;

	private TransportRingsEntity transportRings;
	
	public RingPanelEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.RING_PANEL.get(), pos, state);
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		itemStackHandler.deserializeNBT(registries, nbt.getCompound(INVENTORY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		nbt.put(INVENTORY, itemStackHandler.serializeNBT(registries));
		super.saveAdditional(nbt, registries);
	}
	
	private void drops()
	{
		SimpleContainer inventory = new SimpleContainer(itemStackHandler.getSlots());
		for (int i = 0; i < itemStackHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemStackHandler.getStackInSlot(i));
		}
		
		Containers.dropContents(this.level, this.worldPosition, inventory);
	}
		
	@Override
	public void setRemoved() 
	{
		super.setRemoved();
		lazyItemHandler.invalidate();
		drops();
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

	
	
	public void getNearest6Rings(Level level, BlockPos pos, double maxDistance)
	{
		if(transportRings == null)
			return;

		Optional<List<Transporter>> transporterListOptional = TransporterNetwork.get(level).getTransportersFromDimension(level.dimension());
		
		if(transporterListOptional.isEmpty())
			return;
		
		List<Transporter> transporters = transporterListOptional.get();
		
		transporters.sort((transportRingsA, transportRingsB) ->
		Long.valueOf(distanceSqr(this.getBlockPos(), transportRingsA.getBlockPos()))
		.compareTo(Long.valueOf(distanceSqr(this.getBlockPos(), transportRingsB.getBlockPos()))));
		
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
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientboundRingPanelUpdatePacket(worldPosition, ringsPos, ringsName));
		return;
	}
	
	protected List<TransportRingsEntity> getNearbyTransportRings(int maxDistance)
	{
		List<TransportRingsEntity> transportRingsList = new ArrayList<TransportRingsEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof TransportRingsEntity transportRings)
						transportRingsList.add(transportRings);
				});
			}
		}
		
		return transportRingsList;
	}
	
	private long distanceSqr(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		long distance = x*x + y*y + z*z;
		
		return distance;
	}
	
	public TransportRingsEntity findNearestTransportRings(int maxDistance)
	{
		List<TransportRingsEntity> transportRingsList = getNearbyTransportRings(maxDistance);
		
		transportRingsList.sort((transportRingsA, transportRingsB) ->
				Long.valueOf(distanceSqr(this.getBlockPos(), transportRingsA.getBlockPos()))
				.compareTo(Long.valueOf(distanceSqr(this.getBlockPos(), transportRingsB.getBlockPos()))));
		
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
		ItemStack stack = this.itemStackHandler.getStackInSlot(chosenNumber);
		
		/*if(!stack.isEmpty() && stack.getTag().contains("coordinates"))
		{
			int[] coordinates = this.itemStackHandler.getStackInSlot(chosenNumber).getTag().getIntArray("coordinates");
			targetPos = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
		}
		else */if(chosenNumber < ringsPos.size())
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
			
			transportRings.activate(targetPos);
		}
	}
	
	public int[] getTargetCoords(int chosenNumber)
	{
		ItemStack stack = this.itemStackHandler.getStackInSlot(chosenNumber);
		
		/*if(!stack.isEmpty() && stack.getTag().contains("coordinates"))
			return this.itemStackHandler.getStackInSlot(chosenNumber).getTag().getIntArray("coordinates");*/
		
		//TODO FIX THIS
		//CompoundTag ringsTag = BlockEntityList.get(level).getBlockEntities("TransportRings");
		//int[] coords = {ringsTag.getIntArray(this.rings[chosenNumber])[0], ringsTag.getIntArray(this.rings[chosenNumber])[1], ringsTag.getIntArray(this.rings[chosenNumber])[2]};
		return new int[] {0, 0, 0};//coords;
	}

	//============================================================================================
	//*****************************************Protection*****************************************
	//============================================================================================

	@Override
	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}

	@Override
	public boolean isProtected()
	{
		return isProtected;
	}

	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_block_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);

			return false;
		}

		return true;
	}

}
