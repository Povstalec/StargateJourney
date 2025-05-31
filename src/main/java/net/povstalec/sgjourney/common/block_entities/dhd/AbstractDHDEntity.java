package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.items.energy_cores.IEnergyCore;
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
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.dhd.AbstractDHDBlock;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.packets.ClientboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Address;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDHDEntity extends EnergyBlockEntity implements StructureGenEntity, SymbolInfo.Interface, ProtectedBlockEntity
{
	public static final String PROTECTED = "protected";
	
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public static final String STARGATE_POS = "stargate_pos";
	
	public static final int DEFAULT_ENERGY_TARGET = 150000;
	public static final int DEFAULT_ENERGY_TRANSFER = 2500;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	@Nullable
	private AbstractStargateEntity stargate;
	@Nullable
	protected Vec3i stargateRelativePos;
	
	protected boolean isCenterButtonEngaged;
	protected Address address;
	
	protected boolean enableAdvancedProtocols;
	protected boolean enableCallForwarding;
	
	protected long energyTarget;
	protected int maxEnergyTransfer;
	
	protected final ItemStackHandler energyItemHandler;
	protected final Lazy<IItemHandler> lazyEnergyItemHandler;
	
	protected SymbolInfo symbolInfo;
	
	protected boolean isProtected = false;
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
		
		this.isCenterButtonEngaged = false;
		this.address = new Address(true);
		
		this.enableAdvancedProtocols = false;
		this.enableCallForwarding = false;
		
		this.energyTarget = DEFAULT_ENERGY_TARGET;
		this.maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = Lazy.of(() -> energyItemHandler);
		
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
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		if(tag.contains(STARGATE_POS))
		{
			int[] pos = tag.getIntArray(STARGATE_POS);
			stargateRelativePos = new Vec3i(pos[0], pos[1], pos[2]);
		}
		else
			stargateRelativePos = null;
			
		energyItemHandler.deserializeNBT(registries, tag.getCompound(ENERGY_INVENTORY));
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		super.loadAdditional(tag, registries);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		if(stargateRelativePos != null)
			tag.putIntArray(STARGATE_POS, new int[] {stargateRelativePos.getX(), stargateRelativePos.getY(), stargateRelativePos.getZ()});
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT(registries));
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(isProtected)
			tag.putBoolean(PROTECTED, true);
	}
	
	public SymbolInfo symbolInfo()
	{
		return this.symbolInfo;
	}
	
	
	
	@Override
	public void invalidateCapabilities()
	{
		lazyEnergyItemHandler.invalidate();
		
		super.invalidateCapabilities();
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
					return stack.getItem() instanceof IEnergyCore || stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
				
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
		
		updateDHD(new Address(), false);
		
		this.setChanged();
	}
	
	public void updateDHD(Address address, boolean isStargateConnected)
	{
		this.setAddress(address);
		this.setCenterButtonEngaged(isStargateConnected);
	}
	
	public void setAddress(Address address)
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
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IItemHandler getEnergyItemHandler(Direction side)
	{
		return lazyEnergyItemHandler.get();
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
	
	private boolean stackHasEnergy(ItemStack stack)
	{
		IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(energyStorage != null)
			return energyStorage.canExtract() && energyStorage.getEnergyStored() > 0;
		
		return false;
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
			ItemStack inputStack = energyItemHandler.getStackInSlot(1);
			// Generates energy if needed
			if(energyStack.getItem() instanceof IEnergyCore energyCore && energyCore.maxGeneratedEnergy(energyStack, inputStack) <= (getEnergyCapacity() - getEnergyStored()))
			{
				long generatedEnergy = energyCore.generateEnergy(energyStack, inputStack);
				
				if(generatedEnergy > 0)
					receiveEnergy(generatedEnergy, false);
			}
			else if(energyStack.getCapability(Capabilities.EnergyStorage.ITEM) != null)
			{
				IEnergyStorage energyStorage = energyStack.getCapability(Capabilities.EnergyStorage.ITEM);
				if(energyStorage instanceof ZeroPointEnergy zpmEnergy)
				{
					long energyNeeded = getEnergyCapacity() - getEnergyStored();
					long energyExtracted = zpmEnergy.extractLongEnergy(energyNeeded, false);
					receiveEnergy(energyExtracted, false);
				}
				else
				{
					int energyNeeded = (int) Math.min(getEnergyCapacity() - getEnergyStored(), Integer.MAX_VALUE);
					int energyExtracted = energyStorage.extractEnergy(energyNeeded, false);
					receiveEnergy(energyExtracted, false);
				}
			}
		}
		// Sends energy to the Stargate
		else
		{
			if(stargate.getEnergyStored() < getEnergyTarget())
			{
				long needed = getEnergyTarget() - stargate.getEnergyStored();
				
				// Uses energy from a Energy Item if one is present
				if (stackHasEnergy(energyStack))
				{
					IEnergyStorage energyStorage = energyStack.getCapability(Capabilities.EnergyStorage.ITEM);
					
					if (energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
					{
						long energySent = sgjourneyEnergy.extractLongEnergy(Math.min(maxEnergyDeplete(), needed), false);
						stargate.receiveEnergy(energySent, false);
					} else
					{
						int energySent = energyStorage.extractEnergy(Math.min(Integer.MAX_VALUE, (int) Math.min(maxEnergyDeplete(), needed)), false);
						stargate.receiveEnergy(energySent, false);
					}
				}
				// Uses energy from the DHD energy buffer
				else
				{
					long energySent = depleteEnergy(Math.min(maxEnergyDeplete(), needed), false);
					stargate.receiveEnergy(energySent, false);
				}
			}
		}
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
	
	protected List<AbstractStargateEntity> getNearbyStargates(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate &&
							distance(this.getBlockPos(), stargate.getBlockPos()) <= maxDistance)
						stargates.add(stargate);
				});
			}
		}
		
		return stargates;
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
		List<AbstractStargateEntity> stargates = getNearbyStargates(maxDistance);
		
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
	
	/**
	 * Engages the next Stargate chevron
	 * @param symbol
	 */
	public void engageChevron(int symbol)
	{
		if(this.stargate != null)
		{
			if(!StargateJourneyConfig.disable_energy_use.get() && getEnergyStored() < buttonPressEnergyCost())
			{
				sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), 5);
				return;
			}
			
			if(symbol == 0)
				level.playSound(null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
			else
				level.playSound(null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.25F, 1F);
			
			stargate.dhdEngageSymbol(symbol);
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
	
	public void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientboundDHDUpdatePacket(this.worldPosition, getEnergyStored(), symbolInfo().pointOfOrigin(), symbolInfo().symbols(), this.address.toArray(), this.isCenterButtonEngaged));
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
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_dhd_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
