package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerEntryButton;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

public class PocketCrystalComputerMainScreen extends PocketCrystalComputerScreen
{
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
		
		ItemStack stack = getCrystalInComputer();
		if(stack.getItem() instanceof MemoryCrystalItem)
		{
			ListTag list = MemoryCrystalItem.getMemoryList(stack);
			for(int i = 0; i < list.size(); i++)
			{
				MemoryEntry<?> memoryEntry = MemoryCrystalItem.loadMemoryEntry(list, i);
				addRenderableWidget(new CrystalComputerEntryButton(x - 10, y + i * 30, memoryEntry, button -> {}));
			}
		}
		
		// Already in main screen, so the button shouldn't be active
		mainScreenButton.active = false;
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y)
	{
		this.font.draw(stack, getCrystalInComputer().getHoverName(), x + 266F, y + 132F, 0xffffff);
	}
	
	void updateButtons()
	{
	
	}
}
