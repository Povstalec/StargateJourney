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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
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

public class RingPanelEntity extends TransporterControllerEntity
{
	public static final String ENERGY_INVENTORY = "energy_inventory";
	public static final String CRYSTAL_INVENTORY = "crystal_inventory";
	
	public static final String BUTTONS = "buttons";
	
	//------Button Stuff------
	protected Button[] buttons = { Button.defaultButton(this, 0), Button.defaultButton(this, 1), Button.defaultButton(this, 2), Button.defaultButton(this, 3), Button.defaultButton(this, 4), Button.defaultButton(this, 5) };
	protected int selectedSlot = -1;
	protected int page = -1;
	protected TransporterID.Mutable encodedID = null;
	
	private final ItemStackHandler crystalItemHandler = createCrystalItemHandler();
	private final LazyOptional<IItemHandler> lazyCrystalItemHandler = LazyOptional.of(() -> crystalItemHandler);
	
	private final ItemStackHandler energyItemHandler = createEnergyItemHandler();
	private final LazyOptional<IItemHandler> lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
	
	@Nullable
	private Transporter connectedTransporter;
	
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
				return stack.getItem() instanceof ZeroPointModule || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
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
	
	//TODO Feedback
	//TODO Tell the player there are no rings connected
	//TODO Memory
	//TODO Frequency
	//TODO Interdimensional transport (Materialization Crystals)
	
	public static void tick(Level level, BlockPos pos, BlockState state, RingPanelEntity ringPanel)
	{
		if(level.isClientSide())
			return;
		
		ringPanel.chargeFromEnergyItem();
	}
	
	protected Button nextButton(MinecraftServer server, int index, Iterator<Transporter> transporterIterator)
	{
		ButtonState state = buttonStateAt(index);
		
		if(state == ButtonState.MEMORY)
			return Button.memoryCrystalButton(this, index, true); //TODO Display number of entries, make uninteractable if there are 0 entries
		if(state == ButtonState.FREQUENCY)
			return Button.communicationCrystalButton(this, index, true);
		if(state == ButtonState.MANUAL)
			return Button.controlCrystalButton(this, index, true);
		else if(transporterIterator.hasNext())
			return Button.defaultTransportButton(this, index, server, transporterIterator.next());
		else
			return Button.defaultButton(this, index); // Empty Default Button
	}
	
