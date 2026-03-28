package net.povstalec.sgjourney.client.widgets;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class DHDButton extends Button
{
	public DHDButton(int x, int y, int width, int height, Component component, Button.OnPress onPress)
	{
		super(x, y, width, height, component, onPress, Button.DEFAULT_NARRATION);
	}
	
	@Override
	public boolean keyPressed(int p_93374_, int p_93375_, int p_93376_)
	{
		if(this.active && this.visible)
		{
			if (p_93374_ != 257 && p_93374_ != 32 && p_93374_ != 335)
				return false;
			else
			{
				// Won't play sounds
				//this.playDownSound(Minecraft.getInstance().getSoundManager());
				this.onPress();
				return true;
			}
		}
		else
			return false;
	}
	
	@Override
	public void playDownSound(SoundManager soundManager) {}
	
	public void updateTooltip()
	{
		if(this.tooltip != null)
		{
			boolean isHoveredOrFocused = this.isHoveredOrFocused();
			if(isHoveredOrFocused != this.wasHoveredOrFocused)
			{
				if(isHoveredOrFocused)
					this.hoverOrFocusedStartTime = Util.getMillis();
				
				this.wasHoveredOrFocused = isHoveredOrFocused;
			}
			
			if(isHoveredOrFocused && Util.getMillis() - this.hoverOrFocusedStartTime > (long) this.tooltipMsDelay)
			{
				Screen screen = Minecraft.getInstance().screen;
				if (screen != null) {
					screen.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
				}
			}
		}
		
	}
}
