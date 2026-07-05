package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;

public class PocketCrystalComputerEditScreen extends PocketCrystalComputerScreen
{
	public PocketCrystalComputerEditScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal)
	{
		super(interactionHand, selectedCrystal);
	}
	
	@Override
	protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y)
	{
	
	}
}
