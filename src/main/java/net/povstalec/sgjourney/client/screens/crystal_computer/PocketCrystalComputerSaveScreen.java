package net.povstalec.sgjourney.client.screens.crystal_computer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerEditBox;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.CoordinateEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterIDEntry;

import java.util.ArrayList;
import java.util.List;

public class PocketCrystalComputerSaveScreen extends PocketCrystalComputerScreen
{
	public static final int EDIT_BOX_WIDTH = 156;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	public static final int BUTTON_Y_OFFSET = 22;
	
	protected CrystalComputerEditBox editBox;
	protected BlockPos clickedPos;
	
	protected List<CrystalComputerButton> saveButtons = new ArrayList<>(2);
	
	public PocketCrystalComputerSaveScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal, BlockPos clickedPos)
	{
		super(interactionHand, selectedCrystal);
		
		this.clickedPos = clickedPos;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		editBox = addRenderableWidget(new CrystalComputerEditBox(font, x + 22, y + 38, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.empty()));
		editBox.setMaxLength(18);
		
		// Choose what information to save
		
		// Button to save coordinates
		saveButtons.add(CrystalComputerButton.largeButton(x + 22, y + 10 + 3 * BUTTON_Y_OFFSET, false,
				Component.translatable("screen.sgjourney.crystal_computer.save_coordinates"), ComponentHelper.coordinate(clickedPos),
				button -> saveToMemoryCrystalAndClose(new CoordinateEntry(editBox.getValue(), this.minecraft.level.getGameTime(), clickedPos))));
		addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		
		if(minecraft.level.getBlockEntity(clickedPos) instanceof AbstractTransporterEntity<?> transporter) // Choice between saving position and Transporter ID
		{
			// Button to save Transporter ID
			saveButtons.add(CrystalComputerButton.largeButton(x + 22, y + 10 + 4 * BUTTON_Y_OFFSET, false,
					Component.translatable("screen.sgjourney.crystal_computer.save_transporter_id"), transporter.getID().toComponent(false),
					button -> saveToMemoryCrystalAndClose(new TransporterIDEntry(editBox.getValue(), minecraft.level.getGameTime(), transporter.getID()))));
			addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		}
		else if(minecraft.level.getBlockState(clickedPos).getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			AbstractStargateEntity<?> stargate = stargateBlock.getStargate(minecraft.level, clickedPos, minecraft.level.getBlockState(clickedPos));
			if(stargate != null)
			{
				addRenderableWidget(CrystalComputerButton.largeButton(x + 22, y + 10 + 4 * BUTTON_Y_OFFSET, false,
						Component.translatable("screen.sgjourney.crystal_computer.save_address"),
						Component.translatable("screen.sgjourney.crystal_computer.cant_read_9_chevron_address").withStyle(ChatFormatting.DARK_RED),
						button -> {}));
			}
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, int x, int y)
	{
		
		graphics.drawCenteredString(font, Component.translatable("screen.sgjourney.crystal_computer.save_to_selected_crystal"), x + 101, y + 14, 0xffffff);
		graphics.drawString(font, Component.translatable("screen.sgjourney.crystal_computer.entry_name"), x + 20, y + 28, 0xffffff);
		
		if(selectedCrystal != SelectedCrystal.NONE)
		{
			if(selectedCrystalType(selectedCrystal) == CrystalCache.Type.MEMORY && !memoryCrystalHasFreeSpace(selectedCrystal))
				graphics.drawCenteredString(font, Component.translatable("screen.sgjourney.crystal_computer.memory_crystal_full"), x + 101, y + 64, DARK_RED_COLOR);
			else if(selectedCrystalType(selectedCrystal) != CrystalCache.Type.MEMORY)
				graphics.drawCenteredString(font, Component.translatable("screen.sgjourney.crystal_computer.not_memory_crystal"), x + 101, y + 64, DARK_RED_COLOR);
		}
	}
	
	@Override
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		super.selectCrystal(selectedCrystal);
		
		boolean hasFreeSpace = memoryCrystalHasFreeSpace(selectedCrystal);
		
		for(CrystalComputerButton button : saveButtons)
		{
			button.active = selectedCrystal != SelectedCrystal.NONE && hasFreeSpace;
		}
	}
	
	public void saveToMemoryCrystalAndClose(MemoryEntry<?> memoryEntry)
	{
		saveToMemoryCrystal(memoryEntry);
		this.minecraft.screen.onClose();
	}
}
