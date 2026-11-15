package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.blocks.dhd.AbstractDHDBlock;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.*;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.energy_cores.IEnergyCore;
import net.povstalec.sgjourney.common.misc.*;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.dhd.DHD;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;
import net.povstalec.sgjourney.common.sgjourney.info.SymbolInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.DHDControlledStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractDHDEntity<D extends SGJourneyBlockEntityDHD<?>> extends EnergyBlockEntity implements StructureGenEntity, SymbolInfo.Interface, ProtectedBlockEntity, PDAStatus
{
	public static final int DHD_INFO_DISTANCE = 5;
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public static final String IS_CENTER_BUTTON_ENGAGED = "is_center_button_engaged";
	public static final String ADDRESS = Address.ADDRESS;
	
	public static final String STARGATE_POS = "stargate_pos";
	
	public static final int DEFAULT_ENERGY_TARGET = 0;
	public static final int DEFAULT_ENERGY_TRANSFER = 0;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	protected boolean isCenterButtonEngaged = false;
	protected Address.Mutable address = new Address.Mutable();
	
	protected boolean advancedProtocolsActive = false;
	protected boolean doCallForwarding = false;
	protected boolean hasNetworkRestrictions = false;
	protected Set<Integer> networks = new TreeSet<>();
	
	protected long energyTarget = DEFAULT_ENERGY_TARGET;
	protected long maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
	protected int maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE;
	
	public final ItemStackHandler energyItemHandler;
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler;
	
	public final ZeroPointEnergy zpmEnergy;
	
	protected SymbolInfo symbolInfo;
	
	protected boolean isProtected = false;
	
	@Nullable
	protected Vec3i stargateRelativePos = null;
	
	protected D dhd;
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
		
		this.zpmEnergy = createZPMEnergyStorage();
		
		this.symbolInfo = new SymbolInfo();
	}
	
	@Override
	public void onLoad()
	{
		if(getLevel().isClientSide())
		{
			dhdRun(dhd ->
			{
				AutoCache.Receiver<DHD, DHDControlledStargate> stargateCache = dhd.receiverCache();
				
				// Revalidation
				stargateCache.setRevalidate(() ->
				{
					if(stargateRelativePos == null)
						return false;
					
					BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
					if(stargatePos != null)
					{
						BlockEntity blockEntity = level.getBlockEntity(stargatePos);
						if(blockEntity != null && blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).isPresent())
						{
							// Check if the Stargate at the saved pos is the same Stargate
							return stargateCache.getCached() == blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).resolve().get();
						}
						
					}
					
					return false;
				});
				// Client will only ever attempt to fetch Stargate from the relative pos provided by syncing
				stargateCache.setFetch(() ->
				{
					if(stargateRelativePos == null)
						return null;
					
					BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
					if(stargatePos != null)
					{
						BlockEntity blockEntity = level.getBlockEntity(stargatePos);
						if(blockEntity != null && blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).isPresent())
							return blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).resolve().get();
					}
					
					return null;
				});
			});
		}
		else
		{
			setupServerAutoCache();
			
			if(generationStep == StructureGenEntity.Step.READY)
				generate(); //TODO Logic of loading the DHD Symbols after Stargate, but finding Stargate after generating inventory (do this once there's a loot table for the DHD inventory, don't forget generateAdditional is fired by DHDItem)
			
			updateClient();
		}
		
		super.onLoad();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		energyItemHandler.deserializeNBT(tag.getCompound(ENERGY_INVENTORY));
		InventoryUtil.expandSlotsIfNeeded(energyItemHandler, 2);
		
		if(tag.contains(GENERATION_STEP, Tag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(PROTECTED, Tag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		if(tag.contains(STARGATE_POS, Tag.TAG_INT_ARRAY))
			stargateRelativePos = Conversion.intArrayToVec(tag.getIntArray(STARGATE_POS));
		else
			stargateRelativePos = null;
		
		address.fromArray(tag.getIntArray(ADDRESS));
		isCenterButtonEngaged = tag.getBoolean(IS_CENTER_BUTTON_ENGAGED);
		
		super.load(tag);
		zpmEnergy.updateFromZPMItem(energyItemHandler.getStackInSlot(0));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT());
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(isProtected)
			tag.putBoolean(PROTECTED, true);
		
		if(stargateRelativePos != null)
			tag.putIntArray(STARGATE_POS, Conversion.vecToIntArray(stargateRelativePos));
		
		address.saveToCompoundTag(tag, ADDRESS);
		tag.putBoolean(IS_CENTER_BUTTON_ENGAGED, isCenterButtonEngaged);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		address.saveToCompoundTag(tag, ADDRESS);
		tag.putBoolean(IS_CENTER_BUTTON_ENGAGED, isCenterButtonEngaged);
		
		if(stargateRelativePos != null)
			tag.putIntArray(STARGATE_POS, Conversion.vecToIntArray(stargateRelativePos));
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag)
	{
		energyStorage.setEnergyNoUpdate(tag.getLong(ENERGY));
		
		symbolInfo.loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		address.fromArray(tag.getIntArray(ADDRESS));
		isCenterButtonEngaged = tag.getBoolean(IS_CENTER_BUTTON_ENGAGED);
		
		if(tag.contains(STARGATE_POS, Tag.TAG_INT_ARRAY))
			stargateRelativePos = Conversion.intArrayToVec(tag.getIntArray(STARGATE_POS));
		else
			stargateRelativePos = null;
		dhdRun(dhd -> dhd.receiverCache().markDirty());
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
			handleUpdateTag(tag);
	}
	
	public void setupServerAutoCache()
	{
		dhdRun(dhd ->
		{
			AutoCache.Receiver<DHD, DHDControlledStargate> stargateCache = dhd.receiverCache();
			
			// Revalidation - check if it's not too far
			stargateCache.setRevalidate(() ->
			{
				if(stargateRelativePos == null)
					return false;
				
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
				if(stargatePos != null)
				{
					BlockEntity blockEntity = level.getBlockEntity(stargatePos);
					if(blockEntity != null && blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).isPresent())
					{
						DHDControlledStargate stargate = blockEntity.getCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY).resolve().get();
						// Check if the Stargate at the saved pos is the same Stargate
						return stargateCache.getCached() == stargate && CoordinateHelper.Relative.distanceSqr(stargatePos, getBlockPos()) <= getMaxConnectionDistanceSqr();
					}
				}
				
				return false;
			});
			// Find nearest Stargate that isn't connected to a DHD
			stargateCache.setFetch(() -> LocatorHelper.getNearestCapability(DHDControlledStargate.DHD_CONTROLLED_STARGATE_CAPABILITY, level, worldPosition, maxConnectionDistance,
				stargate -> !stargate.controllerCache().isCached()));
			
			stargateCache.setOnChanged((oldStargate, newStargate) ->
			{
				if(newStargate instanceof BlockEntityStargate<?> blockEntityStargate)
					stargateRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), blockEntityStargate.getBlockPos());
				else
					stargateRelativePos = null;
				
				if(newStargate != null)
					newStargate.updateDHD(dhd);
				updateClient();
			});
		});
	}
	
	@Override
	public SymbolInfo symbolInfo()
	{
		return symbolInfo;
	}
	
	
	
	@Override
	public void invalidateCaps()
	{
		lazyEnergyItemHandler.invalidate();
		
		super.invalidateCaps();
	}
	
	public LazyOptional<IItemHandler> getEnergyItemHandler()
	{
		return lazyEnergyItemHandler.cast();
	}
	
	protected ItemStackHandler createEnergyItemHandler()
	{
		return new ItemStackHandler(2)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				zpmEnergy.updateFromZPMItem(energyItemHandler.getStackInSlot(0));
				setChanged();
				updateClient();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				if(slot == 0)
					return stack.getItem() instanceof IEnergyCore || SyncedConfig.dhd_holds_zpm.get() && stack.getItem() instanceof ZeroPointModule || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
				
				return true;
			}
			
			// Limits the number of items per slot
			public int getSlotLimit(int slot)
			{
				if(slot == 0)
					return 1;
				
				return 64;
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
	
	
	
	public double getMaxConnectionDistance()
	{
		return maxConnectionDistance;
	}
	
	public double getMaxConnectionDistanceSqr()
	{
		return (long) maxConnectionDistance * maxConnectionDistance;
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget < 0 ? CommonStargateConfig.stargate_energy_capacity.get() : this.energyTarget;
	}
	
	public boolean advancedProtocolsActive()
	{
		return this.advancedProtocolsActive;
	}
	
	public int autocloseTicks()
	{
		return advancedProtocolsActive() ? ControlCrystalItem.AUTOCLOSE_TICKS : 0;
	}
	
	public long getStargateEnergy()
	{
		return stargateReturn(Stargate::getEnergyStored, -1L);
	}
	
	public int getStargateOpenTime()
	{
		return stargateReturn(Stargate::getOpenTime, 0);
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		return stargateReturn(Stargate::getTimeSinceLastTraveler, 0);
	}
	
	public void updateDHD(DHDControlledStargate stargate, Address address, boolean shouldEngageCenterButton)
	{
		setAddress(address);
		setCenterButtonEngaged(shouldEngageCenterButton);
		updateClient();
		setChanged();
	}
	
	public void setAddress(Address address)
	{
		this.address.fromAddress(address);
	}
	
	public Address.Mutable getAddress()
	{
		return this.address;
	}
	
	public void setCenterButtonEngaged(boolean isCenterButtonEngaged)
	{
		this.isCenterButtonEngaged = isCenterButtonEngaged;
	}
	
	public boolean isCenterButtonEngaged()
	{
		return this.isCenterButtonEngaged;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	public ZeroPointEnergy createZPMEnergyStorage()
	{
		return new ZeroPointEnergy(energyItemHandler.getStackInSlot(0))
		{
			@Override
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				updateFromZPMItem(energyItemHandler.getStackInSlot(0));
				return super.receiveLongEnergy(maxReceive, simulate);
			}
			
			@Override
			public long depleteEnergy(long maxExtract, boolean simulate)
			{
				updateFromZPMItem(energyItemHandler.getStackInSlot(0));
				return super.depleteEnergy(maxExtract, simulate);
			}
			
			@Override
			public long getTrueEnergyStored()
			{
				updateFromZPMItem(energyItemHandler.getStackInSlot(0));
				return this.energy;
				
			}
			
			@Override
			public void onEnergyChanged(long difference, boolean simulate)
			{
				updateZPMItem(energyItemHandler.getStackInSlot(0));
			}
		};
	}
	
	protected abstract long buttonPressEnergyCost();
	
	@Override
	public boolean canReceiveZeroPointEnergy()
	{
		return SyncedConfig.dhd_holds_zpm.get() || CommonZPMConfig.stargates_use_zero_point_energy.get();
	}
	
	public long minStoredEnergy()
	{
		return energyStorage.getTrueMaxEnergyStored() * 2 / 3;
	}
	
	public abstract long maxEnergyTransfer();
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side != Direction.UP;
	}
	
	@Override
	public long getMaxEnergyExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxEnergyDeplete()
	{
		return Long.MAX_VALUE;
	}
	
	private void tryStoreEnergy(ItemStack energyStack)
	{
		ItemStack inputStack = energyItemHandler.getStackInSlot(1);
		// Generates energy if needed
		if(energyStack.getItem() instanceof IEnergyCore energyCore && energyCore.maxGeneratedEnergy(energyStack, energyItemHandler.getStackInSlot(1)) <= (energyStorage.getTrueMaxEnergyStored() - energyStorage.getTrueEnergyStored()))
		{
			long generatedEnergy = energyCore.generateEnergy(energyStack, inputStack);
			
			if(generatedEnergy > 0)
				energyStorage.receiveLongEnergy(generatedEnergy, false);
		}
		else if(energyStack.getCapability(ForgeCapabilities.ENERGY).isPresent())
		{
			energyStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(otherEnergyStorage ->
			{
				long energyNeeded = energyStorage.getTrueMaxEnergyStored() - energyStorage.getTrueEnergyStored();
				energyStorage.drainOtherEnergyStorage(otherEnergyStorage, energyNeeded);
			});
		}
		else if(SyncedConfig.dhd_holds_zpm.get())
		{
			long energyNeeded = energyStorage.getTrueMaxEnergyStored() - energyStorage.getTrueEnergyStored();
			energyStorage.drainOtherEnergyStorage(zpmEnergy, energyNeeded);
		}
	}
	
	private void tryPowerStargate(DHDControlledStargate stargate, ItemStack energyStack)
	{
		if(stargate.getEnergyStored() < getEnergyTarget())
		{
			long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), stargate.getEnergyStored(), maxEnergyTransfer());
			
			// Uses energy from an Energy Item if one is present
			if(InventoryUtil.stackHasEnergy(energyStack))
			{
				IEnergyStorage energyStorage = energyStack.getCapability(ForgeCapabilities.ENERGY).resolve().get();
				IEnergyStorage stargateEnergy = stargate.getEnergyStorage();
				
				if(stargateEnergy != null)
					SGJourneyEnergy.moveEnergy(energyStorage, stargateEnergy, needed);
			}
			else if(SyncedConfig.dhd_holds_zpm.get() && zpmEnergy.hasEnergy())
				zpmEnergy.fillOtherEnergyStorage(stargate.getEnergyStorage(), needed);
			// Uses energy from the DHD energy buffer
			else
			{
				// Using depleteEnergy() here because extracting is disabled in order to avoid pull-based energy systems siphoning energy from the DHD
				long simulatedOutputAmount = energyStorage.depleteEnergy(needed, true);
				long simulatedReceiveAmount = stargate.receiveEnergy(simulatedOutputAmount, true);
				
				energyStorage.depleteEnergy(simulatedReceiveAmount, false);
				stargate.receiveEnergy(simulatedReceiveAmount, false);
			}
		}
	}
	
	@Override
	public void outputEnergy(Direction outputDirection)
	{
		ItemStack energyStack = energyItemHandler.getStackInSlot(0);
		
		// Stores energy in the DHD buffer
		if(energyStorage.getTrueEnergyStored() < minStoredEnergy())
		{
			try
			{
				tryStoreEnergy(energyStack);
			}
			catch(Exception e)
			{
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		// Sends energy to the Stargate
		else
			stargateRun(stargate -> tryPowerStargate(stargate, energyStack));
	}

	public boolean doCallForwarding()
	{
		return this.doCallForwarding;
	}
	
	public boolean hasNetworkRestrictions()
	{
		return this.hasNetworkRestrictions;
	}
	
	public Set<Integer> getNetworks()
	{
		return this.networks;
	}
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getBlockState();
			
			if(gateState.hasProperty(AbstractDHDBlock.FACING))
				this.direction = gateState.getValue(AbstractDHDBlock.FACING);
			else
				StargateJourney.LOGGER.error("Couldn't find DHD Direction");
		}
		
		return this.direction;
	}
	
	public void sendMessageToNearbyPlayers(Component message, int distance)
	{
		AABB localBox = new AABB(getBlockPos()).inflate(distance);
		level.getEntitiesOfClass(Player.class, localBox).forEach((player) -> player.displayClientMessage(message, true));
	}
	
	protected abstract SoundEvent getEnterSound();
	
	protected abstract SoundEvent getPressSound();
	
	// Do not confuse this with setRemoved()
	public void removeDHD()
	{
		dhdRun(dhd -> dhd.receiverCache().clearTwoWays());
	}
	
	@Override
	public void setRemoved()
	{
		if(level != null && level.isClientSide())
			dhdRun(dhd -> dhd.onDHDBlockEntityRemoved());
		super.setRemoved();
	}
	
	@Override
	public void onChunkUnloaded()
	{
		if(level != null && level.isClientSide())
			dhdRun(dhd -> dhd.onDHDBlockEntityUnloaded());
		super.onChunkUnloaded();
	}
	
	@Nullable
	protected abstract D fetchDHD();
	
	@Nullable
	public D getDHD()
	{
		if(dhd == null)
			dhd = fetchDHD();
		
		return dhd;
	}
	
	// Functional Interface stuff
	
	public void dhdRun(Consumer<D> consumer)
	{
		D dhd = getDHD();
		
		if(dhd != null)
			consumer.accept(dhd);
	}
	
	public void dhdRun(Consumer<D> consumer, Runnable defaultAction)
	{
		D dhd = getDHD();
		
		if(dhd != null)
			consumer.accept(dhd);
		else
			defaultAction.run();
	}
	
	public <T> T dhdReturn(Function<D, T> function, @Nullable T defaultValue)
	{
		D dhd = getDHD();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return defaultValue;
	}
	
	public <T> T dhdReturnOrSupply(Function<D, T> function, @NotNull Supplier<T> supplier)
	{
		D dhd = getDHD();
		
		if(dhd != null)
			return function.apply(dhd);
		
		return supplier.get();
	}
	
	public boolean hasStargate()
	{
		return dhdReturn(dhd -> dhd.receiverCache().isPresent(), false);
	}
	
	public void stargateRun(Consumer<DHDControlledStargate> consumer)
	{
		dhdRun(dhd -> dhd.receiverCache().ifPresent(consumer));
	}
	
	public void stargateRun(Consumer<DHDControlledStargate> consumer, Runnable defaultAction)
	{
		dhdRun(dhd -> dhd.receiverCache().ifPresentOrElse(consumer, defaultAction), defaultAction);
	}
	
	public <T> T stargateReturn(Function<DHDControlledStargate, T> function, @Nullable T defaultValue)
	{
		return dhdReturn(dhd -> dhd.receiverCache().returnCachedOrDefault(function, defaultValue), defaultValue);
	}
	
	public <T> T stargateReturnOrSupply(Function<DHDControlledStargate, T> function, @NotNull Supplier<T> supplier)
	{
		return dhdReturnOrSupply(dhd -> dhd.receiverCache().returnCachedOrSupplyDefault(function, supplier), supplier);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	protected LazyOptional<DHD> getDHDCapability()
	{
		D dhd = getDHD();
		
		if(dhd == null)
			return LazyOptional.empty();
		
		return LazyOptional.of(() -> dhd);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == DHD.DHD_CAPABILITY)
			return getDHDCapability().cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	public abstract void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address);
	
	public void pressButton(int index)
	{
		if(index < 0)
		{
			if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE);
			else
			{
				level.playSound(null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
				engageStargate();
			}
			
			if(REQUIRE_ENERGY)
				energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
		}
		else
		{
			if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE);
			else
			{
				level.playSound(null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.5F, 1F);
				encodeSymbol(index);
			}
			
			if(REQUIRE_ENERGY)
				energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
		}
	}
	
	public boolean engageStargate()
	{
		return dhdReturn(DHD::engageStargate, false);
	}
	
	public boolean encodeSymbol(int symbol)
	{
		return dhdReturn(dhd -> dhd.encodeSymbol(symbol), false);
	}
	
	public boolean isSymbolEncoded(int symbol)
	{
		return this.address.containsSymbol(symbol);
	}
	
	public boolean isSymbolRemapped(int symbol)
	{
		return stargateReturn(stargate -> stargate.getSymbolMap().isReplacingSymbol(symbol), false);
	}
	
	public int getRemappedOriginalSymbol(int symbol)
	{
		return stargateReturn(stargate -> stargate.getSymbolMap().getOriginalSymbol(symbol), symbol);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity<?> dhd)
    {
		if(level.isClientSide())
			return;
		
		dhd.outputEnergy(null);
    }
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		if(symbolInfo().pointOfOrigin() != null)
			status.add(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + symbolInfo().pointOfOrigin().location())).withStyle(ChatFormatting.DARK_PURPLE));
		if(symbolInfo().symbols() != null)
			status.add(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbolInfo().symbols().location())).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		stargateRun(stargate -> status.add(Component.translatable("info.sgjourney.stargate_connected").append(Component.literal(": ").append(ComponentHelper.coordinate(stargate.getPosition())))),
			// Else
			() -> status.add(Component.translatable("info.sgjourney.no_stargate_connected").withStyle(ChatFormatting.AQUA)));
		
		return status;
	}
	
	public void setLocalSymbols()
	{
		if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
	}
	
	public void setSymbolsFromStargate()
	{
		if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
		{
			stargateRun(stargate ->
			{
				if(PointOfOrigin.isValid(level.getServer(), stargate.pointOfOrigin()))
					symbolInfo().setPointOfOrigin(stargate.pointOfOrigin());
				else // Use dimension Point of Origin if Stargate Point of Origin isn't valid
					symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
			});
		}
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
		{
			stargateRun(stargate ->
			{
				if(Symbols.isValid(level.getServer(), stargate.symbols()))
					symbolInfo().setSymbols(stargate.symbols());
				else
					symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
			});
		}
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void setGenerationStep(Step step)
	{
		this.generationStep = step;
	}
	
	@Override
	public Step generationStep()
	{
		return generationStep;
	}
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY; // Marks the DHD as ready for generation
	}
	
	public void generate()
	{
		generateEnergyCore();
		generateAdditional(Step.READY);
		
		generationStep = Step.GENERATED;
	}
	
	public void generateAdditional(StructureGenEntity.Step generationStep) {}
	
	public void setToGenerate()
	{
		generationStep = Step.SETUP; //TODO What does this do?
	}
	
	protected abstract void generateEnergyCore();
	
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
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_dhd_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
