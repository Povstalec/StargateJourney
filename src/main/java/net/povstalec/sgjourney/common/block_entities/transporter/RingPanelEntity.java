package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class RingPanelEntity extends TransporterControllerEntity implements ProtectedBlockEntity
{
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	public static final int MESSAGE_DISTANCE = 3;
	
	public static final String ENERGY_INVENTORY = "energy_inventory";
	public static final String CRYSTAL_INVENTORY = "crystal_inventory";
	
	public static final String BUTTONS = "buttons";
	
	//------Button Stuff------
	protected ButtonState panelState = ButtonState.DEFAULT;
	protected Button[] buttons = { Button.defaultDisabledButton(this, 0), Button.defaultDisabledButton(this, 1), Button.defaultDisabledButton(this, 2), Button.defaultDisabledButton(this, 3), Button.defaultDisabledButton(this, 4), Button.defaultDisabledButton(this, 5) };
	protected int selectedSlot = -1;
	protected int page = -1;
	@Nullable
	protected TransporterID.Mutable encodedID = null;
	
	protected final ItemStackHandler crystalItemHandler = createCrystalItemHandler();
	protected final LazyOptional<IItemHandler> lazyCrystalItemHandler = LazyOptional.of(() -> crystalItemHandler);
	
	protected final ItemStackHandler energyItemHandler = createEnergyItemHandler();
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
	
	@Nullable
	protected Transporter connectedTransporter;
	
	protected boolean isProtected = false;
	
	public RingPanelEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.GOAULD_RING_PANEL.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		if(tag.contains(CRYSTAL_INVENTORY))
			crystalItemHandler.deserializeNBT(tag.getCompound(CRYSTAL_INVENTORY));
		else
			crystalItemHandler.deserializeNBT(tag.getCompound("Inventory")); //TODO For legacy reasons
		energyItemHandler.deserializeNBT(tag.getCompound(ENERGY_INVENTORY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.put(CRYSTAL_INVENTORY, crystalItemHandler.serializeNBT());
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT());
		super.saveAdditional(tag);
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		lazyCrystalItemHandler.invalidate();
		lazyEnergyItemHandler.invalidate();
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = this.saveWithoutMetadata();
		ListTag list = new ListTag();
		for(int i = 0; i < 6; i++)
		{
			if(buttons[i] != null)
				list.add(buttons[i].serialize());
		}
		
		tag.put(BUTTONS, list);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null && tag.contains(BUTTONS, Tag.TAG_LIST))
		{
			ListTag list = tag.getList(BUTTONS, Tag.TAG_COMPOUND);
			for(int i = 0; i < 6; i++)
			{
				buttons[i].deserialize(list.getCompound(i));
			}
		}
	}
	
	public void sendMessageToNearbyPlayers(Component message, int distance)
	{
		AABB localBox = new AABB(getBlockPos()).inflate(distance);
		level.getEntitiesOfClass(Player.class, localBox).forEach((player) -> player.displayClientMessage(message, true));
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
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.getItem() instanceof AbstractCrystalItem;
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
				return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
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
	
	public LazyOptional<IItemHandler> getCrystalItemHandler()
	{
		return lazyCrystalItemHandler.cast();
	}
	
	public LazyOptional<IItemHandler> getEnergyItemHandler()
	{
		return lazyEnergyItemHandler.cast();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return lazyEnergyItemHandler.cast();
		
		return super.getCapability(capability, side);
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
	
	private void chargeFromEnergyItem()
	{
		extractItemEnergy(energyItemHandler.getStackInSlot(0));
	}
	
	//============================================================================================
	//*******************************************Buttons******************************************
	//============================================================================================
	
	public void connectToTransporter(Transporter transporter)
	{
		this.connectedTransporter = transporter;
	}
	
	//TODO Tell the player there are no rings connected
	//TODO Frequency
	//TODO Interdimensional transport (Materialization Crystals)
	
	public static void tick(Level level, BlockPos pos, BlockState state, RingPanelEntity ringPanel)
	{
		if(level.isClientSide())
			return;
		
		ringPanel.chargeFromEnergyItem();
	}
	
	protected Button nextButton(MinecraftServer server, int index, Iterator<Transporter> transporterIterator, boolean enabled, boolean hasEnergy)
	{
		ButtonState state = buttonStateAt(index);
		
		if(state == ButtonState.MEMORY)
			return Button.memoryCrystalButton(this, index, enabled, hasEnergy);
		if(state == ButtonState.FREQUENCY)
			return Button.communicationCrystalButton(this, index, enabled, hasEnergy);
		if(state == ButtonState.MANUAL)
			return Button.controlCrystalButton(this, index, enabled, hasEnergy);
		else if(transporterIterator.hasNext())
			return Button.defaultTransportButton(this, index, server, transporterIterator.next(), enabled, hasEnergy);
		else
			return Button.defaultDisabledButton(this, index); // Empty Default Button
	}
	
	protected void updateButtons()
	{
		panelState = ButtonState.DEFAULT;
		page = -1;
		selectedSlot = -1;
		encodedID = null;
		
		ServerLevel serverLevel = (ServerLevel) getLevel();
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransporters(serverLevel, getBlockPos(), 32768F).iterator(); //TODO Change distance
		
		if(transporterIterator.hasNext())
		{
			Transporter connectionCandidate = transporterIterator.next();
			Vec3 candidatePosition = connectionCandidate.getPosition(serverLevel.getServer());
			
			if(candidatePosition != null && getBlockPos().getCenter().closerThan(candidatePosition, 16D))
				connectToTransporter(connectionCandidate);
			else
				connectToTransporter(null);
		}
		else
			connectToTransporter(null);
		
		boolean buttonHasEnergy = !REQUIRE_ENERGY || energyStorage.hasEnergy(buttonPressEnergyCost());
		for(int i = 0; i < 6; i++)
		{
			buttons[i] = nextButton(serverLevel.getServer(), i, transporterIterator, connectedTransporter != null, buttonHasEnergy);
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
	
	protected void setBaseMemoryCrystalPage(MinecraftServer server, int index)
	{
		if(index < 0)
			return;
		
		selectedSlot = index;
		page = 0;
		panelState = ButtonState.MEMORY;
		setButtonsFromMemoryCrystal(server, index);
	}
	
	protected void setBaseControlCrystalPage(int index)
	{
		if(index < 0)
			return;
		
		encodedID = new TransporterID.Mutable();
		selectedSlot = index;
		page = 0;
		panelState = ButtonState.MANUAL;
		setButtonsForManualControl();
	}
	
	public Button getButtonAt(int index)
	{
		return buttons[index];
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
	
	public ButtonState buttonStateAt(int index)
	{
		return ButtonState.stateFromItem(this.crystalItemHandler.getStackInSlot(index).getItem());
	}
	
	// ======= Memory Crystal =======
	
	protected Button loadButtonFromMemoryCrystal(MinecraftServer server, ListTag list, int index)
	{
		MemoryEntry.Type type = MemoryCrystalItem.memoryTypeAt(list, index);
		
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			Transporter transporter = BlockEntityList.get(server).getTransporter(transporterID.entry());
			if(transporter != null)
				return Button.memoryTransportButton(this, index, true).setTransporter(server, transporter).setTooltip(Component.literal(transporterID.name()).withStyle(ChatFormatting.AQUA));
			else
				return Button.memoryTransportButton(this, index, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.invalid_id").withStyle(ChatFormatting.DARK_RED));
			
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
				return Button.memoryTransportButton(this, index, true).setTransporter(null, Component.literal(coords.name()).withStyle(ChatFormatting.AQUA), coords.asVec3());
			else
				return Button.memoryTransportButton(this, index, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.invalid_location").withStyle(ChatFormatting.DARK_RED));
		}
		
		return new Button(this, index, ButtonState.MEMORY, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.memory_crystal.no_entry"));
	}
	
	protected void setButtonsFromMemoryCrystal(MinecraftServer server, int index)
	{
		ListTag list = MemoryCrystalItem.getMemoryList(this.crystalItemHandler.getStackInSlot(index));
		
		int start = 4 * page;
		for(int i = 0; i < 4; i++)
		{
			buttons[i] = loadButtonFromMemoryCrystal(server, list, start + i);
		}
		
		if(list.size() > 5)
		{
			buttons[4] = page == 0 ? Button.returnButton(this, 4) : Button.pageBackButton(this, 4);
			buttons[5] = Button.pageForwardButton(this, 5, start + 4 < list.size());
		}
		else
		{
			buttons[4] = Button.returnButton(this, 4);
			buttons[5] = loadButtonFromMemoryCrystal(server, list, start + 4);
		}
		
		updateClient();
	}
	
	protected void setButtonsForManualControl()
	{
		int start = 4 * page;
		for(int i = 0; i < 4; i++)
		{
			buttons[i] = nextManualButton(i + start + 1);
		}
		
		if(page <= 0)
		{
			buttons[4] = Button.returnButton(this, 4);
			buttons[5] = Button.pageForwardButton(this, 5, true);
		}
		else
		{
			buttons[4] = Button.pageBackButton(this, 4);
			buttons[5] = Button.enterButton(this, 5);
		}
		
		updateClient();
	}
	
	protected void setButtonsForFrequencyControl()
	{
		
		ServerLevel serverLevel = (ServerLevel) getLevel();
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransporters(serverLevel, getBlockPos(), 32768F).iterator(); //TODO Change distance
		
		for(int i = 0; i < 6; i++)
		{
			buttons[i] = nextManualButton(i); //TODO
		}
		
		updateClient();
	}
	
	protected Button nextManualButton(int index)
	{
		return Button.controlCrystalButton(this, index, true, true).setTooltip(encodedID.toComponent(false).append(Component.literal(index + "-").withStyle(ChatFormatting.LIGHT_PURPLE)));
	}
	
	// ======= Transporting =======
	
	public TransporterInfo.Feedback startCoordTransport(Vec3 coords)
	{
		TransporterInfo.Feedback feedback = connectedTransporter.dialTransporter(level.getServer(), Conversion.vec3ToVec3i(coords));
		updateButtons();
		return feedback;
	}
	
	public TransporterInfo.Feedback startIDTransport(TransporterID transporterID)
	{
		TransporterInfo.Feedback feedback = connectedTransporter.dialTransporter(level.getServer(), transporterID);
		updateButtons();
		return feedback;
	}
	
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
	
	
	
	public enum ButtonState
	{
		DEFAULT, // Transporter Select
		MEMORY,
		FREQUENCY,
		MANUAL,
		
		PAGE_FORWARD,
		PAGE_BACK,
		RETURN,
		ENTER;
		
		public static ButtonState stateFromItem(Item item)
		{
			if(item instanceof MemoryCrystalItem)
				return ButtonState.MEMORY;
			if(item instanceof ControlCrystalItem)
				return ButtonState.MANUAL;
			if(item instanceof CommunicationCrystalItem)
				return ButtonState.FREQUENCY;
			
			return ButtonState.DEFAULT;
		}
	}
	
	public static class Button
	{
		public static final String STATE = "state";
		public static final String ENABLED = "enabled";
		public static final String COORDS = "coords";
		public static final String NAME = "name";
		
		protected final RingPanelEntity parent;
		protected final int index;
		
		protected ButtonState state;
		protected boolean enabled;
		
		@Nullable
		protected TransporterID transporterID = null;
		@Nullable
		protected Component tooltip = null;
		@Nullable
		protected Vec3 coords = null;
		
		@Nullable
		protected Runnable onUpdate = null;
		protected Consumer<Button> onPress;
		
		public Button(RingPanelEntity parent, int index, ButtonState state, boolean enabled)
		{
			this.parent = parent;
			this.index = index;
			
			this.state = state;
			this.enabled = enabled;
		}
		
		public Button setTransporter(TransporterID transporterID, Component tooltip, Vec3 coords)
		{
			this.transporterID = transporterID;
			this.tooltip = tooltip;
			this.coords = coords;
			
			return this;
		}
		
		public Button setTransporter(MinecraftServer server, Transporter transporter)
		{
			return setTransporter(transporter.getID(), transporter.getName(), transporter.getPosition(server));
		}
		
		public ButtonState state()
		{
			return state;
		}
		
		public Button setEnabled(boolean enabled)
		{
			this.enabled = enabled;
			
			return this;
		}
		
		public boolean enabled()
		{
			return enabled;
		}
		
		@Nullable
		public TransporterID transporterID()
		{
			return transporterID;
		}
		
		public Button setTooltip(Component tooltip)
		{
			this.tooltip = tooltip;
			
			return this;
		}
		
		@Nullable
		public Component tooltip()
		{
			return tooltip;
		}
		
		@Nullable
		public Vec3 coords()
		{
			return coords;
		}
		
		public Button setOnPress(Consumer<Button> onPress)
		{
			this.onPress = onPress;
			
			return this;
		}
		
		public void press()
		{
			if(this.onPress != null)
				this.onPress.accept(this);
		}
		
		public Button setUpdate(Runnable onUpdate)
		{
			this.onUpdate = onUpdate;
			
			return this;
		}
		
		private void runOnUpdate()
		{
			if(this.onUpdate != null)
				this.onUpdate.run();
		}
		
		public CompoundTag serialize()
		{
			CompoundTag tag = new CompoundTag();
			
			tag.putByte(STATE, (byte) state.ordinal());
			tag.putBoolean(ENABLED, enabled);
			
			if(coords != null)
				tag.put(COORDS, CoordinateHelper.vec3ToTag(coords));
			if(tooltip != null)
				tag.putString(NAME, Component.Serializer.toJson(this.tooltip));
			
			return tag;
		}
		
		public void deserialize(CompoundTag tag)
		{
			this.state = ButtonState.values()[tag.getByte(STATE)];
			this.enabled = tag.getBoolean(ENABLED);
			
			if(tag.contains(COORDS, Tag.TAG_COMPOUND))
				this.coords = CoordinateHelper.tagToVec3(tag.getCompound(COORDS));
			else
				this.coords = null;
			if(tag.contains(NAME, Tag.TAG_STRING))
				this.tooltip = Component.Serializer.fromJson(tag.getString(NAME));
			else
				this.tooltip = null;
			
			runOnUpdate();
		}
		
		public static Button defaultDisabledButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.DEFAULT, false);
		}
		
		public static Button defaultTransportButton(RingPanelEntity parent, int index, MinecraftServer server, Transporter transporter, boolean enabled, boolean hasEnergy)
		{
			if(!hasEnergy)
				return new Button(parent, index, ButtonState.DEFAULT, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			
			return new Button(parent, index, ButtonState.DEFAULT, enabled).setTransporter(server, transporter).setOnPress(button ->
			{
				if(button.transporterID() != null)
				{
					TransporterInfo.Feedback feedback = button.parent.startIDTransport(button.transporterID());
					if(feedback.isError())
						button.parent.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), MESSAGE_DISTANCE);
				}
			});
		}
		
		public static Button memoryCrystalButton(RingPanelEntity parent, int index, boolean enabled, boolean hasEnergy)
		{
			if(!hasEnergy)
				return new Button(parent, index, ButtonState.MEMORY, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			
			int entryCount = MemoryCrystalItem.countMemoryEntriesOfType(parent.crystalItemHandler.getStackInSlot(index), MemoryEntry.Type.TRANSPORTER_ID);
			if(entryCount == 0) // Memory Crystal holds no Transporter IDs, make the button uninteractable
				return new Button(parent, index, ButtonState.MEMORY, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.memory_entries").append(": 0").withStyle(ChatFormatting.BLUE));
			
			return new Button(parent, index, ButtonState.MEMORY, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.memory_entries").append(": " + entryCount).withStyle(ChatFormatting.BLUE)).setOnPress(button ->
			{
				if(button.parent.page < 0)
					button.parent.setBaseMemoryCrystalPage(button.parent.level.getServer(), index);
				else if(button.transporterID() != null)
				{
					TransporterInfo.Feedback feedback = button.parent.startIDTransport(button.transporterID());
					if(feedback.isError())
						button.parent.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), MESSAGE_DISTANCE);
				}
				else if(button.coords() != null)
				{
					TransporterInfo.Feedback feedback = button.parent.startCoordTransport(button.coords());
					if(feedback.isError())
						button.parent.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), MESSAGE_DISTANCE);
				}
			});
		}
		
		public static Button memoryTransportButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.MEMORY, enabled).setOnPress(button ->
			{
				if(button.transporterID() != null)
				{
					TransporterInfo.Feedback feedback = button.parent.startIDTransport(button.transporterID());
					if(feedback.isError())
						button.parent.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), MESSAGE_DISTANCE);
				}
			});
		}
		
		public static Button controlCrystalButton(RingPanelEntity parent, int index, boolean enabled, boolean hasEnergy)
		{
			if(!hasEnergy)
				return new Button(parent, index, ButtonState.MANUAL, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			
			return new Button(parent, index, ButtonState.MANUAL, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.manual_control").withStyle(ChatFormatting.AQUA)).setOnPress(button ->
			{
				if(button.index < 0)
					button.parent.setBaseControlCrystalPage(button.index);
				else
				{
					button.parent.encodedID.addSymbol(button.index);
					button.parent.setButtonsForManualControl();
				}
			});
		}
		
		public static Button nextFrequencyButton(RingPanelEntity parent, int index, MinecraftServer server, Transporter transporter, boolean enabled, boolean hasEnergy)
		{
			if(!hasEnergy)
				return new Button(parent, index, ButtonState.FREQUENCY, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			
			return new Button(parent, index, ButtonState.FREQUENCY, enabled).setTransporter(server, transporter).setOnPress(button ->
			{
				if(button.transporterID() != null)
				{
					//TODO Automatically change connected Transporter's frequency to match
					//button.parent.connectedTransporter.set
					
					TransporterInfo.Feedback feedback = button.parent.startIDTransport(button.transporterID());
					if(feedback.isError())
						button.parent.sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), MESSAGE_DISTANCE);
				}
			});
		}
		
		public static Button communicationCrystalButton(RingPanelEntity parent, int index, boolean enabled, boolean hasEnergy)
		{
			if(!hasEnergy)
				return new Button(parent, index, ButtonState.FREQUENCY, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			
			return new Button(parent, index, ButtonState.FREQUENCY, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.frequency").withStyle(ChatFormatting.GRAY)).setOnPress(button ->
			{
				parent.setButtonsForFrequencyControl();
			});
		}
		
		public static Button returnButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.RETURN, true).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.return").withStyle(ChatFormatting.RED)).setOnPress(button -> button.parent.updateButtons());
		}
		
		public static Button pageForwardButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.PAGE_FORWARD, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_forward").append(Component.literal(" (" + (parent.page + 1) + ')'))).setOnPress(button ->
			{
				button.parent.page++;
				switch(button.parent.panelState)
				{
					case MEMORY:
						button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
						break;
					case MANUAL:
						button.parent.setButtonsForManualControl();
						break;
					default:
				}
			});
		}
		
		public static Button pageBackButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.PAGE_BACK, true).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_back").append(Component.literal(" (" + (parent.page - 1) + ')'))).setOnPress(button ->
			{
				button.parent.page--;
				switch(button.parent.panelState)
				{
					case MEMORY:
						button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
						break;
					case MANUAL:
						button.parent.setButtonsForManualControl();
						break;
					default:
				}
			});
		}
		
		public static Button enterButton(RingPanelEntity parent, int index)
		{
			if(parent.encodedID == null || parent.encodedID.getLength() == 0)
				return new Button(parent, index, ButtonState.ENTER, false).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.start_transport"));
			
			return new Button(parent, index, ButtonState.ENTER, true).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.start_transport").append(Component.literal(": ").append(parent.encodedID.toComponent(false))).withStyle(ChatFormatting.DARK_AQUA))
					.setOnPress(button -> button.parent.startIDTransport(button.parent.encodedID));
		}
	}
}
