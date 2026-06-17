package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.*;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.energy_cores.IEnergyCore;
import net.povstalec.sgjourney.common.misc.*;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.info.SymbolInfo;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.dhd.AbstractDHDBlock;
import net.povstalec.sgjourney.common.sgjourney.Address;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDHDEntity extends EnergyBlockEntity implements StructureGenEntity, SymbolInfo.Interface, ProtectedBlockEntity, PDAStatus, AutoCache.IController<AbstractDHDEntity, AbstractStargateEntity<?>>
{
	public static final int DHD_INFO_DISTANCE = 3;
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public static final String IS_CENTER_BUTTON_ENGAGED = "is_center_button_engaged";
	public static final String ADDRESS = Address.ADDRESS;
	
	//TODO A temporary addition to make sure people can use DHDs for energy transfer even after updating from older versions
	public static final String CRYSTAL_MODE = "CrystalMode";
	public static final String ENERGY_TRANSFER = "ENERGY_TRANSFER";
	
	public static final String STARGATE_POS = "stargate_pos";
	
	public static final int DEFAULT_ENERGY_TARGET = 0;
	public static final int DEFAULT_ENERGY_TRANSFER = 0;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	protected boolean isCenterButtonEngaged = false;
	protected Address.Mutable address = new Address.Mutable();
	
	protected boolean enableAdvancedProtocols = false;
	protected boolean enableCallForwarding = false;
	protected boolean hasNetworkRestrictions = false;
	protected Set<Integer> networks = new TreeSet<>();
	
	protected long energyTarget = DEFAULT_ENERGY_TARGET;
	protected long maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
	protected int maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE;
	
	public final ItemStackHandler energyItemHandler;
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler;
	
	protected SymbolInfo symbolInfo;
	
	protected boolean isProtected = false;
	
	@Nullable
	protected Vec3i stargateRelativePos = null;
	public final AutoCache.Receiver<AbstractDHDEntity, AbstractStargateEntity<?>> stargateCache = new AutoCache.Receiver<>(this);
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
		
		this.symbolInfo = new SymbolInfo();
	}
	
	@Override
	public void onLoad()
	{
		if(getLevel().isClientSide())
		{
			// Revalidation
			stargateCache.setRevalidate(() ->
			{
				if(stargateRelativePos == null)
					return false;
				
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
				if(stargatePos != null && level.getBlockEntity(stargatePos) instanceof AbstractStargateEntity<?> stargate)
					return stargateCache.getCached() == stargate; // Check if the Stargate at the saved pos is the same Stargate
				
				return false;
			});
			// Client will only ever attempt to fetch Stargate from the relative pos provided by syncing
			stargateCache.setFetch(() ->
			{
				if(stargateRelativePos == null)
					return null;
				
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
				if(stargatePos != null && level.getBlockEntity(stargatePos) instanceof AbstractStargateEntity<?> stargate)
					return stargate;
				
				return null;
			});
		}
		else
		{
			// Revalidation - check if it's not too far
			stargateCache.setRevalidate(() ->
			{
				if(stargateRelativePos == null)
					return false;
				
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), stargateRelativePos);
				if(stargatePos != null && level.getBlockEntity(stargatePos) instanceof AbstractStargateEntity<?> stargate)
					return stargateCache.getCached() == stargate && CoordinateHelper.Relative.distanceSqr(stargatePos, getBlockPos()) <= getMaxConnectionDistanceSqr(); // Check if the Stargate at the saved pos is the same Stargate
				
				return false;
			});
			// Find nearest Stargate that isn't connected to a DHD
			stargateCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractStargateEntity.class, level, worldPosition, maxConnectionDistance,
					stargate -> !stargate.dhdCache.isCached()));
			
			stargateCache.setOnChanged((oldStargate, newStargate) ->
			{
				if(newStargate != null)
					stargateRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), newStargate.getBlockPos());
				else
					stargateRelativePos = null;
				
				updateClient();
			});
			
			if(generationStep == StructureGenEntity.Step.READY)
				generate(); //TODO Logic of loading the DHD Symbols after Stargate, but finding Stargate after generating inventory (do this once there's a loot table for the DHD inventory, don't forget generateAdditional is fired by DHDItem)
			
			updateClient();
		}
		
		super.onLoad();
		stargateCache.fetch(); // Fetch when loading to prevent shenanigans with connections being formed and broken due to not having fetched yet
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
		energyStorage.setEnergy(tag.getLong(ENERGY));
		
		symbolInfo.loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		address.fromArray(tag.getIntArray(ADDRESS));
		isCenterButtonEngaged = tag.getBoolean(IS_CENTER_BUTTON_ENGAGED);
		
		if(tag.contains(STARGATE_POS, Tag.TAG_INT_ARRAY))
			stargateRelativePos = Conversion.intArrayToVec(tag.getIntArray(STARGATE_POS));
		else
			stargateRelativePos = null;
		stargateCache.markDirty();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
			handleUpdateTag(tag);
	}
	
	@Override
	public AutoCache.Receiver<AbstractDHDEntity, AbstractStargateEntity<?>> receiverCache()
	{
		return stargateCache;
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
				setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				if(slot == 0)
					return stack.getItem() instanceof IEnergyCore || stack.getItem() instanceof ZeroPointModule || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
				
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
	
	
	
	public int getMaxConnectionDistance()
	{
		return maxConnectionDistance;
	}
	
	public long getMaxConnectionDistanceSqr()
	{
		return (long) maxConnectionDistance * maxConnectionDistance;
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget < 0 ? CommonStargateConfig.stargate_energy_capacity.get() : this.energyTarget;
	}
	
	public boolean enableAdvancedProtocols()
	{
		return this.enableAdvancedProtocols;
	}
	
	public int autoclose()
	{
		return enableAdvancedProtocols() ? 10 : 0;
	}
	
	public long getStargateEnergy()
	{
		return stargateCache.returnOrDefault(stargate -> stargate.energyStorage.getTrueEnergyStored(), -1L);
	}
	
	public int getStargateOpenTime()
	{
		return stargateCache.returnOrDefault(AbstractStargateEntity::getOpenTime, 0);
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		return stargateCache.returnOrDefault(AbstractStargateEntity::getTimeSinceLastTraveler, 0);
	}
	
	public void updateDHD(Address.Mutable address, boolean isCenterButtonEngaged)
	{
		stargateCache.ifPresentOrElse(stargate -> setAddress(stargate.symbolMap.remapAddress(address)), () -> setAddress(address));
		this.setCenterButtonEngaged(isCenterButtonEngaged);
		updateClient();
	}
	
	public void setAddress(Address.Mutable address)
	{
		this.address = address;
	}
	
	public Address getAddress()
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
	
	protected abstract long buttonPressEnergyCost();
	
	public long minStoredEnergy()
	{
		return energyStorage.getTrueMaxEnergyStored() * 2 / 3;
	}
	
	public abstract long maxEnergyTransfer();
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return true;
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
			energyStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy ->
			{
				if(energy instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long energyNeeded = energyStorage.getTrueMaxEnergyStored() - energyStorage.getTrueEnergyStored();
					long energyExtracted = sgjourneyEnergy.extractLongEnergy(energyNeeded, false);
					energyStorage.receiveLongEnergy(energyExtracted, false);
				}
				else
				{
					int energyNeeded = (int) Math.min(energyStorage.getTrueMaxEnergyStored() - energyStorage.getTrueEnergyStored(), Integer.MAX_VALUE);
					int energyExtracted = energy.extractEnergy(energyNeeded, false);
					energyStorage.receiveLongEnergy(energyExtracted, false);
				}
			});
		}
	}
	
	private void tryPowerStargate(AbstractStargateEntity<?> stargate, ItemStack energyStack)
	{
		if(stargate.energyStorage.getTrueEnergyStored() < getEnergyTarget())
		{
			long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), stargate.energyStorage.getTrueEnergyStored(), maxEnergyTransfer());
			
			// Uses energy from an Energy Item if one is present
			if(InventoryUtil.stackHasEnergy(energyStack))
			{
				IEnergyStorage energyStorage = energyStack.getCapability(ForgeCapabilities.ENERGY).resolve().get();
				
				if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long energySent = sgjourneyEnergy.extractLongEnergy(needed, false);
					stargate.energyStorage.receiveLongEnergy(energySent, false);
				}
				else
				{
					int energySent = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(needed), false);
					stargate.energyStorage.receiveLongEnergy(energySent, false);
				}
			}
			// Uses energy from the DHD energy buffer
			else
			{
				long energySent = energyStorage.depleteEnergy(needed, false);
				stargate.energyStorage.receiveLongEnergy(energySent, false);
			}
		}
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
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
			stargateCache.ifPresent(stargate -> tryPowerStargate(stargate, energyStack));
	}

	public void setCallForwardingState(boolean enableCallForwarding)
	{
		this.enableCallForwarding = enableCallForwarding;
	}

	public boolean callForwardingEnabled()
	{
		return this.enableCallForwarding;
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
	
	public abstract void onDialAttempt(StargateInfo.Feedback feedback, Address address);
	
	public void pressButton(int index)
	{
		if(index < 0)
			engageStargate();
		else
			encodeSymbol(index);
	}
	
	public void engageStargate()
	{
		stargateCache.ifPresentOrElse(stargate ->
				{
					if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
					{
						sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE);
						return;
					}
					
					level.playSound(null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
					
					stargate.dhdEngageStargate();
					stargate.updateDHD(this);
					
					if(REQUIRE_ENERGY)
						energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
				},
				() -> sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE));
	}
	
	public void encodeSymbol(int symbol)
	{
		stargateCache.ifPresentOrElse(stargate ->
				{
					if(stargate.isConnected())
					{
						sendMessageToNearbyPlayers(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.getFeedbackMessage(), DHD_INFO_DISTANCE);
						return;
					}
					
					if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
					{
						sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE);
						return;
					}
					
					level.playSound(null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.5F, 1F);
					
					// Remap symbol if needed while Advanced Protocols are enabled
					if(enableAdvancedProtocols() && !stargate.symbolMap.isSymbolMapped(symbol))
						stargate.indirectEngageSymbol(stargate.symbolMap.remapToRandomSymbol(symbol, this.address.getArray()), false);
					else
						stargate.indirectEngageSymbol(symbol, false);
					stargate.updateDHD(this);
					
					if(REQUIRE_ENERGY)
						energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
				},
				() -> sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), DHD_INFO_DISTANCE));
	}
	
	public boolean isSymbolEncoded(int symbol)
	{
		return this.address.containsSymbol(symbol);
	}
	
	public boolean isSymbolRemapped(int symbol)
	{
		return stargateCache.returnOrDefault(stargate -> stargate.symbolMap.isReplacingSymbol(symbol), false);
	}
	
	public int getRemappedOriginalSymbol(int symbol)
	{
		return stargateCache.returnOrDefault(stargate -> stargate.symbolMap.getOriginalSymbol(symbol), symbol);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
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
		
		if(stargateCache.isPresent())
			status.add(Component.translatable("info.sgjourney.stargate_connected").append(Component.literal(": ").append(ComponentHelper.coordinate(stargateCache.get().getBlockPos()))).withStyle(ChatFormatting.AQUA));
		else
			status.add(Component.translatable("info.sgjourney.no_stargate_connected").withStyle(ChatFormatting.AQUA));
		
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
			if(PointOfOrigin.isValid(level.getServer(), stargateCache.get().symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(stargateCache.get().symbolInfo().pointOfOrigin());
			else // Use dimension Point of Origin if Stargate Point of Origin isn't valid
				symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		}
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
		{
			if(Symbols.isValid(level.getServer(), stargateCache.get().symbolInfo().symbols()))
				symbolInfo().setSymbols(stargateCache.get().symbolInfo().symbols());
			else
				symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
		}
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
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
