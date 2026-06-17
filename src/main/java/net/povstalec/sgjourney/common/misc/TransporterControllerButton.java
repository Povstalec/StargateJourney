package net.povstalec.sgjourney.common.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.transporter.TransporterControllerEntity;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MaterializationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TransporterControllerButton<T extends TransporterControllerEntity>
{
	public static final String STATE = "state";
	public static final String STATUS = "status";
	public static final String COORDS = "coords";
	public static final String NAME = "name";
	public static final String CLOSE_SCREEN = "close_screen";
	
	public enum ButtonState
	{
		DEFAULT(0, ChatFormatting.GREEN), // Transporter Select
		MEMORY(1, ChatFormatting.BLUE),
		NETWORK(2, ChatFormatting.WHITE),
		MANUAL(3, ChatFormatting.AQUA),
		MATERIALIZATION(4, ChatFormatting.DARK_AQUA),
		
		PAGE_FORWARD(5, ChatFormatting.LIGHT_PURPLE),
		PAGE_BACK(5, ChatFormatting.LIGHT_PURPLE),
		RETURN(6, ChatFormatting.RED),
		ENTER(7, ChatFormatting.DARK_AQUA);
		
		public final int textureOffsetX;
		public final ChatFormatting chatFormatting;
		
		ButtonState(int textureOffsetX, ChatFormatting chatFormatting)
		{
			this.textureOffsetX = textureOffsetX;
			this.chatFormatting = chatFormatting;
		}
		
		public static ButtonState stateFromItem(Item item)
		{
			if(item instanceof MemoryCrystalItem)
				return ButtonState.MEMORY;
			if(item instanceof ControlCrystalItem)
				return ButtonState.MANUAL;
			if(item instanceof CommunicationCrystalItem)
				return ButtonState.NETWORK;
			if(item instanceof MaterializationCrystalItem)
				return ButtonState.MATERIALIZATION;
			
			return ButtonState.DEFAULT;
		}
	}
	
	public enum ButtonStatus
	{
		ENABLED(true),
		DISABLED(false),
		NO_TRANSPORTER(false),
		NO_REACHABLE_TRANSPORTER(false),
		NO_POWER(false);
		
		public final boolean isEnabled;
		
		ButtonStatus(boolean isEnabled)
		{
			this.isEnabled = isEnabled;
		}
	}
	
	public final T parent;
	public final int index;
	
	protected ButtonState state;
	protected ButtonStatus status;
	protected boolean closeScreen;
	
	@Nullable
	protected TransporterID transporterID = null;
	@Nullable
	protected Component tooltip = null;
	@Nullable
	protected Vec3 coords = null;
	
	@Nullable
	protected Runnable onUpdate = null;
	protected Consumer<TransporterControllerButton<T>> onPress;
	
	public TransporterControllerButton(T parent, int index, ButtonState state, ButtonStatus status)
	{
		this.parent = parent;
		this.index = index;
		
		this.state = state;
		this.status = status;
	}
	
	public TransporterControllerButton<T> setTransporter(TransporterID transporterID, Component tooltip, Vec3 coords)
	{
		this.transporterID = transporterID;
		this.tooltip = tooltip;
		this.coords = coords;
		
		return this;
	}
	
	public TransporterControllerButton<T> setTransporter(Transporter transporter)
	{
		return setTransporter(transporter.getID(), transporter.getName() != null ? transporter.getName().copy().withStyle(state.chatFormatting) : null, transporter.getPosition());
	}
	
	public TransporterControllerButton<T> setTransporter(Transporter transporter, ChatFormatting chatFormatting)
	{
		return setTransporter(transporter.getID(), transporter.getName() != null ? transporter.getName().copy().withStyle(chatFormatting) : null, transporter.getPosition());
	}
	
	public ButtonState state()
	{
		return state;
	}
	
	public boolean enabled()
	{
		return status.isEnabled;
	}
	
	public TransporterControllerButton<T> setCloseScreen(boolean closeScreen)
	{
		this.closeScreen = closeScreen;
		return this;
	}
	
	public boolean shouldCloseScreen()
	{
		return closeScreen;
	}
	
	@Nullable
	public TransporterID transporterID()
	{
		return transporterID;
	}
	
	public TransporterControllerButton<T> setTooltip(Component tooltip)
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
	
	public TransporterControllerButton<T> setOnPress(Consumer<TransporterControllerButton<T>> onPress)
	{
		this.onPress = onPress;
		
		return this;
	}
	
	public void press()
	{
		if(this.onPress != null)
			this.onPress.accept(this);
	}
	
	public TransporterControllerButton<T> setUpdate(Runnable onUpdate)
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
		tag.putByte(STATUS, (byte) status.ordinal());
		tag.putBoolean(CLOSE_SCREEN, closeScreen);
		
		if(coords != null)
			tag.put(COORDS, CoordinateHelper.vec3ToTag(coords));
		if(tooltip != null)
			tag.putString(NAME, Component.Serializer.toJson(this.tooltip));
		
		return tag;
	}
	
	public void deserialize(CompoundTag tag)
	{
		this.state = ButtonState.values()[tag.getByte(STATE)];
		this.status = ButtonStatus.values()[tag.getByte(STATUS)];
		this.closeScreen = tag.getBoolean(CLOSE_SCREEN);
		
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
	
	
	
	public TransporterControllerButton<T> addStatusTooltip(ButtonStatus status)
	{
		switch(status)
		{
			case NO_POWER -> setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.not_enough_energy").withStyle(ChatFormatting.DARK_RED));
			case NO_TRANSPORTER -> setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter").withStyle(ChatFormatting.DARK_RED));
			case NO_REACHABLE_TRANSPORTER -> setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.no_transporter_in_range").withStyle(ChatFormatting.DARK_RED));
		}
		
		return this;
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> defaultButton(T parent, int index, ButtonStatus status)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.DEFAULT, status).addStatusTooltip(status);
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> memoryButton(T parent, int index, ButtonStatus status)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.MEMORY, status).addStatusTooltip(status);
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> networkButton(T parent, int index, ButtonStatus status)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.NETWORK, status).addStatusTooltip(status);
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> manualControlButton(T parent, int index, ButtonStatus status)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.MANUAL, status).addStatusTooltip(status);
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> materializationButton(T parent, int index, ButtonStatus status)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.MATERIALIZATION, status).addStatusTooltip(status);
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> returnButton(T parent, int index)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.RETURN, ButtonStatus.ENABLED)
				.setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.return").withStyle(ChatFormatting.RED));
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> pageForwardButton(T parent, int index, int nextPage, boolean enabled)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.PAGE_FORWARD, enabled ? ButtonStatus.ENABLED : ButtonStatus.DISABLED)
				.setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_forward").append(Component.literal(" (" + nextPage + ')')).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> pageBackButton(T parent, int index, int prevPage)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.PAGE_BACK, ButtonStatus.ENABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.page_back").append(Component.literal(" (" + prevPage + ')')).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
	
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> emptyEnterButton(T parent, int index)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.ENTER, ButtonStatus.DISABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.start_transport"));
	}
		
	public static <T extends TransporterControllerEntity> TransporterControllerButton<T> enterButton(T parent, int index, TransporterID encodedID)
	{
		return new TransporterControllerButton<>(parent, index, ButtonState.ENTER, ButtonStatus.ENABLED).setTooltip(Component.translatable("tooltip.sgjourney.ring_panel.button.start_transport")
						.append(Component.literal(": ").append(encodedID.toComponent(false))).withStyle(ChatFormatting.DARK_AQUA)).setCloseScreen(true);
	}
}
