package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergySlotBlockEntity;
import net.povstalec.sgjourney.common.blocks.transporter.AbstractTransportRingsBlock;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransporterPeripheral;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import net.povstalec.sgjourney.common.items.crystals.*;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterConnectionEntry;
import net.povstalec.sgjourney.common.sgjourney.transporter.BlockEntityTransportRings;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractTransportRingsEntity<TR extends BlockEntityTransportRings<?>> extends AbstractTransporterEntity<TR>
{
	public static final String EMPTY_SPACE = "empty_space";
	public static final String TRANSPORT_HEIGHT = "transport_height";
	public static final String PROGRESS = "progress";
	
	public static final String CRYSTAL_INVENTORY = "crystal_inventory";
	
	public static final String INTERDIMENSIONAL_TRANSPORT = "interdimensional_transport";
	public static final String TRANSFER_EFFICIENCY = "transfer_efficiency";
	
	public static final int TRANSPORT_TICKS = 21; // Number of ticks Transport Rings wait while in the hover position before they start transporting
	public static final int HOVER_TICKS = 2 * TRANSPORT_TICKS; // Number of ticks Transport Rings wait while in the hover position before they start descending
	
	public static final int MAX_TRANSPORT_HEIGHT = 16;
	public static final int DEFAULT_TRANSFER_EFFICIENCY = 1;
	
	protected CrystalCache<AbstractTransportRingsEntity<?>> crystalCache = createCrystalCache();
	
	protected boolean hasNetworkRestrictions = false; // Network restrictions specified by Crystals (separate from those specified by computers)
	protected Set<Integer> networksCrystalCache = new HashSet<>();// Networks specified by Crystals (separate from those specified by computers)
	
	protected boolean allowInterdimensionalTransport = false;
	
	protected int transferEfficiency = DEFAULT_TRANSFER_EFFICIENCY;
	
	public final ItemStackHandler crystalItemHandler;
	protected final LazyOptional<IItemHandler> lazyCrystalItemHandler;
	
	@Nullable
	private BlockPos transportPos = null;
	public int emptySpace = 0;
	public int transportHeight = 0;
	
	public int progress = -1;
	public int progressOld = -1;
	
	protected int transportSoundLead = 43;
	
	public AbstractTransportRingsEntity(BlockEntityType<?> blockEntityType, TransporterType<TR> transporterType, BlockPos pos, BlockState state, int defaultNetwork)
	{
		super(blockEntityType, transporterType, pos, state, defaultNetwork);
		
		crystalItemHandler = createCrystalHandler();
		lazyCrystalItemHandler = LazyOptional.of(() -> crystalItemHandler);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		crystalItemHandler.deserializeNBT(tag.getCompound(CRYSTAL_INVENTORY));
		
		if(!tag.contains(ENERGY_INVENTORY, CompoundTag.TAG_COMPOUND))
		{
			energyStorage.setEnergy(energyStorage.getTrueMaxEnergyStored());
			energyItemHandler.setStackInSlot(0, PowerCellItem.randomLiquidNaquadahSetup(CommonTechConfig.vial_capacity.get() / 3, CommonTechConfig.vial_capacity.get()));
		}
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.put(CRYSTAL_INVENTORY, crystalItemHandler.serializeNBT());
		super.saveAdditional(tag);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		crystalCache.recalculateCrystals();
	}
	
	@Override
	public void invalidateCaps()
	{
		lazyCrystalItemHandler.invalidate();
		super.invalidateCaps();
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		tag.putInt(EMPTY_SPACE, emptySpace);
		tag.putInt(TRANSPORT_HEIGHT, transportHeight);
		tag.putInt(PROGRESS, progress);
		
		tag.putBoolean(INTERDIMENSIONAL_TRANSPORT, allowInterdimensionalTransport);
		tag.putInt(TRANSFER_EFFICIENCY, transferEfficiency);
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		super.handleUpdateTag(tag);
		
		emptySpace = tag.getInt(EMPTY_SPACE);
		transportHeight = tag.getInt(TRANSPORT_HEIGHT);
		updateProgress(tag.getInt(PROGRESS));
		// The following values are updated here because the inventory isn't updated until the menu is opened,
		// but the info needs to be sent to the Ring Panel even if you don't open the menu
		allowInterdimensionalTransport = tag.getBoolean(INTERDIMENSIONAL_TRANSPORT);
		transferEfficiency = tag.getInt(TRANSFER_EFFICIENCY);
	}
	
	public LazyOptional<IItemHandler> getCrystalItemHandler()
	{
		return lazyCrystalItemHandler.cast();
	}
	
	protected ItemStackHandler createCrystalHandler()
	{
		return new ItemStackHandler(9)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
				crystalCache.recalculateCrystals();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				if(slot == 0)
					return stack.getItem() instanceof MaterializationCrystalItem crystal && !crystal.isLarge();
				
				if(stack.getItem() instanceof AbstractCrystalItem crystal && !crystal.isLarge())
					return crystalCache.isSupported(crystal.getType());
				
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
					return stack;
				
				return super.insertItem(slot, stack, simulate);
				
			}
		};
	}
	
	protected CrystalCache<AbstractTransportRingsEntity<?>> createCrystalCache()
	{
		return new CrystalCache.Generic9<>(this, CrystalCache.ALL)
		{
			@Override
			protected void onReset()
			{
				allowInterdimensionalTransport = false;
				
				hasNetworkRestrictions = false;
				networksCrystalCache.clear();
				
				transferEfficiency = DEFAULT_TRANSFER_EFFICIENCY;
			}
			
			@Override
			protected void fetchCrystals()
			{
				for(int i = 1; i < 9; i++)
				{
					ItemStack stack = crystalItemHandler.getStackInSlot(i);
					Item item = stack.getItem();
					
					if(item instanceof AbstractCrystalItem crystal)
						crystalCache.addCrystal(i, crystal);
				}
			}
			
			@Override
			protected void updateFromCrystals()
			{
				// Allow interdimensional transport if there is a Materialization Crystal placed in the center
				allowInterdimensionalTransport = crystalItemHandler.getStackInSlot(0).getItem() instanceof MaterializationCrystalItem;
				
				crystalCache.controlCrystals().forEach(slot ->
				{
					//TODO Some special entry for Network Restriction
					hasNetworkRestrictions = true;
				});
				
				crystalCache.transferCrystals().forEach(slot ->
				{
					// Each neighboring Transfer Crystal increases efficiency of this crystal
					int efficiency = slot.crystal.getEfficiencyMultiplier() + slot.countNeighborsOfType(Type.TRANSFER);
					
					// Increase efficiency for each neighboring Energy Crystal
					slot.forEachNeighborOfType(Type.ENERGY, neighborSlot -> transferEfficiency *= efficiency);
				});
				
				crystalCache.communicationCrystals().forEach(slot ->
				{
					// Collect frequencies of different Communication Crystals and interpret them as networks the Transporter is in
					if(CommunicationCrystalItem.hasFrequency(crystalItemHandler.getStackInSlot(slot.index)))
						networksCrystalCache.add(CommunicationCrystalItem.getFrequency(crystalItemHandler.getStackInSlot(slot.index)));
				});
				
				controllerCache.markDirtyTwoWays();
				updateTransporter();
			}
		};
	}
	
	@Override
	protected boolean isCorrectEnergySide(Direction side)
	{
		return getBlockState().hasProperty(AbstractTransportRingsBlock.FACING) && side != getBlockState().getValue(AbstractTransportRingsBlock.FACING);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER && (!isProtected() || CommonPermissionConfig.protected_inventory_access.get()))
			return lazyEnergyItemHandler.cast();
		
		return super.getCapability(capability, side);
	}

	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getBlockPos().getX() - 3, getBlockPos().getY() - (3 + MAX_TRANSPORT_HEIGHT), getBlockPos().getZ() - 3, getBlockPos().getX() + 4, getBlockPos().getY() + (4 + MAX_TRANSPORT_HEIGHT), getBlockPos().getZ() + 4);
    }
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	/**
	 * @return Total energy stored, including the energy inside Energy Crystals
	 */
	public long getTotalEnergyStored()
	{
		long energyStored = energyStorage.getTrueEnergyStored();
		for(var slot : crystalCache.energyCrystals().getSlots())
		{
			Optional<IEnergyStorage> energyStorage = crystalItemHandler.getStackInSlot(slot.index).getCapability(ForgeCapabilities.ENERGY).resolve();
			if(energyStorage.isPresent() && energyStorage.get() instanceof SGJourneyEnergy sgjourneyEnergy)
				energyStored += sgjourneyEnergy.getTrueEnergyStored();
		}
		
		return energyStored;
	}
	
	/**
	 * @return Total energy capacity, including the capacity of Energy Crystals
	 */
	public long getTotalEnergyCapacity()
	{
		long energyCapacity = energyStorage.getTrueMaxEnergyStored();
		for(var slot : crystalCache.energyCrystals().getSlots())
		{
			Optional<IEnergyStorage> energyStorage = crystalItemHandler.getStackInSlot(slot.index).getCapability(ForgeCapabilities.ENERGY).resolve();
			if(energyStorage.isPresent() && energyStorage.get() instanceof SGJourneyEnergy sgjourneyEnergy)
				energyCapacity += sgjourneyEnergy.getTrueMaxEnergyStored();
		}
		
		return energyCapacity;
	}
	
	@Override
	protected SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(this.getCapacity(), this.getMaxReceive(), this.getMaxExtract())
		{
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				long storageEnergyReceived = super.receiveLongEnergy(maxReceive, simulate);
				long energyReceived = storageEnergyReceived;
				boolean received = false;
				
				if(energyReceived < maxReceive) // If energy storage can't hold all the energy, try redirecting some of it to the Energy Crystals in the Crystal Inventory
				{
					for(var slot : crystalCache.energyCrystals().getSlots())
					{
						Optional<IEnergyStorage> energyStorage = crystalItemHandler.getStackInSlot(slot.index).getCapability(ForgeCapabilities.ENERGY).resolve();
						if(energyStorage.isPresent() && energyStorage.get() instanceof SGJourneyEnergy sgjourneyEnergy)
						{
							energyReceived += sgjourneyEnergy.receiveLongEnergy(maxReceive - energyReceived, simulate);
							received = true;
							
							if(energyReceived >= maxReceive)
								break;
						}
					}
					
					if(received)
						onEnergyChanged(energyReceived - storageEnergyReceived, simulate);
				}
				
				return energyReceived;
			}
			
			@Override
			public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
			{
				return canReceiveZeroPointEnergy() ? receiveLongEnergy(maxReceive, simulate) : 0;
			}
			
			@Override
			public long depleteEnergy(long maxExtract, boolean simulate)
			{
				long storageEnergyExtracted = super.depleteEnergy(maxExtract, simulate);
				long energyExtracted = storageEnergyExtracted;
				boolean extracted = false;
				
				if(energyExtracted < maxExtract) // If energy storage can't provide all the energy, try getting some of it to from Energy Crystals in the Crystal Inventory
				{
					for(var slot : crystalCache.energyCrystals().getSlots())
					{
						Optional<IEnergyStorage> energyStorage = crystalItemHandler.getStackInSlot(slot.index).getCapability(ForgeCapabilities.ENERGY).resolve();
						if(energyStorage.isPresent() && energyStorage.get() instanceof SGJourneyEnergy sgjourneyEnergy)
						{
							// depleteEnergy() gets around the issue of Energy Crystals having their maximum extract rate set to 100k by default
							long extracting = sgjourneyEnergy.depleteEnergy(maxExtract - energyExtracted, simulate);
							energyExtracted += extracting;
							extracted = true;
							
							if(energyExtracted >= maxExtract)
								break;
						}
					}
					
					if(extracted)
						onEnergyChanged(energyExtracted - storageEnergyExtracted, simulate);
				}
				
				return energyExtracted;
			}
			
			@Override
			public void onEnergyChanged(long difference, boolean simulate)
			{
				energyChanged(difference, simulate);
			}
		};
	}

	//========================================================================================================
	//**********************************************Transporting**********************************************
	//========================================================================================================
	
	@Override
	public Set<Integer> getNetworks()
	{
		Set<Integer> networks = new TreeSet<>(this.networks);
		networks.addAll(this.networksCrystalCache);
		controllerCache.ifPresent(controller -> networks.addAll(controller.getNetworks()));
		
		if(!networks.isEmpty())
			return networks;
		
		return Set.of(defaultNetwork);
	}
	
	@Override
	public Set<Integer> getCachedNetworks()
	{
		Set<Integer> networks = new TreeSet<>(this.networks);
		networks.addAll(this.networksCrystalCache);
		controllerCache.ifCached(controller -> networks.addAll(controller.getNetworks()));
		
		if(!networks.isEmpty())
			return networks;
		
		return Set.of(defaultNetwork);
	}
	
	@Override
	public boolean hasNetworkRestrictions()
	{
		if(super.hasNetworkRestrictions())
			return true;
		
		return hasNetworkRestrictions; // Restrict based on crystals
	}
	
	protected void saveDialAttempt(MemoryEntry<?> memoryEntry)
	{
		CompoundTag entry = memoryEntry.save();
		for(var slot : crystalCache.memoryCrystals().getSlots())
		{
			ItemStack stack = crystalItemHandler.getStackInSlot(slot.index);
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
				entry = memoryCrystal.saveCompound(stack, entry, true); // Save memory and move the oldest one to another crystal
			
			if(entry == null)
				break; // End early if there are no more memories to move back
		}
	}
	
	@Override
	public int getTransferEfficiency()
	{
		return transferEfficiency;
	}
	
	@Override
	public double maxTransportRange()
	{
		return TransporterConnection.estimateMaxRange(getTotalEnergyCapacity(), getTransferEfficiency());
	}
	
	@Override
	public boolean allowInterdimensionalTransport()
	{
		return allowInterdimensionalTransport;
	}
	
	@Override
	public void onDialAttempt(TransporterInfo.FeedbackMessage feedback, TransporterID otherID)
	{
		saveDialAttempt(new TransporterConnectionEntry.ID("", getLevel().getGameTime(), new TransporterConnection.IDResult(otherID, feedback.feedback())));
	}
	
	@Override
	public void onDialAttempt(TransporterInfo.FeedbackMessage feedback, Vec3i coords)
	{
		saveDialAttempt(new TransporterConnectionEntry.Coordinates("", getLevel().getGameTime(), new TransporterConnection.CoordsResult(coords, feedback.feedback())));
	}
	
	@Override
	@Nullable
	public List<Entity> entitiesToTransport()
	{
		AABB localBox = new AABB((transportPos.getX() - 1), (transportPos.getY()), (transportPos.getZ() - 1),
				(transportPos.getX() + 2), (transportPos.getY() + 3), (transportPos.getZ() + 2));
		
		return this.level.getEntitiesOfClass(Entity.class, localBox, entity -> true);
	}
	
	@Override
	public BlockPos transportPos()
	{
		if(transportPos == null)
		{
			this.emptySpace = getEmptySpace();
			this.transportPos = new BlockPos(getBlockPos().getX(), (getBlockPos().getY() + this.emptySpace), getBlockPos().getZ());
		}
		
		return this.transportPos;
	}
	
	@Override
	public boolean connectTransporter(UUID connectionID)
	{
		transportPos();
		return super.connectTransporter(connectionID);
	}
	
	@Override
	public TransporterInfo.FeedbackMessage resetTransporter(TransporterInfo.FeedbackMessage feedback)
	{
		this.emptySpace = 0;
		this.transportHeight = 0;
		this.transportPos = null;
		
		return super.resetTransporter(feedback);
	}
	
	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<TransporterPeripheral> wrapper)
	{
		CCTweakedCompatibility.Transporter.registerTransportRingsMethods(wrapper);
	}
	
	public static int getRingHoverHeight(int transportHeight, int ringNumber)
	{
		return transportHeight - 2 * ringNumber;
	}
	
	public static int getRingHoverStartTicks(int transportHeight, int ringNumber)
	{
		return 6 * ringNumber + getRingHoverHeight(transportHeight, ringNumber);
	}
	
	@Override
	public int getTimeUntilTransport()
	{
		return getRingHoverStartTicks(getTransportHeight(), 4) + TRANSPORT_TICKS;
	}
	
	public int getTransportHeight()
	{
		if(transportHeight == 0)
		{
			int emptySpace = getEmptySpace();
			
			if(emptySpace > 0)
				transportHeight = Math.abs(emptySpace * 4) + 8;
			else
				transportHeight = Math.abs(emptySpace * 4);
		}
		
		return transportHeight;
	}
	
	@Override
	public void updateTicks(int transportTicks, int connectionTime)
	{
		this.progress = connectionTime;
		this.progressOld = connectionTime;
		
		updateClient();
		
		if(transportTicks - getTransportSoundLead() == connectionTime)
			level.playSound(null, transportPos(), SoundInit.TRANSPORT_RINGS_TRANSPORT_PRE.get(), SoundSource.BLOCKS, 0.5F, 1F);
		else if(transportTicks == connectionTime)
			level.playSound(null, transportPos(), SoundInit.TRANSPORT_RINGS_TRANSPORT_POST.get(), SoundSource.BLOCKS, 0.5F, 1F);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractTransportRingsEntity<?> rings)
	{
		EnergySlotBlockEntity.tick(level, pos, state, rings);
		
		if(rings.isConnected())
			rings.doClientProgress();
		else
		{
			rings.progress = -1;
			rings.progressOld = -1;
		}
	}
	
	private void doClientProgress()
	{
		if(!this.level.isClientSide() || this.progress < 0)
			return;
		
		this.progressOld = this.progress;
		this.progress++;
	}
	
	// Only updates the progress if there is none (-1), this should prevent jittering
	public void updateProgress(int progress)
	{
		if(this.progress == -1 || progress == -1)
		{
			this.progress = progress;
			this.progressOld = progress;
		}
	}
	
	public void setProgress(int progress)
	{
		this.progressOld = this.progress;
		this.progress = progress;
	}
	
	public float getProgress(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) this.progress : Mth.lerp(partialTick, this.progressOld, this.progress);
	}
	
	@Override
	public boolean isConnected()
	{
		BlockState state = getBlockState();
		if(state.hasProperty(AbstractTransportRingsBlock.ACTIVATED))
			return state.getValue(AbstractTransportRingsBlock.ACTIVATED);
		
		return false;
	}
	
	@Override
	public boolean isObstructed()
	{
		return getEmptySpace() == 0;
	}
	
	@Override
	public void setConnected(boolean connected)
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = level.getBlockState(pos); // Using this instead of this.getBlockState() because it would break the game if you tried breaking the block
		if(state.hasProperty(AbstractTransportRingsBlock.ACTIVATED))
			level.setBlock(pos, state.setValue(AbstractTransportRingsBlock.ACTIVATED, connected), 2);
		
		loadChunk(connected);
	}
	
	private int getEmptySpace()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = level.getBlockState(pos); // Using this instead of this.getBlockState() because it would break the game if you tried breaking the block
		if(!state.hasProperty(AbstractTransportRingsBlock.FACING))
			return 0;
		
		if(state.getValue(AbstractTransportRingsBlock.FACING) == Direction.DOWN)
		{
			for(int i = 4; i <= 16; i++)
			{
				if(!level.getBlockState(pos.below(i)).getMaterial().isReplaceable() && level.getBlockState(pos.below(i - 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 2)).getMaterial().isReplaceable() && level.getBlockState(pos.below(i - 3)).getMaterial().isReplaceable())
				{
					return -i + 1;
				}
			}
		}
		else
		{
			for(int i = 1; i <= 16; i++)
			{
				if(level.getBlockState(pos.above(i)).getMaterial().isReplaceable() && level.getBlockState(pos.above(i + 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.above(i + 2)).getMaterial().isReplaceable())
				{
					return i;
				}
			}
		}
		return 0;
	}
	
	@Override
	public long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxDeplete()
	{
		return Long.MAX_VALUE;
	}
	
	/**
	 * Transport Rings can make noises before the transport itself starts
	 * @return The number of ticks which the Transport Rings transport sound will get as a head-start before the actual transport begins
	 */
	public int getTransportSoundLead()
	{
		return this.transportSoundLead;
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		crystalCache.recalculateCrystals();
	}
	
	@Override
	public void generateEnergyItem()
	{
		energyItemHandler.setStackInSlot(0, PowerCellItem.randomLiquidNaquadahSetup(CommonTechConfig.vial_capacity.get() / 3, CommonTechConfig.vial_capacity.get()));
	}
}
