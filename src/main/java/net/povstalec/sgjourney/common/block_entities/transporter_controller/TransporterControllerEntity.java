package net.povstalec.sgjourney.common.block_entities.transporter_controller;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.misc.*;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class TransporterControllerEntity extends EnergyBlockEntity implements StructureGenEntity, ProtectedBlockEntity, PDAStatus,
		AutoCache.IController<TransporterControllerEntity, AbstractTransporterEntity<?>>
{
	public static final String TRANSPORTER_POS = "transporter_pos";
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public static final int CONTROLLER_INFO_DISTANCE = 5;
	
	public static final int DEFAULT_ENERGY_TARGET = 0;
	public static final int DEFAULT_ENERGY_TRANSFER = 0;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected Direction direction;
	
	protected Set<Integer> networks = new TreeSet<>();
	
	protected long energyTarget = DEFAULT_ENERGY_TARGET;
	protected long maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
	protected int maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE; // Max distance from which it can connect to a Transporter and control it
	
	protected final ItemStackHandler energyItemHandler = createEnergyItemHandler();
	protected final Lazy<IItemHandler> lazyEnergyItemHandler = Lazy.of(() -> energyItemHandler);
	
	@Nullable
	protected Vec3i transporterRelativePos = null;
	public final AutoCache.Receiver<TransporterControllerEntity, AbstractTransporterEntity<?>> transporterCache = new AutoCache.Receiver<>(this);
	
	protected boolean isProtected = false;
	
	public TransporterControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		if(getLevel().isClientSide())
		{
			// Revalidation
			transporterCache.setRevalidate(() ->
			{
				if(transporterRelativePos == null)
					return false;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporterCache.getCached() == transporter; // Check if the Transporter at the saved pos is the same Transporter
				
				return false;
			});
			// Client will only ever attempt to fetch Transporter from the relative pos provided by syncing
			transporterCache.setFetch(() ->
			{
				if(transporterRelativePos == null)
					return null;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporter;
				
				return null;
			});
		}
		else
		{
			// Revalidation - check if it's not too far
			transporterCache.setRevalidate(() ->
			{
				if(transporterRelativePos == null)
					return false;
				
				BlockPos transporterPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), transporterRelativePos);
				if(transporterPos != null && level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?> transporter)
					return transporterCache.getCached() == transporter && CoordinateHelper.Relative.distanceSqr(transporterPos, getBlockPos()) <= getMaxConnectionDistanceSqr(); // Check if the Transporter at the saved pos is the same Transporter
				
				return false;
			});
			// Find nearest Transporter that isn't connected to a Controller
			transporterCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractTransporterEntity.class, level, worldPosition, maxConnectionDistance,
					transporter -> !transporter.controllerCache.isCached()));
			
			transporterCache.setOnChanged((oldTransporter, newTransporter) ->
			{
				if(newTransporter != null)
					transporterRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), newTransporter.getBlockPos());
				else
					transporterRelativePos = null;
				
				updateClient();
			});
			
			if(generationStep == StructureGenEntity.Step.READY)
				generate();
			
			updateClient();
		}
		
		super.onLoad();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
		
		if(tag.contains(TRANSPORTER_POS, Tag.TAG_INT_ARRAY))
			transporterRelativePos = Conversion.intArrayToVec(tag.getIntArray(TRANSPORTER_POS));
		else
			transporterRelativePos = null;
		
		if(tag.contains(ENERGY_INVENTORY, Tag.TAG_COMPOUND))
			energyItemHandler.deserializeNBT(registries, tag.getCompound(ENERGY_INVENTORY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(transporterRelativePos != null)
			tag.putIntArray(TRANSPORTER_POS, Conversion.vecToIntArray(transporterRelativePos));
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT(registries));
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		lazyEnergyItemHandler.invalidate();
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
		
		if(transporterRelativePos != null)
			tag.putIntArray(TRANSPORTER_POS, Conversion.vecToIntArray(transporterRelativePos));
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries)
	{
		energyStorage.setEnergy(tag.getLong(ENERGY));
		
		if(tag.contains(TRANSPORTER_POS, Tag.TAG_INT_ARRAY))
			transporterRelativePos = Conversion.intArrayToVec(tag.getIntArray(TRANSPORTER_POS));
		else
			transporterRelativePos = null;
		transporterCache.markDirty();
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
			handleUpdateTag(tag, registries);
	}
	
	@Override
	public AutoCache.Receiver<TransporterControllerEntity, AbstractTransporterEntity<?>> receiverCache()
	{
		return transporterCache;
	}
	
	public Set<Integer> getNetworks()
	{
		return networks;
	}
	
	public Set<Integer> getTransporterNetworks()
	{
		return transporterCache.returnCachedOrDefault(AbstractTransporterEntity::getCachedNetworks, getNetworks());
	}
	
	public abstract Direction getDirection();
	
	
	
	public int getMaxConnectionDistance()
	{
		return maxConnectionDistance;
	}
	
	public long getMaxConnectionDistanceSqr()
	{
		return (long) maxConnectionDistance * maxConnectionDistance;
	}
	
	/**
	 * @return Max distance for discovering nearby Transporters for the purposes of establishing a connection
	 */
	public double maxDiscoveryDistance()
	{
		return transporterCache.returnOrDefault(AbstractTransporterEntity::maxTransportRange, 0D);
	}
	
	public long getTransporterEnergy()
	{
		return transporterCache.returnOrDefault(transporter -> transporter.energyStorage.getTrueEnergyStored(), -1L);
	}
	
	public long minStoredEnergy()
	{
		return energyStorage.getTrueMaxEnergyStored() * 2 / 3;
	}
	public long maxEnergyTransfer()
	{
		return this.maxEnergyTransfer;
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget;
	}
	
	private ItemStackHandler createEnergyItemHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
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
	
	private void tryPowerTransporter(AbstractTransporterEntity<?> transporter, ItemStack energyStack)
	{
		if(transporter.energyStorage.getTrueEnergyStored() < getEnergyTarget())
		{
			long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), transporter.energyStorage.getTrueEnergyStored(), maxEnergyTransfer());
			
			// Uses energy from an Energy Item if one is present
			if(InventoryUtil.stackHasEnergy(energyStack))
			{
				IEnergyStorage energyStorage = energyStack.getCapability(Capabilities.EnergyStorage.ITEM);
				
				if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long energySent = sgjourneyEnergy.extractLongEnergy(needed, false);
					transporter.energyStorage.receiveLongEnergy(energySent, false);
				}
				else
				{
					int energySent = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(needed), false);
					transporter.energyStorage.receiveLongEnergy(energySent, false);
				}
			}
			// Uses energy from the Transporter energy buffer
			else
			{
				long energySent = energyStorage.depleteEnergy(needed, false);
				transporter.energyStorage.receiveLongEnergy(energySent, false);
			}
		}
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		ItemStack energyStack = energyItemHandler.getStackInSlot(0);
		
		// Stores energy in the Transporter Controller buffer
		if(energyStorage.getTrueEnergyStored() < minStoredEnergy())
		{
			try
			{
				extractItemEnergy(energyStack);
			}
			catch(Exception e)
			{
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		// Sends energy to the Transporter
		else
			transporterCache.ifPresent(transporter -> tryPowerTransporter(transporter, energyStack));
	}
	
	// ======= Transporting =======
	
	public TransporterInfo.FeedbackMessage startCoordTransport(Vec3 coords)
	{
		return transporterCache.returnOrDefault(transporter -> transporter.dialTransporter(Conversion.vec3ToVec3i(coords)), TransporterInfo.Feedback.NONE.withInfo());
	}
	
	public TransporterInfo.FeedbackMessage startIDTransport(TransporterID transporterID)
	{
		return transporterCache.returnOrDefault(transporter -> transporter.dialTransporter(transporterID), TransporterInfo.Feedback.NONE.withInfo());
	}
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		if(transporterCache.isPresent())
			status.add(Component.translatable("info.sgjourney.transporter_connected").append(Component.literal(": ").append(ComponentHelper.coordinate(transporterCache.get().getBlockPos()))).withStyle(ChatFormatting.DARK_AQUA));
		else
			status.add(Component.translatable("info.sgjourney.no_transporter_connected").withStyle(ChatFormatting.DARK_AQUA));
		
		return status;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransporterControllerEntity controller)
	{
		if(level.isClientSide())
			return;
		
		controller.outputEnergy(null);
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
			generationStep = Step.READY; // Marks the Controller as ready for generation
	}
	
	public void generate()
	{
		generateEnergyItem();
		generateAdditional(Step.READY);
		
		generationStep = Step.GENERATED;
	}
	
	public void generateAdditional(StructureGenEntity.Step generationStep) {}
	
	protected abstract void generateEnergyItem();
	
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
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_transporter_controller_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
