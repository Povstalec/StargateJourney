package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerEditBox;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

public class PocketCrystalComputerEditScreen extends PocketCrystalComputerScreen
{
	public static final int EDIT_BOX_WIDTH = 156;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	protected final PocketCrystalComputerMainScreen mainScreen;
	protected final int index;
	protected final MemoryEntry<?> memoryEntry;
	
	protected CrystalComputerEditBox editBox;
	
	public PocketCrystalComputerEditScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal, PocketCrystalComputerMainScreen mainScreen, MemoryEntry<?> memoryEntry, int index)
	{
		super(interactionHand, selectedCrystal);
		
		this.mainScreen = mainScreen;
		this.memoryEntry = memoryEntry;
		this.index = index;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		editBox = addRenderableWidget(new CrystalComputerEditBox(font, x + 22, y + 35, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.empty()));
		editBox.setMaxLength(18);
		editBox.setValue(memoryEntry.name());
		
		addRenderableWidget(CrystalComputerButton.smallButton(x + 13 + CrystalComputerButton.PAGE_BUTTON_WIDTH, y + 93, true,
				Component.translatable("screen.sgjourney.crystal_computer.save_entry"),
				Component.translatable("screen.sgjourney.crystal_computer.save_entry").append(": ").append(Component.literal(memoryEntry.entryString()).withStyle(memoryEntry.getChatFormatting())),
				button -> editEntry()));
		addRenderableWidget(CrystalComputerButton.smallButton(x + 13 + CrystalComputerButton.PAGE_BUTTON_WIDTH, y + 111, true,
				Component.translatable("screen.sgjourney.crystal_computer.cancel_entry_editing"),
				Component.translatable("screen.sgjourney.crystal_computer.cancel_entry_editing"),
				button -> this.minecraft.setScreen(mainScreen)));
		
		crystalInComputerButton.active = false;
		crystalInHandButton.active = false;
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, int x, int y)
	{
		graphics.drawCenteredString(font, Component.translatable("screen.sgjourney.crystal_computer.edit_entry"), x + 101, y + 14, 0xffffff);
		graphics.drawString(font, Component.translatable("screen.sgjourney.crystal_computer.entry_name").append(":"), x + 20, y + 25, 0xffffff);
		
		Component entryTypeComponent = memoryEntry.entryType().getComponent();
		graphics.drawString(font, Component.translatable("info.sgjourney.entry_type").append(": ").append(entryTypeComponent), x + 20, y + 61, entryTypeComponent.getStyle().getColor().getValue());
		graphics.drawString(font, Component.translatable("info.sgjourney.timestamp").append(": " + memoryEntry.timeStamp()), x + 20, y + 73, 0xffffff);
	}
	
	public void editEntry()
	{
		memoryEntry.setName(editBox.getValue());
		overwriteMemoryEntry(index, memoryEntry);
		this.minecraft.setScreen(mainScreen);
	}
}
