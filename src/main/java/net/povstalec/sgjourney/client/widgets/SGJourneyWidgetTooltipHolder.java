package net.povstalec.sgjourney.client.widgets;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;

import javax.annotation.Nullable;
import java.time.Duration;

public class SGJourneyWidgetTooltipHolder extends WidgetTooltipHolder
{
	@Nullable
	private Tooltip tooltip;
	private Duration delay;
	private long displayStartTime;
	private boolean wasDisplayed;
	
	public SGJourneyWidgetTooltipHolder()
	{
		this.delay = Duration.ZERO;
	}
	
	@Override
	public void setDelay(Duration delay)
	{
		this.delay = delay;
	}
	
	@Override
	public void set(@Nullable Tooltip tooltip)
	{
		this.tooltip = tooltip;
	}
	
	@Override
	public @Nullable Tooltip get()
	{
		return this.tooltip;
	}
	
	@Override
	public void refreshTooltipForNextRenderPass(boolean hovering, boolean focused, ScreenRectangle screenRectangle)
	{
		if(this.tooltip == null)
			this.wasDisplayed = false;
		else
		{
			boolean flag = hovering || focused && Minecraft.getInstance().getLastInputType().isKeyboard();
			if(flag != this.wasDisplayed)
			{
				if(flag)
					this.displayStartTime = Util.getMillis();
				
				this.wasDisplayed = flag;
			}
			
			if(flag && Util.getMillis() - this.displayStartTime > this.delay.toMillis())
			{
				Screen screen = Minecraft.getInstance().screen;
				if(screen != null)
					screen.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(screenRectangle, hovering, focused), focused);
			}
		}
		
	}
	
	protected ClientTooltipPositioner createTooltipPositioner(ScreenRectangle screenRectangle, boolean hovering, boolean focused)
	{
		return DefaultTooltipPositioner.INSTANCE;
	}
	
	@Override
	public void updateNarration(NarrationElementOutput output)
	{
		if(this.tooltip != null)
			this.tooltip.updateNarration(output);
	}
}
