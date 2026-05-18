package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import net.povstalec.sgjourney.common.sgjourney.info.DHDInfo;
import net.povstalec.sgjourney.common.sgjourney.info.SymbolInfo;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public abstract class AbstractDHDEntity extends EnergyBlockEntity implements StructureGenEntity, SymbolInfo.Interface, ProtectedBlockEntity, PDAStatus
{
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public static final String IS_CENTER_BUTTON_ENGAGED = "is_center_button_engaged";
	public static final String ADDRESS = Address.ADDRESS;
	
	//TODO A temporary addition to make sure people can use DHDs for energy transfer even after updating from older versions
	public static final String CRYSTAL_MODE = "CrystalMode";
	public static final String ENERGY_TRANSFER = "ENERGY_TRANSFER";
	
	public static final String STARGATE_POS = "StargatePos";
	
	public static final int DEFAULT_ENERGY_TARGET = 150000;
	public static final int DEFAULT_ENERGY_TRANSFER = 2500;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	@Nullable
	protected AbstractStargateEntity<?> stargate = null;
	@Nullable
	protected Vec3i stargateRelativePos = null;
	
	protected boolean isCenterButtonEngaged = false;
	protected Address.Mutable address = new Address.Mutable();
	
	protected boolean enableAdvancedProtocols = false;
	protected boolean enableCallForwarding = false;
	protected boolean hasNetworkRestrictions = false;
	protected Set<Integer> networks = new HashSet<>();
	
	protected long energyTarget = DEFAULT_ENERGY_TARGET;
	protected long maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
	protected int maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE;
	
	public final ItemStackHandler energyItemHandler;
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler;
	
	protected SymbolInfo symbolInfo;
	
	protected boolean isProtected = false;
	
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
		super.onLoad();
		
		if(getLevel().isClientSide())
			return;
		
		setStargate();
		
		if(generationStep == StructureGenEntity.Step.READY)
			generate();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(STARGATE_POS))
			stargateRelativePos = Conversion.intArrayToVec(tag.getIntArray(STARGATE_POS));
		else
			stargateRelativePos = null;
			
		energyItemHandler.deserializeNBT(tag.getCompound(ENERGY_INVENTORY));
		InventoryUtil.expandSlotsIfNeeded(energyItemHandler, 2);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		super.load(tag);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		if(stargateRelativePos != null)
			tag.putIntArray(STARGATE_POS, Conversion.vecToIntArray(stargateRelativePos));
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT());
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(isProtected)
			tag.putBoolean(PROTECTED, true);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		
		if(stargateRelativePos != null)
			tag.putIntArray(STARGATE_POS, Conversion.vecToIntArray(stargateRelativePos));
		
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		address.saveToCompoundTag(tag, ADDRESS);
		
		tag.putBoolean(IS_CENTER_BUTTON_ENGAGED, isCenterButtonEngaged);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			if(tag.contains(STARGATE_POS))
				stargateRelativePos = Conversion.intArrayToVec(tag.getIntArray(STARGATE_POS));
			else
				stargateRelativePos = null;
			
			energyStorage.setEnergy(tag.getLong(ENERGY));
			
			symbolInfo.loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
			
			address.fromArray(tag.getIntArray(ADDRESS));
			isCenterButtonEngaged = tag.getBoolean(IS_CENTER_BUTTON_ENGAGED);
			
			if(stargateRelativePos != null)
				setStargateFromRelativePos(stargateRelativePos);
		}
	}
	
	public SymbolInfo symbolInfo()
	{
		return this.symbolInfo;
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
	
	
	
	public int getMaxDistance()
	{
		return maxConnectionDistance;
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
		if(stargate == null)
			return -1;
		
		return stargate.energyStorage.getTrueEnergyStored();
	}
	
	public int getStargateOpenTime()
	{
		if(stargate == null)
			return 0;
		
		return stargate.getOpenTime();
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		if(stargate == null)
			return 0;
		
		return stargate.getTimeSinceLastTraveler();
	}
	
	protected void updateStargate()
	{
		if(stargate == null)
			return;
		
		stargate.dhdInfo().setDHD(this);
	}
	
	protected boolean setStargateFromRelativePos(@NotNull Vec3i relative)
	{
		Direction direction = getDirection();
		
		if(direction != null)
		{
			BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(direction, this.getBlockPos(), relative);
			if(stargatePos == null)
				return false;
			
			return setStargateFromPos(stargatePos);
		}
		
		return false;
	}
	
	protected boolean setStargateFromPos(@NotNull BlockPos pos)
	{
		BlockEntity blockEntity = this.getLevel().getBlockEntity(pos);
		if(blockEntity instanceof AbstractStargateEntity<?> stargate)
		{
			this.stargate = stargate;
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Sets the DHD's Stargate to the position specified by the DHD's saved Stargate relative position.
	 * If there is no Stargate at that position, the DHD will remove that position from its memory.
	 * If there is no position saved, DHD will attempt to find a new Stargate
	 */
	public void setStargate()
	{
		if(this.getLevel() == null)
			return;
		
		if(stargate != null)
		{
			if(distance(this.getBlockPos(), stargate.getBlockPos()) > getMaxDistance())
				unsetStargate();
			else
			{
				updateStargate();
				return;
			}
		}

		if(stargateRelativePos == null)
			stargateRelativePos = findNearestStargate(getMaxDistance());

		if(stargateRelativePos != null)
		{
			if(!setStargateFromRelativePos(stargateRelativePos))
				stargateRelativePos = null;
		}
		
		updateStargate();
		
		this.setChanged();
	}
	
	public void unsetStargate()
	{
		if(stargate != null)
		{
			stargate.dhdInfo().unsetDHD(false);
			stargate = null;
		}
		
		stargateRelativePos = null;
		
		updateDHD(new Address.Mutable(), false);
		
		this.setChanged();
	}
	
	public void updateDHD(Address.Mutable address, boolean isStargateConnected)
	{
		if(stargate != null)
			this.setAddress(stargate.symbolMap.remapAddress(address));
		else
			this.setAddress(address);
		this.setCenterButtonEngaged(isStargateConnected);
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
	
	public abstract long maxEnergyDeplete();
	
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
		return CommonDHDConfig.milky_way_dhd_max_energy_extract.get();
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
			long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), stargate.energyStorage.getTrueEnergyStored(), maxEnergyDeplete());
			
			// Uses energy from a Energy Item if one is present
			if(InventoryUtil.stackHasEnergy(energyStack))
			{
				IEnergyStorage energyStorage = energyStack.getCapability(ForgeCapabilities.ENERGY).resolve().get();
				
				if (energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
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
		else if(this.stargate != null)
			tryPowerStargate(this.stargate, energyStack);
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
    
    protected BlockState getState()
    {
    	BlockPos gatePos = this.getBlockPos();
		return this.level.getBlockState(gatePos);
    }
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getState();
			
			if(gateState.getBlock() instanceof AbstractDHDBlock)
				this.direction = gateState.getValue(AbstractDHDBlock.FACING);
			else
				StargateJourney.LOGGER.error("Couldn't find DHD Direction");
		}
		
		return this.direction;
	}
	
	private double distance(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		double stargateDistance = Math.sqrt(x*x + y*y + z*z);
		
		return stargateDistance;
	}
	
	public Vec3i findNearestStargate(int maxDistance)
	{
		List<AbstractStargateEntity<?>> stargates = LocatorHelper.getNearbyStargates(this.getLevel(), this.getBlockPos(), maxDistance);
		
		stargates.sort((stargateA, stargateB) ->
				Double.compare(distance(this.getBlockPos(), stargateA.getBlockPos()), distance(this.getBlockPos(), stargateB.getBlockPos())));
		
		if(!stargates.isEmpty())
		{
			
			for(AbstractStargateEntity<?> stargate : stargates)
			{
				stargate.dhdInfo().revalidateDHD();
				if(!stargate.dhdInfo().hasDHD())
				{
					Direction direction = getDirection();
					
					if(direction != null)
					{
						this.stargate = stargate;
						return CoordinateHelper.Relative.getRelativeOffset(direction, this.getBlockPos(), stargate.getBlockPos());
					}
				}
				
			}
		}
		
		return null;
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
		if(stargate != null)
		{
			if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
			{
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHDInfo.DHD_INFO_DISTANCE);
				return;
			}
			
			level.playSound(null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
			
			stargate.dhdEngageStargate();
			
			if(REQUIRE_ENERGY)
				energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
		}
		else
			sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), DHDInfo.DHD_INFO_DISTANCE);
	}
	
	public void encodeSymbol(int symbol)
	{
		if(stargate != null)
		{
			if(stargate.isConnected())
			{
				sendMessageToNearbyPlayers(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.getFeedbackMessage(), DHDInfo.DHD_INFO_DISTANCE);
				return;
			}
			
			if(REQUIRE_ENERGY && energyStorage.getTrueEnergyStored() < buttonPressEnergyCost())
			{
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), DHDInfo.DHD_INFO_DISTANCE);
				return;
			}
			
			level.playSound(null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.5F, 1F);
			
			// Remap symbol if needed while Advanced Protocols are enabled
			if(enableAdvancedProtocols() && !stargate.symbolMap.isSymbolMapped(symbol))
				symbol = stargate.symbolMap.remapToRandomSymbol(symbol, this.address.getArray());
			
			stargate.indirectEngageSymbol(symbol, false);
			
			if(REQUIRE_ENERGY)
				energyStorage.depleteEnergy(buttonPressEnergyCost(), false);
		}
		else
			sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), DHDInfo.DHD_INFO_DISTANCE);
	}
	
	public boolean isSymbolEncoded(int symbol)
	{
		return this.address.containsSymbol(symbol);
	}
	
	public boolean isSymbolRemapped(int symbol)
	{
		if(this.stargate == null)
			return false;
		
		return this.stargate.symbolMap.isReplacingSymbol(symbol);
	}
	
	public int getRemappedOriginalSymbol(int symbol)
	{
		if(this.stargate == null)
			return symbol;
		
		return this.stargate.symbolMap.getOriginalSymbol(symbol);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;
		
		dhd.outputEnergy(null);
		dhd.updateClient();
    }
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		status.add(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + symbolInfo().pointOfOrigin())).withStyle(ChatFormatting.DARK_PURPLE));
		status.add(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbolInfo().symbols())).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		if(this.stargate != null)
			status.add(Component.translatable("info.sgjourney.stargate_connected").append(Component.literal(": " + this.stargate.get9ChevronAddress())).withStyle(ChatFormatting.AQUA));
		else
			status.add(Component.translatable("info.sgjourney.no_stargate_connected").withStyle(ChatFormatting.RED));
			
		
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
			if(PointOfOrigin.isValid(level.getServer(), this.stargate.symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(this.stargate.symbolInfo().pointOfOrigin());
			else // Use dimension Point of Origin if Stargate Point of Origin isn't valid
				symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		}
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
		{
			if(Symbols.isValid(level.getServer(), this.stargate.symbolInfo().symbols()))
				symbolInfo().setSymbols(this.stargate.symbolInfo().symbols());
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
