package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.Iterator;
import java.util.List;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
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
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDHDEntity extends EnergyBlockEntity implements StructureGenEntity, SymbolInfo.Interface, ProtectedBlockEntity
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
	protected AbstractStargateEntity stargate;
	@Nullable
	protected Vec3i stargateRelativePos;
	
	protected boolean isCenterButtonEngaged;
	protected Address.Mutable address;
	
	protected boolean enableAdvancedProtocols;
	protected boolean enableCallForwarding;
	
	protected long energyTarget;
	protected int maxEnergyTransfer;
	
	protected final ItemStackHandler energyItemHandler;
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler;
	
	protected SymbolInfo symbolInfo;
	
	protected boolean isProtected = false;
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
		
		this.isCenterButtonEngaged = false;
		this.address = new Address.Mutable();
		
		this.enableAdvancedProtocols = false;
		this.enableCallForwarding = false;
		
		this.energyTarget = DEFAULT_ENERGY_TARGET;
		this.maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
		
		this.symbolInfo = new SymbolInfo();;
		
		symbolInfo.setPointOfOrigin(StargateJourney.EMPTY_LOCATION);
		symbolInfo.setSymbols(StargateJourney.EMPTY_LOCATION);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(this.getLevel().isClientSide())
			return;
		
		if(generationStep == StructureGenEntity.Step.READY)
			generate();
		
		this.setStargate();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(STARGATE_POS))
		{
			int[] pos = tag.getIntArray(STARGATE_POS);
			stargateRelativePos = new Vec3i(pos[0], pos[1], pos[2]);
		}
		else
			stargateRelativePos = null;
			
		energyItemHandler.deserializeNBT(tag.getCompound(ENERGY_INVENTORY));
		
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
			tag.putIntArray(STARGATE_POS, new int[] {stargateRelativePos.getX(), stargateRelativePos.getY(), stargateRelativePos.getZ()});
		
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
		
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
		
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
			energyStorage.setEnergy(tag.getLong(ENERGY));
			
			if(tag.contains(POINT_OF_ORIGIN))
				symbolInfo().setPointOfOrigin(new ResourceLocation(tag.getString(POINT_OF_ORIGIN)));
			if(tag.contains(SYMBOLS))
				symbolInfo().setSymbols(new ResourceLocation(tag.getString(SYMBOLS)));
			
			address.fromArray(tag.getIntArray(ADDRESS));
			isCenterButtonEngaged = tag.getBoolean(IS_CENTER_BUTTON_ENGAGED);
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
		return DEFAULT_CONNECTION_DISTANCE;
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget < 0 ? CommonStargateConfig.stargate_energy_capacity.get() : this.energyTarget;
	}
	
	public boolean enableAdvancedProtocols()
	{
		return this.enableAdvancedProtocols;
	}
	
	public long getStargateEnergy()
	{
		if(stargate == null)
			return -1;
		
		return stargate.getEnergyStored();
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
		
		stargate.dhdInfo().setDHD(this, this.enableAdvancedProtocols ? 10 : 0);
	}
	
	protected boolean setStargateFromPos(BlockPos pos)
	{
		BlockEntity blockEntity = this.getLevel().getBlockEntity(pos);
		if(blockEntity instanceof AbstractStargateEntity stargate)
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
			
		updateStargate();
		
		if(stargate != null)
		{
			if(distance(this.getBlockPos(), stargate.getBlockPos()) > getMaxDistance())
				unsetStargate();
			
			return;
		}

		if(stargateRelativePos == null)
			stargateRelativePos = findNearestStargate(getMaxDistance());

		if(stargateRelativePos != null)
		{
			Vec3i pos = stargateRelativePos;
			Direction direction = getDirection();
			
			if(direction != null)
			{
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(direction, this.getBlockPos(), pos);
				
				if(stargatePos != null && !setStargateFromPos(stargatePos))
					stargateRelativePos = null;
			}
		}
		
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
		return getEnergyCapacity() * 2 / 3;
	}
	
	public abstract long maxEnergyDeplete();
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	@Override
	public long maxExtract()
	{
		return CommonDHDConfig.milky_way_dhd_max_energy_extract.get();
	}
	
	private void tryStoreEnergy(ItemStack energyStack)
	{
		ItemStack inputStack = energyItemHandler.getStackInSlot(1);
		// Generates energy if needed
		if(energyStack.getItem() instanceof IEnergyCore energyCore && energyCore.maxGeneratedEnergy(energyStack, energyItemHandler.getStackInSlot(1)) <= (getEnergyCapacity() - getEnergyStored()))
		{
			long generatedEnergy = energyCore.generateEnergy(energyStack, inputStack);
			
			if(generatedEnergy > 0)
				receiveEnergy(generatedEnergy, false);
		}
		else if(energyStack.getCapability(ForgeCapabilities.ENERGY).isPresent())
		{
			energyStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy ->
			{
				if(energy instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long energyNeeded = getEnergyCapacity() - getEnergyStored();
					long energyExtracted = sgjourneyEnergy.extractLongEnergy(energyNeeded, false);
					receiveEnergy(energyExtracted, false);
				}
				else
				{
					int energyNeeded = (int) Math.min(getEnergyCapacity() - getEnergyStored(), Integer.MAX_VALUE);
					int energyExtracted = energy.extractEnergy(energyNeeded, false);
					receiveEnergy(energyExtracted, false);
				}
			});
		}
	}
	
	private void tryPowerStargate(ItemStack energyStack)
	{
		if(stargate.getEnergyStored() < getEnergyTarget())
		{
			long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), stargate.getEnergyStored(), maxEnergyDeplete());
			
			// Uses energy from a Energy Item if one is present
			if(InventoryUtil.stackHasEnergy(energyStack))
			{
				IEnergyStorage energyStorage = energyStack.getCapability(ForgeCapabilities.ENERGY).resolve().get();
				
				if (energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long energySent = sgjourneyEnergy.extractLongEnergy(needed, false);
					stargate.receiveEnergy(energySent, false);
				}
				else
				{
					int energySent = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(needed), false);
					stargate.receiveEnergy(energySent, false);
				}
			}
			// Uses energy from the DHD energy buffer
			else
			{
				long energySent = depleteEnergy(needed, false);
				stargate.receiveEnergy(energySent, false);
			}
		}
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		if(this.stargate == null)
			return;
		
		ItemStack energyStack = energyItemHandler.getStackInSlot(0);
		
		// Stores energy in the DHD buffer
		if(getEnergyStored() < minStoredEnergy())
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
			tryPowerStargate(energyStack);
	}

	public void setCallForwardingState(boolean enableCallForwarding)
	{
		this.enableCallForwarding = enableCallForwarding;
	}

	public boolean callForwardingEnabled()
	{
		return this.enableCallForwarding;
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
		List<AbstractStargateEntity> stargates = LocatorHelper.getNearbyStargates(this.getLevel(), this.getBlockPos(), maxDistance);
		
		stargates.sort((stargateA, stargateB) ->
				Double.valueOf(distance(this.getBlockPos(), stargateA.getBlockPos()))
				.compareTo(Double.valueOf(distance(this.getBlockPos(), stargateB.getBlockPos()))));
		
		if(!stargates.isEmpty())
		{
			Iterator<AbstractStargateEntity> iterator = stargates.iterator();
			
			while(iterator.hasNext())
			{
				AbstractStargateEntity stargate = iterator.next();
				
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
		AABB localBox = new AABB((getBlockPos().getX() - distance), (getBlockPos().getY() - distance), (getBlockPos().getZ() - distance), 
				(getBlockPos().getX() + 1 + distance), (getBlockPos().getY() + 1 + distance), (getBlockPos().getZ() + 1 + distance));
		level.getEntitiesOfClass(Player.class, localBox).stream().forEach((player) -> player.displayClientMessage(message, true));
	}
	
	protected abstract SoundEvent getEnterSound();
	
	protected abstract SoundEvent getPressSound();
	
	public abstract void onDialAttempt(StargateInfo.Feedback feedback, Address address);
	
	/**
	 * Engages the next Stargate chevron
	 * @param symbol
	 */
	public void engageChevron(int symbol)
	{
		if(this.stargate != null)
		{
			if(REQUIRE_ENERGY && getEnergyStored() < buttonPressEnergyCost())
			{
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), 5);
				return;
			}
			
			if(symbol == 0)
				level.playSound(null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
			else
				level.playSound(null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.5F, 1F);
			
			stargate.dhdEngageSymbol(symbol);
			
			if(REQUIRE_ENERGY)
				depleteEnergy(buttonPressEnergyCost(), false);
		}
		else
			sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), 5);
	}
	
	public boolean isSymbolEngaged(int symbol)
	{
		return this.address.containsSymbol(symbol);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;
		
		dhd.outputEnergy(null);
		dhd.updateClient();
    }
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY;
	}
	
	public void generate()
	{
		generateEnergyCore();
		
		generationStep = Step.GENERATED;
	}
	
	public void setToGenerate()
	{
		generationStep = Step.SETUP;
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
