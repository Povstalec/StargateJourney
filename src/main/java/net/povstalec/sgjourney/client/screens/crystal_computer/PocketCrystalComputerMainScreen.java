package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerEditBox;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketCrystalComputerMainScreen extends PocketCrystalComputerScreen
{
	public static final int EDIT_BOX_WIDTH = 156;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	protected int page = 0;
	
	protected int copiedIndex = -1;
	protected SelectedCrystal copiedCrystal = SelectedCrystal.NONE;
	@Nullable
	protected MemoryEntry<?> copiedEntry = null;
	
	protected List<CrystalComputerButton> removableButtons = new ArrayList<>(5);
	
	protected CrystalComputerButton pageBackButton;
	protected CrystalComputerButton pageForwardButton;
	protected CrystalComputerButton copyCancelButton;
	
	protected CrystalComputerEditBox editBox;
	protected CrystalComputerButton saveFrequencyButton;
	
	public PocketCrystalComputerMainScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal)
	{
		super(interactionHand, selectedCrystal);
	}
    
    @Override
    public void init()
    {
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		super.init();
		// Already in main screen, so the button shouldn't be active
		mainScreenButton.active = false;
		
		editBox = addRenderableWidget(new CrystalComputerEditBox(font, x + 22, y + 58, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.empty()));
		editBox.setFilter(text ->
		{
			if(text.isEmpty())
				return true;
			
			try { Integer.parseInt(text); return true; }
			catch (NumberFormatException e) { return false; }
		});
		
		this.editBox.setMaxLength(19);
		
		addRenderableWidget(saveFrequencyButton = CrystalComputerButton.smallButton(x + 13 + CrystalComputerButton.PAGE_BUTTON_WIDTH, y + 111, true,
				Component.translatable("screen.sgjourney.crystal_computer.save_frequency"),
				Component.translatable("screen.sgjourney.crystal_computer.save_frequency"),
				button -> setFrequencyAndClose()));
		
		updateButtons();
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, int x, int y)
	{
		ItemStack crystalStack = getCrystal(selectedCrystal);
		
		if(!crystalStack.isEmpty())
		{
			graphics.drawCenteredString(font, crystalStack.getHoverName(), x + 101, y + 14, 0xffffff);
			
			if(crystalStack.getItem() instanceof AbstractCrystalItem crystal)
			{
				if(crystal.getType() == CrystalCache.Type.COMMUNICATION)
					graphics.drawString(font, Component.translatable("tooltip.sgjourney.communication_crystal.frequency").append(":"), x + 20, y + 45, 0xffffff);
			}
		}
		else
			graphics.drawCenteredString(font, Component.translatable("screen.sgjourney.crystal_computer.no_crystal_selected"), x + 101, y + 67, 0xffffff);
	}
	
	@Override
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		super.selectCrystal(selectedCrystal);
		updateButtons();
	}
	
	public void updateButtons()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		
		for(CrystalComputerButton button : removableButtons)
		{
			removeWidget(button);
		}
		removableButtons.clear();
		
		removeWidget(pageBackButton);
		removeWidget(pageForwardButton);
		removeWidget(copyCancelButton);
		
		int entriesTotal = 1;
		
		ItemStack stack = getCrystal(selectedCrystal);
		CrystalCache.Type type = selectedCrystalType(selectedCrystal);
		
		boolean isMemoryCrystalSelected = type == CrystalCache.Type.MEMORY;
		if(isMemoryCrystalSelected)
		{
			ListTag list = MemoryCrystalItem.getMemoryList(stack);
			entriesTotal += list.size();
			int i, index;
			for(i = 0, index = page * 5; index <= list.size() && i < 5; index = ++i + page * 5)
			{
				if(index > 0)
					addEntry(x, y, i, index - 1, list.size(), MemoryCrystalItem.loadMemoryEntry(list, index - 1));
				else // Extra entry button you can use to create a new entry
					addEntry(x, y, i, index - 1, list.size(), null);
			}
		}
		/*else if(type == CrystalCache.Type.COMMUNICATION)
		{
		
		
		}*/
		/*else if(type == CrystalCache.Type.CONTROL)
		{
		
		}*/
		
		// Page back
		pageBackButton = addRenderableWidget(CrystalComputerButton.pageBackButton(x + 13, y + 113, page > 0, page > 0 ?
				Component.translatable("screen.sgjourney.crystal_computer.page_back").append(": " + (page - 1)) :
				Component.translatable("screen.sgjourney.crystal_computer.page_back"), button -> previousPage()));
		pageBackButton.visible = isMemoryCrystalSelected;
		
		// Cancel copying
		copyCancelButton = addRenderableWidget(CrystalComputerButton.smallButton(x + 13 + CrystalComputerButton.PAGE_BUTTON_WIDTH, y + 113, isPasting(), Component.translatable("screen.sgjourney.crystal_computer.cancel_entry_copy"),
				isPasting() ? Component.translatable("screen.sgjourney.crystal_computer.cancel_entry_copy").append(": ").append(copiedEntry.toComponent()) : Component.translatable("screen.sgjourney.crystal_computer.cancel_entry_copy"),
				button -> stopCopy()));
		copyCancelButton.visible = isMemoryCrystalSelected;
		
		// Page forward
		pageForwardButton = addRenderableWidget(CrystalComputerButton.pageForwardButton(x + 13 + CrystalComputerButton.PAGE_BUTTON_WIDTH + CrystalComputerButton.SMALL_BUTTON_WIDTH, y + 113, (page + 1) * 5 < entriesTotal, (page + 1) * 5 < entriesTotal ?
				Component.translatable("screen.sgjourney.crystal_computer.page_forward").append(": " + (page + 1)) :
				Component.translatable("screen.sgjourney.crystal_computer.page_forward"),
				button -> nextPage()));
		pageForwardButton.visible = isMemoryCrystalSelected;
		
		crystalInComputerButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_COMPUTER && !getCrystalInComputer().isEmpty();
		crystalInHandButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_HAND && !getCrystalInHand().isEmpty();
		
		if(CommunicationCrystalItem.hasFrequency(stack))
			editBox.setValue(Integer.toString(CommunicationCrystalItem.getFrequency(stack)));
		editBox.visible = selectedCrystalType(selectedCrystal) == CrystalCache.Type.COMMUNICATION;
		saveFrequencyButton.visible = selectedCrystalType(selectedCrystal) == CrystalCache.Type.COMMUNICATION;
	}
	
	protected void addRemovableButton(CrystalComputerButton button)
	{
		removableButtons.add(button);
		addRenderableWidget(button);
	}
	
	public void nextPage()
	{
		page++;
		updateButtons();
	}
	
	public void previousPage()
	{
		page--;
		updateButtons();
	}
	
	public void addEntry(int x, int y, int posIndex, int logicIndex, int listSize, @Nullable MemoryEntry<?> memoryEntry)
	{
		if(memoryEntry != null) // Saved Entry
		{
			Component entryComponent = memoryEntry.name().isEmpty() ? Component.literal(memoryEntry.entryString()).withStyle(memoryEntry.getChatFormatting()) : Component.literal(memoryEntry.name()).withStyle(ChatFormatting.GREEN);
			addRemovableButton(CrystalComputerButton.entryButton(x + 19, y + 24 + posIndex * 18, true,
					entryComponent, Component.literal("[" + logicIndex + "] ").withStyle(ChatFormatting.BLUE).append(memoryEntry.toComponent()),
					button -> editEntry(memoryEntry, logicIndex)));
			
			// Copy-paste
			if(!isPasting())
				addRemovableButton(CrystalComputerButton.copyButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, true, Component.translatable("screen.sgjourney.crystal_computer.copy_entry"), button -> copyEntry(logicIndex)));
			else
				addRemovableButton(CrystalComputerButton.pasteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, copiedIndex != logicIndex || copiedCrystal != selectedCrystal, Component.translatable("screen.sgjourney.crystal_computer.paste_entry"), button -> pasteEntry(logicIndex)));
			// Delete
			addRemovableButton(CrystalComputerButton.deleteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 20, y + 24 + posIndex * 18, !isPasting(), Component.translatable("screen.sgjourney.crystal_computer.delete_entry"), button -> deleteEntry(memoryEntry, logicIndex)));
			// Move up-down
			addRemovableButton(CrystalComputerButton.moveUpButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + posIndex * 18, !isPasting() && logicIndex != 0 /* Can't move above first one */, Component.translatable("screen.sgjourney.crystal_computer.move_entry_up"), button -> swapEntries(logicIndex, logicIndex - 1)));
			addRemovableButton(CrystalComputerButton.moveDownButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + 8 + posIndex * 18, !isPasting() && logicIndex != listSize - 1 /* Can't move below last one */, Component.translatable("screen.sgjourney.crystal_computer.move_entry_down"), button -> swapEntries(logicIndex, logicIndex + 1)));
		}
		else // New entry
		{
			boolean hasFreeSpace = memoryCrystalHasFreeSpace(selectedCrystal);
			addRemovableButton(CrystalComputerButton.entryButton(x + 19, y + 24 + posIndex * 18, hasFreeSpace,
					Component.literal("+").withStyle(ChatFormatting.BOLD), hasFreeSpace ? Component.translatable("screen.sgjourney.crystal_computer.create_entry") : Component.translatable("screen.sgjourney.crystal_computer.memory_crystal_full").withStyle(ChatFormatting.DARK_RED),
					button -> createEntry(logicIndex)));
			
			// Copy-paste
			if(!isPasting())
				addRemovableButton(CrystalComputerButton.copyButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, false, Component.translatable("screen.sgjourney.crystal_computer.copy_entry"), button -> {}));
			else
				addRemovableButton(CrystalComputerButton.pasteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, true, Component.translatable("screen.sgjourney.crystal_computer.paste_entry"), button -> pasteNewEntry()));
			// Delete
			addRemovableButton(CrystalComputerButton.deleteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 20, y + 24 + posIndex * 18, false, Component.translatable("screen.sgjourney.crystal_computer.delete_entry"), button -> {}));
			// Move up-down
			addRemovableButton(CrystalComputerButton.moveUpButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + posIndex * 18, false /* Can't move a nonexistent entry */, Component.translatable("screen.sgjourney.crystal_computer.move_entry_up"), button -> {}));
			addRemovableButton(CrystalComputerButton.moveDownButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + 8 + posIndex * 18, false /* Can't move a nonexistent entry */, Component.translatable("screen.sgjourney.crystal_computer.move_entry_down"), button -> {}));
		}
	}
	
	public boolean isPasting()
	{
		return copiedEntry != null;
	}
	
	public void createEntry(int index)
	{
		this.minecraft.setScreen(new PocketCrystalComputerCreateScreen(interactionHand, selectedCrystal, this, index));
	}
	
	public void editEntry(MemoryEntry<?> memoryEntry, int index)
	{
		this.minecraft.setScreen(new PocketCrystalComputerEditScreen(interactionHand, selectedCrystal, this, memoryEntry, index));
	}
	
	public void deleteEntry(MemoryEntry<?> memoryEntry, int index)
	{
		this.minecraft.setScreen(new PocketCrystalComputerDeleteScreen(interactionHand, selectedCrystal, this, memoryEntry, index));
	}
	
	public void swapEntries(int indexA, int indexB)
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem)
			{
				MemoryCrystalItem.swapMemoryEntries(stack, indexA, indexB);
				return true;
			}
			
			return false;
		});
		
		updateButtons();
	}
	
	public void copyEntry(int index)
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem)
			{
				copiedIndex = index;
				copiedCrystal = selectedCrystal;
				copiedEntry = MemoryCrystalItem.loadMemoryEntry(stack, index);
			}
			
			return false;
		});
		
		updateButtons();
	}
	
	public void stopCopy()
	{
		copiedIndex = -1;
		copiedCrystal = SelectedCrystal.NONE;
		copiedEntry = null;
		updateButtons();
	}
	
	public void pasteEntry(int index)
	{
		overwriteMemoryEntry(index, copiedEntry);
		
		stopCopy();
	}
	
	public void pasteNewEntry()
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystalItem)
			{
				memoryCrystalItem.saveMemoryEntry(stack, copiedEntry, true);
				return true;
			}
			
			return false;
		});
		
		copiedIndex = -1;
		copiedCrystal = SelectedCrystal.NONE;
		copiedEntry = null;
		
		updateButtons();
	}
	
	public void setFrequencyAndClose()
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof CommunicationCrystalItem)
			{
				String value = editBox.getValue();
				if(value.isEmpty())
					CommunicationCrystalItem.unsetFrequency(stack);
				else
					CommunicationCrystalItem.setFrequency(stack, Integer.parseInt(value));
				
				return true;
			}
			
			return false;
		});
		
		updateButtons();
		onClose();
	}
}
