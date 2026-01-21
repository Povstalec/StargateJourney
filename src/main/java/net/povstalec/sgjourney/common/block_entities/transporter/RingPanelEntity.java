package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.*;

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
	protected Button[] buttons = { new Button(), new Button(), new Button(), new Button(), new Button(), new Button() };
	protected int selectedSlot = -1;
	protected int page = -1;
	
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
	protected long capacity()
	{
		return CommonTransporterConfig.ring_panel_energy_capacity.get();
	}
	
	@Override
	protected long maxReceive()
	{
		return CommonTransporterConfig.ring_panel_max_energy_receive.get();
	}
	
	@Override
	protected long maxExtract()
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
	
	protected Button nextButton(MinecraftServer server, ButtonState state, Iterator<Transporter> transporterIterator)
	{
		if(state == ButtonState.MEMORY)
			return new Button(state, true, Component.literal("Open Memory Entries").withStyle(ChatFormatting.BLUE)); //TODO Display number of entries, make uninteractable if there are 0 entries
		if(state == ButtonState.FREQUENCY)
			return new Button(state, true, Component.literal("Frequency").withStyle(ChatFormatting.BLUE));
		if(state == ButtonState.MANUAL)
			return new Button(state, true, Component.literal("Manual connection").withStyle(ChatFormatting.BLUE));
		else if(transporterIterator.hasNext())
			return new Button(ButtonState.DEFAULT, server, transporterIterator.next()); // Transporter Default Button
		else
			return new Button(ButtonState.DEFAULT, false); // Empty Default Button
	}
	
	protected void updateButtons()
	{
		page = -1;
		selectedSlot = -1;
		
		ServerLevel serverLevel = (ServerLevel) getLevel();
		Iterator<Transporter> transporterIterator = LocatorHelper.findNearestTransporters(serverLevel, getBlockPos(), 32768F, 0).iterator(); //TODO Specify frequency
		
		if(transporterIterator.hasNext()) //TODO Limit connection distance to this Transporter
			connectToTransporter(transporterIterator.next());
		
		for(int i = 0; i < 6; i++)
		{
			buttons[i] = nextButton(serverLevel.getServer(), buttonStateAt(i), transporterIterator);
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
	
	public void pageForward()
	{
		page++;
		setButtonsFromMemoryCrystal(level.getServer(), selectedSlot);
	}
	
	public void pageBack()
	{
		page--;
		setButtonsFromMemoryCrystal(level.getServer(), selectedSlot);
	}
	
	public Button getButtonAt(int index)
	{
		return buttons[index];
	}
	
	public void pressButton(int index)
	{
		//TODO Button press cost
		
		Button button = buttons[index];
		
		if(button.state() == ButtonState.DEFAULT)
			pressDefaultButton(button, index);
		// Crystal Buttons
		else if(button.state() == ButtonState.MEMORY)
			pressMemoryCrystalButton(button, index);
		else if(button.state() == ButtonState.FREQUENCY)
			pressCommunicationCrystalButton(button, index);
		else if(button.state() == ButtonState.MANUAL)
			pressControlCrystalButton(button, index);
		// Pages
		else if(button.state() == ButtonState.PAGE_BACK)
			pageBack();
		else if(button.state() == ButtonState.PAGE_FORWARD)
			pageForward();
		else if(button.state() == ButtonState.RETURN)
			updateButtons();
		
		level.playSound(null, this.getBlockPos(), SoundInit.RING_PANEL_PRESS.get(), SoundSource.BLOCKS, 0.5F, 1F);
	}
	
	public ButtonState buttonStateAt(int index)
	{
		return ButtonState.stateFromItem(this.crystalItemHandler.getStackInSlot(index).getItem());
	}
	
	protected void pressDefaultButton(Button button, int index)
	{
		if(button.transporter() != null)
			startTransport(button.transporter());
		//TODO alternative return
	}
	
	// ======= Memory Crystal =======
	
	protected void pressMemoryCrystalButton(Button button, int index)
	{
		if(page < 0)
			setBaseCrystalPage(level.getServer(), index);
		else if(button.transporter() != null)
			startTransport(button.transporter());
		else if(button.coords() != null)
			startCoordTransport(button.coords());
		//TODO alternative return
	}
	
	protected Button loadButtonFromMemoryCrystal(MinecraftServer server, ListTag list, int index) //TODO Translatable components
	{
		MemoryEntry.Type type = MemoryCrystalItem.memoryTypeAt(list, index);
		
		if(type == MemoryEntry.Type.TRANSPORTER_ID)
		{
			MemoryEntry.TransporterID transporterID = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.TRANSPORTER_ID, index);
			Transporter transporter = BlockEntityList.get(server).getTransporter(transporterID.entry());
			if(transporter != null)
				return new Button(ButtonState.MEMORY, server, transporter, Component.literal(transporterID.name()).withStyle(ChatFormatting.AQUA));
			else
				return new Button(ButtonState.MEMORY, false, Component.literal("Invalid Transporter ID").withStyle(ChatFormatting.DARK_RED));
			
		}
		else if(type == MemoryEntry.Type.COORDINATES)
		{
			MemoryEntry.Coordinates coords = MemoryCrystalItem.loadMemoryEntry(list, MemoryEntry.Type.COORDINATES, index);
			if(coords != null)
				return new Button(ButtonState.MEMORY, true, null, Component.literal(coords.name()).withStyle(ChatFormatting.AQUA), coords.asVec3());
			else
				return new Button(ButtonState.MEMORY, false, Component.literal("No Transporter at location").withStyle(ChatFormatting.DARK_RED));
		}
		
		return new Button(ButtonState.MEMORY, false, Component.literal("No Entry"));
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
			buttons[4] = page == 0 ? new Button(ButtonState.RETURN, true) : new Button(ButtonState.PAGE_BACK, true, Component.literal("Prev Page (" + (page - 1) + ')'));
			buttons[5] = new Button(ButtonState.PAGE_FORWARD, start + 4 < list.size(), Component.literal("Next Page (" + (page + 1) + ')'));
		}
		else
		{
			buttons[4] = new Button(ButtonState.RETURN, true);
			buttons[5] = loadButtonFromMemoryCrystal(server, list, start + 4);
		}
		
		updateClient();
	}
	
	// ======= Communication Crystal =======
	
	protected void pressCommunicationCrystalButton(Button button, int index)
	{
		//TODO
	}
	
	// ======= Control Crystal =======
	
	protected void pressControlCrystalButton(Button button, int index)
	{
		//TODO
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
		
		private ButtonState state;
		private boolean enabled;
		
		@Nullable
		private Transporter transporter;
		@Nullable
		private Component tooltip;
		@Nullable
		private Vec3 coords;
		
		@Nullable
		private Runnable onUpdate = null;
		
		public Button(ButtonState state, boolean enabled, @Nullable Transporter transporter, @Nullable Component tooltip, @Nullable Vec3 coords)
		{
			this.state = state;
			this.enabled = enabled;
			
			this.transporter = transporter;
			this.tooltip = tooltip;
			this.coords = coords;
		}
		
		public Button()
		{
			this(ButtonState.DEFAULT, false, null, null, null);
		}
		
		public Button(ButtonState state, boolean enabled, Component tooltip)
		{
			this(state, enabled, null, tooltip, null);
		}
		
		public Button(ButtonState state, boolean enabled)
		{
			this(state, enabled, null, null, null);
		}
		
		public Button(ButtonState state, MinecraftServer server, Transporter transporter)
		{
			this(state, true, transporter, transporter.getName(), transporter.getPosition(server));
		}
		
		public Button(ButtonState state, MinecraftServer server, Transporter transporter, Component component)
		{
			this(state, true, transporter, component, transporter.getPosition(server));
		}
		
		public ButtonState state()
		{
			return state;
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
		
		public void setUpdate(Runnable onUpdate)
		{
			this.onUpdate = onUpdate;
		}
		
		private void runOnUpdate()
		{
			if(onUpdate != null)
				onUpdate.run();
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
	}
}
