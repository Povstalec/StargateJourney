package net.povstalec.sgjourney.client.screens.crystal_computer;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerMainScreenButton;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerSaveButton;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.CoordinateEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterIDEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketCrystalComputerSaveScreen extends PocketCrystalComputerScreen
{
	public static final int EDIT_BOX_WIDTH = 156;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	public static final int BUTTON_Y_OFFSET = 22;
	
	public enum SaveLocation
	{
		CRYSTAL_IN_COMPUTER,
		CRYSTAL_IN_HAND
	}
	
	@Nullable
	protected SaveLocation saveLocation = null;
	
	protected EditBox editBox;
	protected BlockPos clickedPos;
	
	protected CrystalComputerSaveButton crystalInComputerButton;
	protected CrystalComputerSaveButton crystalInHandButton;
	
	protected List<CrystalComputerSaveButton> saveButtons = new ArrayList<>(2);
	
	public PocketCrystalComputerSaveScreen(InteractionHand interactionHand, BlockPos clickedPos)
	{
		super(interactionHand);
		
		this.clickedPos = clickedPos;
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		
		this.editBox = new EditBox(this.font, x + 14, y + 14, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.empty());
		addRenderableWidget(this.editBox);
		
		// Choose save location
		
		//TODO Set active based on if there is a crystal present
		
		// Button to set Crystal in computer as the target
		crystalInComputerButton = CrystalComputerSaveButton.small(this.width / 2 - (CrystalComputerSaveButton.SMALL_BUTTON_WIDTH + 3), y + 10 + BUTTON_Y_OFFSET,
				Component.literal("A"), Component.literal("Test A"), true,
				button -> setSaveLocation(SaveLocation.CRYSTAL_IN_COMPUTER));
		addRenderableWidget(crystalInComputerButton);
		
		// Button to set Crystal in hand as the target
		crystalInHandButton = CrystalComputerSaveButton.small(this.width / 2 + 3, y + 10 + BUTTON_Y_OFFSET,
				Component.literal("B"), Component.literal("Test B"), true,
				button -> setSaveLocation(SaveLocation.CRYSTAL_IN_HAND));
		addRenderableWidget(crystalInHandButton);
		
		// Choose what information to save
		
		// Button to save coordinates
		saveButtons.add(CrystalComputerSaveButton.big(x + 14, y + 10 + 3 * BUTTON_Y_OFFSET,
				ComponentHelper.coordinate(clickedPos), Component.translatable("screen.sgjourney.crystal_computer.save_coordinates"), false,
				button -> saveToMemoryCrystal(new CoordinateEntry(editBox.getValue(), this.minecraft.level.getGameTime(), clickedPos))));
		addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		
		if(this.minecraft.level.getBlockEntity(clickedPos) instanceof AbstractTransporterEntity<?> transporter) // Choice between saving position and Transporter ID
		{
			// Button to save Transporter ID
			saveButtons.add(CrystalComputerSaveButton.big(x + 14, y + 10 + 4 * BUTTON_Y_OFFSET,
					transporter.getID().toComponent(false), Component.translatable("screen.sgjourney.crystal_computer.save_transporter_id"), false,
					button -> saveToMemoryCrystal(new TransporterIDEntry(editBox.getValue(), this.minecraft.level.getGameTime(), transporter.getID()))));
			addRenderableWidget(saveButtons.get(saveButtons.size() - 1));
		}
		
		// Button to take you to the main screen
		addRenderableWidget(new CrystalComputerMainScreenButton(this.width / 2 + 86, this.height / 2 - 10,
				Component.empty(), Component.translatable("screen.sgjourney.crystal_computer.main_screen"),
				button -> this.minecraft.setScreen(new PocketCrystalComputerMainScreen(interactionHand))));
	}
	
	protected void setSaveLocation(SaveLocation saveLocation)
	{
		this.saveLocation = saveLocation;
		
		if(saveLocation == SaveLocation.CRYSTAL_IN_COMPUTER)
		{
			crystalInComputerButton.active = false;
			crystalInHandButton.active = true;
		}
		else
		{
			crystalInComputerButton.active = true;
			crystalInHandButton.active = false;
		}
		
		for(CrystalComputerSaveButton button : saveButtons)
		{
			button.active = true;
		}
	}
	
	protected void saveToMemoryCrystal(MemoryEntry<?> memoryEntry)
	{
		getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			ItemStack stack = itemHandler.extractItem(0, 1, false);
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			{
				memoryCrystal.saveMemoryEntry(stack, memoryEntry, true);
				itemHandler.insertItem(0, stack, false);
				updateServer();
			}
		});
		
		//TODO Save to different Crystals
		
		this.minecraft.screen.onClose();
	}
}
