package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

public class PocketCrystalComputerDeleteScreen extends PocketCrystalComputerScreen
{
	protected final PocketCrystalComputerMainScreen mainScreen;
	protected final int index;
	protected final MemoryEntry<?> memoryEntry;
	
	public PocketCrystalComputerDeleteScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal, PocketCrystalComputerMainScreen mainScreen, MemoryEntry<?> memoryEntry, int index)
	{
		super(interactionHand, selectedCrystal);
		
		this.mainScreen = mainScreen;
		this.memoryEntry = memoryEntry;
		this.index = index;
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		
		addRenderableWidget(CrystalComputerButton.largeButton(x + 22, y + 10 + 30, true,Component.translatable("screen.sgjourney.crystal_computer.confirm_delete").withStyle(ChatFormatting.DARK_RED),
				Component.translatable("screen.sgjourney.crystal_computer.confirm_delete").append(": ").withStyle(ChatFormatting.DARK_RED).append(memoryEntry.toComponent()), button -> deleteEntry()));
		addRenderableWidget(CrystalComputerButton.largeButton(x + 22, y + 10 + 75, true, Component.translatable("screen.sgjourney.crystal_computer.cancel_celete").withStyle(ChatFormatting.AQUA),
				Component.translatable("screen.sgjourney.crystal_computer.cancel_delete").append(": ").withStyle(ChatFormatting.AQUA).append(memoryEntry.toComponent()), button -> this.minecraft.setScreen(mainScreen)));
	}
		
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y) {}
	
	public void deleteEntry()
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem)
			{
				MemoryCrystalItem.deleteMemoryEntry(stack, index);
				return true;
			}
			
			return false;
		});
		
		mainScreen.updateButtons();
		this.minecraft.setScreen(mainScreen);
	}
}
