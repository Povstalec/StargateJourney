package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerSaveButton;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
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
	
	protected EditBox editBox;
	protected BlockPos clickedPos;
	
	protected List<CrystalComputerSaveButton> saveButtons = new ArrayList<>(2);
	
	public PocketCrystalComputerSaveScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal, BlockPos clickedPos)
	{
		super(interactionHand, selectedCrystal);
		
		this.clickedPos = clickedPos;
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		
		this.editBox = new EditBox(this.font, x + 22, y + 28, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.empty());
		addRenderableWidget(this.editBox);
		
		// Choose what information to save
		
		// Button to save coordinates
		saveButtons.add(CrystalComputerSaveButton.big(x + 22, y + 10 + 3 * BUTTON_Y_OFFSET,
				Component.translatable("screen.sgjourney.crystal_computer.save_coordinates"), ComponentHelper.coordinate(clickedPos), false,
				button -> saveToMemoryCrystalAndClose(new CoordinateEntry(editBox.getValue(), this.minecraft.level.getGameTime(), clickedPos))));
		addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		
		if(this.minecraft.level.getBlockEntity(clickedPos) instanceof AbstractTransporterEntity<?> transporter) // Choice between saving position and Transporter ID
		{
			// Button to save Transporter ID
			saveButtons.add(CrystalComputerSaveButton.big(x + 22, y + 10 + 4 * BUTTON_Y_OFFSET,
					Component.translatable("screen.sgjourney.crystal_computer.save_transporter_id"), transporter.getID().toComponent(false), false,
					button -> saveToMemoryCrystalAndClose(new TransporterIDEntry(editBox.getValue(), this.minecraft.level.getGameTime(), transporter.getID()))));
			addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		}
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y)
	{
		this.font.draw(stack, Component.translatable("screen.sgjourney.crystal_computer.save_to_selected_crystal"), x + 15, y + 15, 0xffffff);
	}
	
	@Override
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		super.selectCrystal(selectedCrystal);
		
		crystalInComputerButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_COMPUTER && !getCrystalInComputer().isEmpty();
		crystalInHandButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_HAND && !getCrystalInHand().isEmpty();
		
		for(CrystalComputerSaveButton button : saveButtons)
		{
			button.active = selectedCrystal != SelectedCrystal.NONE;
		}
	}
	
	public void saveToMemoryCrystalAndClose(MemoryEntry<?> memoryEntry)
	{
		saveToMemoryCrystal(memoryEntry);
		this.minecraft.screen.onClose();
	}
}
