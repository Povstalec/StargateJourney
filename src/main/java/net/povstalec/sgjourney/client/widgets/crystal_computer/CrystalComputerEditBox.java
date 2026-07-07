package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class CrystalComputerEditBox extends EditBox
{
	/*public static final int MAIN_COLOR = 0xFF001632;
	public static final int OUTLINE_COLOR = 0xFF000c1d;
	public static final int SELECTED_OUTLINE_COLOR = 0xFFFFFFFF;*/
	
	public CrystalComputerEditBox(Font font, int x, int y, int width, int height, Component tooltip)
	{
		super(font, x, y, width, height, tooltip);
		
		//setBordered(false);
	}
	
	/*public void renderButton(PoseStack poseStack, int x, int y, float partialTicks)
	{
		int i = this.isFocused() ? SELECTED_OUTLINE_COLOR : OUTLINE_COLOR;
		fill(poseStack, getX() - 1, getY() - 1, getX() + this.width + 1, getY() + this.height + 1, i);
		fill(poseStack, getX(), getY(), getX() + this.width, getY() + this.height, MAIN_COLOR);
		
		super.renderButton(poseStack, x, y, partialTicks);
	}*/
}
