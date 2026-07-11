package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public class PocketCrystalComputerCreateScreen extends PocketCrystalComputerScreen
{
	protected final PocketCrystalComputerMainScreen mainScreen;
	protected final int index;
	
	public PocketCrystalComputerCreateScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal, PocketCrystalComputerMainScreen mainScreen, int index)
	{
		super(interactionHand, selectedCrystal);
		
		this.mainScreen = mainScreen;
		this.index = index;
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, int x, int y)
	{
		graphics.drawCenteredString(font, Component.literal("TBD"), x + 101, y + 61, DARK_RED_COLOR);
	}
}