	protected void updateButtons()
	{
		page = -1;
		selectedSlot = -1;
		encodedID = null;
		
		ServerLevel serverLevel = (ServerLevel) getLevel();
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransporters(serverLevel, getBlockPos(), 32768F, 0).iterator(); //TODO Specify frequency
		
		if(transporterIterator.hasNext()) //TODO Limit connection distance to this Transporter
			connectToTransporter(transporterIterator.next());
		
		for(int i = 0; i < 6; i++)
		{
			buttons[i] = nextButton(serverLevel.getServer(), i, transporterIterator);
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
	
	protected void setBaseCrystalPage(MinecraftServer server, int index)
	{
		if(index < 0)
			return;
		
		selectedSlot = index;
		page = 0;
		setButtonsFromMemoryCrystal(server, index);
	}
	
	public Button getButtonAt(int index)
	{
		return buttons[index];
	}
	
	public void pressButton(int index)
	{
		//TODO Button press energy cost
		
		getButtonAt(index).press();
		
		level.playSound(null, this.getBlockPos(), SoundInit.RING_PANEL_PRESS.get(), SoundSource.BLOCKS, 0.5F, 1F);
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
	
	protected Button nextManualButton(int index)
	{
		TransporterID.Mutable transporterID = new TransporterID.Mutable(encodedID);
		transporterID.addSymbol(index);
		return new Button(this, index, ButtonState.MANUAL, true).setTooltip(Component.literal(transporterID.toString()));
	}
	
	// ======= Transporting =======
	
	public void startCoordTransport(Vec3 coords)
	{
		if(level.getBlockEntity(new BlockPos(coords)) instanceof AbstractTransporterEntity transporterEntity)
			startTransport(transporterEntity.getTransporter());
	}
	
	public void startTransport(Transporter target)
	{
		TransporterNetwork.get(level).createConnection(level.getServer(), connectedTransporter, target);
		page = -1;
	}
	
	
	
	public enum ButtonState
	{
		DEFAULT, // Transporter Select
		MEMORY,
		FREQUENCY,
		MANUAL,
		
		PAGE_FORWARD,
		PAGE_BACK,
		RETURN;
		
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
		protected Transporter transporter = null;
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
		
		public Button setTransporter(Transporter transporter, Component tooltip, Vec3 coords)
		{
			this.transporter = transporter;
			this.tooltip = tooltip;
			this.coords = coords;
			
			return this;
		}
		
		public Button setTransporter(MinecraftServer server, Transporter transporter)
		{
			return setTransporter(transporter, transporter.getName(), transporter.getPosition(server));
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
		public Transporter transporter()
		{
			return transporter;
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
		
		public static Button defaultButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.DEFAULT, false).setOnPress(button ->
			{
				if(button.transporter() != null)
					button.parent.startTransport(button.transporter());
				
				//TODO return some feedback
			});
		}
		
		public static Button defaultTransportButton(RingPanelEntity parent, int index, MinecraftServer server, Transporter transporter)
		{
			return new Button(parent, index, ButtonState.DEFAULT, true).setTransporter(server, transporter).setOnPress(button ->
			{
				if(button.transporter() != null)
					button.parent.startTransport(button.transporter());
				
				//TODO return some feedback
			});
		}
		
		public static Button memoryCrystalButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.MEMORY, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.memory_entries").withStyle(ChatFormatting.BLUE)).setOnPress(button ->
			{
				if(button.parent.page < 0)
					button.parent.setBaseCrystalPage(button.parent.level.getServer(), index);
				else if(button.transporter() != null)
					button.parent.startTransport(button.transporter());
				else if(button.coords() != null)
					button.parent.startCoordTransport(button.coords());
				
				//TODO return some feedback
			});
		}
		
		public static Button memoryTransportButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.MEMORY, enabled).setOnPress(button ->
			{
				if(button.transporter() != null)
					button.parent.startTransport(button.transporter());
				
				//TODO return some feedback
			});
		}
		
		public static Button controlCrystalButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.MANUAL, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.manual_control").withStyle(ChatFormatting.AQUA)).setOnPress(button ->
			{
				if(button.parent.encodedID == null)
					button.parent.encodedID = new TransporterID.Mutable();
				else
					button.parent.encodedID.addSymbol(index + 1); //TODO Edge cases
				
				for(int i = 0; i < 4; i++)
				{
					button.parent.buttons[i] = button.parent.nextManualButton(i + 1);
				}
				
				button.parent.buttons[4] = Button.returnButton(button.parent, 4);
				button.parent.buttons[5] = button.parent.nextManualButton(5);
				
				button.parent.updateClient();
			});
		}
		
		public static Button communicationCrystalButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.FREQUENCY, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.frequency").withStyle(ChatFormatting.GRAY));
		}
		
		public static Button returnButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.RETURN, true).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.return").withStyle(ChatFormatting.RED)).setOnPress(button -> button.parent.updateButtons());
		}
		
		public static Button pageForwardButton(RingPanelEntity parent, int index, boolean enabled)
		{
			return new Button(parent, index, ButtonState.PAGE_FORWARD, enabled).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_back").append(Component.literal(" (" + (parent.page - 1) + ')'))).setOnPress(button ->
			{
				button.parent.page++;
				button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
			});
		}
		
		public static Button pageBackButton(RingPanelEntity parent, int index)
		{
			return new Button(parent, index, ButtonState.PAGE_BACK, true).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_forward").append(Component.literal(" (" + (parent.page + 1) + ')'))).setOnPress(button ->
			{
				button.parent.page--;
				button.parent.setButtonsFromMemoryCrystal(button.parent.level.getServer(), button.parent.selectedSlot);
			});
		}
	}
}
