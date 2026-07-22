package net.povstalec.sgjourney.common.block_entities.transporter_controller;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.blocks.transporter_controller.GoauldRingPanelBlock;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.misc.TransporterControllerButton;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.CoordinateEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterIDEntry;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class GoauldRingPanelEntity extends TransporterControllerEntity
{
	//TODO Interdimensional transport (Materialization Crystals)
	
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String CRYSTAL_INVENTORY = "crystal_inventory";
	
	public static final String BUTTONS = "buttons";
	
	public final CrystalCache<GoauldRingPanelEntity> crystalCache = createCrystalCache();
	
	//------Button Stuff------
	protected TransporterControllerButton.ButtonState panelState = TransporterControllerButton.ButtonState.DEFAULT;
	protected List<TransporterControllerButton<GoauldRingPanelEntity>> buttons = Arrays.asList(
			TransporterControllerButton.defaultButton(this, 0, TransporterControllerButton.ButtonStatus.DISABLED), TransporterControllerButton.defaultButton(this, 1, TransporterControllerButton.ButtonStatus.DISABLED),
			TransporterControllerButton.defaultButton(this, 2, TransporterControllerButton.ButtonStatus.DISABLED), TransporterControllerButton.defaultButton(this, 3, TransporterControllerButton.ButtonStatus.DISABLED),
			TransporterControllerButton.defaultButton(this, 4, TransporterControllerButton.ButtonStatus.DISABLED), TransporterControllerButton.defaultButton(this, 5, TransporterControllerButton.ButtonStatus.DISABLED)
	);
	protected int selectedSlot = -1;
	protected int page = -1;
	@Nullable
	protected TransporterID.Mutable encodedID = null;
	
	protected final ItemStackHandler crystalItemHandler = createCrystalItemHandler();
	protected final Lazy<IItemHandler> lazyCrystalItemHandler = Lazy.of(() -> crystalItemHandler);
	
	public GoauldRingPanelEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.GOAULD_RING_PANEL.get(), pos, state);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		if(tag.contains(CRYSTAL_INVENTORY))
			crystalItemHandler.deserializeNBT(registries, tag.getCompound(CRYSTAL_INVENTORY));
		else
			crystalItemHandler.deserializeNBT(registries, tag.getCompound("Inventory")); //TODO For legacy reasons
		
		if(!tag.contains(ENERGY_INVENTORY, CompoundTag.TAG_COMPOUND))
		{
			energyStorage.setEnergy(energyStorage.getTrueMaxEnergyStored());
			energyItemHandler.setStackInSlot(0, PowerCellItem.randomLiquidNaquadahSetup(CommonTechConfig.vial_capacity.get() / 3, CommonTechConfig.vial_capacity.get()));
		}
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put(CRYSTAL_INVENTORY, crystalItemHandler.serializeNBT(registries));
		super.saveAdditional(tag, registries);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		crystalCache.recalculateCrystals();
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		lazyCrystalItemHandler.invalidate();
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = super.getUpdateTag(registries);
		ListTag list = new ListTag();
		for(int i = 0; i < 6; i++)
		{
			list.add(buttons.get(i).serialize(registries));
		}
		
		tag.put(BUTTONS, list);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		super.onDataPacket(net, packet, registries);
		CompoundTag tag = packet.getTag();
		if(tag != null && tag.contains(BUTTONS, Tag.TAG_LIST))
		{
			ListTag list = tag.getList(BUTTONS, Tag.TAG_COMPOUND);
			for(int i = 0; i < 6; i++)
			{
				buttons.get(i).deserialize(list.getCompound(i), registries);
			}
		}
	}
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getBlockState();
			
			if(gateState.hasProperty(GoauldRingPanelBlock.FACING))
				this.direction = gateState.getValue(GoauldRingPanelBlock.FACING);
			else
				StargateJourney.LOGGER.error("Couldn't find Ring Panel Direction");
		}
		
		return this.direction;
	}
	
	public void sendMessageToNearbyPlayers(Component message, int distance)
	{
		AABB localBox = new AABB(getBlockPos()).inflate(distance);
		level.getEntitiesOfClass(Player.class, localBox).forEach((player) -> player.displayClientMessage(message, true));
	}
	
	protected CrystalCache<GoauldRingPanelEntity> createCrystalCache()
	{
		return new CrystalCache.Generic6<>(this, CrystalCache.ALL)
		{
			@Override
			protected void onReset()
			{
				networks.clear();
				
				energyTarget = 0;
				maxEnergyTransfer = 0;
			}
			
			@Override
			protected void fetchCrystals()
			{
				for(int i = 0; i < 6; i++)
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
				// If there are 4 regular crystals or 3 advanced crystals
				if(crystalCache.energyCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
					energyTarget = -1;
				else
					crystalCache.energyCrystals().forEach(slot -> energyTarget += slot.crystal.energyTargetIncrease());
				
				// If there are 4 regular crystals or 3 advanced crystals
				if(crystalCache.transferCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
					energyTarget = -1;
				else
					crystalCache.transferCrystals().forEach(slot -> maxEnergyTransfer += slot.crystal.getMaxTransfer());
				
				crystalCache.communicationCrystals().forEach(slot ->
				{
					// Collect frequencies of different Communication Crystals and interpret them as networks the Transporter is in
					if(CommunicationCrystalItem.hasFrequency(crystalItemHandler.getStackInSlot(slot.index)))
						networks.add(CommunicationCrystalItem.getFrequency(crystalItemHandler.getStackInSlot(slot.index)));
				});
				
				transporterCache.markDirtyTwoWays();
				transporterCache.ifPresent(AbstractTransporterEntity::updateTransporter);
			}
		};
	}
	
	//============================================================================================
	//*****************************************Inventory******************************************
	//============================================================================================
	
	private ItemStackHandler createCrystalItemHandler()
	{
		return new ItemStackHandler(6)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
					tryUpdateButtons(slot);
					crystalCache.recalculateCrystals();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
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
	
	public IItemHandler getCrystalItemHandler()
	{
		return lazyCrystalItemHandler.get();
	}
	
	@Nullable
	public IItemHandler getEnergyItemHandler()
	{
		if(!isProtected() || CommonPermissionConfig.protected_inventory_access.get())
			return lazyEnergyItemHandler.get();
		return null;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected void energyChanged(long difference, boolean simulate)
	{
		// If energy was 0 but now isn't, update buttons
		// If energy wasn't 0 but now is, update buttons
		if(energyStorage.getTrueEnergyStored() - difference == 0 || energyStorage.getTrueEnergyStored() == 0)
			updateButtons();
		super.energyChanged(difference, simulate);
	}
	
	@Override
	protected long getCapacity()
	{
		return CommonTransporterConfig.ring_panel_energy_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonTransporterConfig.ring_panel_max_energy_receive.get();
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	protected long getMaxDeplete()
	{
		return CommonTransporterConfig.ring_panel_max_energy_extract.get();
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonTransporterConfig.ring_panel_button_press_energy_cost.get();
	}
	
	//============================================================================================
	//*******************************************Buttons******************************************
	//============================================================================================
	
	private TransporterControllerButton<GoauldRingPanelEntity> pageForwardButton(boolean enabled)
	{
		return TransporterControllerButton.pageForwardButton(this, 5, page + 1, enabled).setOnPress(button ->
		{
			button.parent.page++;
			switch(button.parent.panelState)
			{
				case MEMORY -> button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
				case MANUAL -> button.parent.setButtonsForManualControl();
			}
		});
	}
	
	private TransporterControllerButton<GoauldRingPanelEntity> pageBackButton()
	{
		return TransporterControllerButton.pageBackButton(this, 4, page - 1).setOnPress(button ->
		{
			button.parent.page--;
			switch(button.parent.panelState)
			{
				case MEMORY -> button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
				case MANUAL -> button.parent.setButtonsForManualControl();
			}
		});
	}
	
	private TransporterControllerButton<GoauldRingPanelEntity> defaultButton(Transporter transporter, int index, TransporterControllerButton.ButtonStatus status)
	{
		return TransporterControllerButton.defaultButton(this, index, status).setTransporter(transporter).setCloseScreen(true).setOnPress(button ->
		{
			if(button.transporterID() != null && button.parent.checkBusy())
			{
				TransporterInfo.FeedbackMessage feedback = button.parent.startIDTransport(button.transporterID());
				if(feedback.feedback().isError())
					button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
			}
		});
	}
	
	// ======= Memory Crystal =======
	
	private TransporterControllerButton<GoauldRingPanelEntity> memoryButton(int index, TransporterControllerButton.ButtonStatus status)
	{
		ItemStack stack = crystalItemHandler.getStackInSlot(index);
		int entryCount = MemoryCrystalItem.countMemoryEntriesOfType(stack, MemoryEntry.Type.TRANSPORTER_ID, MemoryEntry.Type.COORDINATES);
		if(entryCount == 0) // Memory Crystal holds no Transporter IDs, make the button not interactable
			return TransporterControllerButton.memoryButton(this, index, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.memory_entries").append(": 0").withStyle(ChatFormatting.BLUE));
		
		if(!status.isEnabled)
			return TransporterControllerButton.memoryButton(this, index, status);
		
		MutableComponent tooltip = stack.has(DataComponents.ITEM_NAME) ? stack.getHoverName().copy() : Component.translatable("tooltip.sgjourney.ring_panel.button.memory_entries");
		
		return TransporterControllerButton.memoryButton(this, index, status).setTooltip(tooltip.append(": " + entryCount).withStyle(ChatFormatting.BLUE)).setOnPress(button ->
		{
			if(button.parent.page < 0)
				button.parent.setBaseMemoryCrystalPage(button.parent.level.getServer(), index);
		});
	}
	
	protected void setBaseMemoryCrystalPage(MinecraftServer server, int index)
	{
		if(index < 0)
			return;
		
		selectedSlot = index;
		page = 0;
		panelState = TransporterControllerButton.ButtonState.MEMORY;
		setButtonsFromMemoryCrystal(server, index);
	}
	
	private void setButtonsFromMemoryCrystal(MinecraftServer server, int index)
	{
		ListTag list = MemoryCrystalItem.getMemoryList(this.crystalItemHandler.getStackInSlot(index));
		
		int start = 4 * page;
		for(int i = 0; i < 4; i++)
		{
			buttons.set(i, loadButtonFromMemoryCrystal(server, list, start + i));
		}
		
		if(list.size() > 5)
		{
			buttons.set(4, page == 0 ? TransporterControllerButton.returnButton(this, 4).setOnPress(button -> button.parent.updateButtons()) : pageBackButton());
			buttons.set(5, pageForwardButton(start + 4 < list.size()));
		}
		else
		{
			buttons.set(4, TransporterControllerButton.returnButton(this, 4).setOnPress(button -> button.parent.updateButtons()));
			buttons.set(5, loadButtonFromMemoryCrystal(server, list, start + 4));
		}
		
		updateClient();
	}
	
	private TransporterControllerButton<GoauldRingPanelEntity> loadButtonFromMemoryCrystal(MinecraftServer server, ListTag list, int index)
	{
		MemoryEntry.Type<?> type = MemoryCrystalItem.memoryTypeAt(list, index);
		
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			TransporterIDEntry transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			Transporter transporter = BlockEntityList.get(server).getTransporter(transporterID.entry());
			if(transporter != null)
			{
				if(transporterID.name().isEmpty() && transporter.getName() != null)
					return memoryTransportButton(index).setTransporter(transporter);
				else
					return memoryTransportButton(index).setTransporter(transporter, Component.literal(transporterID.name()).withStyle(ChatFormatting.GREEN));
			}
			else
				return TransporterControllerButton.memoryButton(this, index, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.invalid_id").withStyle(ChatFormatting.DARK_RED));
			
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			CoordinateEntry coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
				return memoryTransportButton(index).setTransporter(null, Component.literal(coords.name()).withStyle(ChatFormatting.GREEN), coords.asVec3());
			else
				return TransporterControllerButton.memoryButton(this, index, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.invalid_location").withStyle(ChatFormatting.DARK_RED));
		}
		
		return TransporterControllerButton.memoryButton(this, index, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.no_entry").withStyle(ChatFormatting.BLUE));
	}
	
	private TransporterControllerButton<GoauldRingPanelEntity> memoryTransportButton(int index)
	{
		return TransporterControllerButton.memoryButton(this, index, TransporterControllerButton.ButtonStatus.ENABLED).setCloseScreen(true).setOnPress(button ->
		{
			if(button.transporterID() != null && button.parent.checkBusy())
			{
				TransporterInfo.FeedbackMessage feedback = button.parent.startIDTransport(button.transporterID());
				if(feedback.feedback().isError())
					button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
			}
			else if(button.coords() != null && button.parent.checkBusy())
			{
				TransporterInfo.FeedbackMessage feedback = button.parent.startCoordTransport(button.coords());
				if(feedback.feedback().isError())
					button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
			}
		});
	}
	
	// ======= Communication Crystal =======
	
	private TransporterControllerButton<GoauldRingPanelEntity> communicationCrystalButton(int index, TransporterControllerButton.ButtonStatus status)
	{
		if(!CommunicationCrystalItem.hasFrequency(crystalItemHandler.getStackInSlot(index)))
			return TransporterControllerButton.networkButton(this, index, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.frequency.none"));
		
		if(!status.isEnabled)
			return TransporterControllerButton.networkButton(this, index, status);
		else
		{
			return TransporterControllerButton.networkButton(this, index, status).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.frequency").append(": " + CommunicationCrystalItem.getFrequency(crystalItemHandler.getStackInSlot(index))).withStyle(ChatFormatting.GRAY))
					.setOnPress(button -> button.parent.setBaseCommunicationCrystalPage(index, CommunicationCrystalItem.getFrequency(button.parent.crystalItemHandler.getStackInSlot(index))));
		}
	}
	
	protected void setBaseCommunicationCrystalPage(int index, int frequency)
	{
		if(index < 0)
			return;
		
		selectedSlot = index;
		page = 0;
		panelState = TransporterControllerButton.ButtonState.NETWORK;
		setButtonsForNetworkControl(frequency);
	}
	
	protected void setButtonsForNetworkControl(int network)
	{
		ServerLevel serverLevel = (ServerLevel) getLevel();
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInDimension(serverLevel, transporterCache.get().getBlockPos(), maxDiscoveryDistance(), transporter ->
				!transporterCache.get().getID().equals(transporter.getID()) &&
						transporter.getNetworks().contains(network)
		).iterator();
		
		for(int i = 0; i < 4; i++)
		{
			if(transporterIterator.hasNext())
				buttons.set(i, nextNetworkButton(serverLevel.getServer(), transporterIterator.next(), i));
			else
				buttons.set(i, TransporterControllerButton.networkButton(this, i, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter_in_network", network)));
		}
		
		buttons.set(4, TransporterControllerButton.returnButton(this, 4).setOnPress(button -> button.parent.updateButtons()));
		if(transporterIterator.hasNext())
			buttons.set(5, nextNetworkButton(serverLevel.getServer(), transporterIterator.next(), 5));
		else
			buttons.set(5, TransporterControllerButton.networkButton(this, 5, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter_in_network", network)));
		
		updateClient();
	}
	
	protected TransporterControllerButton<GoauldRingPanelEntity> nextNetworkButton(MinecraftServer server, Transporter transporter, int index)
	{
		return TransporterControllerButton.networkButton(this, index, TransporterControllerButton.ButtonStatus.ENABLED).setTransporter(transporter).setCloseScreen(true).setOnPress(button ->
		{
			if(button.transporterID() != null && button.parent.checkBusy())
			{
				TransporterInfo.FeedbackMessage feedback = button.parent.startIDTransport(button.transporterID());
				if(feedback.feedback().isError())
					button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
			}
		});
	}
	
	// ======= Control Crystal =======
	
	private TransporterControllerButton<GoauldRingPanelEntity> controlCrystalButton(int index, TransporterControllerButton.ButtonStatus status)
	{
		if(!status.isEnabled)
			return TransporterControllerButton.manualControlButton(this, index, status);
		
		return TransporterControllerButton.manualControlButton(this, index, status).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.manual_control").withStyle(ChatFormatting.AQUA))
				.setOnPress(button -> button.parent.setBaseControlCrystalPage(button.index));
	}
	
	private void setBaseControlCrystalPage(int index)
	{
		if(index < 0)
			return;
		
		encodedID = new TransporterID.Mutable();
		selectedSlot = index;
		page = 0;
		panelState = TransporterControllerButton.ButtonState.MANUAL;
		setButtonsForManualControl();
	}
	
	private void setButtonsForManualControl()
	{
		int start = 4 * page;
		for(int i = 0; i < 4; i++)
		{
			buttons.set(i, nextManualButton(i + start + 1));
		}
		
		if(page <= 0)
		{
			buttons.set(4, TransporterControllerButton.returnButton(this, 4).setOnPress(button -> button.parent.updateButtons()));
			buttons.set(5, pageForwardButton(true));
		}
		else
		{
			buttons.set(4, pageBackButton());
			buttons.set(5, encodedID == null || encodedID.getLength() == 0 ? TransporterControllerButton.emptyEnterButton(this, 5) :
					TransporterControllerButton.enterButton(this, 5, encodedID).setOnPress(button ->
					{
						if(button.parent.checkBusy())
						{
							TransporterInfo.FeedbackMessage feedback = button.parent.startIDTransport(button.parent.encodedID);
							if(feedback.feedback().isError())
								button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
						}
					}));
		}
		
		updateClient();
	}
	
	private TransporterControllerButton<GoauldRingPanelEntity> nextManualButton(int index)
	{
		if(encodedID.canGrow())
			return TransporterControllerButton.manualControlButton(this, index, TransporterControllerButton.ButtonStatus.ENABLED).setTooltip(encodedID.toComponent(false).append(Component.literal(index + "-").withStyle(ChatFormatting.LIGHT_PURPLE)))
					.setOnPress(button ->
					{
						button.parent.encodedID.addSymbol(button.index);
						button.parent.setButtonsForManualControl();
					});
		else
			return TransporterControllerButton.manualControlButton(this, index, TransporterControllerButton.ButtonStatus.ENABLED).setTooltip(encodedID.toComponent(false));
	}
	
	// ======= Materialization Crystal =======
	
	private TransporterControllerButton<GoauldRingPanelEntity> materializationCrystalButton(int index, TransporterControllerButton.ButtonStatus status)
	{
		if(!status.isEnabled)
			return TransporterControllerButton.materializationButton(this, index, status);
		
		return TransporterControllerButton.materializationButton(this, index, status).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.interdimensional").withStyle(ChatFormatting.DARK_AQUA))
				.setOnPress(button -> button.parent.setBaseMaterializationCrystalPage(button.index));
	}
	
	protected void setBaseMaterializationCrystalPage(int index)
	{
		if(index < 0)
			return;
		
		selectedSlot = index;
		page = 0;
		panelState = TransporterControllerButton.ButtonState.NETWORK;
		setButtonsForInterdimensionalControl();
	}
	
	protected void setButtonsForInterdimensionalControl()
	{
		ServerLevel serverLevel = (ServerLevel) getLevel();
		
		final Set<ResourceKey<Level>> dimensions = new HashSet<>();
		dimensions.add(level.dimension()); // Added to ignore the Dimension this Ring Panel is in
		
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInRegion(serverLevel, getBlockPos(), maxDiscoveryDistance(), transporter ->
		{
			ResourceKey<Level> transporterDimension = transporter.getDimension();
			if(transporterDimension == null || dimensions.contains(transporterDimension))
				return false; // Attempt to collect only one Transporter from each Dimension at most
			
			boolean add = !transporterCache.get().getID().equals(transporter.getID()) &&
					!transporter.isNetworkRestricted(getTransporterNetworks()) &&
					transporter.allowInterdimensionalTransport();
			
			if(add)
				dimensions.add(transporterDimension); // It should be okay to do it like this, because this filter is applied on the sorted stream of Transporters
			
			return add;
		}).iterator();
		
		for(int i = 0; i < 4; i++)
		{
			if(transporterIterator.hasNext())
				buttons.set(i, nextInterdimensionalButton(transporterIterator.next(), i));
			else
				buttons.set(i, TransporterControllerButton.materializationButton(this, i, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter_interdimensional").withStyle(ChatFormatting.DARK_AQUA)));
		}
		
		buttons.set(4, TransporterControllerButton.returnButton(this, 4).setOnPress(button -> button.parent.updateButtons()));
		if(transporterIterator.hasNext())
			buttons.set(5, nextInterdimensionalButton(transporterIterator.next(), 5));
		else
			buttons.set(5, TransporterControllerButton.materializationButton(this, 5, TransporterControllerButton.ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter_interdimensional").withStyle(ChatFormatting.DARK_AQUA)));
		
		updateClient();
	}
	
	protected TransporterControllerButton<GoauldRingPanelEntity> nextInterdimensionalButton(Transporter transporter, int index)
	{
		return TransporterControllerButton.materializationButton(this, index, TransporterControllerButton.ButtonStatus.ENABLED).setTransporter(transporter).setCloseScreen(true).setOnPress(button ->
		{
			if(button.transporterID() != null && button.parent.checkBusy())
			{
				TransporterInfo.FeedbackMessage feedback = button.parent.startIDTransport(button.transporterID());
				if(feedback.feedback().isError())
					button.parent.sendMessageToNearbyPlayers(feedback.getMessageComponent(), CONTROLLER_INFO_DISTANCE);
			}
		});
	}
	
	//============================================================================================
	//*******************************************Control******************************************
	//============================================================================================
	
	private TransporterControllerButton<GoauldRingPanelEntity> nextButton(int index, Iterator<Transporter> transporterIterator, TransporterControllerButton.ButtonStatus status)
	{
		TransporterControllerButton.ButtonState state = buttonStateAt(index);
		
		if(state == TransporterControllerButton.ButtonState.MEMORY)
			return memoryButton(index, status);
		if(state == TransporterControllerButton.ButtonState.NETWORK)
			return communicationCrystalButton(index, status);
		if(state == TransporterControllerButton.ButtonState.MANUAL)
			return controlCrystalButton(index, status);
		if(state == TransporterControllerButton.ButtonState.MATERIALIZATION)
			return materializationCrystalButton(index, status);
		else if(transporterIterator != null && transporterIterator.hasNext())
			return defaultButton(transporterIterator.next(), index, status);
		else if(status.isEnabled)
			return TransporterControllerButton.defaultButton(this, index, TransporterControllerButton.ButtonStatus.NO_REACHABLE_TRANSPORTER); // Empty Default Button because no more Transporters are reachable
		else
			return TransporterControllerButton.defaultButton(this, index, status); // Empty Default Button because of status issues
	}
	
	protected void updateButtons()
	{
		transporterCache.markDirtyTwoWays();
		
		panelState = TransporterControllerButton.ButtonState.DEFAULT;
		page = -1;
		selectedSlot = -1;
		encodedID = null;
		
		ServerLevel serverLevel = (ServerLevel) getLevel();
		boolean buttonHasEnergy = !REQUIRE_ENERGY || energyStorage.hasEnergy(buttonPressEnergyCost());
		if(transporterCache.isPresent())
		{
			Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransportersInDimension(serverLevel, transporterCache.get().getBlockPos(), maxDiscoveryDistance(), transporter ->
					!transporterCache.get().getID().equals(transporter.getID()) && // Don't show the Tranporter the Ring Panel is connected to
							// !transporter.isNetworkRestricted(getTransporterNetworks()) && // Don't show restricted Transporters
							!transporterCache.get().isNetworkRestricted(transporter.getNetworks()) // Don't show Transporters in other networks if this one is restricted
			).iterator();
			for(int i = 0; i < 6; i++)
			{
				buttons.set(i, nextButton(i, transporterIterator, buttonHasEnergy ? TransporterControllerButton.ButtonStatus.ENABLED : TransporterControllerButton.ButtonStatus.NO_POWER));
			}
		}
		else
		{
			for(int i = 0; i < 6; i++)
			{
				buttons.set(i, nextButton(i, null, TransporterControllerButton.ButtonStatus.NO_TRANSPORTER));
			}
		}
		
		updateClient();
	}
	
	protected void tryUpdateButtons(int updatedSlot)
	{
		if(!this.getLevel().isClientSide() && page < 0 || updatedSlot == selectedSlot)
			updateButtons();
	}
	
	public void tryUpdateButtons()
	{
		tryUpdateButtons(-1);
	}
	
	public TransporterControllerButton<GoauldRingPanelEntity> getButtonAt(int index)
	{
		return buttons.get(index);
	}
	
	public void pressButton(int index)
	{
		if(REQUIRE_ENERGY && !energyStorage.hasEnergy(buttonPressEnergyCost()))
		{
			sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.ring_panel.error.not_enough_energy").withStyle(ChatFormatting.DARK_RED), 3);
			return;
		}
		
		level.playSound(null, this.getBlockPos(), SoundInit.RING_PANEL_PRESS.get(), SoundSource.BLOCKS, 0.5F, 1F);
		
		getButtonAt(index).press();
	}
	
	public TransporterControllerButton.ButtonState buttonStateAt(int index)
	{
		return TransporterControllerButton.ButtonState.stateFromItem(this.crystalItemHandler.getStackInSlot(index).getItem());
	}
	
	// ======= Transporting =======
	
	public boolean checkBusy()
	{
		if(transporterCache.returnOrDefault(transporter -> !transporter.isConnected(), true))
			return true;
		
		sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.ring_remote.error.transport_rings_busy").withStyle(ChatFormatting.DARK_RED), CONTROLLER_INFO_DISTANCE);
		updateButtons();
		return false;
	}
	
	public TransporterInfo.FeedbackMessage startCoordTransport(Vec3 coords)
	{
		TransporterInfo.FeedbackMessage feedback = super.startCoordTransport(coords);
		updateButtons();
		return feedback;
	}
	
	public TransporterInfo.FeedbackMessage startIDTransport(TransporterID transporterID)
	{
		TransporterInfo.FeedbackMessage feedback = super.startIDTransport(transporterID);
		updateButtons();
		return feedback;
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
