package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketCrystalComputerMainScreen extends PocketCrystalComputerScreen
{
	protected int page = 0;
	
	protected int copiedIndex = -1;
	protected SelectedCrystal copiedCrystal = SelectedCrystal.NONE;
	@Nullable
	protected MemoryEntry<?> copiedEntry = null;
	
	protected List<CrystalComputerButton> removableButtons = new ArrayList<>(5);
	
	public PocketCrystalComputerMainScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal)
	{
		super(interactionHand, selectedCrystal);
	}
    
    @Override
    public void init()
    {
    	super.init();
		
		updateButtons();
		// Already in main screen, so the button shouldn't be active
		mainScreenButton.active = false;
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y)
	{
		ItemStack crystalStack = getCrystal(selectedCrystal);
		
		if(!crystalStack.isEmpty())
			this.font.draw(stack, crystalStack.getHoverName(), x + 15, y + 15, 0xffffff);
		else
			this.font.draw(stack, Component.literal("NO CRYSTAL SELECTED"), x + 15, y + 15, 0xffffff);
	}
	
	@Override
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		super.selectCrystal(selectedCrystal);
		updateButtons();
	}
	
	public void updateButtons()
	{
		for(CrystalComputerButton button : removableButtons)
		{
			removeWidget(button);
		}
		removableButtons.clear();
		
		ItemStack stack = getCrystal(selectedCrystal);
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			int x = (width - imageWidth) / 2;
			int y = (height - imageHeight) / 2;
			
			ListTag list = MemoryCrystalItem.getMemoryList(stack);
			int i, index;
			for(i = 0, index = page; index <= list.size() && i < 5; index = ++i + page)
			{
				if(index > 0)
					addEntry(x, y, index, index - 1, list.size(), MemoryCrystalItem.loadMemoryEntry(list, index - 1));
				else // Extra entry button you can use to create a new entry
					addEntry(x, y, index, index - 1, list.size(), null);
			}
		}
	}
	
	protected void addRemovableButton(CrystalComputerButton button)
	{
		removableButtons.add(button);
		addRenderableWidget(button);
	}
	
	public void addEntry(int x, int y, int posIndex, int logicIndex, int listSize, @Nullable MemoryEntry<?> memoryEntry)
	{
		if(memoryEntry != null)
		{
			addRemovableButton(CrystalComputerButton.entryButton(x + 19, y + 24 + posIndex * 18, true,
					memoryEntry.toComponent(), Component.literal("[" + logicIndex + "] ").withStyle(ChatFormatting.BLUE).append(memoryEntry.toComponent()),
					button -> {}));
			
			//TODO Account for Memory Crystal memory limit
			
			// Copy-paste
			if(!isPasting())
				addRemovableButton(CrystalComputerButton.copyButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, true, Component.literal("COPY"), button -> copyEntry(logicIndex)));
			else
				addRemovableButton(CrystalComputerButton.pasteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, copiedIndex != logicIndex || copiedCrystal != selectedCrystal, Component.literal("PASTE"), button -> pasteEntry(logicIndex)));
			// Delete
			addRemovableButton(CrystalComputerButton.deleteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 20, y + 24 + posIndex * 18, !isPasting(), Component.literal("DELETE"), button -> deleteEntry(memoryEntry, logicIndex)));
			// Move up-down
			addRemovableButton(CrystalComputerButton.moveUpButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + posIndex * 18, !isPasting() && logicIndex != 0 /* Can't move above first one */, Component.literal("MOVE UP"), button -> swapEntries(logicIndex, logicIndex - 1)));
			addRemovableButton(CrystalComputerButton.moveDownButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + 8 + posIndex * 18, !isPasting() && logicIndex != listSize - 1 /* Can't move below last one */, Component.literal("MOVE DOWN"), button -> swapEntries(logicIndex, logicIndex + 1)));
		}
		else
		{
			addRemovableButton(CrystalComputerButton.entryButton(x + 19, y + 24 + posIndex * 18, true,
					Component.literal("+").withStyle(ChatFormatting.BOLD), Component.literal("Create new entry").withStyle(ChatFormatting.BLUE),
					button -> {}));
			
			// Copy-paste
			if(!isPasting())
				addRemovableButton(CrystalComputerButton.copyButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, false, Component.literal("COPY"), button -> {}));
			else
				addRemovableButton(CrystalComputerButton.pasteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 2, y + 24 + posIndex * 18, true, Component.literal("PASTE"), button -> pasteNewEntry()));
			// Delete
			addRemovableButton(CrystalComputerButton.deleteButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 20, y + 24 + posIndex * 18, false, Component.literal("DELETE"), button -> {}));
			// Move up-down
			addRemovableButton(CrystalComputerButton.moveUpButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + posIndex * 18, false /* Can't move a nonexistent entry */, Component.literal("MOVE UP"), button -> {}));
			addRemovableButton(CrystalComputerButton.moveDownButton(x + 19 + CrystalComputerButton.ENTRY_BUTTON_WIDTH + 38, y + 24 + 8 + posIndex * 18, false /* Can't move a nonexistent entry */, Component.literal("MOVE DOWN"), button -> {}));
		}
	}
	
	public boolean isPasting()
	{
		return copiedEntry != null;
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
	
	public void pasteEntry(int index)
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem)
			{
				MemoryCrystalItem.overwriteMemoryEntry(stack, copiedEntry, index);
				return true;
			}
			
			return false;
		});
		
		copiedIndex = -1;
		copiedCrystal = SelectedCrystal.NONE;
		copiedEntry = null;
		
		updateButtons();
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
}
